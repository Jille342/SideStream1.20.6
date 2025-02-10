
package client.features.modules.combat;

import client.Client;
import client.event.Event;
import client.event.listeners.*;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.MultiBooleanSetting;
import client.settings.NumberSetting;
import client.utils.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.*;

public class LegitAura2 extends Module
{
    float[] fixed;
    float[] angles = null;
    boolean isSilent  =false;
    boolean isInstant = false;
    private double currentCPS;
   float[] serverSideAngles;
    NumberSetting rangeSetting;
    ModeSetting sortmode;;
    NumberSetting fov;
    BooleanSetting hitThroughWalls;
    BooleanSetting clickOnly;
    public static ModeSetting rotationmode;
    NumberSetting maxCPS;
    NumberSetting minCPS;
    BooleanSetting moveFix;
    BooleanSetting itemCheck;
    BooleanSetting testMove;
    BooleanSetting silent;
    NumberSetting legitAimSpeed;
    NumberSetting swingRange;
    BooleanSetting smartSilent;
    NumberSetting legitfastmultipliter;
    BooleanSetting targetESP;
    MultiBooleanSetting targeting;
    NumberSetting angleStepSetting;
    MultiBooleanSetting legitInstantSettings;
    BooleanSetting legitMoveTurnFast;




    public LegitAura2()
    {
        super("LegitAura2", 0, Category.COMBAT);
    }

