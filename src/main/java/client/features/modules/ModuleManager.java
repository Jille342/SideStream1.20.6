package client.features.modules;

import client.event.listeners.EventKey;
import client.features.modules.combat.*;
import client.features.modules.misc.*;
import client.features.modules.movement.DebugSpeed;
import client.features.modules.movement.InventoryMove;
import client.features.modules.movement.Sprint;
import client.features.modules.player.NoBreakDelay;
import client.features.modules.render.*;
import client.setting.*;
import client.event.Event;

import client.utils.MCUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager implements MCUtil
{
	public static CopyOnWriteArrayList<Module> modules =
		new CopyOnWriteArrayList<Module>();
	
	public ModuleManager()
	{
		modules.add(new LegitAura());
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
		
		modules.add(new Debug());
		modules.add(new InventoryMove());
		modules.add(new NoFov());
		modules.add(new DebugSpeed());
		modules.add(new CivBreak());
	}
	
	public static class ModuleComparator implements Comparator<Module>
	{
		@Override
		public int compare(Module o1, Module o2)
		{
			if(o1.priority > o2.priority)
				return -1;
			if(o1.priority < o2.priority)
				return 1;
			return 0;
		}
	}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventKey)
		{
			int i = ((EventKey)e).key;
			if(i != 0)
			{
				ModuleManager.modules.forEach(m -> {
					if(m.getKeyCode() == i)
						m.toggle();
				});
			}
		}
		modules.sort(new ModuleComparator());
		
		modules.forEach(m -> {
			if(m.isEnable())
				m.onEvent(e);
		});
	}
	
	public static List<Module> getModulesbyCategory(Module.Category c)
	{
		List<Module> moduleList = new ArrayList<>();
		for(Module m : modules)
			if(m.getCategory() == c)
				moduleList.add(m);
		return moduleList;
	}
	
	public static Module getModulebyClass(Class<? extends Module> c)
	{
		return modules.stream().filter(m -> m.getClass() == c).findFirst()
			.orElse(null);
	}
	
	public static Module getModulebyName(String str)
	{
		return modules.stream().filter(m -> m.getName() == str).findFirst()
			.orElse(null);
	}
	
	public static Module getModulebyLowerName(String str)
	{
		return modules.stream().filter(m -> m.getName().equalsIgnoreCase(str))
			.findFirst().orElse(null);
	}
	
	public static void toggle(Class<? extends Module> c)
	{
		Module module = modules.stream().filter(m -> m.getClass() == c)
			.findFirst().orElse(null);
		if(module != null)
			module.toggle();
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
