package client.command.impl;

import client.Client;
import client.command.Command;
import client.utils.ChatUtils;
import java.io.File;
import net.minecraft.text.Text;

public class Config extends Command {

    public Config() {
        super("Config", "Loads your config",
            ".config delete <name>, .config load <name>, .config save <name>",
            "config", "c");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length != 2)
            return false;
        switch (args[0].toLowerCase()) {

            case "load":
                if (Client.getConfigManager().loadConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(String
                        .format("Successfully loaded config: '%s'", args[1])));
                } else {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(
                        String.format("Failed to load config: '%s'", args[1])));
                }
                break;
            case "save":
                if (Client.getConfigManager().saveConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(
                        String.format("Successfully saved config: '%s'", args[1])));
                } else {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(
                        String.format("Failed to save config: '%s'", args[1])));
                }
                break;
            case "delete":
                if (Client.getConfigManager().deleteConfig(args[1])) {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(String
                        .format("Successfully deleted config: '%s'", args[1])));
                } else {
                    mc.inGameHud.getChatHud().addMessage(Text.literal(
                        String.format("Failed to delete config: '%s'", args[1])));
                }
                break;
            case "list":
                ChatUtils.printChat("List below");
                for (File file : Client.getConfigManager().getCustomConfigs()) {
                    ChatUtils.printChat(file.getName());
                }
        }
        return true;
    }
}
