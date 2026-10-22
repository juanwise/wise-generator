package thumbnailgenerator.service;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thumbnailgenerator.dto.Tournament;
import thumbnailgenerator.service.games.GameEnumService;
import thumbnailgenerator.utils.startgg.QueryUtils;
import thumbnailgenerator.utils.startgg.StandingsParser;
import thumbnailgenerator.utils.startgg.StartGGUrlUtils;

/**
 * Builds a Top 8 generation script from a Start.gg standings link.
 * <p>
 * The standings give the top 8 placements; the character shown for each
 * placement is the one that player picked the most during the event. The
 * script is in the same format {@link Top8FileService} reads from a file, so
 * it is generated with the existing Top 8 code.
 */
@Service
public class Top8StandingsService {

    private static final Logger LOGGER = LogManager.getLogger(Top8StandingsService.class);
    public static final int TOP_PLACEMENTS = 8;

    private @Autowired StartGGService startGGService;
    private @Autowired GameEnumService gameEnumService;

    @Getter
    public static class Result {
        private final String script;
        private final String eventName;
        private final List<String> warnings;

        Result(String script, String eventName, List<String> warnings) {
            this.script = script;
            this.eventName = eventName;
            this.warnings = warnings;
        }
    }

    /**
     * Requires {@link StartGGService#initClient(String)} to have been called.
     *
     * @param standingsUrl         any Start.gg event link (the /standings page works)
     * @param tournament           tournament whose Top 8 settings are used
     * @param charactersPerPlayer  how many characters to show per placement (1-5)
     * @throws IllegalArgumentException when the link/event/game is not usable
     */
    public Result buildTop8Script(String standingsUrl, Tournament tournament, int charactersPerPlayer)
            throws ExecutionException, InterruptedException {
        var slug = StartGGUrlUtils.extractEventSlug(standingsUrl);
        LOGGER.info("Reading top {} standings of event {}.", TOP_PLACEMENTS, slug);

        var standingsResponse = requireData(
                startGGService.runQuery(QueryUtils.eventStandingsQuery(slug, TOP_PLACEMENTS)));
        var event = StandingsParser.parseEventStandings(standingsResponse);
        if (event.getStandings().isEmpty()) {
            throw new IllegalArgumentException("This event has no standings yet. "
                    + "Standings are only available once the event has been completed.");
        }

        var game = startGGService.findGameByStartGGId(event.getVideoGameId());
        if (game == null) {
            throw new IllegalArgumentException("The game of event \"" + event.getEventName()
                    + "\" is not supported by this program.");
        }

        var entrantIds = event.getStandings().stream()
                .map(StandingsParser.Standing::getEntrantId)
                .collect(Collectors.toSet());
        var counts = readCharacterPicks(event.getEventId(), entrantIds);

        var warnings = new ArrayList<String>();
        var script = new StringBuilder();
        script.append(tournament.getTournamentId()).append(";")
                .append(game.name()).append(";")
                .append(gameEnumService.getDefaultArtType(game).getEnumName())
                .append(System.lineSeparator());

        for (var standing : event.getStandings()) {
            var characterIds = StandingsParser.mostUsedCharacters(
                    counts.get(standing.getEntrantId()), charactersPerPlayer);
            if (characterIds.isEmpty()) {
                warnings.add(standing.getName() + " (" + ordinal(standing.getPlacement())
                        + ") has no character data on Start.gg. A random character was used.");
                script.append(characterPair("random")).append(System.lineSeparator());
                continue;
            }
            var pairs = new ArrayList<String>();
            for (int characterId : characterIds) {
                pairs.add(characterPair(startGGService.findCodeByStartggId(game, characterId)));
            }
            script.append(String.join(";", pairs)).append(System.lineSeparator());
        }

        if (event.getStandings().size() < TOP_PLACEMENTS) {
            warnings.add("Only " + event.getStandings().size() + " placements were found. "
                    + "The remaining slots of the Top 8 will be empty.");
        }
        LOGGER.debug("Top 8 script:{}{}", System.lineSeparator(), script);
        return new Result(script.toString(), event.getEventName(), warnings);
    }

    private Map<Integer, Map<Integer, Integer>> readCharacterPicks(int eventId, Set<Integer> entrantIds)
            throws ExecutionException, InterruptedException {
        var counts = new HashMap<Integer, Map<Integer, Integer>>();
        int page = 0;
        int totalPages;
        do {
            page++;
            JsonObject response = requireData(startGGService.runQuery(
                    QueryUtils.eventCharacterSelectionsQuery(eventId, page)));
            totalPages = StandingsParser.getTotalPages(response);
            StandingsParser.tallyCharacters(response, new HashSet<>(entrantIds), counts);
        } while (page < totalPages);
        return counts;
    }

    /** Start.gg answers API problems (bad token, rate limit...) with an "errors" entry and no data. */
    private JsonObject requireData(JsonObject response) {
        if (response == null || !response.has("data") || response.get("data").isJsonNull()) {
            var detail = response != null && response.has("errors") ? " " + response.get("errors") : "";
            throw new IllegalArgumentException("Start.gg did not return any data." + detail);
        }
        return response;
    }

    private String characterPair(String characterCode) {
        // Top 8 files use "character;alt" pairs. Start.gg does not tell us the alt.
        return characterCode + ";1";
    }

    private String ordinal(int placement) {
        return placement + suffix(placement);
    }

    private String suffix(int n) {
        if (n % 100 >= 11 && n % 100 <= 13) return "th";
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
