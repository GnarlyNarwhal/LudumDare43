
package com.gnarly.game.platforms;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.game.Main;
import com.gnarly.game.play.Map;

public class HeavenPlatform extends Platform {
	
	public TexRect leftRect;
	public TexRect centerRect;
	public TexRect rightRect;
	
	private int segments;
	
	public HeavenPlatform(Camera camera, float x, float y, int width) {
		super(x, x + width * WIDTH, y);
		segments = width;
		if (leftRect == null) {
			leftRect   = new TexRect(camera, "HeavenPlatformLeft.png",   0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
			centerRect = new TexRect(camera, "HeavenPlatformCenter.png", 0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
			rightRect  = new TexRect(camera, "HeavenPlatformRight.png",  0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
		}
	}
	
	public void update() {
		if (flag == FLAG_DELETE) {
			time += Main.dtime;
			float amount = (float) (Math.sin(Math.PI * 2 * time / PULSE_SPEED / Map.timeScale) + 1) / 4 + 0.25f;
			leftRect.setMix(  0.5f, 0, 0, 1, amount);
			centerRect.setMix(0.5f, 0, 0, 1, amount);
			rightRect.setMix( 0.5f, 0, 0, 1, amount);
			leftRect.setAlpha(1);
			centerRect.setAlpha(1);
			rightRect.setAlpha(1);
		}
		else if (flag == FLAG_CREATE) {
			time += Main.dtime;
			float amount = (float) (Math.sin(Math.PI * 2 * time / PULSE_SPEED / Map.timeScale) + 1) / 16 + 0.125f;
			leftRect.setMix(0, 1, 0, 1, 0.5f);
			centerRect.setMix(0, 1, 0, 1, 0.5f);
			rightRect.setMix(0, 1, 0, 1, 0.5f);
			leftRect.setAlpha(amount);
			centerRect.setAlpha(amount);
			rightRect.setAlpha(amount);
		}
		else {
			time = 0;
			leftRect.setMix(0, 0, 0, 1, 0);
			centerRect.setMix(0, 0, 0, 1, 0);
			rightRect.setMix(0, 0, 0, 1, 0);
			leftRect.setAlpha(1);
			centerRect.setAlpha(1);
			rightRect.setAlpha(1);
		}
	}
	
	public void render() {
		leftRect.setPosition(left, y - 1 * Map.SCALE);
		leftRect.render();
		centerRect.setPosition(left, y - 1 * Map.SCALE);
		for (int i = 1; i < segments - 1; ++i) {
			centerRect.translate(WIDTH, 0, 0);
			centerRect.render();
		}
		rightRect.setPosition(right - WIDTH, y - 1 * Map.SCALE);
		rightRect.render();
	}
}
