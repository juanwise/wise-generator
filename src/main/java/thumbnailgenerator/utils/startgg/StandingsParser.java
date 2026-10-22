package thumbnailgenerator.utils.startgg;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Turns raw Start.gg API responses (standings and per-game character
 * selections) into plain data used to build a Top 8 generation script.
 */
public final class StandingsParser {

    private StandingsParser() {
    }

    public static final class Standing {
        private final int placement;
        private final int entrantId;
        private final String name;

        public Standing(int placement, int entrantId, String name) {
            this.placement = placement;
            this.entrantId = entrantId;
            this.name = name;
        }

        public int getPlacement() {
            return placement;
        }

        public int getEntrantId() {
            return entrantId;
        }

        public String getName() {
            return name;
        }
    }

    public static final class EventInfo {
        private final int eventId;
        private final String eventName;
        private final int videoGameId;
        private final List<Standing> standings;

        public EventInfo(int eventId, String eventName, int videoGameId, List<Standing> standings) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.videoGameId = videoGameId;
            this.standings = standings;
        }

        public int getEventId() {
            return eventId;
        }

        public String getEventName() {
            return eventName;
        }

        public int getVideoGameId() {
            return videoGameId;
        }

        public List<Standing> getStandings() {
            return standings;
        }
    }

    /**
     * @param response full response of {@link QueryUtils#eventStandingsQuery}
     * @throws IllegalArgumentException when the event does not exist
     */
    public static EventInfo parseEventStandings(JsonObject response) {
        var data = response.getAsJsonObject("data");
        if (data == null || !data.has("event") || data.get("event").isJsonNull()) {
            throw new IllegalArgumentException("Start.gg could not find this event. "
                    + "Check the link and that the event is public.");
        }
        var event = data.getAsJsonObject("event");
        var standings = new ArrayList<Standing>();
        if (event.has("standings") && !event.get("standings").isJsonNull()) {
            var nodes = event.getAsJsonObject("standings").getAsJsonArray("nodes");
            if (nodes != null) {
                for (JsonElement node : nodes) {
                    var obj = node.getAsJsonObject();
                    var entrant = obj.get("entrant");
                    if (obj.get("placement").isJsonNull() || entrant == null || entrant.isJsonNull()) {
                        continue;
                    }
                    var entrantObj = entrant.getAsJsonObject();
                    standings.add(new Standing(
                            obj.get("placement").getAsInt(),
                            entrantObj.get("id").getAsInt(),
                            stripSponsor(entrantObj.get("name").getAsString())));
                }
            }
        }
        standings.sort(Comparator.comparingInt(Standing::getPlacement));

        int videoGameId = -1;
        if (event.has("videogame") && !event.get("videogame").isJsonNull()) {
            videoGameId = event.getAsJsonObject("videogame").get("id").getAsInt();
        }
        return new EventInfo(
                event.get("id").getAsInt(),
                event.get("name").getAsString(),
                videoGameId,
                standings);
    }

    /** "Team | Tag" -> "Tag", same convention used for thumbnails. */
    static String stripSponsor(String name) {
        return name.contains(" | ") ? name.split(" \\| ")[1].trim() : name;
    }

    /**
     * @return total number of pages of a sets-selection response
     */
    public static int getTotalPages(JsonObject response) {
        return response.getAsJsonObject("data")
                .getAsJsonObject("event")
                .getAsJsonObject("sets")
                .getAsJsonObject("pageInfo")
                .get("totalPages").getAsInt();
    }

    /**
     * Adds the character picks found in one page of sets to {@code counts}
     * (entrant id -> character start.gg id -> times picked), only for the
     * given entrants.
     */
    public static void tallyCharacters(JsonObject response, Set<Integer> entrantIds,
                                       Map<Integer, Map<Integer, Integer>> counts) {
        var sets = response.getAsJsonObject("data")
                .getAsJsonObject("event")
                .getAsJsonObject("sets");
        var nodes = sets.getAsJsonArray("nodes");
        if (nodes == null) {
            return;
        }
        for (JsonElement setElement : nodes) {
            var games = setElement.getAsJsonObject().get("games");
            if (games == null || games.isJsonNull()) {
                continue;
            }
            for (JsonElement game : games.getAsJsonArray()) {
                var selections = game.getAsJsonObject().get("selections");
                if (selections == null || selections.isJsonNull()) {
                    continue;
                }
                addSelections(selections.getAsJsonArray(), entrantIds, counts);
            }
        }
    }

    private static void addSelections(JsonArray selections, Set<Integer> entrantIds,
                                      Map<Integer, Map<Integer, Integer>> counts) {
        for (JsonElement selectionElement : selections) {
            var selection = selectionElement.getAsJsonObject();
            var entrant = selection.get("entrant");
            if (entrant == null || entrant.isJsonNull()
                    || selection.get("selectionValue").isJsonNull()) {
                continue;
            }
            var type = selection.get("selectionType");
            if (type != null && !type.isJsonNull() && !"CHARACTER".equals(type.getAsString())) {
                continue;
            }
            int entrantId = entrant.getAsJsonObject().get("id").getAsInt();
            if (!entrantIds.contains(entrantId)) {
                continue;
            }
            int character = selection.get("selectionValue").getAsInt();
            counts.computeIfAbsent(entrantId, k -> new HashMap<>())
                    .merge(character, 1, Integer::sum);
        }
    }

    /**
     * @return the {@code max} most picked characters, most picked first.
     *         Ties keep a stable order (lowest id first).
     */
    public static List<Integer> mostUsedCharacters(Map<Integer, Integer> counts, int max) {
        var result = new ArrayList<Integer>();
        if (counts == null) {
            return result;
        }
        counts.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(max)
                .forEach(e -> result.add(e.getKey()));
        return result;
    }
}
