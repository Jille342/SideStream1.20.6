package client.command.impl;


import client.command.Command;
import client.features.modules.Module;
import client.features.modules.ModuleManager;
import client.utils.ChatUtils;
import org.lwjgl.glfw.GLFW;

public class Bind extends Command {

    public Bind() {
        super("Bind", "", "bind <Module> <Key>", "bind", "b");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 2) {
            for (Module m : ModuleManager.modules) {
                if (m.getName().toLowerCase().equals(args[0].toLowerCase())) {
                   int keycode =  getKeyCodeFromKey(args[1]);

                   m.setKeyCode(keycode);
                    ChatUtils.printChat("Module "+m.getName() +" bound to key:"+args[1].toUpperCase());
                    return true;
                }
            }
        }
        return false;
    }
    public int getKeyCodeFromKey(String key)
    {
        for(int i = 39; i < 97; i++)
        {
            if(key.equalsIgnoreCase(GLFW.glfwGetKeyName(GLFW.GLFW_KEY_UNKNOWN, i)))
                return i;
        }
    return 0;
    }

}
