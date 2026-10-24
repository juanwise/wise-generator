package thumbnailgenerator.enums.games.ssbf2;

import lombok.Getter;
import thumbnailgenerator.enums.interfaces.CharacterEnum;

@Getter
public enum SmashFlash2Enum implements CharacterEnum {

    MARIO("Mario", "mario", -1, 12),
    LUIGI("Luigi", "luigi", -1, 12),
    PEACH("Peach", "peach", -1, 12),
    BOWSER("Bowser", "bowser", -1, 12),
    YOSHI("Yoshi", "yoshi", -1, 12),
    WARIO("Wario", "wario", -1, 12),
    WALUIGI("Waluigi", "waluigi", -1, 12),
    DONKEY_KONG("Donkey Kong", "donkey_kong", -1, 8),
    LINK("Link", "link", -1, 12),
    ZELDA("Zelda", "zelda", -1, 12),
    SHEIK("Sheik", "sheik", -1, 12),
    GANONDORF("Ganondorf", "ganondorf", -1, 12),
    PIKACHU("Pikachu", "pikachu", -1, 12),
    JIGGLYPUFF("Jigglypuff", "jigglypuff", -1, 12),
    PICHU("Pichu", "pichu", -1, 12),
    LUCARIO("Lucario", "lucario", -1, 12),
    KIRBY("Kirby", "kirby", -1, 12),
    KING_DEDEDE("King Dedede", "king_dedede", -1, 12),
    META_KNIGHT("Meta Knight", "meta_knight", -1, 12),
    BANDANA_DEE("Bandana Dee", "bandana_dee", -1, 12),
    FOX("Fox", "fox", -1, 8),
    FALCO("Falco", "falco", -1, 12),
    KRYSTAL("Krystal", "krystal", -1, 12),
    SAMUS("Samus", "samus", -1, 12),
    ZERO_SUIT_SAMUS("Zero Suit Samus", "zero_suit_samus", -1, 12),
    CAPTAIN_FALCON("Captain Falcon", "captain_falcon", -1, 12),
    NESS("Ness", "ness", -1, 12),
    MARTH("Marth", "marth", -1, 8),
    PIT("Pit", "pit", -1, 12),
    ISAAC("Isaac", "isaac", -1, 12),
    CHIBI_ROBO("Chibi-Robo", "chibi_robo", -1, 12),
    MR_GAME_AND_WATCH("Mr. Game & Watch", "mr_game_and_watch", -1, 12),
    SONIC("Sonic", "sonic", -1, 12),
    TAILS("Tails", "tails", -1, 12),
    MEGA_MAN("Mega Man", "mega_man", -1, 12),
    RYU("Ryu", "ryu", -1, 12),
    PAC_MAN("Pac-Man", "pac_man", -1, 12),
    LLOYD("Lloyd", "lloyd", -1, 12),
    SIMON("Simon", "simon", -1, 12),
    BOMBERMAN("Bomberman", "bomberman", -1, 12),
    BLACK_MAGE("Black Mage", "black_mage", -1, 12),
    SORA("Sora", "sora", -1, 12),
    RAYMAN("Rayman", "rayman", -1, 12),
    GOKU("Goku", "goku", -1, 12),
    LUFFY("Luffy", "luffy", -1, 12),
    NARUTO("Naruto", "naruto", -1, 12),
    ICHIGO("Ichigo", "ichigo", -1, 12),
    SANDBAG("Sandbag", "sandbag", -1, 12),
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
