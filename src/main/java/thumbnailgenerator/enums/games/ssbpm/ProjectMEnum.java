package thumbnailgenerator.enums.games.ssbpm;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.CharacterEnum;

@Getter
public enum ProjectMEnum implements CharacterEnum {

    MARIO("Mario", "mario", -1, 4),
    LUIGI("Luigi", "luigi", -1, 4),
    PEACH("Peach", "peach", -1, 4),
    WARIO("Wario", "wario", -1, 4),
    YOSHI("Yoshi", "yoshi", -1, 4),
    BOWSER("Bowser", "bowser", -1, 4),
    DONKEY_KONG("Donkey Kong", "donkey_kong", -1, 4),
    DIDDY_KONG("Diddy Kong", "diddy_kong", -1, 4),
    CAPTAIN_FALCON("Captain Falcon", "captain_falcon", -1, 4),
    FOX("Fox", "fox", -1, 4),
    FALCO("Falco", "falco", -1, 4),
    WOLF("Wolf", "wolf", -1, 4),
    LINK("Link", "link", -1, 4),
    TOON_LINK("Toon Link", "toon_link", -1, 4),
    ZELDA("Zelda", "zelda", -1, 4),
    SHEIK("Sheik", "sheik", -1, 4),
    GANONDORF("Ganondorf", "ganondorf", -1, 4),
    PIKACHU("Pikachu", "pikachu", -1, 4),
    JIGGLYPUFF("Jigglypuff", "jigglypuff", -1, 4),
    MEWTWO("Mewtwo", "mewtwo", -1, 4),
    SQUIRTLE("Squirtle", "squirtle", -1, 4),
    IVYSAUR("Ivysaur", "ivysaur", -1, 4),
    CHARIZARD("Charizard", "charizard", -1, 4),
    LUCARIO("Lucario", "lucario", -1, 4),
    SAMUS("Samus", "samus", -1, 4),
    ZERO_SUIT_SAMUS("Zero Suit Samus", "zero_suit_samus", -1, 4),
    NESS("Ness", "ness", -1, 4),
    LUCAS("Lucas", "lucas", -1, 4),
    KIRBY("Kirby", "kirby", -1, 4),
    META_KNIGHT("Meta Knight", "meta_knight", -1, 4),
    KING_DEDEDE("King Dedede", "king_dedede", -1, 4),
    MARTH("Marth", "marth", -1, 4),
    ROY("Roy", "roy", -1, 4),
    IKE("Ike", "ike", -1, 4),
    MR_GAME_AND_WATCH("Mr. Game & Watch", "mr_game_and_watch", -1, 4),
    ROB("R.O.B.", "rob", -1, 4),
    ICE_CLIMBERS("Ice Climbers", "ice_climbers", -1, 4),
    PIT("Pit", "pit", -1, 4),
    OLIMAR("Olimar", "olimar", -1, 4),
    SNAKE("Snake", "snake", -1, 4),
    SONIC("Sonic", "sonic", -1, 4),
    KNUCKLES("Knuckles", "knuckles", -1, 4),
    RANDOM("Random", "random", -1, 1);

    private final String name;
    private final String code;
    private final int startGGId;
    private final int altQuantity;

    ProjectMEnum(String name, String code, int startGGId, int altQuantity) {
        this.name = name;
        this.code = code;
        this.startGGId = startGGId;
        this.altQuantity = altQuantity;
    }
}
