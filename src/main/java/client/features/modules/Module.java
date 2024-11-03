package client.features.modules;

import client.event.Event;
import client.features.modules.render.ClickGUI;
import client.settings.KeyBindSetting;
import client.settings.Setting;
import client.utils.Translate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

public class Module {

    public static MinecraftClient mc = MinecraftClient.getInstance();
    @Getter
    private final Translate translate = new Translate(0.0F, 0.0F);
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

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        init();
    }

    public void init() {
    }

    public Module(String name, int keyCode, Category category, boolean enable) {
        this(name, keyCode, category);
        this.enable = enable;
    }

    public Module(String name, int keyCode, Category category) {
        if (this instanceof ClickGUI) {
            this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
            setKeyCode(keyCode);
        } else {
            this.keyBindSetting = new KeyBindSetting("KeyBind", keyCode);
        }
        this.name = name;
        this.settings.add(keyBindSetting);
        this.category = category;
        this.priority = 0;
        init();
    }

    public Module(String name, int keyCode, Category category, int priority) {
        this(name, keyCode, category);
        this.priority = priority;
    }

    public void addSetting(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    public int getKeyCode() {
        return keyBindSetting.getKeyCode();
    }

    public void setKeyCode(int keyCode) {
        this.keyBindSetting.setKeyCode(keyCode);
    }

    public boolean isEnabled() {
        return enable;
    }

    public void setEnabled(boolean enable) {
        this.enable = enable;
    }

    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }

    public void setTag(String string) {
        setDisplayName(name + " " + "§7" + string);
    }

    public void toggle() {
        enable = !enable;
        if (enable) {
            onEnabled();
        } else {
            onDisabled();
        }
    }

    public void onEnabled() {
    }

    public void onDisabled() {
    }

    public void onEvent(Event<?> e) {
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        MISC("Misc"),
        PLAYER("Player"),
        RENDER("Render"),
        WORLD("World");

        public final String name;

        Category(String name) {
            this.name = name;
        }
    }

}
