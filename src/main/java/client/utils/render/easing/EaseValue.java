package client.utils.render.easing;

import client.utils.render.AnimationUtil;

public abstract class EaseValue {

	public EaseValue() {
		this.timer = new Time();
	}

	public float duration;
	public AnimationUtil.Mode easeMode;
	public Time timer;

	public abstract void updateEase();

}
