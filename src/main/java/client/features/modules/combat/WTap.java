/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 */
package client.features.modules.combat;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import client.event.Event;
import client.event.listeners.EventRender2D;
import client.features.modules.Module;
import client.settings.NumberSetting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class WTap
        extends Module {
    public static double comboLasts;
    public static boolean comboing;
    public static boolean hitCoolDown;
    public static boolean alreadyHit;
    public static int hitTimeout;
    public static int hitsWaited;
    private final NumberSetting minActionTicks = new NumberSetting("Mmin Delay: ", 5.0, 1.0, 100.0, 1);
    private final NumberSetting maxActionTicks = new NumberSetting("Max Delay: ", 12.0, 1.0, 100.0, 1);
    private final NumberSetting minOnceEvery = new NumberSetting("Min Hits:", 1.0, 1.0, 10.0, 1);
    private final NumberSetting maxOnceEvery = new NumberSetting("Max Hits:", 1.0, 1.0, 10.0, 1);
    private final NumberSetting range = new NumberSetting("Range", 3.0, 3.0, 6.0, 1);

    public WTap() {
        super("WTap", 0, Category.COMBAT);
        addSetting(minActionTicks,maxActionTicks,minOnceEvery,maxOnceEvery,range);
    }
    
    
  public void onEvent(Event<?> event){
        if(event instanceof EventRender2D){
            if (mc.player == null) {
                return;
            }
            if (comboing) {
                if ((double)System.currentTimeMillis() >= comboLasts) {
                    comboing = false;
                    finishCombo();
                    return;
                }
                return;
            }
            if ((mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY&& mc.options.attackKey.isPressed())||(  LegitAura2.target!= null && LegitAura2.targets.size()==1)) {
                LivingEntity target = mc.crosshairTarget== null? LegitAura2.target: (LivingEntity) mc.targetedEntity;
                if (target == null) {
                    return;
                }
                if ((double)mc.player.distanceTo(target) <= this.range.getValue()) {
                    if (target.hurtTime >= 1) {
                        if ( !(target instanceof PlayerEntity)) {
                            return;
                        }
                        if (hitCoolDown && !alreadyHit) {
                            if (++hitsWaited >= hitTimeout) {
                                hitCoolDown = false;
                                hitsWaited = 0;
                            } else {
                                alreadyHit = true;
                                return;
                            }
                        }
                        if (!alreadyHit) {
                            hitTimeout = this.minOnceEvery.getValue() == this.maxOnceEvery.getValue() ? (int)this.minOnceEvery.getValue() : ThreadLocalRandom.current().nextInt((int)this.minOnceEvery.getValue(), (int)this.maxOnceEvery.getValue());
                            hitCoolDown = true;
                            hitsWaited = 0;
                            comboLasts = ThreadLocalRandom.current().nextDouble(this.minActionTicks.getValue(), this.maxActionTicks.getValue() + 0.01) + (double)System.currentTimeMillis();
                            comboing = true;
                            startCombo();
                            alreadyHit = true;
                        }
                    } else {
                        if (alreadyHit) {
                            // empty if block
                        }
                        alreadyHit = false;
                    }
                }
            }
        }
  }


    private static void finishCombo() {
        if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
          mc.options.forwardKey.setPressed(true);
        }
    }

    private static void startCombo() {
        if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            mc.options.forwardKey.setPressed(false);
            KeyBinding.updatePressedStates();
        }
    }

}