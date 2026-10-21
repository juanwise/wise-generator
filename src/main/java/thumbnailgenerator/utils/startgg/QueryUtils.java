package thumbnailgenerator.utils.startgg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueryUtils {
    private static final Logger LOGGER = LogManager.getLogger(QueryUtils.class);

    public static String tournamentDetailsQuery(String tournamentURL){
        String[] urlSplit = tournamentURL.split("/");
        return "query{ " +
                "tournament(slug:  \""+ urlSplit[3] + "/" + urlSplit[4] + "\") {id " +
                "streams{ streamName }" +
                "events{ id name " +
                "videogame { id name } " +
                "phases{ id name " +
                "phaseGroups{ nodes{ id displayIdentifier " +
                "}}}}}}";
    }

    public static String getSetsByEvent(int eventId, int currentPage){
        return getSetsByEvent(eventId, currentPage, false);
    }

    public static String getSetsByEvent(int eventId, int currentPage, boolean withCountry){
        return "query {" +
                "event(id: " + eventId + ") { id name " +
                setPagesAndMatchesSubQuery(currentPage, withCountry) +
                "}}";
    }

    public static String getSetsByPhase(int phaseId, int currentPage){
        return getSetsByPhase(phaseId, currentPage, false);
    }

    public static String getSetsByPhase(int phaseId, int currentPage, boolean withCountry){
        return "query {" +
                "phase(id: " + phaseId + ") { id name " +
                setPagesAndMatchesSubQuery(currentPage, withCountry) +
                "}}";
    }

    public static String getSetsByPhaseGroup(int phaseGroupId, int currentPage){
        return getSetsByPhaseGroup(phaseGroupId, currentPage, false);
    }

    public static String getSetsByPhaseGroup(int phaseGroupId, int currentPage, boolean withCountry){
        return "query {" +
                "phaseGroup(id: " + phaseGroupId + ") { id displayIdentifier " +
                setPagesAndMatchesSubQuery(currentPage, withCountry) +
                "}}";
    }

    /**
     * Top placements of an event, from a link such as
     * tournament/my-tournament/event/ultimate-singles.
     */
    public static String eventStandingsQuery(String eventSlug, int topPlacements){
        return "query { event(slug: \"" + eventSlug + "\") { id name " +
                "videogame { id name } " +
                "standings(query: {page: 1, perPage: " + topPlacements + "}) { " +
                "nodes { placement entrant { id name } } } } }";
    }

    /**
     * Characters picked in every game of an event, used to find the most
     * played character of each top placement.
     */
    public static String eventCharacterSelectionsQuery(int eventId, int currentPage){
        return "query { event(id: " + eventId + ") { " +
                "sets(page: " + currentPage + " perPage: 20 sortType: RECENT){ " +
                "pageInfo { totalPages } " +
                "nodes { games { selections { entrant { id } selectionValue selectionType } } } " +
                "} } }";
    }

    private static String setPagesAndMatchesSubQuery(int currentPage, boolean withCountry){
        return "sets(page: " + currentPage + " perPage: 20 sortType: RECENT){ " +
                "pageInfo { totalPages } " +
                matchDetailsSubQuery(withCountry) +
                "}";
    }

    private static String matchDetailsSubQuery(boolean withCountry){
        var entrantFields = withCountry
                ? "name participants{ user{ location{ country } } }"
                : "name";
        return "nodes { fullRoundText " +
                "slots{ entrant{ " + entrantFields + " } } " +
                "games{ selections{ entrant { name } selectionValue selectionType} } " +
                "stream{ streamName } " +
                "}";
    }

}
