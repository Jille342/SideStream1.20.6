
package client.features.modules.combat;

import client.event.Event;
import client.event.listeners.EventInput;
import client.event.listeners.EventMotion;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.*;
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

import java.util.*;

public class LegitAura2 extends Module
{
    float[] fixed;
    float[] angles = null;
    boolean isSilent  =false;
    boolean isInstant = false;
    private double currentCPS;
    BooleanSetting targetMobs;
    BooleanSetting ignoreTeamsSetting;
   float[] serverSideAngles;
    NumberSetting rangeSetting;
    ModeSetting sortmode;
    BooleanSetting targetInvisibles;
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
    BooleanSetting legitInstant;
    BooleanSetting smartSilent;
    BooleanSetting smartLegitInstant;
    NumberSetting legitInstantAimSpeed;

    public LegitAura2()
    {
        super("LegitAura2", 0, Category.COMBAT);
    }

    @Override
    public void init()
    {
        this.rangeSetting = new NumberSetting("Range", 3.0, 0, 4.2, 0.1);
        this.targetMobs =
                new BooleanSetting("Target Mobs", true);
        swingRange = new NumberSetting("Swing Range",4.2, 3.0, 6.0, 0.1);
        this.targetInvisibles = new BooleanSetting("Target Invisibles", false);
        this.ignoreTeamsSetting = new BooleanSetting("Ignore Teams", true);
        this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
        minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);
        sortmode = new ModeSetting("SortMode", "Angle",
                new String[]{"Angle","HurtTime","Distance"});
        rotationmode = new ModeSetting("Rotation Mode", "Normal",
                new String[]{"None", "Normal", "Normal2", "Legit"});
        moveFix = new BooleanSetting("Move Fix", true);
        itemCheck = new BooleanSetting("Item Check", true);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);
        hitThroughWalls = new BooleanSetting("Hit Through Walls", false);
        clickOnly = new BooleanSetting("Click Only", true);
        testMove = new BooleanSetting("Test Move", true);
        silent = new BooleanSetting("Silent", true);
        legitAimSpeed = new NumberSetting("Legit Aim Speed", 0.1D, 0.05D,1.0, 0.01D);
        legitInstant = new BooleanSetting("Legit Instant", true);
        smartSilent = new BooleanSetting("Smart Silent",false);
        smartLegitInstant = new BooleanSetting("Smart Legit Instant", false);
        legitInstantAimSpeed = new NumberSetting("Legit Instant Aim Speed", 0.1, 0.01, 0.5, 0.01D);

        addSetting(rotationmode, maxCPS, minCPS
                , ignoreTeamsSetting, sortmode,
                targetInvisibles, fov, hitThroughWalls, rangeSetting, clickOnly, moveFix, itemCheck, testMove,silent, legitAimSpeed,swingRange,legitInstant,smartSilent,smartLegitInstant,targetMobs, legitInstantAimSpeed);
        super.init();
    }
    public static ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
    private final TimeHelper attackTimer = new TimeHelper();
   public static LivingEntity target = null;

    @Override
    public void onEvent(Event<?> e)
    {

        if(e instanceof EventUpdate)
        {
            if(clickOnly.enabled && !mc.options.attackKey.isPressed())
                return;
            target = findTarget();
            if(smartSilent.getValue()){
                if(targets.size() >= 2){
                    isSilent =true;
                } else {
                    isSilent = false;
                }
            }else {
                isSilent = silent.getValue();
            }
            if(smartLegitInstant.getValue()){
                if(targets.size() >= 2){
                    isInstant =true;
                } else {
                    isInstant = false;
                }
            }else {
                isInstant = legitInstant.getValue();
            }

            setTag(sortmode.getMode() + " " + targets.size());
            if(target != null)
            {
                if(!(Objects.requireNonNull(mc.player).isUsingItem()
                        && itemCheck.isEnabled()))
                {

                    if(e.isPre())
                    {

                        if(target != null)
                        {
                            if(currentCPS == 0)
                            {
                                currentCPS = 1;
                            }
                            if(attackTimer.hasReached(1000/currentCPS))
                            {
                                currentCPS = RandomUtils.nextDouble(minCPS.getValue(),
                                        maxCPS.getValue());
                                attack(target);
                                attackTimer.reset();
                            }

                            if(!target.isAlive() || target.age < 10)
                                targets.remove(target);
                        }
                    }

                    super.onEvent(e);
                }
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
                if(rotationmode.getMode().equalsIgnoreCase("Normal"))
                {
                    angles =
                            RotationUtils.getRotationsEntity(target);

                } else
                if(rotationmode.getMode().equalsIgnoreCase("Normal2"))
                {
                    angles = RotationUtils
                            .getRotationsRandom((LivingEntity)target);

                } else
                if(rotationmode.getMode().equalsIgnoreCase("Legit"))
                {
                    float aimSpeed = (float) legitAimSpeed.getValue();
                     aimSpeed = (float)
                            RandomUtils.nextFloat(aimSpeed - 0.02f, aimSpeed + 0.02f)*0.1f;
                       angles = rotationUtils.calcRotation(target, aimSpeed, (float) rangeSetting.getValue(), isInstant, isSilent, angles, (float) legitInstantAimSpeed.getValue());
                     //   angles = RotationUtils.getLimitedAngles(serverSideAngles,tempAngles,target);
                }
                if(angles != null){
                   // fixed = RotationUtils.fixedSensitivity(angles, 0.1F);
                    fixed = RotationUtils.applySensitivityPatch(angles, serverSideAngles);
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

    }

    private float getFoVDistance(final float yaw, final Entity e) {
        return ((Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f) : (Math.abs(RotationUtils.getRotationsEntity((LivingEntity) e)[0] - yaw) % 360.0f));
    }

    public void attack(Entity target)
    {
        if(angles != null) {
                EntityHitResult hitResult = RaytraceUtils.rayCastByRotation(angles[0], angles[1], (float) rangeSetting.getValue());
                if (hitResult != null && hitResult.getEntity() != mc.player && hitResult.getEntity() == target) {
                        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(PlayerInteractEntityC2SPacket.attack(target, Objects.requireNonNull(mc.player).isSneaking()));
            }
        }
        Objects.requireNonNull(mc.player).swingHand(Hand.MAIN_HAND);
    }

    private LivingEntity findTarget()
    {
        targets.clear();

        assert mc.world != null;
        for(Entity entity : mc.world.getEntities())
        {
            if(entity instanceof LivingEntity && entity != mc.player)
            {
                if(!entity.isAlive() || entity.age < 10)
                {
                    continue;
                }
                if(entity.isInvisible() && !targetInvisibles.enabled)
                    continue;

                if(!RotationUtils.fov(entity, fov.value))
                    continue;
                if(!mc.player.canSee(entity) && !hitThroughWalls.isEnabled())
                    continue;
                double focusRange = swingRange.getValue();
                if(distanceTo(entity) > focusRange)
                    continue;
                if(entity instanceof PlayerEntity)
                {

                    if(ignoreTeamsSetting.enabled
                            && ServerHelper.isTeammate((PlayerEntity)entity))
                    {
                        continue;
                    }
                    if(AntiBots.isBot((PlayerEntity)entity))
                        continue;

                    targets.add((LivingEntity)entity);
                }else if(entity instanceof MobEntity
                        && targetMobs.enabled)
                {
                    targets.add((LivingEntity)entity);
                }
            }
        }

        if(targets.isEmpty())
            return null;
        switch(sortmode.getMode())
        {
            case "Angle":
                targets.sort(Comparator
                        .comparingDouble(RotationUtils::calculateYawChangeToDst));
                break;
            case"HurtTime":
                targets.sort(Comparator.comparingInt(o -> o.hurtTime));
                break;
            case "Distance":
                targets.sort(Comparator.comparingDouble(
                        (entity) -> (double)mc.player.distanceTo((Entity)entity)));
                break;
        }

        return targets.getFirst();
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
       Vec3d  entityPosition  =new Vec3d(MathHelper.clamp(eye.x, bb.minX, bb.maxX),
                MathHelper.clamp(eye.y, bb.minY, bb.maxY),
                MathHelper.clamp(eye.z, bb.minZ, bb.maxZ));
       return entityPosition.distanceTo(mc.player.getEyePos());
    }
}
