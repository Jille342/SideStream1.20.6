package client.command;

import client.Client;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

public abstract class Command {

    @Setter
    @Getter
    public String name, description, syntax;
    @Setter
    @Getter
    public List<String> aliases = new ArrayList<String>();
    protected MinecraftClient mc = Client.mc;

    public Command(String name, String description, String syntax,
                   String... aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = Arrays.asList(aliases);
    }

    public abstract boolean onCommand(String[] args, String command);
}
