package org.overrun.ballsland;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.overrun.swgl.core.phys.p2d.AABRect2f;
import org.overrun.swgl.core.util.math.Direction;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Utils {
    public static void drawCircle(float r,
                                  int slices) {
        glBegin(GL_POLYGON);
        for (int i = 0; i < slices; i++) {
            float rad = 2 * (float) Math.PI * i / slices;
            float sin = Math.sin(rad);
            float cos = Math.cosFromSin(sin, rad);
            glVertex2f(r * cos, r * sin);
        }
        glEnd();
    }

    public static Direction vectorDirection(Vector2fc target) {
        float max = 0.0f;
        var bestMatch = Direction.UP;
        for (var dir : Direction.values()) {
            if (dir.isOnAxisZ()) continue;
            float invLength = Math.invsqrt(target.x() * target.x() + target.y() * target.y());
            float dot = (target.x() * invLength) * dir.getOffsetX() + (target.y() * invLength) * dir.getOffsetY();
            if (dot > max) {
                max = dot;
                bestMatch = dir;
            }
        }
        return bestMatch;
    }

    public static Vector2f findClosestPointOnAAR(float minX, float maxX,
                                                 float minY, float maxY,
                                                 float pointX, float pointY,
                                                 Vector2f dst) {
        return dst.set(
            Math.clamp(minX, maxX, pointX),
            Math.clamp(minY, maxY, pointY)
        );
    }

    public static Vector2f findClosestPointOnAAR(AABRect2f aar,
                                                 float pointX, float pointY,
                                                 Vector2f dst) {
        return findClosestPointOnAAR(
            aar.minX(), aar.maxX(),
            aar.minY(), aar.maxY(),
            pointX, pointY,
            dst
        );
    }

    public static Vector2f findClosestPointOnAAR(AABRect2f aar,
                                                 Vector2fc point,
                                                 Vector2f dst) {
        return findClosestPointOnAAR(
            aar,
            point.x(), point.y(),
            dst
        );
    }

    public static Vector2f differenceVec(float pointX, float pointY,
                                         float centerX, float centerY,
                                         Vector2f dst) {
        return dst.set(pointX - centerX, pointY - centerY);
    }

    public static Vector2f differenceVec(Vector2fc point,
                                         float centerX, float centerY,
                                         Vector2f dst) {
        return differenceVec(point.x(), point.y(),
            centerX, centerY, dst);
    }

    public static Vector2f differenceVec(Vector2fc point,
                                         Vector2fc center,
                                         Vector2f dst) {
        return differenceVec(point,
            center.x(), center.y(),
            dst);
    }
}
