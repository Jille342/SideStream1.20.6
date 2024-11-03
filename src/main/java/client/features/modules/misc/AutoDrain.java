package client.features.modules.misc;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.ServerHelper;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Hand;

public class AutoDrain extends Module {
    public boolean did;
    NumberSetting range;
    ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
    LivingEntity target;
    int current;
    NumberSetting fov;

    public AutoDrain() {
        super("AutoDrain", 0, Category.MISC);
    }

    public void init() {
        super.init();
        range = new NumberSetting("Range", 4.0, 3, 8, 0.1);
        this.fov = new NumberSetting("FOV", 20D, 0D, 360D, 1.0D);

        addSetting(range, fov);
    }

    public void onEvent(Event<?> event) {

        if (event instanceof EventUpdate) {
            target = findTarget();
            if (target != null && target instanceof PlayerEntity) {
                Scoreboard scoreboard =
                    Objects.requireNonNull(mc.world).getScoreboard();
                ScoreboardObjective scoreboardObjective = scoreboard
                    .getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
                if (scoreboardObjective != null) {
                    GameProfile gameProfile =
                        ((PlayerEntity) target).getGameProfile();
                    ReadableScoreboardScore score = scoreboard.getScore(
                        ScoreHolder.fromProfile(gameProfile),
                        scoreboardObjective);
                    if (score != null) {
                        int targetHealth = Objects.requireNonNull(score).getScore();
                        int bestItemIndex = -1;
                        int oldSlot = -1;
                        if (targetHealth <= 5 && !did) {
                            current = Objects.requireNonNull(mc.player)
                                .getInventory().selectedSlot;
                            for (int b1 = 0; b1 < 9; b1++) {
                                ItemStack itemStack =
                                    mc.player.getInventory().getStack(b1);
                                if (itemStack == null) {
                                    continue;
                                }
                                if (itemStack.getItem() == Items.RED_DYE && itemStack
                                    .getName().toString().contains("READY")) {
                                    oldSlot = mc.player.getInventory().selectedSlot;
                                    bestItemIndex = b1;
                                }
                            }
                            if (bestItemIndex != -1) {
                                mc.player.getInventory().selectedSlot = bestItemIndex;
                                mc.interactionManager.interactEntity(mc.player, target, Hand.MAIN_HAND);
                                mc.player.getInventory().selectedSlot = oldSlot;
                            }

                        } else {
                        }
                    }
                }
            }
        }
    }

    private LivingEntity findTarget() {
        targets.clear();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player) {
                if (!entity.isAlive() || entity.age < 10) {
                    continue;
                }
                if (PlayerHelper.isInFov(entity, fov.value))
                    continue;
                double focusRange =
                    mc.player.canSee(entity) ? range.value : 3.5;
                if (mc.player.distanceTo(entity) > focusRange)
                    continue;
                if (entity instanceof PlayerEntity) {
                    if (ServerHelper.isTeammate((PlayerEntity) entity)) {
                        continue;
                    }
                    targets.add((LivingEntity) entity);
                }
            }
        }
        if (targets.isEmpty())
            return null;
        this.targets.sort(Comparator.comparingDouble(
            (entity) -> (double) mc.player.distanceTo((Entity) entity)));
        return targets.get(0);
    }
}
