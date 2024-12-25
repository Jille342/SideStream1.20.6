/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.mixin.client;

import java.io.File;
import java.util.UUID;

import client.Client;
import client.event.listeners.EventTick;
import client.mixin.mixininterface.IMinecraftClient;
import lombok.Setter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.client.session.Session;
import net.minecraft.util.thread.ReentrantThreadExecutor;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient
	extends ReentrantThreadExecutor<Runnable>
	implements WindowEventHandler, IMinecraftClient , MinecraftClientAccessor
{
	@Shadow
	@Final
	public File runDirectory;
	@Shadow
	public ClientPlayerInteractionManager interactionManager;
	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	@Final
	private YggdrasilAuthenticationService authenticationService;
	
	private Session wurstSession;
	private ProfileKeysImpl wurstProfileKeys;

	@Setter
    private float tickSpeedMultiplier = 1.0F; // Default tick speed multiplier
	
	private MixinMinecraftClient(Client wurst, String name)
	{
		super(name);
	}
	
	@Inject(method = {"stop"}, at = @At("HEAD"))
	public void shutdown(CallbackInfo ci)
	{
		Client.shutdown();
	}
	
	@Inject(method = "tick", at = @At("RETURN"))
	private void runTick(CallbackInfo ci)
	{
		EventTick eventTick = new EventTick();
		Client.onEvent(eventTick);
	}
	
	@Inject(method = "<init>",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
	public void init(CallbackInfo ci)
	{
		Client.init();
	}
	
	@Inject(at = @At("HEAD"),
		method = "getSession()Lnet/minecraft/client/session/Session;",
		cancellable = true)
	private void onGetSession(CallbackInfoReturnable<Session> cir)
	{
		if(wurstSession != null)
			cir.setReturnValue(wurstSession);
	}
	
	@Inject(at = @At("RETURN"),
		method = "getGameProfile()Lcom/mojang/authlib/GameProfile;",
		cancellable = true)
	public void onGetGameProfile(CallbackInfoReturnable<GameProfile> cir)
	{
		if(wurstSession == null)
			return;
		
		GameProfile oldProfile = cir.getReturnValue();
		GameProfile newProfile = new GameProfile(wurstSession.getUuidOrNull(),
			wurstSession.getUsername());
		newProfile.getProperties().putAll(oldProfile.getProperties());
		cir.setReturnValue(newProfile);
	}
	
	@Inject(at = @At("HEAD"),
		method = "getProfileKeys()Lnet/minecraft/client/session/ProfileKeys;",
		cancellable = true)
	private void onGetProfileKeys(CallbackInfoReturnable<ProfileKeys> cir)
	{
		// if(WurstClient.INSTANCE.getOtfs().noChatReportsOtf.isActive())
		// cir.setReturnValue(ProfileKeys.MISSING);
		
		if(wurstProfileKeys == null)
			return;
		
		cir.setReturnValue(wurstProfileKeys);
	}
	
	@Override
	public void setSession(Session session)
	{
		wurstSession = session;
		
		UserApiService userApiService = authenticationService
			.createUserApiService(session.getAccessToken());
		UUID uuid = wurstSession.getUuidOrNull();
		wurstProfileKeys =
			new ProfileKeysImpl(userApiService, uuid, runDirectory.toPath());
	}

}
