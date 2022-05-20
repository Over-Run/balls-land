package org.overrun.ballsland;

import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.io.IFileProvider;

import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.opengl.GL11.*;
import static org.overrun.swgl.core.gl.GLClear.*;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class BallsLand extends GlfwApplication {
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    public static final String VERSION = PlainTextAsset.createStr("_version.txt", FILE_PROVIDER);
    private Scene scene;

    @Override
    public void prepare() {
        GlobalConfig.useLegacyGL = true;
        WindowConfig.initialTitle = "Balls Land " + VERSION;
    }

    @Override
    public void start() {
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        clearColor(0.192f, 0.2f, 0.208f, 1.0f);
        scene = new Scene();
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        scene.renderScene(window.getWidth(), window.getHeight());
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
    }
}
