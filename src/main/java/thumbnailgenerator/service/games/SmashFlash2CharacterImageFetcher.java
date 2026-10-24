package thumbnailgenerator.service.games;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import thumbnailgenerator.dto.Fighter;
import thumbnailgenerator.enums.interfaces.FighterArtTypeEnum;

import java.net.MalformedURLException;
import java.net.URL;

@Service
public class SmashFlash2CharacterImageFetcher extends CharacterImageFetcher {

    @Value("${character-image.ssbf2.artwork.path}")
    private String artworkPath;

    // Smash Flash 2 images are hosted in this program's own repository (assets/characters/ssbf2/...),
    // not in the shared character image repository.
    @Value("${character-image.ssbf2.base-url}")
    private String ssbf2BaseUrl;

    @Override
    public URL getOnlineUrl(Fighter fighter, FighterArtTypeEnum artType, boolean backup)
            throws MalformedURLException {
        String urlString = artworkPath + "/" + fighter.getUrlName() + "/" + fighter.getAlt() +".png";
        return new URL(ssbf2BaseUrl + urlString);
    }
}
