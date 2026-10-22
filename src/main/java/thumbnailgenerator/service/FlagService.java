package thumbnailgenerator.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provides country flag images. Flags are looked up in the local flags folder
 * (assets/flags/pt.png) first; when missing they are downloaded once and kept
 * there. Users can also drop their own PNG files in that folder.
 */
@Service
public class FlagService {

    private static final Logger LOGGER = LogManager.getLogger(FlagService.class);
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 10000;

    private @Value("${flag.local.path:assets/flags/}") String localPath;
    private @Value("${flag.base-url:https://flagcdn.com/w160/}") String baseUrl;

    // Remembers failures too, so a batch does not retry the network for every thumbnail.
    private final Map<String, Optional<BufferedImage>> cache = new ConcurrentHashMap<>();

    /**
     * @param countryCode ISO 3166-1 alpha-2 code, any case
     * @return the flag, or null when it is not available
     */
    public BufferedImage getFlag(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) {
            return null;
        }
        var code = countryCode.trim().toLowerCase(Locale.ROOT);
        return cache.computeIfAbsent(code, c -> Optional.ofNullable(loadFlag(c))).orElse(null);
    }

    private BufferedImage loadFlag(String code) {
        var dir = new File(localPath);
        var file = new File(dir, code + ".png");
        try {
            if (file.isFile()) {
                LOGGER.info("Loading flag {} from {}.", code, file.getPath());
                return ImageIO.read(file);
            }
        } catch (IOException e) {
            LOGGER.warn("Could not read local flag {}.", file.getPath(), e);
        }

        var url = baseUrl + code + ".png";
        LOGGER.info("Flag {} not found locally. Downloading from {}.", code, url);
        try {
            var connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            try (var in = connection.getInputStream()) {
                var image = ImageIO.read(in);
                if (image == null) {
                    LOGGER.warn("Downloaded flag {} is not a valid image.", code);
                    return null;
                }
                saveLocalCopy(image, dir, file);
                return image;
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            LOGGER.warn("Flag {} could not be downloaded from {}: {}. "
                    + "Add a PNG named {}.png to {} to use it offline.",
                    code, url, e.getMessage(), code, localPath);
            return null;
        }
    }

    private void saveLocalCopy(BufferedImage image, File dir, File file) {
        try {
            if (!dir.exists() && !dir.mkdirs()) {
                LOGGER.warn("Could not create flags folder {}.", dir.getPath());
                return;
            }
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            LOGGER.warn("Could not save flag copy {}.", file.getPath(), e);
        }
    }
}
