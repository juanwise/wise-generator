package thumbnailgenerator.dto.startgg.match;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class LocationGG {
    @Expose
    @SerializedName("country")
    private String country;
}
