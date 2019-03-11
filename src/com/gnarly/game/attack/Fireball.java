package com.gnarly.game.attack;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;
import com.gnarly.game.play.Map;
import com.gnarly.game.play.Player;

public class Fireball extends Attack {

	private static final float MIN_SPEED = 280 * Map.SCALE;
	private static final float MAX_SPEED = 360 * Map.SCALE;
	
	private static Texture fireball;
	
	private Camera camera;
	private Player player;
	
	private TexRect incendiary;
	
	private float speed;
	private float time;
	
	private boolean complete;
	private boolean smote;
	
	public Fireball(Camera camera, Player player) {
		this.camera = camera;
		this.player = player;
		if (fireball == null)
			fireball = new Texture("Fireball.png");
		incendiary = new TexRect(camera, fireball, (float) (Map.SCALE + Math.random() * (camera.getWidth() - 46 * Map.SCALE)), Map.MAP_HEIGHT, -0.03f, 48 * Map.SCALE, 64 * Map.SCALE, 0, false);
		speed = (float) (Map.MAP_HEIGHT / (MIN_SPEED + (MAX_SPEED - MIN_SPEED) * Math.random() * Map.timeScale));
		time = 0;
	}

	public void update() {
		time += Main.dtime;
		incendiary.setY((1 - (time / speed)) * Map.MAP_HEIGHT);
		if (incendiary.getY() < player.getY() + player.getHeight() && incendiary.getY() + incendiary.getHeight() > player.getY() && incendiary.getX() < player.getX() + player.getWidth() && incendiary.getX() + incendiary.getWidth() > player.getX())
			smote = true;
		if (incendiary.getY() <= -incendiary.getHeight())
			complete = true;
	}

	public void render() {
		incendiary.render();
	}

	public boolean struckDown() {
		return smote;
	}

	public boolean isComplete() {
		return complete;
	}
}
