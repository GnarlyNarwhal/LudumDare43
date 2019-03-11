package com.gnarly.game.attack;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.ColRect;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.model.Vao;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2t;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;
import com.gnarly.game.play.Map;
import com.gnarly.game.play.Player;

public class CloudStrike extends Attack {

	private static final float ENTER    = 2;
	private static final float CHARGING = 3;
	private static final float FIRING   = 3.3f;
	private static final float EXITING  = 4.5f;
	
	private static final float CLOUD_HEIGHT = -150;
	
	private static final float BOLT_DURATION = 0.1f;
	private static final int   NUM_BOLTS     = 3;
	
	private TexRect clouds;
	private TexRect charging;
	private TexRect striking;
	
	private static Texture[] bolts;
	private Bolt lightning;
	private ColRect flash;
	
	private Camera camera;
	private Player player;
	
	private boolean targeted;
	private boolean complete;
	private boolean smote;
	
	private float targetX;
	private float targetY;
	private float cloudX;
	
	private int bolt;
	private float boltTime;
	
	private float startX;
	private float startY;
	
	private float time;
	
	public CloudStrike(Camera camera, Player player) {
		if (bolts == null) {
			bolts = new Texture[3];
			bolts[0] = new Texture("0LightningBolt.png");
			bolts[1] = new Texture("1LightningBolt.png");
			bolts[2] = new Texture("2LightningBolt.png");
		}
			
		clouds    = new TexRect(camera, "StormCloud.png",       0, 0, -0.04f, 720, 351,  0, true);
		charging  = new TexRect(camera, "StormCloudCharge.png", 0, 0, -0.03f, 720, 351,  0, true);
		striking  = new TexRect(camera, "StormCloudStrike.png", 0, 0, -0.03f, 720, 467,  0, true);
		lightning = new Bolt(camera, 227, 2156);
		flash     = new ColRect(camera, 0, 0, 0, camera.getWidth(), camera.getHeight(), 0.41f, 0.8f, 1, 1, true);
		charging.setAlpha(0);
		
		this.camera = camera;
		this.player = player;
		targeted = false;
		complete = false;
		smote = false;
		bolt = 0;
		cloudX = (float) ((camera.getWidth() - clouds.getWidth() - Map.SCALE * 4) * Math.random() + Map.SCALE * 2);
	}
	
	public void update() {
		time += Main.dtime;
		if (time < ENTER) {
			clouds.setPosition(cloudX + (camera.getWidth() - cloudX) * (1 - (time / ENTER)), CLOUD_HEIGHT);
		}
		else if (time < CHARGING) {
			if (!targeted) {
				charging.setPosition(clouds.getX(), clouds.getY());
				striking.setPosition(clouds.getX(), clouds.getY());
				targetX = player.getX() + player.getWidth() / 2;
				targetY = player.getY() + player.getHeight() / 2 - camera.getY();
				targeted = true;
				
				startX = cloudX + clouds.getWidth() / 2;
				startY = CLOUD_HEIGHT + clouds.getHeight() * 3 / 4;
				
				float angle = (float) (Math.atan2(targetY - startY, targetX - startX) - Math.PI / 2);
				
				lightning.set(startX, startY, angle);
			}
			charging.setAlpha((time - ENTER) / (CHARGING - ENTER));
		}
		else if (time < FIRING) {
			boltTime += Main.dtime;
			while (boltTime > BOLT_DURATION) {
				bolt = (bolt + 1) % NUM_BOLTS;
				boltTime -= BOLT_DURATION;
			}
			lightning.setTexture(bolts[bolt]);
			float value = (0.75f * (1 - (time - CHARGING) / (FIRING - CHARGING)));
			flash.setAlpha(value * value * value * value);
			
			int[] side = new int[4];
			side[0] = checkSide(player.getX(),                     player.getY() - camera.getY());
			side[1] = checkSide(player.getX() + player.getWidth(), player.getY() - camera.getY());
			side[2] = checkSide(player.getX(),                     player.getY() + player.getHeight() - camera.getY());
			side[3] = checkSide(player.getX() + player.getWidth(), player.getY() + player.getHeight() - camera.getY());
			
			for (int i = 1; i < side.length; ++i)
				if (side[i] != side[0])
					smote = true;
		}
		else if (time < EXITING)
			clouds.setPosition(-clouds.getWidth() + (cloudX + clouds.getWidth()) * (1 - ((time - FIRING) / (EXITING - FIRING))), CLOUD_HEIGHT);
		else
			complete = true;
	}

	private int checkSide(float x, float y) {
		int side = 0;
		
		if (targetY != startY) {
			float slope = (float) ((targetY - startY) / (targetX - startX));
			
			float boltY = startY + slope * (x - startX);
			if (y < boltY)
				side = 0;
			else
				side = 1;
		}
		else {
			if (x < startX)
				side = 0;
			else
				side = 1;
		}
		return side;
	}
	
	public void render() {
		if (time < CHARGING) {
			clouds.render();
			charging.render();
		}
		else if (time < FIRING) {
			if (bolt == 2)
				charging.render();
			else
				striking.render();
			lightning.render();
			flash.render();
		}
		else
			clouds.render();
	}

	public boolean struckDown() {
		return smote;
	}

	public boolean isComplete() {
		return complete;
	}

	private static class Bolt {

		private static Vao vao;
		
		private Texture texture;
		private Shader2t shader;
		
		private Camera camera;
		
		float x;
		float y;
		float angle;
		float width;
		float height;
		
		public Bolt(Camera camera, float width, float height) {
			this.camera = camera;
			this.width = width;
			this.height = height;
			
			shader = Shader.SHADER2T;

			float vertices[] = {
				 0.5f, 0, 0, // Top left
				 0.5f, 1, 0, // Bottom left
				-0.5f, 1, 0, // Bottom right
				-0.5f, 0, 0  // Top right
			};
			int indices[] = {
				0, 1, 3,
				1, 2, 3
			};
			float[] texCoords = {
				1, 0,
				1, 1,
				0, 1,
				0, 0
			};
			vao = new Vao(vertices, indices);
			vao.addAttrib(texCoords, 2);
		}
		
		public void render() {
			texture.bind();
			shader.enable();
			shader.setMVP(camera.getProjection().translate(x, y, -0.15f).rotateZ(angle).scale(width, height, 1));
			vao.render();
			shader.disable();
			texture.unbind();
		}
		
		public void setTexture(Texture texture) {
			this.texture = texture;
		}
		
		public void set(float x, float y, float angle) {
			this.x = x;
			this.y = y;
			this.angle = angle;
		}
	}
}
