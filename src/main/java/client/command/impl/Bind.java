package client.command.impl;


import client.Client;
import client.command.Command;
import client.features.modules.Module;
import client.utils.ChatUtils;
import client.utils.MCUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class Bind extends Command implements MCUtil {
	public Bind() {
		super("Bind", ".bind <module name> <key name>", "bind", "b");
	}


	@Override
	public boolean onCommand(String[] args, String command) {
		if (args.length != 2) {
			return true;
		} else {
			Module module = Client.getModuleManager().getModuleIgnoreCase(args[0]);
			if (module == null) {
			ChatUtils.printChat(String.format("Module '%S' not found", args[0]));
				return false;
			} else {
				InputUtil.Key key = InputUtil.fromKeyCode(256, 0);

				try {
					key = InputUtil.fromTranslationKey(String.format("key.keyboard.%s", args[1].toLowerCase()));
				} catch (IllegalArgumentException var5) {
				}

				module.setKeyCode(key.getCode() == 256 ? -1 : key.getCode());
				if (module.getKeyCode() == -1) {
					ChatUtils.printChat(String.format("Module %s is now unbounded", module.getName()));
				} else {
					ChatUtils.printChat(String.format("Module %s is now bound with %s", module.getName(), key.getTranslationKey()));
				}

				return false;
			}
		}
	}
}
