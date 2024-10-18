package client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerHelper {

    private static Map<UUID, String> nameCache = new HashMap<>();
    private String name;
    private static Map<String, UUID> uuidCache = new HashMap<>();
    private static Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();


    public static boolean isTeammate(PlayerEntity player) {
       return player.isTeammate(MinecraftClient.getInstance().player);
    }

   // public static boolean isFriend(PlayerEntity player) {
     //   return FriendRegistry.getFriends().stream().anyMatch(ign -> ign.equals(player.getName()));
    //}
}
