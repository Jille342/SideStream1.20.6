package client.utils;

import client.Client;
import net.minecraft.text.Text;

public class ChatUtils implements MCUtil {
    public final static String chatPrefix = "\2477[\2476Ex\2477] \2478>> \247f";
    public final static String ircchatPrefix = "\2477[\2476Ex\2479IRC\2477] \247f";

    public static void printChat(String text) {

            mc.inGameHud.getChatHud().addMessage(Text.of("["+ Client.NAME+ "] "+text));

    }


    public static void printChatNoName(String text) {

        mc.inGameHud.getChatHud().addMessage(Text.of((text)));

    }


    public static void sendChat(String text) {
        mc.player.sendMessage(Text.of(text));
    }
}