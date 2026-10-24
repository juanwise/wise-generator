package thumbnailgenerator.service.games;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thumbnailgenerator.dto.Fighter;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class ProjectMCharacterImageFetcher extends CharacterImageFetcher {

    @Value("${character-image.ssbpm.artwork.path}")
    private String artworkPath;

    // Project M images are hosted in this program's own repository (assets/characters/ssbpm/...),
    // not in the shared character image repository.
    @Value("${character-image.ssbpm.base-url}")
    private String ssbpmBaseUrl;

    @Override
    public URL getOnlineUrl(Fighter fighter, FighterArtTypeEnum artType, boolean backup)
            throws MalformedURLException {
        String urlString = artworkPath + "/" + fighter.getUrlName() + "/" + fighter.getAlt() +".png";
        return new URL(ssbpmBaseUrl + urlString);
    }
}
