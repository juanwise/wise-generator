package thumbnailgenerator.utils.thumbnails;

/**
 * Decides where a country flag is drawn relative to a player's name.
 * The flag sits to the left of the (centred) name text.
 */
public final class FlagPlacement {

    private FlagPlacement() {
    }

    /**
     * @param slotIndex  0 for player 1 (left half), 1 for player 2 (right half)
     * @param slotWidth  width of one half of the thumbnail
     * @param textWidth  rendered width of the player's name
     * @param flagWidth  width of the scaled flag
     * @param flagHeight height of the scaled flag
     * @param textBoxY   y of the box the name text is drawn into
     * @param textBoxHeight height of that box
     * @param gap        space between flag and name
     * @param margin     minimum distance to the left edge of the half
     * @return {x, y} of the flag's top-left corner
     */
    public static int[] position(int slotIndex, int slotWidth, int textWidth,
                                 int flagWidth, int flagHeight,
                                 int textBoxY, int textBoxHeight,
                                 int gap, int margin) {
        int slotStart = slotIndex * slotWidth;
        int center = slotStart + slotWidth / 2;
        int x = center - textWidth / 2 - gap - flagWidth;
        x = Math.max(x, slotStart + margin);
        int y = textBoxY + (textBoxHeight - flagHeight) / 2;
        return new int[]{x, y};
    }
}
