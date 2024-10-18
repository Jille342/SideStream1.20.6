package client.mixin.client;

import client.Client;
import client.event.listeners.EventKey;
import client.ui.clicckgui2.ClickGui;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class MixinKeyboard {
    MinecraftClient mc = MinecraftClient.getInstance();
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;", shift = At.Shift.AFTER))
    private void onOnKey(long window, int key, int scancode, int i, int modifiers, CallbackInfo ci) {
        InputUtil.Key inputKey = InputUtil.fromKeyCode(key, scancode);
        if (mc.currentScreen == null && i == 1) {
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                mc.setScreen(new ClickGui());
            }
            EventKey eventKey = new EventKey(key, inputKey.getTranslationKey(), i, modifiers);
            Client.onEvent(eventKey);
        }
    }
}
