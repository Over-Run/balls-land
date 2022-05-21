package org.overrun.ballsland;

import org.joml.Vector2f;
import org.overrun.swgl.core.util.math.Direction;

/**
 * @author squid233
 * @since 0.1.0
 */
public record Collision(boolean collision,
                        Direction dir,
                        Vector2f diffVector) {
}
