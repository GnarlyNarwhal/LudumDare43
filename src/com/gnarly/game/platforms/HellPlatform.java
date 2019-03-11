
package com.gnarly.game.platforms;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.game.Main;
import com.gnarly.game.play.Map;

public class HellPlatform extends Platform {
	
	public TexRect leftRect;
	public TexRect centerRect;
	public TexRect rightRect;
	
	private int segments;
	
	public HellPlatform(Camera camera, float x, float y, int width) {
		super(x, x + width * WIDTH, y);
		segments = width;
		if (leftRect == null) {
			leftRect   = new TexRect(camera, "HellPlatformLeft.png",   0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
			centerRect = new TexRect(camera, "HellPlatformCenter.png", 0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
			rightRect  = new TexRect(camera, "HellPlatformRight.png",  0, 0, -0.05f, WIDTH, 19 * Map.SCALE, 0, false);
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
			float amount = 0.5f - (float) (Math.sin(Math.PI * 2 * time / PULSE_SPEED / Map.timeScale) + 1) / 8;
			leftRect.setMix(0, 1, 0, 1, 0.85f);
			centerRect.setMix(0, 1, 0, 1, 0.85f);
			rightRect.setMix(0, 1, 0, 1, 0.85f);
			leftRect.setAlpha(amount);
			centerRect.setAlpha(amount);
			rightRect.setAlpha(amount);
		}
		else {
			time = 0;
			leftRect.setMix(0.5f, 1, 0.6f, 1, 0);
			centerRect.setMix(0.5f, 1, 0.6f, 1, 0);
			rightRect.setMix(0.5f, 1, 0.6f, 1, 0);
			leftRect.setAlpha(1);
			centerRect.setAlpha(1);
			rightRect.setAlpha(1);
		}
	}
	
	public void render() {
		leftRect.setPosition(left, y - 3 * Map.SCALE);
		leftRect.render();
		centerRect.setPosition(left, y - 3 * Map.SCALE);
		for (int i = 1; i < segments - 1; ++i) {
			centerRect.translate(WIDTH, 0, 0);
			centerRect.render();
		}
		rightRect.setPosition(right - WIDTH, y - 3 * Map.SCALE);
		rightRect.render();
	}
}
