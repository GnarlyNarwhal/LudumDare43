package com.gnarly.game.play;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;

public class Player extends TexRect {

	private static final float STAB_DELAY   = 0.15f;
	private static final float DASH_SPEED   = 2000;
	private static final float DASH_DELAY   = 0.4f;
	private static final float DASH_TIME    = 0.15f;
	private static final float DASH_RESET   = 1;
	private static final float SPEED        = 384;
	private static final float GROUND_JUMP  = 1280;
	private static final float AERIAL_JUMP  = 900;
	private static final float GRAVITY_UP   = 2400;
	private static final float GRAVITY_DOWN = 3000;
	private static final float FALL         = 3200;
	private static final int   NUM_JUMPS    = 8;
	
	private Window window;

	private Vector3f oldPosition;
	private Vector3f velocity;

	private boolean didHitPlatform = true;
	
	private boolean fallThrough = false;
	
	private float dashTime = -DASH_RESET;
	private float stabTime;

	private Texture stand;
	private Texture stab;
	private Texture dash;
	
	private float jumpForce;
	private int numJumps;
	
	public Player(Window window, Camera camera) {
		super(camera, "AbrahamStand.png", 0, 0, -0.1f, 16 * Map.SCALE, 16 * Map.SCALE, 0, false);
		this.window = window;
		oldPosition = new Vector3f();
		velocity = new Vector3f();
		stand = new Texture("AbrahamStand.png");
		stab  = new Texture("AbrahamStab.png");
		dash  = new Texture("AbrahamDash.png");
	}
	
	public void update() {
		oldPosition.set(position);
		
		fallThrough = false;
		if (didHitPlatform) {
			velocity.y = 0;
			didHitPlatform = false;
		}
		
		velocity.x = 0;
		
		double delta = Main.dtime;
		setTexture(stand);
		
		if (stabTime > 0) {
			setTexture(stab);
			stabTime -= delta;
		}

		if (dashTime > 0) {
			setTexture(dash);
			if (delta > dashTime) {
				double tdelta = delta;
				position.x += (float) (direction * DASH_SPEED * dashTime * ((Map.timeScale - 1) * 0.25 + 1));
				delta -= dashTime;
				dashTime -= tdelta;
			}
			else {
				velocity.x = (float) (direction * DASH_SPEED * delta * ((Map.timeScale - 1) * 0.25 + 1));
				dashTime -= delta;
				delta = 0;
			}
		}
		else if (dashTime > -DASH_DELAY / Map.timeScale) {
			setTexture(dash);
			dashTime -= delta;
		}
		else if (dashTime > -DASH_RESET / Map.timeScale) {
			dashTime -= delta;
		}
			
		if ((window.keyPressed(GLFW_KEY_W) == Window.BUTTON_PRESSED || window.keyPressed(GLFW_KEY_SPACE) == Window.BUTTON_PRESSED) && numJumps < NUM_JUMPS) {
			velocity.y = -(float) (jumpForce * ((Map.timeScale - 1) * 0.5 + 1));
			++numJumps;
		}
		if (window.keyPressed(GLFW_KEY_A) >= Window.BUTTON_PRESSED && dashTime <= 0) {
			velocity.x = -(float) (SPEED * Map.timeScale * delta);
			direction  = -1;
		}
		if (window.keyPressed(GLFW_KEY_S) >= Window.BUTTON_PRESSED)
			fallThrough = true;
		if (window.keyPressed(GLFW_KEY_D) >= Window.BUTTON_PRESSED && dashTime <= 0) {
			velocity.x = (float) (SPEED * Map.timeScale * delta);
			direction  = 1;
		}
		if (window.keyPressed(GLFW_KEY_J) == Window.BUTTON_PRESSED && dashTime <= -DASH_RESET / Map.timeScale) {
			dashTime = DASH_TIME;
			stabTime = 0;
		}
		else if (window.keyPressed(GLFW_KEY_K) == Window.BUTTON_PRESSED && stabTime <= 0 && dashTime <= 0) {
			stabTime = STAB_DELAY;
		}
		
		if (velocity.y < FALL) {
			if (velocity.y < 0)
				velocity.y += GRAVITY_UP * Map.timeScale * Main.dtime;
			else
				velocity.y += GRAVITY_DOWN * Map.timeScale * Main.dtime;
		}
		else
			velocity.y = FALL;
		
		position.add(velocity.x, (float) (velocity.y * Main.dtime), 0);
	}
	
	public void attemptSacrifice(Sacrifice sacrifice) {
		if (dashTime > 0 &&
			sacrifice.getY() < position.y + 11 * Map.SCALE &&
			sacrifice.getY() + sacrifice.getHeight() > position.y + 10 * Map.SCALE &&
			((direction == -1 && oldPosition.x > sacrifice.getX() && position.x < sacrifice.getX() + sacrifice.getWidth()) ||
			 (direction ==  1 && oldPosition.x + width < sacrifice.getX() + sacrifice.getWidth() && position.x + width > sacrifice.getX()))) {
			sacrifice.sacrifice();
		}
		else if (stabTime > 0 &&
			sacrifice.getY() < position.y + 11 * Map.SCALE &&
			sacrifice.getY() + sacrifice.getHeight() > position.y + 10 * Map.SCALE &&
			((direction == -1 && position.x < sacrifice.getX() + sacrifice.getWidth() && position.x > sacrifice.getX()) ||
			 (direction ==  1 && position.x + width > sacrifice.getX() && position.x + width < sacrifice.getX() + sacrifice.getWidth()))) {
			sacrifice.sacrifice();
		}
	}
	
	public void render() {
		super.render();
	}

	public void hitPlatform() {
		didHitPlatform = true;
		numJumps = 0;
		jumpForce = GROUND_JUMP;
	}
	
	public float getPastX() {
		return oldPosition.x;
	}
	
	public float getPastY() {
		return oldPosition.y;
	}
	
	public float getTimeScale(float scale) {
		return Map.timeScale;
	}
	
	public boolean shouldFallThrough() {
		return fallThrough;
	}
	
	public void setYVelocity(float velocity) {
		this.velocity.y = velocity;
	}
	
	public void inFreeFall() {
		jumpForce = AERIAL_JUMP;
	}
}
