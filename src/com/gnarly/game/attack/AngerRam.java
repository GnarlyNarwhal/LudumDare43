package com.gnarly.game.attack;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.ColRect;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;
import com.gnarly.game.play.Map;
import com.gnarly.game.play.Player;

public class AngerRam extends Attack {

	private static final float WIDTH  = 512;
	private static final float HEIGHT = 448;
	
	private static final float MOVE_DELAY    = 0.4f;
	private static final float BASE_DELAY    = 1.4f;
	private static final float RUN_TIME      = 1.5f;
	private static final float COMPLETE_TIME = 2.0f;
	
	private static Texture angerRam;
	
	private Camera camera;
	private Player player;
	
	private TexRect ram;
	private ColRect trail;
	
	private float time;
	
	private boolean smote;
	private boolean complete;
	
	public AngerRam(Camera camera, Player player) {
		if (angerRam == null)
			angerRam = new Texture("AngerRam.png");
		this.camera = camera;
		this.player = player;
		ram = new TexRect(camera, angerRam, -WIDTH, player.getY() + (player.getHeight() - HEIGHT) / 2, -0.03f, WIDTH, HEIGHT, 0, false);
		trail = new ColRect(camera, 0, ram.getY() + HEIGHT / 4, -0.03f, camera.getWidth(), HEIGHT / 2, 0.75f, 0, 0, 0.5f, false);
	}

	public void update() {
		time += Main.dtime;
		if (time < MOVE_DELAY / Map.timeScale)
			ram.setX(-WIDTH + WIDTH / 4 * time / MOVE_DELAY);
		else if (time > BASE_DELAY / Map.timeScale && ram.getX() < camera.getWidth()) {
			float newX = camera.getWidth() * (time - BASE_DELAY) / (RUN_TIME - BASE_DELAY);
			trail.setWidth(newX);
			if (ram.getX() + ram.getWidth() < player.getX() && newX + ram.getWidth() > player.getX() && player.getY() + player.getHeight() > ram.getY() && player.getY() < ram.getY() + ram.getHeight() * 0.8f)
				smote = true;
			ram.setX(newX);
		}
		else if (time > COMPLETE_TIME / Map.timeScale)
			complete = true;
		else if (time > RUN_TIME / Map.timeScale)
			trail.setColor(0.75f, 0, 0, 0.5f * (1 - (time - RUN_TIME) / (COMPLETE_TIME - RUN_TIME)));
	}

	public void render() {
		if (time < RUN_TIME)
			ram.render();
		if (time > BASE_DELAY)
			trail.render();
	}
	
	public boolean struckDown() {
		return smote;
	}

	public boolean isComplete() {
		return complete;
	}
}
