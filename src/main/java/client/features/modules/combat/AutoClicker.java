package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.TimeHelper;
import net.minecraft.block.AirBlock;
import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.RandomUtils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class AutoClicker extends Module {
//    private final BooleanSetting ignoreFriendsSetting = registerSetting(BooleanSetting.builder()
    //          .name("Ignore Friends")
    //          .value(true)
    //        .build()
    // );





    private final TimeHelper leftStopWatch = new TimeHelper();
    private final TimeHelper rightStopWatch = new TimeHelper();

    private boolean attacked;
    private boolean clicked;
    private int breakTick;

    BooleanSetting leftClickSetting;

    BooleanSetting ignoreTeamsSetting;
    NumberSetting leftCpsSetting;
    NumberSetting rightCpsSetting;
    BooleanSetting rightClickSetting;
    public AutoClicker() {
        super("Auto Clicker", 0, Category.COMBAT);



    }
    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
        this.rightCpsSetting = new NumberSetting("RightCPS", 7, 0, 20, 1f);
        this.leftCpsSetting = new NumberSetting("LeftCPS", 7, 0, 20, 1f);
        this.rightClickSetting = new BooleanSetting("RightClick", true);


        addSetting(leftClickSetting, ignoreTeamsSetting, rightClickSetting, rightCpsSetting, leftClickSetting, leftCpsSetting);
    }

    @Override
    public void onDisable() {
        attacked = false;
        clicked = false;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if (mc.options.attackKey.isPressed() && shouldClick(true)) {
                doLeftClick();
            }

            if (rightClickSetting.enable && mc.options.useKey.isPressed() && shouldClick(false)) {
                doRightClick();
            }
        }
    }


    private void doLeftClick() {
        int cps = (int) leftCpsSetting.getValue();
        if (attacked && mc.player.age % RandomUtils.nextInt(1, 3) == 0) {
            attacked = false;
            return;
        }

        if (!leftStopWatch.hasReached(calculateTime(cps))) {
            return;
        }

        PlayerHelper.legitAttack();
        attacked = true;
    }

    private void doRightClick() {
        int cps = (int) rightCpsSetting.getValue();
        if (clicked && mc.player.age % RandomUtils.nextInt(1, 3) == 0) {
            clicked = false;
            return;
        }

        if (!rightStopWatch.hasReached(calculateTime(cps))) {
            return;
        }

        clicked = true;
    }

    private long calculateTime(int cps) {
        return (long) ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public boolean shouldClick(boolean left) {
        if (!mc.isWindowFocused()) {
            return false;
        }

        if (mc.player.isUsingItem()) {
            return false;
        }

        if (mc.crosshairTarget != null && left) {
            if ( mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)mc.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();
                if (block instanceof AirBlock ) {
                    return true;
                }

                if (mc.options.attackKey.isPressed()) {
                    if (breakTick > 1) {
                        return false;
                    }
                    breakTick++;
                } else {
                    breakTick = 0;
                }
            } else {
                breakTick = 0;

            }
        }
        return true;
    }
}
