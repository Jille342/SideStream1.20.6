/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.ui.gui.altmanager.screens;

import client.ui.gui.altmanager.LoginException;
import client.ui.gui.altmanager.LoginManager;
import client.ui.gui.altmanager.MicrosoftLoginManager;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

import java.net.Proxy;

public final class DirectLoginScreen extends AltEditorScreen
{
	public DirectLoginScreen(Screen prevScreen)
	{
		super(prevScreen, Text.literal("Direct Login"));
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Login";
	}
	
	@Override
	protected void pressDoneButton()
	{
		String nameOrEmail = getNameOrEmail();
		String password = getPassword();
		
		if(password.isEmpty())
			if(nameOrEmail.contains("@alt.com")){
			//TheAlteningAuthentication.theAltening();
                try {
                    MicrosoftLoginManager.login(nameOrEmail, "password");
                } catch (LoginException e) {
                    throw new RuntimeException(e);
                }
            }else {
				LoginManager.changeCrackedName(nameOrEmail);
			}
		else
			try
			{
			//TheAlteningAuthentication.mojang();

				MicrosoftLoginManager.login(nameOrEmail, password);
				
			}catch(LoginException e)
			{
				message = "\u00a7c\u00a7lMicrosoft:\u00a7c " + e.getMessage();
				doErrorEffect();
				return;
			}
		
		message = "";
		client.setScreen(new TitleScreen());
	}
}
