package org.overrun.ballsland;

import org.overrun.swgl.core.GlfwApplication;
import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.PlainTextAsset;
import org.overrun.swgl.core.cfg.GlobalConfig;
import org.overrun.swgl.core.cfg.WindowConfig;
import org.overrun.swgl.core.io.IFileProvider;
import org.overrun.swgl.core.io.ResManager;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.overrun.swgl.core.gl.GLBlendFunc.ONE_MINUS_SRC_ALPHA;
import static org.overrun.swgl.core.gl.GLBlendFunc.SRC_ALPHA;
import static org.overrun.swgl.core.gl.GLClear.*;
import static org.overrun.swgl.core.gl.GLStateMgr.blendFunc;
import static org.overrun.swgl.core.gl.GLStateMgr.enableBlend;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class BallsLand extends GlfwApplication {
    private static BallsLand instance;
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    public static final String VERSION = PlainTextAsset.createStr("_version.txt", FILE_PROVIDER);
    public AssetManager assetMgr;
    private Scene scene;
    private GameState gameState;

    public static BallsLand getInstance() {
        if (instance == null) {
            instance = new BallsLand();
        }
        return instance;
    }

    private void updateTitle() {
        window.setTitle(WindowConfig.initialTitle + " Game state: " + gameState);
    }

    @Override
    public void prepare() {
        GlobalConfig.useLegacyGL = true;
        WindowConfig.initialTitle = "Balls Land " + VERSION;
        WindowConfig.initialCustomIcon = window1 ->
            window1.setIcon(FILE_PROVIDER,
                "icon16.png",
                "icon32.png",
                "icon48.png");
    }

    @Override
    public void start() {
        final var vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            window.moveToCenter(vidMode.width(), vidMode.height());

        clearColor(0.192f, 0.2f, 0.208f, 1.0f);
        enableBlend();
        blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA);
        resManager = new ResManager();
        assetMgr = resManager.addResource(new AssetManager());
        Textures.setAssetMgr(assetMgr);
        Textures.addTexture("brick.png", target -> {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        });
        assetMgr.reloadAssets();
        scene = new Scene();
    }

    @Override
    public void postStart() {
        gameState = GameState.PRE_START;
        updateTitle();
    }

    @Override
    public void tick() {
        if (gameState != GameState.PLAYING)
            return;
        scene.tick();
    }

    @Override
    public void update() {
        if (gameState != GameState.PLAYING)
            return;
        scene.update((float) timer.deltaFrameTime);
    }

    @Override
    public void run() {
        clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
        scene.renderScene(window.getWidth(), window.getHeight());
    }

    @Override
    public void onKeyRelease(int key, int scancode, int mods) {
        if (key == GLFW_KEY_SPACE && gameState == GameState.PRE_START) {
            gameState = GameState.PLAYING;
            updateTitle();
        }
    }

    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, window.getWidth(), 0, window.getHeight(), -1, 1);
        glMatrixMode(GL_MODELVIEW);
    }
}
