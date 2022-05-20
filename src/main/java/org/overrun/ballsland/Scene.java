package org.overrun.ballsland;

import org.overrun.swgl.core.phys.p2d.AABRect2f;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Scene {
    private static final int BORDER_SIZE = 5;
    private static final int LAND_SIZE = 512;
    private static final int SIZE = LAND_SIZE + BORDER_SIZE * 2;
    /**
     * up
     */
    private static final AABRect2f
        BORDER_U = new AABRect2f(0, SIZE - BORDER_SIZE, SIZE, SIZE);
    /**
     * down
     */
    private static final AABRect2f
        BORDER_D = new AABRect2f(0, 0, SIZE, BORDER_SIZE);
    /**
     * left
     */
    private static final AABRect2f
        BORDER_L = new AABRect2f(0, BORDER_SIZE, BORDER_SIZE, SIZE - BORDER_SIZE);
    /**
     * right
     */
    private static final AABRect2f
        BORDER_R = new AABRect2f(SIZE - BORDER_SIZE, BORDER_SIZE, SIZE, SIZE - BORDER_SIZE);

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

    public void renderScene(int vw, int vh) {
        glPushMatrix();
        glTranslatef((vw - SIZE) * 0.5f, (vh - SIZE) * 0.5f, 0);
        renderBorders();
        glPopMatrix();
    }
}
