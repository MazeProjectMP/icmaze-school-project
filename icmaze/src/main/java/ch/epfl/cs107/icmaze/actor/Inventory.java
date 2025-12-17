package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
/**
 * Represents the player inventory and its user interface in the ICMaze game.
 * <p>
 * The {@code Inventory} stores collected items such as pickaxe ownership, key identifiers,
 * and money. It also provides a HUD drawing component ({@link InventoryGraphic}) that renders
 * the inventory bar at the top of the screen, scaled according to the camera factor.
 * </p>
 */

public class Inventory {
    private boolean hasPickaxe;
    private final List<Integer> keyIds;
    private int money;
    private double width, height;
    // UI
    private final InventoryGraphic inventoryGraphic;

    /**
     * Creates a new empty inventory.
     * The inventory starts with no pickaxe, no keys, and zero money.
     */
    public Inventory() {
        /// compteur initial : 0
        hasPickaxe = false;
        keyIds = new ArrayList<>();
        money = 0;
        inventoryGraphic = new InventoryGraphic();
    }

    /**
     * Adds a pickaxe to the inventory.
     */
    public void addPickaxe() {
        hasPickaxe = true;
    }

    /**
     * Adds a key identifier to the inventory.
     * @param identificator (int): identifier of the key to store
     */
    public void addKey(int identificator) {
        keyIds.add(identificator);
    }
    /**
     * Adds money to the inventory.
     * Only strictly positive values are added.
     * @param value (int): amount to add
     */
    public void addValue(int value) {
        if (value > 0) money += value;
    }

    /**
     * Indicates whether the inventory contains a pickaxe.
     * @return (boolean): {@code true} if the player has a pickaxe
     */
    public boolean hasPickaxe() {
        return hasPickaxe;
    }

    /**
     * Gets the number of stored keys.
     * @return (int): number of keys currently in the inventory
     */
    public int getKeyCount() {
        return keyIds.size();
    }

    /**
     * Gets a copy of the stored key identifiers.
     * @return (ArrayList<Integer>): a copy of the key identifiers list
     */
    public ArrayList<Integer> getKeysIds() {
        return new ArrayList<>(keyIds);
    }

    /**
     * Gets the amount of money stored in the inventory.
     * @return (int): current money amount
     */
    public int getMoney() {
        return money;
    }

