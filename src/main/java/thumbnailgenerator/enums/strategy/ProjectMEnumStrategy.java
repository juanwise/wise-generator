package thumbnailgenerator.enums.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thumbnailgenerator.dto.Game;
import thumbnailgenerator.enums.games.ssbpm.ProjectMEnum;
import thumbnailgenerator.enums.games.ssbpm.ProjectMFighterArtTypeEnum;
import thumbnailgenerator.enums.interfaces.CharacterEnum;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;
import thumbnailgenerator.enums.interfaces.GameEnumStrategy;
import thumbnailgenerator.service.games.CharacterImageFetcher;
import thumbnailgenerator.service.games.ProjectMCharacterImageFetcher;

@Component
public class ProjectMEnumStrategy implements GameEnumStrategy {

    @Value("${thumbnail.settings.ssbpm.artwork.default}")
    private String defaultArtworkImageSettingsFile;
    @Autowired
    private ProjectMCharacterImageFetcher fetcher;

    @Override
    public Game getGame() {
        return Game.SSBPM;
    }

    @Override
    public Class<? extends CharacterEnum> getCharacterEnumClass() {
        return ProjectMEnum.class;
    }

    @Override
    public Class<? extends FighterArtTypeEnum> getFighterArtTypeEnumClass() {
        return ProjectMFighterArtTypeEnum.class;
    }

    @Override
    public CharacterImageFetcher getImageFetcher() {
        return fetcher;
    }

    @Override
    public FighterArtTypeEnum getDefaultFighterArtTypeEnumClass() {
        return ProjectMFighterArtTypeEnum.ARTWORK;
    }

    @Override
    public String getDefaultFighterImageSettingsFile(FighterArtTypeEnum fighterArtTypeEnum) {
        return defaultArtworkImageSettingsFile;
    }
}
