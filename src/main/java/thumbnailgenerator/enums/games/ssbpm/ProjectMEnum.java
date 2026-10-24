package thumbnailgenerator.enums.games.ssbpm;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.CharacterEnum;

@Getter
public enum ProjectMEnum implements CharacterEnum {

    MARIO("Mario", "mario", -1, 12),
    LUIGI("Luigi", "luigi", -1, 8),
    PEACH("Peach", "peach", -1, 3),
    WARIO("Wario", "wario", -1, 12),
    YOSHI("Yoshi", "yoshi", -1, 23),
    BOWSER("Bowser", "bowser", -1, 1),
    DONKEY_KONG("Donkey Kong", "donkey_kong", -1, 18),
    DIDDY_KONG("Diddy Kong", "diddy_kong", -1, 5),
    CAPTAIN_FALCON("Captain Falcon", "captain_falcon", -1, 13),
    FOX("Fox", "fox", -1, 1),
    FALCO("Falco", "falco", -1, 1),
    WOLF("Wolf", "wolf", -1, 7),
    LINK("Link", "link", -1, 9),
    TOON_LINK("Toon Link", "toon_link", -1, 12),
    ZELDA("Zelda", "zelda", -1, 7),
    SHEIK("Sheik", "sheik", -1, 15),
    GANONDORF("Ganondorf", "ganondorf", -1, 18),
    PIKACHU("Pikachu", "pikachu", -1, 20),
    JIGGLYPUFF("Jigglypuff", "jigglypuff", -1, 1),
    MEWTWO("Mewtwo", "mewtwo", -1, 1),
    SQUIRTLE("Squirtle", "squirtle", -1, 1),
    IVYSAUR("Ivysaur", "ivysaur", -1, 18),
    CHARIZARD("Charizard", "charizard", -1, 1),
    LUCARIO("Lucario", "lucario", -1, 18),
    SAMUS("Samus", "samus", -1, 19),
    ZERO_SUIT_SAMUS("Zero Suit Samus", "zero_suit_samus", -1, 19),
    NESS("Ness", "ness", -1, 1),
    LUCAS("Lucas", "lucas", -1, 13),
    KIRBY("Kirby", "kirby", -1, 18),
    META_KNIGHT("Meta Knight", "meta_knight", -1, 18),
    KING_DEDEDE("King Dedede", "king_dedede", -1, 5),
    MARTH("Marth", "marth", -1, 4),
    ROY("Roy", "roy", -1, 10),
    IKE("Ike", "ike", -1, 1),
    MR_GAME_AND_WATCH("Mr. Game & Watch", "mr_game_and_watch", -1, 4),
    ROB("R.O.B.", "rob", -1, 5),
    ICE_CLIMBERS("Ice Climbers", "ice_climbers", -1, 14),
    PIT("Pit", "pit", -1, 5),
    OLIMAR("Olimar", "olimar", -1, 15),
    SNAKE("Snake", "snake", -1, 13),
    SONIC("Sonic", "sonic", -1, 22),
    KNUCKLES("Knuckles", "knuckles", -1, 22),
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