    @Override
    public void init()
    {
        angleStepSetting = new NumberSetting("Angle Step", 1,1 ,180,1);
        targeting = new MultiBooleanSetting("Targeting");
        targeting.addValue("Targeting Mobs",true);
        targeting.addValue("Ignore Teams", true);
        targeting.addValue("Targeting Invisibles", false);
        rangeSetting = new NumberSetting("Range", 3.1, 3.0,4.2, 0.1);
        swingRange = new NumberSetting("Swing Range",4.2, 3.0, 6.0, 0.1);maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
        minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);
        sortmode = new ModeSetting("SortMode", "Angle", new String[]{"Angle","HurtTime","Distance", "Cycle"});
        rotationmode = new ModeSetting("Rotation Mode", "Normal",
                new String[]{ "Normal", "Normal2", "Legit"});
        moveFix = new BooleanSetting("Move Fix", true);
        itemCheck = new BooleanSetting("Item Check", true);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
        clickOnly = new BooleanSetting("Click Only", true);
        testMove = new BooleanSetting("Test Move", true);
        silent = new BooleanSetting("Silent", true);
        legitAimSpeed = new NumberSetting("Legit Aim Speed", 0.1D, 0.05D,1.0, 0.01D);
        legitInstantSettings = new MultiBooleanSetting("Legit Instant Settigs");
        legitInstantSettings.addValue("Legit Instant", false);
        legitInstantSettings.addValue("Smart Legit Instant", false);
        smartSilent = new BooleanSetting("Smart Silent",false);
        legitMoveTurnFast = new BooleanSetting("Legit Move Turn Fast", true);
        legitfastmultipliter = new NumberSetting("Legit Move Turn Fast Multipliter", 0.1, 0.1, 0.5, 0.01D);
targetESP = new BooleanSetting("Target ESP", true);
        addSetting(angleStepSetting,rotationmode, maxCPS, minCPS
                        ,targeting, sortmode,
              fov, hitThroughWalls, rangeSetting, clickOnly, moveFix, itemCheck, testMove,silent, legitAimSpeed,swingRange,smartSilent,legitMoveTurnFast,legitInstantSettings,legitfastmultipliter,targetESP);
        super.init();
    }
    public static ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
    private final TimeHelper attackTimer = new TimeHelper();
   public static LivingEntity target = null;

    @Override
    public void onEvent(Event<?> e)
    {

        if(e instanceof EventUpdate) {
            setTag(sortmode.getMode() + " " + targets.size());
            boolean legitInstant =  legitInstantSettings.getValues().get("Legit Instant");
            boolean smartLegitInstant = legitInstantSettings.getValues().get("Smart Legit Instant");
            if((Objects.requireNonNull(mc.player).isUsingItem()  && itemCheck.isEnabled()) || clickOnly.enabled && !mc.options.attackKey.isPressed()){
                targets.clear();
                target = null;
                return;
            }
            findTargets();
            sortTargets();
            if (!targets.isEmpty()) {
                target = targets.getFirst();
                if (smartSilent.getValue()) {
                    if (targets.size() >= 2) {
                        isSilent = true;
                    } else {
                        isSilent = false;
                    }
                } else {
                    isSilent = silent.getValue();
                }
                if (smartLegitInstant) {
                    if (targets.size() >= 2) {
                        isInstant = true;
                    } else {
                        isInstant = false;
                    }
                } else {
                    isInstant = legitInstant;
                }


                if (target != null) {
                            attack(target);
                    }
            } else {
                target = null;
            }
        }
        if(e instanceof EventMotion)
        {
            if(target != null)
            {
                EventMotion event = (EventMotion)e;
               serverSideAngles =   ((EventMotion) e).getServerSideAngles();
                if(fixed != null){
                    if(isSilent) {
                        event.setYaw(fixed[0]);
                        event.setPitch(fixed[1]);
                    }
                }
            }
        }
        if(e instanceof EventInput)
        {
            ((EventInput)e).moveFix = moveFix.isEnabled();
            if(testMove.isEnabled()) {
                assert mc.player != null;
                if (mc.player.age % 3 == 0) {
                    ((EventInput) e).setSlowDownFactor(0);
                }
            }
        }
        if(e instanceof EventRender2D){
            if( target != null) {
                RotationUtils rotationUtils = new RotationUtils();
                if(rotationmode.getMode().equalsIgnoreCase("Normal"))
                {
                    angles =
                            RotationUtils.getRotationsEntity(target);

                } else
                if(rotationmode.getMode().equalsIgnoreCase("Normal2"))
                {
                    angles = RotationUtils.getRotationsRandom((LivingEntity)target);

                } else
                if(rotationmode.getMode().equalsIgnoreCase("Legit"))
                {
                    boolean shouldTurnFast = legitMoveTurnFast.getValue() && PlayerHelper.isMoving();
                    float[] currentAngles;
                    if(isSilent){
                       currentAngles = angles;
                    } else {
                        currentAngles = new float[]{mc.player.getYaw(mc.getTickDelta()), mc.player.getPitch(mc.getTickDelta())};
                    }
                    float aimSpeed = (float) legitAimSpeed.getValue();
                     aimSpeed = (float)
                            RandomUtils.nextFloat(aimSpeed - 0.02f, aimSpeed + 0.02f)*0.1f;
                       angles = rotationUtils.calcRotation(legitMoveTurnFast.getValue(),target, aimSpeed, (float) rangeSetting.getValue(), isInstant,  currentAngles, (float) legitfastmultipliter.getValue());
                     //   angles = RotationUtils.getLimitedAngles(serverSideAngles,tempAngles,target);
                }
                if(angles != null){
                    float angleStep = ((float) angleStepSetting.getValue());
                   // fixed = rotationUtils.fixedSensitivity(angles, 0.1F);
                    float[] tempAngles = new float[]{angles[0], angles[1]};
                    if (tempAngles[0] > angles[0]) {
                        tempAngles[0] -= angleStep;
                    } else if (tempAngles[0] < angles[0]) {
                        tempAngles[0] += angleStep;
                    }
                    float yawDiff = tempAngles[0] - angles[0];
                    if (yawDiff < angleStep) {
                        tempAngles[0] = angles[0];
                    }
                    fixed =rotationUtils.applySensitivityPatch(tempAngles, serverSideAngles );
                }
                if (!isSilent && fixed != null) {
                    mc.player.setYaw(fixed[0]);
                    mc.player.setPitch(fixed[1]);
                }
            } else {
                serverSideAngles= new float[]{
                        mc.player.getYaw(), mc.player.getPitch()
                };
                angles = new float[]{
                        mc.player.getYaw(), mc.player.getPitch()
                };
            }
        }
        if( e instanceof EventRender3D){
            MatrixStack matrixStack = ((EventRender3D) e).getMatrix();
            float partialTicks = ((EventRender3D) e).getPartialTicks();
            if(!targets.isEmpty() && target != null && targetESP.isEnabled()){
                LivingEntity entity = target;
                int color = 0;
                color = (((LivingEntity) target).hurtTime == 0) ? new Color(0, 200, 0, 50).getRGB() : new Color(231, 0, 30, 50).getRGB();
                double interpolatedX = MathHelper.lerp(partialTicks,
                        entity.prevX, entity.getX());
                double interpolatedY = MathHelper.lerp(partialTicks,
                        entity.prevY, entity.getY());
                double interpolatedZ = MathHelper.lerp(partialTicks,
                        entity.prevZ, entity.getZ());

                Box boundingBox = entity.getBoundingBox().offset(
                        interpolatedX - entity.getX(),
                        interpolatedY - entity.getY(),
                        interpolatedZ - entity.getZ());
                RenderingUtils.draw3DBox2(
                        matrixStack.peek().getPositionMatrix(),
                        boundingBox, color);

            }
        }

    }

    private float getFoVDistance(final float yaw, final Entity e) {
        return ((Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f) : (Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f));
    }

    public void attack(Entity target)
    {
        if (currentCPS == 0) {
            currentCPS = 1;
        }
        if (attackTimer.hasReached(1000 / currentCPS)) {
            currentCPS = RandomUtils.nextDouble(minCPS.getValue(),
                    maxCPS.getValue());
            if(target != null) {
                if (fixed != null) {
                    EntityHitResult hitResult = RaytraceUtils.rayCastByRotation(fixed[0], fixed[1], (float) rangeSetting.getValue());
                    if (hitResult != null && hitResult.getEntity() != mc.player) {
                        EventAttack eventAttack = new EventAttack(target);
                        Client.onEvent(eventAttack);
                        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(PlayerInteractEntityC2SPacket.attack(target, Objects.requireNonNull(mc.player).isSneaking()));
                    }
                }
            }
            Objects.requireNonNull(mc.player).swingHand(Hand.MAIN_HAND);
            attackTimer.reset();
        }

    }

    private void findTargets()
    {
        targets.clear();
        for(Entity entity : Objects.requireNonNull(mc.world).getEntities())
        {
            if(isValid(entity)){
                if(distanceTo(entity)<= rangeSetting.getValue()){
                    targets.add((LivingEntity) entity);
                } else if (distanceTo(entity) <= swingRange.getValue()){
                 attack(null);
                }

            }
        }

    }
    public boolean isValid(Entity entity){
        boolean targetInvisibles =  targeting.getValues().get("Targeting Invisibles");
       boolean targetMobs =  targeting.getValues().get("Targeting Mobs");
       boolean ignoreTeams =  targeting.getValues().get("Ignore Teams");
        if(entity instanceof LivingEntity && entity != mc.player)
        {
            if(!entity.isAlive() || entity.age < 10)
            {
                return false;
            }
            if(entity.isInvisible() && !targetInvisibles)
                return false;

            if(!RotationUtils.fov(entity, fov.value))
                return false;
            if(!Objects.requireNonNull(mc.player).canSee(entity) && !hitThroughWalls.isEnabled())
                return false;

            if(entity instanceof PlayerEntity)
            {

                if(ignoreTeams && ServerHelper.isTeammate((PlayerEntity)entity))
                {
                    return false;
                }
                if(AntiBots.isBot((PlayerEntity)entity))
                    return false;
            }else if(entity instanceof MobEntity && !targetMobs)
            {
                return false;
            }
            return true;
        }
        return false;
    }

    private void sortTargets() {
        float yaw = Objects.requireNonNull(mc.player).getYaw();
        String Sort = sortmode.getMode();
        switch (Sort) {
            case "Distance":
                targets.sort((o1, o2) -> {
                    double dist1 = Objects.requireNonNull(mc.player).squaredDistanceTo((Entity) o1);
                    double dist2 = Objects.requireNonNull(mc.player).squaredDistanceTo((Entity) o2);
                    return Double.compare(dist1, dist2);
                });
                break;
            case "Angle":
                targets.sort(Comparator.comparingDouble(RotationUtils::calculateYawChangeToDst));
                break;
            case "Cycle":
                targets.sort(Comparator.comparingDouble(player -> yawDistCycle(player, yaw)));
                break;
            case"HurtTime":
                targets.sort(Comparator.comparingInt(o -> o.hurtTime));
                break;
        }
    }

    private double yawDistCycle(LivingEntity e, float yaw) {
        Vec3d difference = e.getPos().add(0.0D, (e.getEyeHeight(e.getPose()) / 2.0F), 0.0D).subtract(Objects.requireNonNull(mc.player).getEyePos());
        return Math.abs(yaw - Math.atan2(difference.getZ(), difference.getX())) % 90.0D;
    }

    @Override
    public void onEnabled()
    {
        targets.clear();
        target = null;
        super.onEnabled();
    }



    @Override
    public void onDisabled()
    {
        targets.clear();
        target = null;
        super.onDisabled();
    }
    public double distanceTo( Entity entity){
        Vec3d eye = Objects.requireNonNull(mc.player).getEyePos();
        Box bb = entity.getBoundingBox();
       Vec3d  entityPosition  = new Vec3d(MathHelper.clamp(eye.x, bb.minX, bb.maxX),
                MathHelper.clamp(eye.y, bb.minY, bb.maxY),
                MathHelper.clamp(eye.z, bb.minZ, bb.maxZ));
       return entityPosition.distanceTo(eye);
    }
}
