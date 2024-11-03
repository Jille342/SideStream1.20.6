package client.settings;

import java.util.function.Supplier;

public class Setting<T> {

    public final Supplier<Boolean> visibility;
    public String name;
    public T value;

    public Setting(String name, Supplier<Boolean> visibility, T value) {
        this.name = name;
        this.visibility = visibility;
        this.value = value;
    }

    public String getValueString() {
        return value.toString();
    }

}
