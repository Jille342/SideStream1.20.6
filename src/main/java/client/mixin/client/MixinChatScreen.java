/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.mixin.client;

import client.Client;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(ChatScreen.class)
public abstract class MixinChatScreen extends Screen
{
	@Shadow
	protected TextFieldWidget chatField;
	
	private MixinChatScreen(Text title)
	{
		super(title);
	}
	
	@Inject(at = @At("HEAD"),
		method = "sendMessage(Ljava/lang/String;Z)V",
		cancellable = true)
	public void onSendMessage(String message, boolean addToHistory,
		CallbackInfo ci)
	{
		// Ignore empty messages just like vanilla
		if((message = normalize(message)).isEmpty())
			return;
		if(Client.commandManager.handleCommand(message))
		{
			ci.cancel();
		}
	}
	
	@Shadow
	public abstract String normalize(String chatText);
}
