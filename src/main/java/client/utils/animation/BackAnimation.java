package client.utils.animation;

public class BackAnimation extends AnimationUtil {

    @Override
    public double calcPercent() {
        return -2.2 * tick * tick + 3.2 * tick;
    }
}