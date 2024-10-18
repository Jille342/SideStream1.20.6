package client.command.impl;

import client.command.Command;
import client.Client;

public class Prefix extends Command {

    public Prefix() {
        super("Prefix", "", "prefix <String>", "prefix");
    }

    @Override
    public boolean onCommand(String[] args, String command) {
        if (args.length == 1) {
            Client.commandManager.prefix = args[0];
            return true;
        }
        return false;
    }
}