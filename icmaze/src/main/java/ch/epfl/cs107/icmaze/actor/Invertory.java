package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.engine.actor.TextGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.TextAlign;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public class Invertory {
    public enum Item {
        KEY("icmaze/key"),
        PICKAXE("icmaze/pickaxe"),
        HEART("icmaze/heart");

        private final String spriteName;
        Item(String spriteName) { this.spriteName = spriteName; }
    }

    private static final float ICON_SIZE = 1.0f;
    private static final float PADDING = 0.35f;
    private static final float GAP = 0.55f;
    private static final float TEXT_SIZE = 0.4f;

    private static final int ICON_DEPTH = 10_000;
    private static final int TEXT_DEPTH = 10_001;

    private static final class Slot {
        int count;
        final ImageGraphics icon;
        final TextGraphics text;

        Slot(String spriteName) {
            icon = new ImageGraphics(ResourcePath.getSprite(spriteName),
                    ICON_SIZE, ICON_SIZE, null, Vector.ZERO, 1f, ICON_DEPTH);

            text = new TextGraphics("x0", TEXT_SIZE, Color.WHITE,
                    null, 0f, false, false,
                    new Vector(ICON_SIZE + 0.15f, ICON_SIZE / 2f),
                    TextAlign.Horizontal.LEFT, TextAlign.Vertical.MIDDLE,
                    1f, TEXT_DEPTH);

            count = 0;
        }
    }

    private final Map<Item, Slot> slots = new EnumMap<>(Item.class);

    public Invertory() {
        for (Item item : Item.values()) {
            slots.put(item, new Slot(item.spriteName));
        }
    }

    public void add(Item item, int delta) {
        Slot s = slots.get(item);
        if (s == null) return;
        s.count = Math.max(0, s.count + delta);
    }

    public void set(Item item, int value) {
        Slot s = slots.get(item);
        if (s == null) return;
        s.count = Math.max(0, value);
    }

    public int get(Item item) {
        Slot s = slots.get(item);
        return (s == null) ? 0 : s.count;
    }

    /** Dessine l'UI en haut-gauche de l'écran, pas affectée par la caméra. */
    public void draw(Canvas canvas) {
        Viewport vp = Viewport.from(canvas);

        Vector bottomLeft = vp.bottomLeft;
        float x = (float) (bottomLeft.x + PADDING);
        float y = (float) (bottomLeft.y + vp.height - PADDING - ICON_SIZE);

        for (Item item : Item.values()) {
            Slot s = slots.get(item);
            if (s == null || s.count <= 0) continue;

            Transform t = Transform.I.translated(new Vector(x, y));

            s.icon.setRelativeTransform(t);
            s.icon.draw(canvas);

            s.text.setText("x" + s.count);
            s.text.setRelativeTransform(t);
            s.text.draw(canvas);

            x += ICON_SIZE + GAP;
        }
    }

    /** Copié de la logique de Dialog.draw() pour obtenir la taille visible en “world units”. */
    private static final class Viewport {
        final float width;
        final float height;
        final Vector bottomLeft;

        private Viewport(float width, float height, Vector bottomLeft) {
            this.width = width;
            this.height = height;
            this.bottomLeft = bottomLeft;
        }

        static Viewport from(Canvas canvas) {
            double w = canvas.getTransform().getX().getX();
            double h = canvas.getTransform().getY().getY();

            double ratio = canvas.getWidth() / (double) canvas.getHeight();
            if (ratio > 1) h = w / ratio;
            else w = h * ratio;

            float width = (float) w;
            float height = (float) h;

            Vector bottomLeft = canvas.getPosition().sub(new Vector(width / 2f, height / 2f));
            return new Viewport(width, height, bottomLeft);
        }
    }
}
