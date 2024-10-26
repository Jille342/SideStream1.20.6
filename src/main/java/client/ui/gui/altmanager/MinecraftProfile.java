/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.ui.gui.altmanager;

import lombok.Getter;

import java.util.UUID;

@Getter
public final class MinecraftProfile
{
	private final UUID uuid;
	private final String name;
	private final String accessToken;
	
	public MinecraftProfile(UUID uuid, String name, String mcAccessToken)
	{
		this.uuid = uuid;
		this.name = name;
		this.accessToken = mcAccessToken;
	}
}
