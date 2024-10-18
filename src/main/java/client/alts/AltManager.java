package client.alts;

import java.util.ArrayList;
import java.util.List;

public class AltManager {
    public List<Alt> contents = new ArrayList<>();

    private Alt lastAlt;

    public List<Alt> getAlts() {
        return this.contents;
    }

    public Alt getLastAlt() {
        return this.lastAlt;
    }

    public void setLastAlt(Alt alt) {
        this.lastAlt = alt;
    }
    public void login(Alt alt)
    {

        alt.login();

    }
    public void remove(int index)
    {
        contents.remove(index);

    }
}
