package thumbnailgenerator.enums.games.ssbpm;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;

@Getter
public enum ProjectMFighterArtTypeEnum implements FighterArtTypeEnum {
    ARTWORK("Artworks");

    private String value;

    ProjectMFighterArtTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getEnumName() {
        return this.name().toUpperCase();
    }

}
