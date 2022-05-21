package org.overrun.ballsland;

import org.overrun.swgl.core.asset.AssetManager;
import org.overrun.swgl.core.asset.tex.ITextureParam;
import org.overrun.swgl.core.asset.tex.Texture2D;
import org.overrun.swgl.core.io.IFileProvider;

/**
 * @author squid233
 * @since 0.1.0
 */
public final class Textures {
    private static final IFileProvider FILE_PROVIDER = IFileProvider.ofCaller();
    private static AssetManager assetMgr;

    public static void setAssetMgr(AssetManager assetMgr) {
        Textures.assetMgr = assetMgr;
    }

    public static void addTexture(String name,
                                  ITextureParam param) {
        Texture2D.createAssetParam(assetMgr, name, param, FILE_PROVIDER);
    }

    public static Texture2D getTexture(String name) {
        return Texture2D.getAsset(assetMgr, name).orElseThrow();
    }
}
