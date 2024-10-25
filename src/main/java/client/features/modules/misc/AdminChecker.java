package client.features.modules.misc;

import java.util.*;

import client.features.modules.Module;
import client.settings.ModeSetting;
import client.settings.NumberSetting;
import client.utils.ChatUtils;
import client.utils.TimeHelper;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;

public class AdminChecker extends Module
{
	private int lastAdmins;
	
	private final ArrayList<String> admins;
	
	private final TimeHelper timer;
	NumberSetting delay;
	private final TimeHelper timer2 = new TimeHelper();
	
	Window window = mc.getWindow();
	ModeSetting checkMode;
	
	public AdminChecker()
	{
		super("AdminChecker", 0, Category.MISC);
		this.admins = new ArrayList<>();
		this.timer = new TimeHelper();
	}
	
	public void init()
	{
		this.delay = new NumberSetting("Chat Delay", 1000, 1000, 5000, 1000F);
		checkMode = new ModeSetting("Check Mode ", "Rank",
			new String[]{"Rank", "Tell"});
		addSetting(delay, checkMode);
		super.init();
		
	}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventRender2D)
		{
			if(!this.admins.isEmpty())
			{
				// font.drawStringWithShadow("" +
				// String.valueOf(this.admins.size()), ((new
				// ScaledResolution(mc)).getScaledWidth() / 2 -
				// mc.fontRenderer.getStringWidth("" +
				// String.valueOf(this.admins.size())) + 20), ((new
				// ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
			}else
			{
				// Fonts.font.drawString("Admins: " +
				// String.valueOf(this.admins.size()), ((double) (new
				// window.getScaledWidth() / 2 -
				// mc.fontRenderer.getStringWidth("Admins: " +
				// String.valueOf(this.admins.size())) + 20), ((new
				// ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
			}
		}
		if(e instanceof EventUpdate)
		{
			if(this.timer.hasReached(5000.0F))
			{
				int completionid = 0;
				this.timer.reset();
				mc.player.networkHandler.getCommandSource()
					.onCommandSuggestions(completionid + 1, null);
				if(checkMode.getMode().equals("Rank"))
					mc.player.networkHandler.sendPacket(
						new RequestCommandCompletionsC2SPacket(completionid,
							"/rank "));
				else if(checkMode.getMode().equals("Tell"))
					mc.player.networkHandler.sendPacket(
						new RequestCommandCompletionsC2SPacket(completionid,
							"/tell "));
			}
			setTag(String.valueOf(admins.size()));
			if(!this.admins.isEmpty())
				displayAdmins();
		}
		if(e instanceof EventPacket)
		{
			if(e.isIncoming())
			{
				EventPacket event = ((EventPacket)e);
				Packet<?> p = event.getPacket();
				if(p instanceof CommandSuggestionsS2CPacket packet)
				{
					this.admins.clear();
					
					for(Suggestion suggestion : packet.getSuggestions()
						.getList())
					{
						for(String staff : getAdministrators())
						{
							if(suggestion.getText().equalsIgnoreCase(staff))
							{
								this.admins.add(staff);
								displayAdmins();
							}
						}
					}
				}
				
				this.lastAdmins = this.admins.size();
			}
		}
	}
	
	public void displayAdmins()
	{
		if(timer2.hasReached(delay.value))
		{
			ChatUtils.printChat(
				String.valueOf("INC " + admins + " " + admins.size()));
			timer2.reset();
		}
	}
	
	public String[] getAdministrators()
	{
		return new String[]{"ACrispyTortilla", "ArcticStorm141", "ArsMagia",
			"Captainbenedict", "Carrots386", "DJ_Pedro", "DocCodeSharp",
			"FullAdmin", "Galap", "HighlifeTTU", "ImbC", "InstantLightning",
			"JTGangsterLP6", "Kevin_is_Panda", "Kingey", "Marine_PvP",
			"MissHilevi", "Mistri", "Mosh_Von_Void", "Navarr", "PokeTheEye",
			"Rafiki2085", "Robertthegoat", "Sevy13", "andrew323", "dLeMoNb",
			"lazertester", "noobfan", "skillerfox3", "storm345", "windex_07",
			"AlecJ", "JACOBSMILE", "Wayvernia", "gunso_", "Hughzaz",
			"Murgatron", "SaxaphoneWalrus", "_Ahri", "SakuraWolfVeghetto",
			"SnowVi1liers", "jiren74", "Dange", "Tatre", "Pichu2002",
			"LegendaryAlex", "LaukNLoad", "M4bi", "HellionX2", "Ktrompfl",
			"Bupin", "Murgatron", "Outra", "CoastinJosh", "sabau", "Axyy",
			"lPirlo", "ImAbbyy"};
	}
}
