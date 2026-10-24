package thumbnailgenerator.enums.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thumbnailgenerator.dto.Game;
import thumbnailgenerator.enums.games.ssbf2.SmashFlash2Enum;
import thumbnailgenerator.enums.games.ssbf2.SmashFlash2FighterArtTypeEnum;
import thumbnailgenerator.enums.interfaces.CharacterEnum;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;
import thumbnailgenerator.enums.interfaces.GameEnumStrategy;
import thumbnailgenerator.service.games.CharacterImageFetcher;
import thumbnailgenerator.service.games.SmashFlash2CharacterImageFetcher;

@Component
public class SmashFlash2EnumStrategy implements GameEnumStrategy {

    @Value("${thumbnail.settings.ssbf2.artwork.default}")
    private String defaultArtworkImageSettingsFile;
    @Autowired
    private SmashFlash2CharacterImageFetcher fetcher;

    @Override
    public Game getGame() {
        return Game.SSBF2;
    }

    @Override
    public Class<? extends CharacterEnum> getCharacterEnumClass() {
        return SmashFlash2Enum.class;
    }

    @Override
    public Class<? extends FighterArtTypeEnum> getFighterArtTypeEnumClass() {
        return SmashFlash2FighterArtTypeEnum.class;
    }

    @Override
    public CharacterImageFetcher getImageFetcher() {
        return fetcher;
    }

    @Override
    public FighterArtTypeEnum getDefaultFighterArtTypeEnumClass() {
        return SmashFlash2FighterArtTypeEnum.ARTWORK;
    }

    @Override
    public String getDefaultFighterImageSettingsFile(FighterArtTypeEnum fighterArtTypeEnum) {
        return defaultArtworkImageSettingsFile;
    }
}
