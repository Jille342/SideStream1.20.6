package client.utils;

import client.Client;
import net.minecraft.text.Text;

import java.util.Objects;

public class ChatUtils implements MCUtil
{
	public  static String name = Client.NAME;
	public final static String chatPrefix  = "["+"§b"+name.substring(0, 1).replaceAll(name.substring(0, 1),
			name.substring(0, 1))
			+ name.substring(1).replaceAll(name.substring(1),
			"\247f" + name.substring(1)) + "]";
	
	public static void printChat(String text)
	{
		
		mc.inGameHud.getChatHud().addMessage(Text.of(chatPrefix+ " " + text));
		
	}
	
	public static void printChatNoName(String text)
	{
		
		mc.inGameHud.getChatHud().addMessage(Text.of((text)));
		
	}
	
	public static void sendChat(String text)
	{
        Objects.requireNonNull(mc.player).sendMessage(Text.of(text));
	}
}
