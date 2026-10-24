package thumbnailgenerator.enums.games.ssbf2;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.CharacterEnum;

@Getter
public enum SmashFlash2Enum implements CharacterEnum {

    MARIO("Mario", "mario", -1, 4),
    LUIGI("Luigi", "luigi", -1, 4),
    PEACH("Peach", "peach", -1, 4),
    BOWSER("Bowser", "bowser", -1, 4),
    YOSHI("Yoshi", "yoshi", -1, 4),
    WARIO("Wario", "wario", -1, 4),
    WALUIGI("Waluigi", "waluigi", -1, 4),
    DONKEY_KONG("Donkey Kong", "donkey_kong", -1, 4),
    LINK("Link", "link", -1, 4),
    ZELDA("Zelda", "zelda", -1, 4),
    SHEIK("Sheik", "sheik", -1, 4),
    GANONDORF("Ganondorf", "ganondorf", -1, 4),
    PIKACHU("Pikachu", "pikachu", -1, 4),
    JIGGLYPUFF("Jigglypuff", "jigglypuff", -1, 4),
    PICHU("Pichu", "pichu", -1, 4),
    LUCARIO("Lucario", "lucario", -1, 4),
    KIRBY("Kirby", "kirby", -1, 4),
    META_KNIGHT("Meta Knight", "meta_knight", -1, 4),
    BANDANA_DEE("Bandana Dee", "bandana_dee", -1, 4),
    FOX("Fox", "fox", -1, 4),
    FALCO("Falco", "falco", -1, 4),
    KRYSTAL("Krystal", "krystal", -1, 4),
    SAMUS("Samus", "samus", -1, 4),
    ZERO_SUIT_SAMUS("Zero Suit Samus", "zero_suit_samus", -1, 4),
    CAPTAIN_FALCON("Captain Falcon", "captain_falcon", -1, 4),
    NESS("Ness", "ness", -1, 4),
    MARTH("Marth", "marth", -1, 4),
    PIT("Pit", "pit", -1, 4),
    ISAAC("Isaac", "isaac", -1, 4),
    CHIBI_ROBO("Chibi-Robo", "chibi_robo", -1, 4),
    MR_GAME_AND_WATCH("Mr. Game & Watch", "mr_game_and_watch", -1, 4),
    SONIC("Sonic", "sonic", -1, 4),
    TAILS("Tails", "tails", -1, 4),
    MEGA_MAN("Mega Man", "mega_man", -1, 4),
    RYU("Ryu", "ryu", -1, 4),
    PAC_MAN("Pac-Man", "pac_man", -1, 4),
    LLOYD("Lloyd", "lloyd", -1, 4),
    SIMON("Simon", "simon", -1, 4),
    BOMBERMAN("Bomberman", "bomberman", -1, 4),
    BLACK_MAGE("Black Mage", "black_mage", -1, 4),
    SORA("Sora", "sora", -1, 4),
    RAYMAN("Rayman", "rayman", -1, 4),
    GOKU("Goku", "goku", -1, 4),
    LUFFY("Luffy", "luffy", -1, 4),
    NARUTO("Naruto", "naruto", -1, 4),
    ICHIGO("Ichigo", "ichigo", -1, 4),
    SANDBAG("Sandbag", "sandbag", -1, 4),
    RANDOM("Random", "random", -1, 1);

    private final String name;
    private final String code;
    private final int startGGId;
    private final int altQuantity;

    SmashFlash2Enum(String name, String code, int startGGId, int altQuantity) {
        this.name = name;
        this.code = code;
        this.startGGId = startGGId;
        this.altQuantity = altQuantity;
    }
}
