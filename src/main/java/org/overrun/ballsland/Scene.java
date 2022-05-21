package org.overrun.ballsland;

import org.overrun.swgl.core.gl.GLStateMgr;
import org.overrun.swgl.core.phys.p2d.AABRect2f;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Scene {
    public static final int BRICK_PIXELS = 32;
    public static final int BALL_PIXELS = 32;
    public static final float BALL_DIAMETER = (float) BALL_PIXELS / BRICK_PIXELS;
    public static final float BALL_RADIUS = BALL_DIAMETER * 0.5f;
    public static final float BALL_OFFSET = 0.5f - BALL_RADIUS;
    public static final int BORDER_PIXELS = 5;
    public static final int LAND_PIXELS = 512;
    public static final int LAND_SIZE = LAND_PIXELS / BRICK_PIXELS;
    public static final int PIXELS = LAND_PIXELS + BORDER_PIXELS * 2;
    /**
     * up
     */
    private static final AABRect2f
        BORDER_U = new AABRect2f(0, PIXELS - BORDER_PIXELS, PIXELS, PIXELS);
    /**
     * down
     */
    private static final AABRect2f
        BORDER_D = new AABRect2f(0, 0, PIXELS, BORDER_PIXELS);
    /**
     * left
     */
    private static final AABRect2f
        BORDER_L = new AABRect2f(0, BORDER_PIXELS, BORDER_PIXELS, PIXELS - BORDER_PIXELS);
    /**
     * right
     */
    private static final AABRect2f
        BORDER_R = new AABRect2f(PIXELS - BORDER_PIXELS, BORDER_PIXELS, PIXELS, PIXELS - BORDER_PIXELS);
    private final ColorType[][] bricks;
    public final Map<Ball, ColorType> balls = new LinkedHashMap<>();
    private final Map<ColorType, Integer> baseCount = new LinkedHashMap<>();

    public Scene() {
        for (var ct : ColorType.values()) {
            baseCount.put(ct, 4);
        }
        bricks = new ColorType[LAND_SIZE][LAND_SIZE];
        initArea(ColorType.R, 0, LAND_SIZE / 2);
        initArea(ColorType.Y, LAND_SIZE / 2, LAND_SIZE / 2);
        initArea(ColorType.G, 0, 0);
        initArea(ColorType.B, LAND_SIZE / 2, 0);
        addBall(ColorType.R);
        addBall(ColorType.Y);
        addBall(ColorType.G);
        addBall(ColorType.B);
    }

    private void addBall(ColorType type) {
        float x, y;
        switch (type) {
            case R -> {
                x = 2.0f;
                y = LAND_SIZE - 3;
            }
            case Y -> {
                x = LAND_SIZE - 3;
                y = LAND_SIZE - 3;
            }
            case G -> {
                x = 2.0f;
                y = 2.0f;
            }
            case B -> {
                x = LAND_SIZE - 3;
                y = 2.0f;
            }
            default -> throw new IllegalArgumentException();
        }
        balls.put(new Ball(type, this, x, y), type);
    }

    private void initArea(ColorType colorType,
                          int offX, int offY) {
        for (int y = 0; y < LAND_SIZE / 2; y++) {
            for (int x = 0; x < LAND_SIZE / 2; x++) {
                setBrick(colorType, x + offX, y + offY);
            }
        }
    }

    public static boolean isBaseBrick(int x, int y) {
        return
            // R
            (x >= 0 && x < 2 && y + 2 >= LAND_SIZE && y < LAND_SIZE) ||
            // Y
            (x + 2 >= LAND_SIZE && x < LAND_SIZE && y + 2 >= LAND_SIZE && y < LAND_SIZE) ||
            // G
            (x >= 0 && x < 2 && y >= 0 && y < 2) ||
            // B
            (x + 2 >= LAND_SIZE && x < LAND_SIZE && y >= 0 && y < 2);
    }

    public void setBrick(ColorType colorType, int x, int y) {
        var oldType = bricks[y][x];
        bricks[y][x] = colorType;
        if (isBaseBrick(x, y)) {
            if (oldType != null) {
                baseCount.put(oldType, baseCount.get(oldType) - 1);
                baseCount.put(colorType, baseCount.get(colorType) + 1);
            }
        }
    }

    public ColorType getBrick(int x, int y) {
        return bricks[y][x];
    }

    public AABRect2f getBrickAAR(int x, int y) {
        return AABRect2f.ofSize(x, y, 1.0f, 1.0f);
    }

    private void renderBorder(AABRect2f border) {
        glVertex2f(border.minX(), border.maxY());
        glVertex2f(border.minX(), border.minY());
        glVertex2f(border.maxX(), border.minY());
        glVertex2f(border.maxX(), border.maxY());
    }

    private void renderBorders() {
        glColor3f(0.23f, 0.28f, 0.33f);
        glBegin(GL_QUADS);
        renderBorder(BORDER_U);
        renderBorder(BORDER_D);
        renderBorder(BORDER_L);
        renderBorder(BORDER_R);
        glEnd();
    }

    private void renderBricks() {
        Textures.getTexture("brick.png").bind();
        glBegin(GL_QUADS);
        for (int x = 0; x < LAND_SIZE; x++) {
            for (int y = 0; y < LAND_SIZE; y++) {
                Brick.render(getBrick(x, y), x, y);
            }
        }
        glEnd();
    }

    private void renderBalls() {
        Textures.getTexture("ball.png").bind();
        glBegin(GL_QUADS);
        for (var ball : balls.keySet()) {
            ball.render();
        }
        glEnd();
    }

    private void checkRemove() {
        for (var it = balls.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            var ball = e.getKey();
            if (ball.removed) {
                it.remove();
            }
        }
    }

    public void update(float delta) {
        for (var e : balls.entrySet()) {
            var ball = e.getKey();
            ball.update(delta);
            boolean noBase = baseCount.get(e.getValue()) <= 0;
            if (ball.removed || noBase) {
                ball.removed = true;
            }
        }
        checkRemove();
        Arrays.stream(ColorType.values())
            .filter(colorType -> baseCount.get(colorType) > 0 && !balls.containsValue(colorType))
            .forEachOrdered(type -> {
                var rnd = new Random();
                int c = rnd.nextInt(1, 101);
                for (int i = 0; i < c; i++) {
                    addBall(type);
                }
            });
    }

    public void tick() {
        for (var e : balls.keySet()) {
            e.tick();
        }
        checkRemove();
    }

    public void renderScene(int vw, int vh) {
        glPushMatrix();
        glTranslatef((vw - PIXELS) * 0.5f, (vh - PIXELS) * 0.5f, 0);
        renderBorders();
        glTranslatef(BORDER_PIXELS, BORDER_PIXELS, 0);
        GLStateMgr.enableTexture2D();
        glPushMatrix();
        glScalef(BRICK_PIXELS, BRICK_PIXELS, 1.0f);
        renderBricks();
        renderBalls();
        glPopMatrix();
        GLStateMgr.disableTexture2D();
        glPopMatrix();
    }
}
