package com.gnarly.game.play;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;

public class Ram extends TexRect implements Sacrifice {

	public static final float WIDTH = 24 * Map.SCALE;

	private static final float GRAVITY_UP   = 2400;
	private static final float GRAVITY_DOWN = 3000;
	private static final float FALL         = 3200;

	private static Texture stand;
	private static Texture dead;
	private static Texture cstand;
	private static Texture cdead;
	
	private Vector3f oldPosition;
	private Vector3f velocity;

	private boolean didHitPlatform = true;
	private boolean fallThrough = false;
	
	private int score = 0;
	
	private boolean cegg;
	
	public Ram(Camera camera, float x, float y, boolean cegg) {
		super(camera, stand, x, y, -0.15f, WIDTH, 16 * Map.SCALE, 0, false);
		if (stand == null) {
			stand = new Texture("Ram.png");
			dead  = new Texture("RipRam.png");
			cstand = new Texture("CRam.png");
			cdead  = new Texture("CRipRam.png");
		}
		this.cegg = cegg;
		oldPosition = new Vector3f();
		velocity = new Vector3f();
		if (!cegg)
			setTexture(stand);
		else
			setTexture(cstand);
	}
	
	public void update() {
		oldPosition.set(position);
		if (didHitPlatform) {
			velocity.y = 0;
			didHitPlatform = false;
		}
		
		if (velocity.y < FALL) {
			if (velocity.y < 0)
				velocity.y += GRAVITY_UP * Map.timeScale * Main.dtime;
			else
				velocity.y += GRAVITY_DOWN * Map.timeScale * Main.dtime;
		}
		else
			velocity.y = FALL;
		
		position.add(velocity.mul((float) Main.dtime, new Vector3f()));
	}
	
	public float getPastY() {
		return oldPosition.y;
	}

	public void hitPlatform() {
		didHitPlatform = true;
	}
	
	public void sacrifice() {
		score = 1;
		if (!cegg)
			setTexture(dead);
		else
			setTexture(cdead);
		fallThrough = true;
	}
	
	public boolean shouldFallThrough() {
		return fallThrough;
	}

	public boolean shouldRemove() {
		return position.y > Map.MAP_HEIGHT;
	}
	
	public int getPoints() {
		return score;
	}
}
