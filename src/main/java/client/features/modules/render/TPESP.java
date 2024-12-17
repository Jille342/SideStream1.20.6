
package client.features.modules.render;

import client.event.listeners.EventRender3D;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.NumberSetting;
import client.utils.RenderingUtils;
import client.utils.TimeHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TPESP extends Module {

    public static NumberSetting range;
    public TPESP() {
        super("TPESP", 0,Category.RENDER);
        range = new NumberSetting("Range", 100, 1, 128, 1);
        addSetting(range);

    }

    public static ArrayList<BlockVec> blocks = new ArrayList<>();

    private final ExecutorService searchThread = Executors.newSingleThreadExecutor();
    private final TimeHelper searchTimer = new TimeHelper();
    private boolean canContinue;

    public void onEnabled() {
        super.onEnabled();
        blocks.clear();
        canContinue = true;
    }

    public void onUpdate(EventUpdate eventUpdate) {
        if (searchTimer.hasReached(1000) && canContinue) {
            searchTimer.reset();
            CompletableFuture.supplyAsync(this::scan, searchThread).thenAcceptAsync(this::sync, Util.getMainWorkerExecutor());
            canContinue = false;
        }
    }

    private ArrayList<BlockVec> scan() {
        ArrayList<BlockVec> blocks = new ArrayList<>();
        int startX = (int) Math.floor(mc.player.getX() - range.getValue());
        int endX = (int) Math.ceil(mc.player.getX() + range.getValue());
        int startY = mc.world.getBottomY() + 1;
        int endY = mc.world.getTopY();
        int startZ = (int) Math.floor(mc.player.getZ() - range.getValue());
        int endZ = (int) Math.ceil(mc.player.getZ() + range.getValue());

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState bs = mc.world.getBlockState(pos);
                    if (bs.getBlock() == Blocks.NETHER_QUARTZ_ORE) {
                        blocks.add(new BlockVec(pos.getX(), pos.getY(), pos.getZ()));
                    }
                }
            }
        }
        return blocks;
    }

    private void sync(ArrayList<BlockVec> b) {
        blocks = b;
        canContinue = true;
    }

    public void onRender3D(EventRender3D eventRender3D) {
        if (blocks.isEmpty()) return;

            for (BlockVec vec : Lists.newArrayList(blocks)) {

                if (vec.getDistance(mc.player.getPos()) > range.getValue() * range.getValue()) {
                    blocks.remove(vec);
                    continue;
                }

                Box b = new Box(vec.x, vec.y, vec.z, vec.x + 1, vec.y + 1, vec.z + 1);
                int color = new Color(239, 235, 41, 255).getRGB();

                RenderingUtils.draw3DBox2(
                        eventRender3D.getMatrix().peek().getPositionMatrix(),
                        b, color);
            //    if (fill.getValue()) Render3DEngine.FILLED_QUEUE.add(new Render3DEngine.FillAction(b, color.getValue().getColorObject()));

               // if (outline.getValue()) Render3DEngine.OUTLINE_QUEUE.add(new Render3DEngine.OutlineAction(b, color.getValue().getColorObject(), 2f));
/*
                if (tracers.getValue()) {
                    Vec3d vec2 = new Vec3d(0, 0, 75)
                            .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                            .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                            .add(mc.cameraEntity.getEyePos());

                    Render3DEngine.drawLineDebug(vec2, vec.getVector(), color.getValue().getColorObject());
                }

 */
        }
    }



    public record BlockVec(double x, double y, double z) {
        public double getDistance(@NotNull Vec3d v) {
            double dx = x - v.x;
            double dy = y - v.y;
            double dz = z - v.z;
            return dx * dx + dy * dy + dz * dz;
        }

        public Vec3d getVector() {
            return new Vec3d(x + 0.5f, y + 0.5f, z + 0.5f);
        }
    }
}
