package org.overrun.ballsland;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Brick {
    public static void render(ColorType type,
                              int x,
                              int y) {
        var color = type.color();
        float x1 = x + 1.0f;
        float y1 = y + 1.0f;
        float r = color.x();
        float g = color.y();
        float b = color.z();
        if (Scene.isBaseBrick(x, y)) {
            r *= 0.75f;
            g *= 0.75f;
            b *= 0.75f;
        }
        glColor3f(r, g, b);
        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(x, y1);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(x, y);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(x1, y);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(x1, y1);
    }
}
