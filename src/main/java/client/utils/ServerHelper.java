package client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ServerHelper
{
	
	private static Map<UUID, String> nameCache = new HashMap<>();
	private String name;
	private static Map<String, UUID> uuidCache = new HashMap<>();
	private static Gson gson = (new GsonBuilder())
		.registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
	
	public static boolean isTeammate(PlayerEntity player)
	{
		return player.isTeammate(MinecraftClient.getInstance().player);
	}
	private Pattern COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

	public String stripMinecraftColorCodes(String input){
		return COLOR_PATTERN.matcher(input).replaceAll("");
	}
	
	// public static boolean isFriend(PlayerEntity player) {
	// return FriendRegistry.getFriends().stream().anyMatch(ign ->
	// ign.equals(player.getName()));
	// }
}
