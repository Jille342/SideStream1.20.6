package client.mixin.client;

import client.Client;
import client.features.modules.ModuleManager;
import client.features.modules.render.LowFireOverlay;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameOverlayRenderer.class)
public  class InGameOverlayRendererMixin {

    @Redirect(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private static VertexConsumer lowFireHook(VertexConsumer vertexConsumer, float red, float green, float blue, float alpha) {
        return vertexConsumer.color(red,
                green,
                blue,
                ModuleManager.getModulebyClass(LowFireOverlay.class).isEnabled()? 0.15f * alpha : alpha);
    }
}