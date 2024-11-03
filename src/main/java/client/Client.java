package client;

import client.alts.AltManager;
import client.command.CommandManager;
import client.config.ConfigManager;
import client.event.Event;
import client.event.listeners.EventMotion;
import client.features.modules.ModuleManager;
import client.mixin.mixininterface.IMinecraftClient;
import client.ui.BackgroundManager;
import client.ui.HUD2;
import client.utils.RotationUtils;
import java.io.File;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public class Client {
    public static final String NAME = "SideStream";
    public static final String VERSION = "20241101";
    public static HUD2 hud2 = new HUD2();
    public static String bgLocation = "client/bg.png";
    public static AltManager altManager;
    @Getter
    public static ConfigManager configManager;

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static final File FOLDER = new File(mc.runDirectory, NAME);
    public static IMinecraftClient IMC = (IMinecraftClient) mc;
    @Getter
    public static CommandManager commandManager;
    @Getter
    public static ModuleManager moduleManager;

    public static void init() {
        System.out.println("Starting " + NAME + " Build " + VERSION);
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        BackgroundManager.loadBackgroundImage();
        makeClientDirectory();
        altManager = new AltManager();
        configManager = new ConfigManager();
    }

    public static void makeClientDirectory() {
        if (!FOLDER.exists()) {
            FOLDER.mkdir();
        }
    }

    public static Event<?> onEvent(Event<?> e) {
        if (e instanceof EventMotion) {
            RotationUtils.virtualYaw = ((EventMotion) e).getServerSideAngles()[0];
            RotationUtils.virtualPitch = ((EventMotion) e).getServerSideAngles()[1];
        }
        moduleManager.onEvent(e);
        return e;
    }

    public static void shutdown() {
    }
}
