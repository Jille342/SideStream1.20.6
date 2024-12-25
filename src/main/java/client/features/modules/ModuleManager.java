package client.features.modules;

import client.event.listeners.EventKey;
import client.features.modules.combat.*;
import client.features.modules.misc.*;
import client.features.modules.movement.DebugSpeed;
import client.features.modules.movement.Flight;
import client.features.modules.movement.InventoryMove;
import client.features.modules.movement.Sprint;
import client.features.modules.player.*;
import client.features.modules.render.*;
import client.settings.*;
import client.event.Event;
import client.utils.MCUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleManager implements MCUtil
{
	public static CopyOnWriteArrayList<Module> modules =
		new CopyOnWriteArrayList<Module>();
	
	public ModuleManager()
	{
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
		modules.add(new AntiVoid());
		modules.add(new ToggleSneak());
		modules.add(new WTap2());
		modules.add(new Criticals());
		modules.add(new FastBreak());
		modules.add(new Blink());
		modules.add(new Fucker());
		modules.add(new TPESP());
		modules.add(new AntiDebuff());
	}
	
	public static class ModuleComparator implements Comparator<Module>
	{
		@Override
		public int compare(Module o1, Module o2)
		{
			return Integer.compare(o2.priority, o1.priority);
		}
	}
	public @Nullable Module getModuleIgnoreCase(String moduleName) {
		return (Module)modules.stream().filter((m) -> {
			return m.getName().equalsIgnoreCase(moduleName);
		}).findFirst().orElse((Module) null);
	}


	public void onEvent(Event<?> e)
	{
		if(e instanceof EventKey)
		{
			int i = ((EventKey)e).code;
			if(i != 0)
			{
				ModuleManager.modules.forEach(m -> {
					if(m.getKeyCode() == i)
						m.toggle();
				});
			}
		}
		modules.sort(new ModuleComparator());
		if(mc.player == null || mc.world == null)
			return;
		modules.forEach(m -> {
			if(m.isEnabled())
				m.onEvent(e);
		});
	}
	
	public static List<Module> getModulesbyCategory(Module.Category c)
	{
		return modules.stream().filter(m -> m.category == c).toList();
	}
	
	public static Module getModulebyClass(Class<? extends Module> c)
	{
		return modules.stream().filter(m -> m.getClass() == c).findFirst()
			.orElse(null);
	}
	
	public static Module getModulebyName(@NotNull String str)
	{
		return modules.stream().filter(m -> m.getName().equals(str)).findFirst()
			.orElse(null);
	}
	
	public static Module getModulebyLowerName(String str)
	{
		return modules.stream().filter(m -> m.getName().equalsIgnoreCase(str))
			.findFirst().orElse(null);
	}
	
	public static void toggle(Class<? extends Module> c)
	{
		modules.stream().filter(m -> m.getClass() == c).findFirst()
			.ifPresent(Module::toggle);
	}
	
	public CopyOnWriteArrayList<Module> getModules()
	{
		return modules;
	}
	
	public static Setting<?> getSetting(Module module, int index)
	{
		if(module.settings.size() > index)
		{
			return module.settings.get(index);
		}else
		{
			return module.settings.getFirst();
		}
	}
	
}
