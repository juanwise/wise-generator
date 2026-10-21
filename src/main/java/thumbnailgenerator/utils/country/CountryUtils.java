package thumbnailgenerator.utils.country;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Helpers to convert between country names and ISO 3166-1 alpha-2 codes.
 */
public final class CountryUtils {

    private static final Set<String> CODES = new HashSet<>();
    private static final Map<String, String> NAME_TO_CODE = new HashMap<>();
    private static final List<CountryOption> OPTIONS = new ArrayList<>();

    static {
        for (String code : Locale.getISOCountries()) {
            var name = new Locale("", code).getDisplayCountry(Locale.ENGLISH);
            CODES.add(code);
            NAME_TO_CODE.put(normalize(name), code);
            OPTIONS.add(new CountryOption(code, name));
        }
        OPTIONS.sort(Comparator.comparing(CountryOption::getName));

        // Names Start.gg / users commonly use that differ from the JDK's names
        alias("USA", "US");
        alias("United States of America", "US");
        alias("UK", "GB");
        alias("Great Britain", "GB");
        alias("England", "GB");
        alias("Scotland", "GB");
        alias("Wales", "GB");
        alias("Northern Ireland", "GB");
        alias("Russian Federation", "RU");
        alias("Korea, Republic of", "KR");
        alias("Republic of Korea", "KR");
        alias("Czech Republic", "CZ");
        alias("Turkey", "TR");
        alias("Hong Kong", "HK");
        alias("Macau", "MO");
        alias("Vietnam", "VN");
        alias("Ivory Coast", "CI");
        alias("Cape Verde", "CV");
        alias("Swaziland", "SZ");
        alias("Burma", "MM");
    }

    private CountryUtils() {
    }

    private static void alias(String name, String code) {
        NAME_TO_CODE.put(normalize(name), code);
    }

    private static String normalize(String value) {
        var decomposed = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    /**
     * Accepts either an ISO alpha-2 code ("pt", "PT") or an English country
     * name ("Portugal") and returns the upper case code.
     *
     * @return the code, or null when the value is empty or not recognised
     */
    public static String toCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        var trimmed = value.trim();
        if (trimmed.length() == 2) {
            var upper = trimmed.toUpperCase(Locale.ROOT);
            return CODES.contains(upper) ? upper : null;
        }
        return NAME_TO_CODE.get(normalize(trimmed));
    }

    /** All selectable countries, sorted by name, starting with "No flag". */
    public static List<CountryOption> getOptions() {
        var result = new ArrayList<CountryOption>();
        result.add(CountryOption.NONE);
        result.addAll(OPTIONS);
        return result;
    }

    public static CountryOption findOption(String code) {
        var normalized = toCode(code);
        if (normalized == null) {
            return CountryOption.NONE;
        }
        return OPTIONS.stream()
                .filter(o -> o.getCode().equals(normalized))
                .findFirst()
                .orElse(CountryOption.NONE);
    }
}
