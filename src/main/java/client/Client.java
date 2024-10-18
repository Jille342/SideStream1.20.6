package client;

import client.alts.AltManager;
import client.command.CommandManager;
import client.config.ConfigManager;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.modules.ModuleManager;
import client.mixin.mixininterface.IMinecraftClient;
import client.ui.BackgroundManager;
import client.ui.HUD2;
import client.utils.WorldUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.io.File;

public class Client
{
    public static final String NAME = "SideStream";
    public static final String VERSION = "20241017";
	public static HUD2 hud2 = new HUD2();
	public static String bgLocation = "client/bg1.png";
	public static  AltManager altManager;
	public static ConfigManager configManager;


	public static IMinecraftClient IMC;
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final File FOLDER = new File(mc.runDirectory,NAME);
	public static CommandManager commandManager = new CommandManager();
	public static ModuleManager moduleManager;


    public static void init()
    {
		System.out.println("Starting " + NAME);
		IMC = (IMinecraftClient)mc;
		commandManager.init();
		ModuleManager.registerModules();
		ModuleManager.loadModuleSetting();
		BackgroundManager.loadBackgroundImage();
		altManager = new AltManager();
		configManager = new ConfigManager();
		moduleManager = new ModuleManager();
	}

	public static Event<?> onEvent(Event<?> e) {
		if (e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			Packet p = event.getPacket();
			if (p instanceof WorldTimeUpdateS2CPacket) {
				WorldUtils.onTime((WorldTimeUpdateS2CPacket) p);
			}
		}
    	ModuleManager.onEvent(e);
		return e;
	}
	public static void shutdown(){
		ModuleManager.saveModuleSetting();
	}

public static ModuleManager getModuleManager(){
		return moduleManager;
}
}
