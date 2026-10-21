package thumbnailgenerator.utils.startgg;

import java.util.regex.Pattern;

public final class StartGGUrlUtils {

    private static final Pattern EVENT_URL = Pattern.compile(
            "(?:start|smash)\\.gg/(tournament/[\\w-]+)/events?/([\\w-]+)",
            Pattern.CASE_INSENSITIVE);

    private StartGGUrlUtils() {
    }

    /**
     * Extracts the event slug ("tournament/xxx/event/yyy") from any Start.gg
     * event link, e.g. https://www.start.gg/tournament/xxx/event/yyy/standings
     *
     * @throws IllegalArgumentException when the link is not a Start.gg event link
     */
    public static String extractEventSlug(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Empty Start.gg link.");
        }
        var matcher = EVENT_URL.matcher(url.trim());
        if (!matcher.find()) {
            throw new IllegalArgumentException(url + " is not a valid Start.gg event link. "
                    + "Ex.: https://www.start.gg/tournament/my-tournament/event/ultimate-singles/standings");
        }
        return matcher.group(1) + "/event/" + matcher.group(2);
    }
}
