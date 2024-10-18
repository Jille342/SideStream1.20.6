package client.mixin.client;

import client.ui.BackgroundManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RotatingCubeMapRenderer.class)
public class MixinRotatingCubeMapRenderer {
    /**
     * @author Jill
     * @reason FuckOff
     */
    @Overwrite
    public void render(DrawContext context, int width, int height, float alpha, float tickDelta) {
        BackgroundManager.drawBackGround(context,width,height);
    }
}
