package client.features.modules;

import client.event.listeners.EventKey;
import client.features.modules.combat.*;
import client.features.modules.misc.*;
import client.features.modules.movement.Sprint;
import client.features.modules.player.NoBreakDelay;
import client.features.modules.render.*;
import client.setting.*;
import client.Client;
import client.event.Event;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleManager {
static MinecraftClient mc = MinecraftClient.getInstance();
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

	public static void registerModules(){
		modules.add(new ClickGUI());
modules.add(new Fullbright());
		modules.add(new AutoClicker());
		modules.add(new Sprint());
		modules.add(new AimAssist());
		modules.add(new BetterFightSound());
		modules.add(new HUD2());
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
modules.add(new VanillaNameTags());
modules.add(new NoHurtcam());
modules.add(new AntiVelocity());
modules.add(new LegitAura());
	}

	public static class ModuleComparator implements Comparator<Module> {
		@Override
		public int compare(Module o1, Module o2) {
			if(o1.priority > o2.priority)
				return -1;
			if(o1.priority < o2.priority)
				return 1;
			return 0;
		}
	}

	public static void onEvent(Event<?> e) {
		if(e instanceof EventKey) {
			int i = ((EventKey) e).key;
			if (i != 0) {
				ModuleManager.modules.forEach(m -> {
					if(m.getKeyCode() == i)
						m.toggle();
				});
			}
		}
		Collections.sort(ModuleManager.modules, new ModuleComparator());

		ModuleManager.modules.stream().forEach(m -> {
			if(m.isEnable()) m.onEvent(e);
		});
	}

	public static List<Module> getModulesbyCategory(Module.Category c) {
		List<Module> moduleList = new ArrayList<>();
		for(Module m : modules)
			if(m.getCategory() == c)
				moduleList.add(m);
		return moduleList;
	}

	public static Module getModulebyClass(Class<? extends Module> c) {
		return modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
	}

	public static Module getModulebyName(String str) {
		return modules.stream().filter(m -> m.getName() == str).findFirst().orElse(null);
	}
	public static Module getModulebyLowerName(String str) {
		return modules.stream().filter(m -> m.getName().equalsIgnoreCase(str)).findFirst().orElse(null);
	}

	public static void toggle(Class<? extends Module> c) {
		Module module = modules.stream().filter(m -> m.getClass() == c).findFirst().orElse(null);
		if(module != null)
			module.toggle();
	}

	public static void saveModuleSetting() {
		File directory = new File(mc.runDirectory, Client.NAME);
		File setting = new File(directory, "Settings");

		if(!directory.exists()){
			directory.mkdir();
		}
		if(!setting.exists()){
			setting.mkdir();
		}

		try{
			for (Module m : modules){
				File module = new File(setting, m.getName());
				if (!module.exists()) {
					module.createNewFile();
				}

				PrintWriter pw = new PrintWriter(module);

				final String[] str = {""};

				str[0] += m.isEnable()?"1":"0";
				str[0] += "\n";

				m.settings.forEach(s -> {
					if(s instanceof KeyBindSetting){
						str[0] += "0"+String.valueOf(((KeyBindSetting) s).getKeyCode());
					}
					if(s instanceof BooleanSetting){
						str[0] += ((BooleanSetting)s).isEnable()?"11":"10";
					}
					if(s instanceof ModeSetting){
						str[0] += "2"+ ((ModeSetting) s).index;
					}
					if(s instanceof NumberSetting){
						str[0] += "3"+ String.valueOf(((NumberSetting) s).value);
					}
					str[0] += "\n";
				});

				pw.print(str[0]);
				pw.close();
			}
		}catch (IOException e){

		}
	}
	public  CopyOnWriteArrayList<Module> getModules(){
		return modules;
	}
	public static Setting getSetting(Module module, int index){
		if(module.settings.size()>index){
			return module.settings.get(index);
		} else {
			return module.settings.getFirst();
		}
	}

	public static void loadModuleSetting() {
	/*	File directory = new File(mc.runDirectory, Client.NAME);
		File setting = new File(directory, "Settings");

		if (setting.isDirectory()){
			for (Module m : modules) {
				File SettingFile = new File(setting, m.getName());
				try {
					FileReader filereader = new FileReader(SettingFile);
					int ch;
					String str = "";
					while((ch = filereader.read()) != -1){
						str += String.valueOf((char)ch);
					}
					int i = 0;
					for (String val : Arrays.asList(str.split("\n"))) {
						if(i == 0) {
							m.setEnable(val.equals("1")?true:false);
						}else {

							String dat = val.substring(1);
							if (val.startsWith("0")) {
								//KeyBindSetting bind = (KeyBindSetting)m.settings.get(i-1);
								KeyBindSetting bind2 = (KeyBindSetting) getSetting(m,i-1);
								bind2.keyCode = Integer.parseInt(dat);
							}
							if (val.startsWith("1")) {
							//	BooleanSetting bind = (BooleanSetting)m.settings.get(i-1);
								BooleanSetting bind2 = (BooleanSetting) getSetting(m,i-1);
								if(val.equals("11"))
								bind2.setEnable(true);
							}
							if (val.startsWith("2")) {
							//	ModeSetting bind = (ModeSetting)m.settings.get(i-1);
								ModeSetting bind = (ModeSetting) getSetting(m,i-1);
								bind.index = Integer.parseInt(dat);
							}
							if (val.startsWith("3")) {

								NumberSetting bind = (NumberSetting) getSetting(m,i-1);
								bind.value = Double.parseDouble(dat);

							}
						}
						i++;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException | ClassCastException | StringIndexOutOfBoundsException e) {
					e.printStackTrace();
					SettingFile.delete();
				}
			}
	*/	}



}
