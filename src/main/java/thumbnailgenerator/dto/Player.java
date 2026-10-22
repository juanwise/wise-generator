package thumbnailgenerator.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thumbnailgenerator.enums.interfaces.CharacterEnum;


@Getter
@Setter
@ToString
public class Player {
    private String  playerName;
    private List<Fighter> fighterList;
    /** ISO 3166-1 alpha-2 code (upper case) of the player's country; null when unknown. */
    private String countryCode;

    public Player(String playerName, List<Fighter> fighterList) {
        this(playerName, fighterList, null);
    }

    public Player(String playerName, List<Fighter> fighterList, String countryCode) {
        this.playerName = playerName;
        this.fighterList = fighterList;
        this.countryCode = countryCode;
    }

    public static List<Player> generatePreviewPlayers(CharacterEnum[] characterEnums){
        List<Player> list = new ArrayList<>();
        var listChar1 = new ArrayList<Fighter>();
        var listChar2 = new ArrayList<Fighter>();

        var random = new Random();
        var char1 = characterEnums[random.nextInt(characterEnums.length)];
        var char2 = characterEnums[random.nextInt(characterEnums.length)];

        listChar1.add(new Fighter(char1.getName(), char1.getCode(), 1, false));
        listChar2.add(new Fighter(char2.getName(), char2.getCode(), 1, false));

        list.add(new Player("Player1", listChar1));
        list.add(new Player("Player2", listChar2));
        return list;
    }

    public Fighter getFighter(int index){
        return this.fighterList.get(index);
    }

    public List<Fighter> getSecondaryFighters(){
        return this.fighterList.subList(1,this.fighterList.size());
    }

    public void setFighterFlip(int index, boolean flip){
        this.fighterList.get(index).setFlip(flip);
    }
}
