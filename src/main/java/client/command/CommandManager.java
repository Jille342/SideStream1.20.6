package client.command;

import client.command.impl.Bind;
import client.command.impl.Config;
import client.command.impl.Prefix;
import client.command.impl.Toggle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public static String prefix = ".";
    public List<Command> commands = new ArrayList<Command>();

    public CommandManager() {
        commands.add(new Toggle());
        commands.add(new Prefix());
        commands.add(new Bind());
        commands.add(new Config());
    }

    public boolean handleCommand(String str) {
        if (str.startsWith(prefix)) {
            str = str.substring(1);

            String[] args = str.split(" ");
            if (args.length > 0) {
                String commandName = args[0];
                for (Command c : commands) {
                    if (c.aliases.contains(commandName)) {
                        if (c.onCommand(Arrays.copyOfRange(args, 1, args.length),
                            str)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
