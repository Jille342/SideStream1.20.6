package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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

            if (mc.player.age % 10 == 0) {
                int range = (int)this.range.getValue();

                for(int x = -range; x <= range; ++x) {
                    for(int y = -range; y <= range; ++y) {
                        for(int z = -range; z <= range; ++z) {
                            Block block = mc.world.getBlockState(mc.player.getBlockPos().add(x, y, z)).getBlock();
                            if (tp.getValue() && block == Blocks.NETHER_QUARTZ_ORE) {
                                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(mc.player.getBlockPos().toCenterPos(), Direction.UP, mc.player.getBlockPos().add(x, y, z), false));

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
