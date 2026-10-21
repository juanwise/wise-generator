package thumbnailgenerator.dto.startgg.match;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;

@Getter
public class EntrantGG {
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("participants")
    private List<ParticipantGG> participants;

    /**
     * @return country (as written in the player's Start.gg profile) of the
     *         first participant that has one, or null
     */
    public String getCountry() {
        if (participants == null) {
            return null;
        }
        for (ParticipantGG participant : participants) {
            if (participant != null && participant.getUser() != null
                    && participant.getUser().getLocation() != null
                    && participant.getUser().getLocation().getCountry() != null) {
                return participant.getUser().getLocation().getCountry();
            }
        }
        return null;
    }
}
