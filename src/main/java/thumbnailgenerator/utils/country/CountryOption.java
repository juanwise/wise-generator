package thumbnailgenerator.utils.country;

/**
 * A selectable country for the flag option. {@link #NONE} represents
 * "no country" (no flag drawn).
 */
public final class CountryOption {
    public static final CountryOption NONE = new CountryOption(null, "No flag");

    private final String code;
    private final String name;

    public CountryOption(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /** Upper case ISO 3166-1 alpha-2 code, or null for {@link #NONE}. */
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return code == null ? name : name + " (" + code + ")";
    }
}