    /**
     * Consumes (removes) a key with the given identifier from the inventory.
     * @param identificator (int): identifier of the key to remove
     * @return (boolean): {@code true} if a matching key was found and removed
     */
    public boolean useKey(int identificator) {
        for (int i = 0; i < keyIds.size(); i++) {
            if (keyIds.get(i) == identificator) {
                keyIds.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Spends money from the inventory if possible.
     * If {@code amount <= 0}, this method succeeds immediately.
     * @param amount (int): amount of money to spend
     * @return (boolean): {@code true} if the money was spent, {@code false} otherwise
     */
    public boolean spendMoney(int amount) {
        if (amount <= 0) return true;
        if (money < amount) return false;
        money -= amount;
        return true;
    }

    /**
     * Sets the window size used for UI layout computations.
     * @param width (double): window width
     * @param height (double): window height
     */
    public void setWindowSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the current stored window width.
     * @return (double): window width
     */
    private double getWindowWidth() {
        return width;
    }

    /**
     * Gets the current stored window height.
     * @return (double): window height
     */
    private double getWindowHeight(){
        return height;
    }

    /**
     * Draws the inventory user interface on the canvas.
     * @param canvas (Canvas): the canvas to draw on
     * @param camScaleFactor (float): current camera scale factor used to scale the HUD
     */
    public void draw(Canvas canvas, float camScaleFactor) {
        inventoryGraphic.draw(canvas, camScaleFactor);
    }

    /**
     * Internal HUD component responsible for fixing the inventory bar.
     * This class draws a background bar, three slots, their icons, and numeric counters
     * (pickaxe, keys, money). Sizes and spacing are scaled using the camera scale factor.
     */
    private class InventoryGraphic {
        private static final float BASE_SLOT_SIZE = 0.55f;
        private static final float BASE_ICON_SIZE = 0.35f;

        private static final float BASE_SLOT_GAP = 0.15f;
        private static final float BASE_PAD_X = 0.20f;
        private static final float BASE_PAD_Y = 0.15f;
        private static final float BASE_TOP_MARGIN = 0.10f;

        private static final float BASE_COUNTER_FONT = 0.25f;
        private static final float BASE_COUNTER_ZONE = 0.45f;
        private static final float BASE_COUNTER_DROP = 0.05f;

        /// Depths
        private static final double BAR_DEPTH = 9998;
        private static final double SLOT_DEPTH = 9999;
        private static final double ICON_DEPTH = 10000;
        private static final float  TEXT_DEPTH = 10001f;

        private final ImageGraphics bar;
        private final List<ImageGraphics> slots;
        private final List<ImageGraphics> icons;
        private final List<TextGraphics> counters;

        /**
         * Initializes inventory HUD graphics.
         */
        public InventoryGraphic() {
            /// BAR
            bar = new ImageGraphics(ResourcePath.getSprite("icmaze/inventory_bg"), 1, 1);
            bar.setDepth(BAR_DEPTH);

            /// 3 slots
            slots = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                ImageGraphics slot = new ImageGraphics(ResourcePath.getSprite("icmaze/inventory.slot"), 1, 1);
                slot.setDepth(SLOT_DEPTH);
                slots.add(slot);
            }

            /// 3 icons
            icons = new ArrayList<>();
            ImageGraphics pickaxe = new ImageGraphics(ResourcePath.getSprite("icmaze/pickaxe"), 1, 1);
            ImageGraphics key = new ImageGraphics(ResourcePath.getSprite("icmaze/key"), 1, 1);
            ImageGraphics crystal = new ImageGraphics(ResourcePath.getSprite("icmaze/red_crystal"), 1, 1);

            pickaxe.setDepth(ICON_DEPTH);
            key.setDepth(ICON_DEPTH);
            crystal.setDepth(ICON_DEPTH);

            icons.add(pickaxe);
            icons.add(key);
            icons.add(crystal);

            /// 3 counters
            counters = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                TextGraphics t = new TextGraphics(
                        "0",
                        BASE_COUNTER_FONT,
                        Color.WHITE,
                        null,
                        0f,
                        false,
                        false,
                        Vector.ZERO,
                        TextAlign.Horizontal.CENTER,
                        TextAlign.Vertical.MIDDLE,
                        1.0f,
                        TEXT_DEPTH
                );
                counters.add(t);
            }
        }

        /**
         * Draws the inventory HUD on screen.
         * @param canvas (Canvas): drawing canvas
         * @param cameraScale (float): camera scale factor
         */
        public void draw(Canvas canvas, float cameraScale) {


            float s = cameraScale / ICMazeArea.DEFAULT_SCALE_FACTOR;

            /// Current size
            float SLOT_SIZE = BASE_SLOT_SIZE * s;
            float ICON_SIZE = BASE_ICON_SIZE * s;

            float SLOT_GAP = BASE_SLOT_GAP * s;
            float PAD_X = BASE_PAD_X * s;
            float PAD_Y = BASE_PAD_Y * s;
            float TOP_MARGIN = BASE_TOP_MARGIN * s;

            float COUNTER_FONT = BASE_COUNTER_FONT * s;
            float COUNTER_ZONE = BASE_COUNTER_ZONE * s;

            float COUNTER_DROP = BASE_COUNTER_DROP * s;

            /// Recomputed bar dimensions
            float barWidth = 2 * PAD_X + 3 * SLOT_SIZE + 2 * SLOT_GAP;
            float barHeight = 2 * PAD_Y + SLOT_SIZE + COUNTER_ZONE;

            /// Updates graphism size
            bar.setWidth(barWidth);
            bar.setHeight(barHeight);

            for (int i = 0; i < 3; i++) {
                slots.get(i).setWidth(SLOT_SIZE);
                slots.get(i).setHeight(SLOT_SIZE);

                icons.get(i).setWidth(ICON_SIZE);
                icons.get(i).setHeight(ICON_SIZE);

                counters.get(i).setFontSize(COUNTER_FONT);
            }

            Vector center = canvas.getTransform().getOrigin();
            double h = canvas.getScaledHeight();
            Vector topCenter = center.add(0, h / 2.0);

            float xBar = (float) (topCenter.x - barWidth / 2f);
            float yBar = (float) (topCenter.y - TOP_MARGIN - barHeight);

            /// Draws bar
            bar.setAnchor(new Vector(xBar, yBar));
            bar.draw(canvas);

            /// Values
            int[] values = new int[] {
                    hasPickaxe() ? 1 : 0,
                    getKeyCount(),
                    getMoney()
            };

            /// Slots (top)
            float ySlot = yBar + barHeight - PAD_Y - SLOT_SIZE;

            /// compteurs (bottom) -> centered + dropped with 0.25f
            float yTextCenter = yBar + PAD_Y + (COUNTER_ZONE * 0.5f) - COUNTER_DROP;

            for (int i = 0; i < 3; i++) {
                float xSlot = xBar + PAD_X + i * (SLOT_SIZE + SLOT_GAP);

                /// Slot
                slots.get(i).setAnchor(new Vector(xSlot, ySlot));
                slots.get(i).draw(canvas);

                /// Centered icon
                float xIcon = xSlot + (SLOT_SIZE - ICON_SIZE) / 2f;
                float yIcon = ySlot + (SLOT_SIZE - ICON_SIZE) / 2f;
                icons.get(i).setAnchor(new Vector(xIcon, yIcon));
                icons.get(i).draw(canvas);

                /// Counter centered below the slot
                float xTextCenter = xSlot + SLOT_SIZE / 2f;

                counters.get(i).setText(String.valueOf(values[i]));
                counters.get(i).setRelativeTransform(
                        Transform.I.translated(new Vector(xTextCenter, yTextCenter))
                );
                counters.get(i).draw(canvas);
            }
        }
    }
}
