package thumbnailgenerator.enums.games.ssbf2;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;

@Getter
public enum SmashFlash2FighterArtTypeEnum implements FighterArtTypeEnum {
    ARTWORK("Artworks");

    private String value;

    SmashFlash2FighterArtTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getEnumName() {
        return this.name().toUpperCase();
    }

}
