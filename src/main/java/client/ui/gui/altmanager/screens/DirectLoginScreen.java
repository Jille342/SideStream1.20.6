/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.ui.gui.altmanager.screens;

import client.Client;
import client.utils.UUIDUtils;
import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import com.thealtening.auth.service.ServiceSwitcher;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.net.Proxy;
import java.util.Optional;

public final class DirectLoginScreen extends AltEditorScreen {
	MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
	MicrosoftAuthResult result;

	public DirectLoginScreen(Screen prevScreen) {
		super(prevScreen, Text.literal("Direct Login"));
	}

	@Override
	protected String getDoneButtonText() {
		return "Login";
	}

	@Override
	protected void pressDoneButton() {
		String nameOrEmail = getNameOrEmail();
		String password = getPassword();

		if (password.isEmpty())
			if (nameOrEmail.contains("@alt.com")) {

            }
		else
			{
				Session session =
						new Session(nameOrEmail, Uuids.getOfflinePlayerUuid(nameOrEmail), "",
								Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);

				Client.IMC.setSession(session);

			}
		else
			try {
				result =authenticator.loginWithCredentials(nameOrEmail,password);
				Session session = new Session(result.getProfile().getName(), UUIDUtils.uuidFromString(result.getProfile().getId().toString()) ,
						result.getAccessToken(), Optional.empty(), Optional.empty(),
						Session.AccountType.MOJANG);
				Client.IMC.setSession(session);
			} catch (Exception e) {
                throw new RuntimeException(e);
            }

        message = "";
		client.setScreen(new TitleScreen());
	}
}
