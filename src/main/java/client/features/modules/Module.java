package client.features.modules;

import client.event.listeners.*;
import client.features.modules.render.ClickGUI;
import client.settings.KeyBindSetting;
import client.settings.Setting;
import client.event.Event;
import client.utils.ChatUtils;
import client.utils.Logger;
import client.utils.Translate;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module
{
	
	@Getter
	private final Translate translate = new Translate(0.0F, 0.0F);
	
	public static MinecraftClient mc = MinecraftClient.getInstance();
	
	@Setter
	@Getter
	public Category category;
	public KeyBindSetting keyBindSetting;
	@Setter
	@Getter
	public String name;
	@Setter
	public String displayName;
	public boolean enable;
	
	public int priority;
	
	@Getter
	public List<Setting> settings = new ArrayList<Setting>();
	
	public Module(String name, Category category)
	{
		this.name = name;
		this.category = category;
		init();
	}
	
	public Module(String name, int keyCode, Category category)
	{
		if(this instanceof ClickGUI)
		{
			this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
			setKeyCode(keyCode);
		}else
		{
			this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
		}
		this.name = name;
		this.settings.add(keyBindSetting);
		this.category = category;
		this.priority = 0;
		init();
	}
	
	public Module(String name, int keyCode, Category category, boolean enable)
	{
		this(name, keyCode, category);
		this.enable = enable;
	}
	
	public Module(String name, int keyCode, Category category, int priority)
	{
		this(name, keyCode, category);
		this.priority = priority;
	}
	
	public void addSetting(Setting... settings)
	{
		this.settings.addAll(Arrays.asList(settings));
	}
	
	public int getKeyCode()
	{
		return keyBindSetting.getKeyCode();
	}
	
	public void setKeyCode(int keyCode)
	{
		this.keyBindSetting.setKeyCode(keyCode);
	}
	
	public boolean isEnabled()
	{
		return enable;
	}
	
	public void setEnabled(boolean enable)
	{
		this.enable = enable;
	}
	
	public String getDisplayName()
	{
		return displayName == null ? name : displayName;
	}
	
	public void setTag(String string)
	{
		setDisplayName(name + " " + "\247f" + string);
	}
	
	public void toggle()
	{
		enable = !enable;
		if(enable)
		{
			onEnabled();
		}else
		{
			onDisabled();
		}
	}
	
	public void init()
	{}
	
	public void onEnabled()
	{}
	
	public void onDisabled()
	{}
	
	public void onEvent(Event<?> e)
	{
		if(e instanceof EventUpdate){
			onUpdate((EventUpdate) e);
		}
		if(e instanceof EventAttack){
			onAttack(((EventAttack) e));
		}
		if(e instanceof EventRender2D){
			onRender2D((EventRender2D) e);
		}
		if(e instanceof EventRender3D){
			onRender3D(((EventRender3D) e));
		}
		if(e instanceof EventPacket){
			onPacket(((EventPacket) e));
		}
		if(e instanceof EventMotion){
			onMotion(((EventMotion) e));
		}
		if(e instanceof EventTick){
			onTick(((EventTick) e));
		}
		if(e instanceof EventInput){
			onInput(((EventInput) e));
		}
		if(e instanceof EventNameTag){
			onNameTag(((EventNameTag) e));
		}
	}
	public void onAttack(EventAttack event){}
	public void onNameTag(EventNameTag event){}
	public void onInput(EventInput event){}
	public void onUpdate(EventUpdate event){}
	public void onRender2D(EventRender2D event){}
	public void onRender3D(EventRender3D event){}
	public void onPacket(EventPacket event){}
	public void onMotion(EventMotion event){}
	public void onTick(EventTick event){}
	public enum Category
	{
		COMBAT("Combat"),
		MOVEMENT("Movement"),
		MISC("Misc"),
		PLAYER("Player"),
		RENDER("Render");
		public final String name;
		
		Category(String name)
		{
			this.name = name;
		}
	}
	
}
