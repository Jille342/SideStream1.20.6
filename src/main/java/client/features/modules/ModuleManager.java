package client.features.modules;

import client.event.Event;
import client.event.listeners.EventKey;
import client.features.modules.combat.AimAssist;
import client.features.modules.combat.AntiBots;
import client.features.modules.combat.AntiVelocity;
import client.features.modules.combat.AutoClicker;
import client.features.modules.combat.AutoSword;
import client.features.modules.combat.BowAimbot;
import client.features.modules.combat.HitBoxes;
import client.features.modules.combat.LegitAura2;
import client.features.modules.combat.Reach;
import client.features.modules.combat.WTap;
import client.features.modules.misc.AdminChecker;
import client.features.modules.misc.AutoDrain;
import client.features.modules.misc.BetterFightSound;
import client.features.modules.misc.CivBreak;
import client.features.modules.misc.Debug;
import client.features.modules.misc.NameProtect;
import client.features.modules.misc.TPBreaker;
import client.features.modules.movement.DebugSpeed;
import client.features.modules.movement.Flight;
import client.features.modules.movement.InventoryMove;
import client.features.modules.movement.Sprint;
import client.features.modules.player.AutoTool;
import client.features.modules.player.InvManager;
import client.features.modules.player.NoBreakDelay;
import client.features.modules.render.ClickGUI;
import client.features.modules.render.EntityESP;
import client.features.modules.render.Fullbright;
import client.features.modules.render.HUD;
import client.features.modules.render.NameTags;
import client.features.modules.render.NoFov;
import client.features.modules.render.NoHurtcam;
import client.settings.Setting;
import client.utils.MCUtil;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;

public class ModuleManager implements MCUtil {
    public static CopyOnWriteArrayList<Module> modules =
        new CopyOnWriteArrayList<Module>();

    public ModuleManager() {
        modules.add(new LegitAura2());
        modules.add(new ClickGUI());
        modules.add(new Fullbright());
        modules.add(new AutoClicker());
        modules.add(new Sprint());
        modules.add(new AimAssist());
        modules.add(new BetterFightSound());
        modules.add(new HUD());
        modules.add(new BowAimbot());
        modules.add(new NameProtect());
        modules.add(new NoBreakDelay());
        modules.add(new TPBreaker());
        modules.add(new Reach());
        modules.add(new AutoDrain());
        modules.add(new NameTags());
        modules.add(new AntiBots());
        modules.add(new AdminChecker());
        modules.add(new HitBoxes());
        modules.add(new EntityESP());
        modules.add(new NoHurtcam());
        modules.add(new AntiVelocity());
        modules.add(new InvManager());
        modules.add(new Debug());
        modules.add(new InventoryMove());
        modules.add(new NoFov());
        modules.add(new DebugSpeed());
        modules.add(new CivBreak());
        modules.add(new AutoSword());
        modules.add(new AutoTool());
        modules.add(new Flight());
        modules.add(new WTap());
    }

    public static List<Module> getModulesbyCategory(Module.Category c) {
        return modules.stream().filter(m -> m.category == c).toList();
    }

    public static Module getModulebyClass(Class<? extends Module> c) {
        return modules.stream().filter(m -> m.getClass() == c).findFirst()
            .orElse(null);
    }

    public static Module getModulebyName(@NotNull String str) {
        return modules.stream().filter(m -> m.getName().equals(str)).findFirst()
            .orElse(null);
    }

    public static Module getModulebyLowerName(String str) {
        return modules.stream().filter(m -> m.getName().equalsIgnoreCase(str))
            .findFirst().orElse(null);
    }

    public static void toggle(Class<? extends Module> c) {
        modules.stream().filter(m -> m.getClass() == c).findFirst()
            .ifPresent(Module::toggle);
    }

    public static Setting<?> getSetting(Module module, int index) {
        if (module.settings.size() > index) {
            return module.settings.get(index);
        } else {
            return module.settings.getFirst();
        }
    }

    public void onEvent(Event<?> e) {
        if (e instanceof EventKey) {
            int i = ((EventKey) e).code;
            if (i != 0) {
                ModuleManager.modules.forEach(m -> {
                    if (m.getKeyCode() == i)
                        m.toggle();
                });
            }
        }
        modules.sort(new ModuleComparator());
        if (mc.player == null || mc.world == null)
            return;
        modules.forEach(m -> {
            if (m.isEnabled())
                m.onEvent(e);
        });
    }

    public CopyOnWriteArrayList<Module> getModules() {
        return modules;
    }

    public static class ModuleComparator implements Comparator<Module> {
        @Override
        public int compare(Module o1, Module o2) {
            return Integer.compare(o2.priority, o1.priority);
        }
    }

}
