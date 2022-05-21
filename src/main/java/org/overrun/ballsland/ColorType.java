package org.overrun.ballsland;

import org.overrun.swgl.core.util.FloatTri;

/**
 * @author squid233
 * @since 0.1.0
 */
public enum ColorType {
    R(0xFF0000),
    Y(0xFFD800),
    G(0x4CFF00),
    B(0x0094FF);

    private final int rgb;
    private final FloatTri color;

    ColorType(int rgb) {
        this.rgb = rgb;
        int r = rgb << 8 >>> 24;
        int g = rgb << 16 >>> 24;
        int b = rgb << 24 >>> 24;
        final float inv = 1.0f / 255.0f;
        color = new FloatTri(r * inv, g * inv, b * inv);
    }

    public int rgb() {
        return rgb;
    }

    public FloatTri color() {
        return color;
    }
}
