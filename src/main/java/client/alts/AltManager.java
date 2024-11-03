package client.alts;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AltManager {
    public List<Alt> alts = new ArrayList<>();

    @Setter
    private Alt lastAlt;

    public void login(Alt alt) {

        alt.login();

    }

    public void remove(int index) {
        alts.remove(index);

    }
}
