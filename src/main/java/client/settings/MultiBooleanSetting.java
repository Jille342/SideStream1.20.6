package client.settings;

import java.util.HashMap;
import java.util.function.Supplier;

public class MultiBooleanSetting extends Setting
{

    public HashMap<String, Boolean> values;
    public boolean expand;

    public MultiBooleanSetting(String name)
    {
        super(name, null, null);
        this.name = name;
        this.values = values;
    }
    public MultiBooleanSetting(String name, Supplier<Boolean> visibility)
    {
        super(name, visibility, null);
        this.name = name;
        this.values = values;
    }
    public void addValue(String name, boolean value) {
        if(values == null) {
            values = new HashMap<>();
        }
        this.values.put(name, value);
    }

    public HashMap<String, Boolean> getValues(){
        return values;
    }
}
