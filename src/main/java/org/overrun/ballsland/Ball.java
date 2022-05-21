package org.overrun.ballsland;

import org.joml.Vector2f;
import org.overrun.swgl.core.phys.p2d.AABRect2f;
import org.overrun.swgl.core.phys.p2d.BCircle2f;
import org.overrun.swgl.core.util.math.Direction;

import java.util.Random;

import static org.lwjgl.opengl.GL11.*;
import static org.overrun.ballsland.Scene.*;
import static org.overrun.ballsland.Utils.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Ball {
    // todo: we need to implement the base build to spawn balls
    public static final int LIFETIME = 300;
    public final ColorType colorType;
    public final Scene scene;
    public final Vector2f position;
    public final Vector2f velocity;
    public final BCircle2f bCircle;
    public boolean start = true;
    public boolean removed = false;
    public int age = 0;

    public Ball(ColorType colorType,
                Scene scene,
                float x, float y) {
        this.colorType = colorType;
        this.scene = scene;
        position = new Vector2f();
        var rnd = new Random();
        velocity = new Vector2f(rnd.nextFloat(-0.8f, 0.8f), rnd.nextFloat(-0.8f, 0.8f))
            .normalize()
            .mul(5.0f);
        bCircle = new BCircle2f().r(BALL_RADIUS);
        setPos(x, y);
    }

    public void setPos(float x, float y) {
        position.set(x, y);
        bCircle.x(x + BALL_RADIUS).y(y + BALL_RADIUS);
    }

    public CollisionSrc testCollisionSrc(AABRect2f aar) {
        return testCollisionSrc(aar.minX(), aar.minY(), aar.maxX(), aar.maxY());
    }

    public CollisionSrc testCollisionSrc(float minX, float minY,
                                         float maxX, float maxY) {
        if (position.x() <= minX)
            return CollisionSrc.LEFT;
        if (position.x() + BALL_DIAMETER >= maxX)
            return CollisionSrc.RIGHT;
        if (position.y() <= minY)
            return CollisionSrc.DOWN;
        if (position.y() + BALL_DIAMETER >= maxY)
            return CollisionSrc.UP;
        return CollisionSrc.NONE;
    }

    public void rebound(AABRect2f aar) {
        rebound(aar.minX(), aar.minY(), aar.maxX(), aar.maxY());
    }

    public void rebound(float minX, float minY,
                        float maxX, float maxY) {
        switch (testCollisionSrc(minX, minY, maxX, maxY)) {
            case UP -> {
                velocity.y = -velocity.y();
                position.y = maxY - BALL_DIAMETER;
            }
            case DOWN -> {
                velocity.y = -velocity.y();
                position.y = minY;
            }
            case LEFT -> {
                velocity.x = -velocity.x();
                position.x = minX;
            }
            case RIGHT -> {
                velocity.x = -velocity.x();
                position.x = maxX - BALL_DIAMETER;
            }
        }
    }

    public CollisionSrc testBorder() {
        return testCollisionSrc(0.0f, 0.0f, LAND_SIZE, LAND_SIZE);
    }

    public Collision checkCollision(AABRect2f aar) {
        var closest = findClosestPointOnAAR(aar,
            bCircle.x(), bCircle.y(),
            new Vector2f());
        var difference = differenceVec(closest,
            bCircle.x(), bCircle.y(),
            new Vector2f());
        if (difference.length() <= BALL_RADIUS)
            return new Collision(true, vectorDirection(difference), difference);
        return new Collision(false, Direction.UP, null);
    }

    public void doCollisions() {
        for (int x = 0; x < LAND_SIZE; x++) {
            for (int y = 0; y < LAND_SIZE; y++) {
                if (scene.getBrick(x, y) == colorType) continue;

                var aar = scene.getBrickAAR(x, y);
                var collision = checkCollision(aar);
//                if (collision) {
//                    if (getBrick(x, y) == ball.colorType) continue;
//                    setBrick(ball.colorType, x, y);
//                }

                if (collision.collision()) {
                    scene.setBrick(colorType, x, y);
                    removed = true;
                    if (false) {
                        // collision resolution
                        var dir = collision.dir();
                        var diffVector = collision.diffVector();
                        if (dir.isOnAxisX()) {
                            velocity.x = -velocity.x();
                            // relocate
                            float penetration = BALL_RADIUS - Math.abs(diffVector.x());
                            if (dir == Direction.EAST)
                                position.x += penetration; // move ball to right
                            else
                                position.x -= penetration; // move ball to left
                        } else {
                            velocity.y = -velocity.y();
                            // relocate
                            float penetration = BALL_RADIUS - Math.abs(diffVector.y());
                            if (dir == Direction.UP)
                                position.y += penetration; // move ball to up
                            else
                                position.y -= penetration; // move ball to down
                        }
                    }
                }
            }
        }
    }

    public void update(float delta) {
        position.add(velocity.x() * delta, velocity.y() * delta);
        rebound(0.0f, 0.0f, LAND_SIZE, LAND_SIZE);
        if (!start) {
            doCollisions();
        }
        start = false;
        bCircle.x(position.x() + BALL_RADIUS).y(position.y() + BALL_RADIUS);
    }

    public void tick() {
        ++age;
        if (age > LIFETIME)
            removed = true;
    }

    public void render() {
        var color = colorType.color();
        glPushMatrix();
        glTranslatef(bCircle.x(), bCircle.y(), 0.0f);
        glColor3f(color.x() * 0.5f, color.y() * 0.5f, color.z() * 0.5f);
        drawCircle(BALL_RADIUS, 80);
        glColor3f(color.x(), color.y(), color.z());
        drawCircle(BALL_RADIUS - (1 / (float) BALL_PIXELS), 80);
        glPopMatrix();
    }
}
