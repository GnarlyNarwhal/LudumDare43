package com.gnarly.game.play;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;

public class SinusoidalBaby extends TexRect implements Sacrifice {
	
	public static final float HEIGHT = 16 * Map.SCALE;
	
	private static final float BASE_FLUX_HEIGHT = 40 * Map.SCALE;
	private static final int   MIN_FLUXES       = 2;
	private static final int   MAX_FLUXES       = 10;
	private static final float MIN_SPEED        = 50 * Map.SCALE;
	private static final float MAX_SPEED        = 80 * Map.SCALE;

	private static final float GRAVITY_UP   = 2400;
	private static final float GRAVITY_DOWN = 3000;
	private static final float FALL         = 3200;
	
	private static float nextFlux = generateFlux();
	
	private static Texture idle;
	private static Texture head;
	private static Texture body;
	private static Texture cidle;
	private static Texture chead;
	private static Texture cbody;
	
	private float fluxHeight;
	private float numFluxes;
	private float speed;
	private float time;
	private float baseHeight;
	private float direction;
	
	private boolean sacrificed;
	private TexRect severedHead;
	private Vector3f headVelocity;
	private Vector3f bodyVelocity;

	private int score = 0;

	private boolean cegg;
	
	public SinusoidalBaby(Camera camera, float y, int direction, boolean cegg) {
		super(camera, idle, -16 * Map.SCALE, 0, -0.15f, 16 * Map.SCALE, 16 * Map.SCALE, 0, false);
		if (idle == null) {
			idle = new Texture("BabyTPose.png");
			head = new Texture("BabyHead.png");
			body = new Texture("BabyBody.png");
			cidle = new Texture("CBabyTPose.png");
			chead = new Texture("CBabyHead.png");
			cbody = new Texture("CBabyBody.png");
		}
		this.cegg = cegg;
		if (!cegg)
			setTexture(idle);
		else
			setTexture(cidle);
		this.direction = direction;
		if (direction < 0)
			position.x = camera.getWidth();
		baseHeight = y;
		fluxHeight = nextFlux;
		nextFlux   = generateFlux();
		numFluxes  = (float) (2 * Math.PI *      (MIN_FLUXES + (MAX_FLUXES * Map.timeScale - MIN_FLUXES) * Math.random()));
		speed      = camera.getWidth() / (float) (MIN_SPEED  + (MAX_SPEED  * Map.timeScale - MIN_SPEED)  * Math.random());
		time       = 0;
	}
	
	public void update() {
		if (!sacrificed) {
			time += Main.dtime;
			float x = time / speed;
			position.x = x * (camera.getWidth() + 16 * Map.SCALE) - 16 * Map.SCALE;
			if (direction < 0)
				position.x = camera.getWidth() - position.x;
			position.y = (float) (baseHeight + Math.sin(x * numFluxes) * fluxHeight);
		}
		else {
			if (headVelocity.y < FALL) {
				if (headVelocity.y < 0)
					headVelocity.y += GRAVITY_UP * Main.dtime;
				else
					headVelocity.y += GRAVITY_DOWN * Main.dtime;
			}
			else
				headVelocity.y = FALL;

			severedHead.translate(headVelocity.mul((float) Main.dtime, new Vector3f()));
			
			if (bodyVelocity.y < FALL) {
				if (bodyVelocity.y < 0)
					bodyVelocity.y += GRAVITY_UP * Main.dtime;
				else
					bodyVelocity.y += GRAVITY_DOWN * Main.dtime;
			}
			else
				bodyVelocity.y = FALL;

			position.add(bodyVelocity.mul((float) Main.dtime, new Vector3f()));
		}
	}
	
	public void render() {
		if (sacrificed)
			severedHead.render();
		super.render();
	}
	
	public void sacrifice() {
		sacrificed = true;
		if (!cegg) {
			severedHead = new TexRect(camera, head, position.x, position.y, -0.15f, 6 * Map.SCALE, 6 * Map.SCALE, 0, false);
			setTexture(body);
		}
		else {
			severedHead = new TexRect(camera, chead, position.x, position.y, -0.15f, 6 * Map.SCALE, 6 * Map.SCALE, 0, false);
			setTexture(cbody);
		}
		headVelocity = new Vector3f(0, -500 * Map.timeScale, 0);
		bodyVelocity = new Vector3f(0, 0, 0);
		score = 3;
	}
	
	public boolean shouldRemove() {
		if (!sacrificed)
			return (direction ==  1 && position.x >= camera.getWidth()) ||
				   (direction == -1 && position.x <= -16 * Map.SCALE);
		else {
			return severedHead.getY() > Map.MAP_HEIGHT;
		}
	}
	
	private static float generateFlux() {
		return (float) (Math.random() / 8 + 0.875) * BASE_FLUX_HEIGHT * Map.timeScale;
	}
	
	public static float getFlux() {
		return nextFlux;
	}
	
	public int getPoints() {
		return score;
	}
}
