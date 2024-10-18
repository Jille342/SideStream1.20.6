/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.ui.gui.altmanager.screens;

import client.Client;
import client.alts.Alt;
import client.alts.AltManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class AddAltScreen extends AltEditorScreen
{
	
	public AddAltScreen(Screen prevScreen, AltManager altManager)
	{
		super(prevScreen, Text.literal("New Alt"));
	}
	
	@Override
	protected String getDoneButtonText()
	{
		return "Add";
	}
	
	@Override
	protected void pressDoneButton()
	{
		String nameOrEmail = getNameOrEmail();
		String password = getPassword();
		
		Client.altManager.contents.add(new Alt(nameOrEmail, password));
		
		client.setScreen(prevScreen);
	}
}
