package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.NumberSetting;
import client.utils.RaycastUtils;
import client.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Created by cool1 on 1/19/2017.
 */
public class Fucker extends Module
{

    public static BlockPos blockBreaking;
    float[] rotations = null;
    private  BooleanSetting tp;
    private  BooleanSetting suiren;
    private  NumberSetting range;



    public Fucker()
    {
        super("Fucker", 0, Category.MISC);
    }

    @Override
    public void onDisabled()
    {
        blockBreaking = null;
        super.onDisabled();
    }

    public void init()
    {
        tp = new BooleanSetting("TP ",true);
        suiren = new BooleanSetting("Suiren", false);
        range = new NumberSetting("Range ", 6.0 , 6.0, 7.0 ,0.1 );
super.init();
addSetting(tp,suiren, range);
    }

    @Override
    public void onEvent(Event<?> event)
    {
        if(event instanceof EventUpdate)
        {
            setTag(String.valueOf(range.getValue()));
            RaycastUtils raycastUtils = new RaycastUtils();
            if (mc.player.age % 10 == 0) {
                int range = (int)this.range.getValue();

                for(int x = -range; x <= range; ++x) {
                    for(int y = -range; y <= range; ++y) {
                        for(int z = -range; z <= range; ++z) {
                            BlockPos tpPos = mc.player.getBlockPos().add(x, y, z);
                            Block block = mc.world.getBlockState(tpPos).getBlock();
                            if (tp.getValue() && block == Blocks.NETHER_QUARTZ_ORE) {
                              HitResult  hitResult = raycastUtils.rayCast(RotationUtils.getAngleToBlockPos(tpPos),Math.sqrt(tpPos.getSquaredDistance(mc.player.getEyePos())), mc.getTickDelta());
                                Direction facing = ((BlockHitResult)hitResult).getSide();
                                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(mc.player.getBlockPos().toCenterPos(), facing, tpPos, false));

                            }

                            boolean shouldBreak = suiren.getValue() && block == Blocks.LILY_PAD;
                            if (shouldBreak) {
                                mc.interactionManager.updateBlockBreakingProgress(mc.player.getBlockPos().add(x, y, z), Direction.UP);
                                mc.player.swingHand(Hand.MAIN_HAND);
                            }
                        }
                    }
                }

            }
        }


    }
    }

