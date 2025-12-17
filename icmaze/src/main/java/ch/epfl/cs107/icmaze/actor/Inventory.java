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

public class Inventory {
    private boolean hasPickaxe;
    private final List<Integer> keyIds;
    private int money;
    private double width, height;
    // UI
    private final InventoryGraphic inventoryGraphic;

    public Inventory() {
        hasPickaxe = false;          // compteur initial : 0
        keyIds = new ArrayList<>();  // compteur initial : 0
        money = 0;                   // compteur initial : 0
        inventoryGraphic = new InventoryGraphic();
    }

    public void addPickaxe() {
        hasPickaxe = true;
    }

    public void addKey(int identificator) {
        keyIds.add(identificator);
    }

    public void addValue(int value) {
        if (value > 0) money += value;
    }

    public boolean hasPickaxe() {
        return hasPickaxe;
    }

    public int getKeyCount() {
        return keyIds.size();
    }

    public ArrayList<Integer> getKeysIds() {
        return new ArrayList<>(keyIds);
    }
    public int getMoney() {
        return money;
    }

    public boolean useKey(int identificator) {
        for (int i = 0; i < keyIds.size(); i++) {
            if (keyIds.get(i) == identificator) {
                keyIds.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean spendMoney(int amount) {
        if (amount <= 0) return true;
        if (money < amount) return false;
        money -= amount;
        return true;
    }

    public void setWindowSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    private double getWindowWidth() {
        return width;
    }
    private double getWindowHeight(){
        return height;
    }

    // -----------------------------
    // Draw UI
    // -----------------------------
    public void draw(Canvas canvas, float camScaleFactor) {
        inventoryGraphic.draw(canvas, camScaleFactor);
    }

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

        // Depths
        private static final double BAR_DEPTH = 9998;
        private static final double SLOT_DEPTH = 9999;
        private static final double ICON_DEPTH = 10000;
        private static final float  TEXT_DEPTH = 10001f;

        private final ImageGraphics bar;
        private final List<ImageGraphics> slots;
        private final List<ImageGraphics> icons;
        private final List<TextGraphics> counters;

        public InventoryGraphic() {
            // BAR
            bar = new ImageGraphics(ResourcePath.getSprite("icmaze/inventory_bg"), 1, 1);
            bar.setDepth(BAR_DEPTH);

            // 3 slots
            slots = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                ImageGraphics slot = new ImageGraphics(ResourcePath.getSprite("icmaze/inventory.slot"), 1, 1);
                slot.setDepth(SLOT_DEPTH);
                slots.add(slot);
            }

            // 3 icons
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

            // 3 counters
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

        public void draw(Canvas canvas, float cameraScale) {

            // Rapport en fonction du scalle factor
            float s = cameraScale / ICMazeArea.DEFAULT_SCALE_FACTOR;

            // tailles actuelles
            float SLOT_SIZE = BASE_SLOT_SIZE * s;
            float ICON_SIZE = BASE_ICON_SIZE * s;

            float SLOT_GAP = BASE_SLOT_GAP * s;
            float PAD_X = BASE_PAD_X * s;
            float PAD_Y = BASE_PAD_Y * s;
            float TOP_MARGIN = BASE_TOP_MARGIN * s;

            float COUNTER_FONT = BASE_COUNTER_FONT * s;
            float COUNTER_ZONE = BASE_COUNTER_ZONE * s;

            float COUNTER_DROP = BASE_COUNTER_DROP * s;

            // dimensions bar recalculées
            float barWidth = 2 * PAD_X + 3 * SLOT_SIZE + 2 * SLOT_GAP;
            float barHeight = 2 * PAD_Y + SLOT_SIZE + COUNTER_ZONE;

            // update tailles des graphismes
            bar.setWidth(barWidth);
            bar.setHeight(barHeight);

            for (int i = 0; i < 3; i++) {
                slots.get(i).setWidth(SLOT_SIZE);
                slots.get(i).setHeight(SLOT_SIZE);

                icons.get(i).setWidth(ICON_SIZE);
                icons.get(i).setHeight(ICON_SIZE);

                counters.get(i).setFontSize(COUNTER_FONT);
            }

            // --- placement top-center ---
            Vector center = canvas.getTransform().getOrigin();
            double h = canvas.getScaledHeight();
            Vector topCenter = center.add(0, h / 2.0);

            float xBar = (float) (topCenter.x - barWidth / 2f);
            float yBar = (float) (topCenter.y - TOP_MARGIN - barHeight);

            // draw bar
            bar.setAnchor(new Vector(xBar, yBar));
            bar.draw(canvas);

            // valeurs
            int[] values = new int[] {
                    hasPickaxe() ? 1 : 0,
                    getKeyCount(),
                    getMoney()
            };

            // slots (haut)
            float ySlot = yBar + barHeight - PAD_Y - SLOT_SIZE;

            // compteurs (bas) -> centrés + descendus de 0.25f
            float yTextCenter = yBar + PAD_Y + (COUNTER_ZONE * 0.5f) - COUNTER_DROP;

            for (int i = 0; i < 3; i++) {
                float xSlot = xBar + PAD_X + i * (SLOT_SIZE + SLOT_GAP);

                // slot
                slots.get(i).setAnchor(new Vector(xSlot, ySlot));
                slots.get(i).draw(canvas);

                // icon centrée
                float xIcon = xSlot + (SLOT_SIZE - ICON_SIZE) / 2f;
                float yIcon = ySlot + (SLOT_SIZE - ICON_SIZE) / 2f;
                icons.get(i).setAnchor(new Vector(xIcon, yIcon));
                icons.get(i).draw(canvas);

                // compteur centré sous le slot
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
