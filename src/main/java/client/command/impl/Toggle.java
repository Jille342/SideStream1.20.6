package client.command.impl;


import client.command.Command;
import client.features.modules.Module;
import client.features.modules.ModuleManager;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "", "toggle <Module>", "toggle", "t");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 1) {
            for (Module m : ModuleManager.modules) {
                if (m.getName().toLowerCase().equals(args[0].toLowerCase())) {
                    m.toggle();
                    return true;
                }
            }
        }
        return false;
    }
}
