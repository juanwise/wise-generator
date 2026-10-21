package thumbnailgenerator.dto.startgg.match;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class UserGG {
    @Expose
    @SerializedName("location")
    private LocationGG location;
}
