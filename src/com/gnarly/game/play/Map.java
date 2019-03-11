package com.gnarly.game.play;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;
import com.gnarly.game.Main;
import com.gnarly.game.attack.AttackFactory;
import com.gnarly.game.platforms.Platform;
import com.gnarly.game.platforms.PlatformFactory;

public class Map {
	
	private static final float PLATFORM_DELAY = 5;
	
	private static final int ADVANCE_LEVEL = 25;
	
	private static final float ATTACK_RATE  = 5;
	
	private static final float GRAVITY_UP   = 2400;
	private static final float GRAVITY_DOWN = 3000;
	private static final float FALL         = 3200;
	
	public static final float SCALE = 4;
	
	public static float MAP_HEIGHT;
	
	private static final int BABY_RATE = 7;

	private static final int NO_DEATH      = 0;
	private static final int DEVIL_SNATCH  = 1;
	private static final int RAM_SMASHING  = 2;
	private static final int INCINERATION  = 3;
	private static final int ELECTROCUTION = 4;
		
	private static Texture   DEVIL_HAND;
	private static Texture   FLYING_ABRAHAM;
	private static Texture   CRISPY_ABRAHAM;
	private static Texture[] SHOCKED_ABRAHAM;
	
	private static final float SHOCK_FRAME_LENGTH = 0.15f;
	
	public static float timeScale = 1;
	
	private Camera camera;
	
	private Player player;
	private ArrayList<Sacrifice> sacrifices;
	private ArrayList<Platform> platforms;
	private ArrayList<Platform> nextSet;
	
	private TexRect background;
	
	private float sacrificesAtATime = 2;
	private int numSacrifices = 0;
	private int totalSacrifices = 0;
	
	private int death = NO_DEATH;
	private float deathTime = 0;
	
	private TexRect deathRect;
	
	private AttackFactory attacks;
	private float attackTime;
	
	private Vector3f velocity;
	
	private int shockFrame;
	
	private int lastScore;
	private int score;
	private int stage;
	
	private float platformTime;
	
	private boolean complete;
	
	private TexRect scoreText;
	private NumberDisplay scoreDisplay;
	
	public Map(Window window, Camera camera) {
		MAP_HEIGHT = camera.getHeight() * 3;
		
		this.camera = camera;
		platforms = new ArrayList<>();
		nextSet = new ArrayList<>();
		player = new Player(window, camera);
		
		background = new TexRect(camera, "background.png", 0, 0, -0.2f, camera.getWidth(), MAP_HEIGHT, 0, false);
		
		sacrifices = new ArrayList<>();
		
		if (DEVIL_HAND == null) {
			DEVIL_HAND     = new Texture("AbrahamDevilSnatch.png");
			FLYING_ABRAHAM = new Texture("AbrahamRamSmash.png");
			CRISPY_ABRAHAM = new Texture("AbrahamFried.png");
			
			SHOCKED_ABRAHAM = new Texture[4];
			SHOCKED_ABRAHAM[0] = new Texture("AbrahamShocked0.png");
			SHOCKED_ABRAHAM[1] = new Texture("AbrahamShocked1.png");
			SHOCKED_ABRAHAM[2] = new Texture("AbrahamShocked2.png");
			SHOCKED_ABRAHAM[3] = new Texture("AbrahamShocked3.png");
		}
		
		deathRect = new TexRect(camera, DEVIL_HAND, 0, 0, 0, 0, 0, 0, false);
		
		attacks = new AttackFactory(camera, player, (int id) -> {
			switch (id) {
				case AttackFactory.ANGER_RAM:
					death = RAM_SMASHING;
					deathRect.setTexture(FLYING_ABRAHAM);
					deathRect.set(player.getX(), player.getY(), player.getWidth(), player.getHeight());
					velocity.set(500 * SCALE, -500 * SCALE, 0);
					break;
				case AttackFactory.FIREBALL:
					death = INCINERATION;
					deathRect.setTexture(CRISPY_ABRAHAM);
					deathRect.set(player.getX(), player.getY(), player.getWidth(), player.getHeight());
					velocity.set(0, -20 * SCALE, 0);
					break;
				case AttackFactory.LIGHTNING:
					death = ELECTROCUTION;
					deathRect.setTexture(SHOCKED_ABRAHAM[0]);
					deathRect.set(player.getX(), player.getY(), player.getWidth(), player.getHeight());
					shockFrame = 0;
					break;
			}
		});
		
		velocity = new Vector3f();
		
		scoreDisplay = new NumberDisplay(camera, camera.getWidth() / 2 - 300 + Map.SCALE * 4, 10, 0, 93, NumberDisplay.JUSTIFICATION_LEFT, 0, 8);
		
		scoreText = new TexRect(camera, "Score.png", camera.getWidth() / 2 - 500 - Map.SCALE * 4, 10, 0, 200, 93, 0, true);
		
		reload();
	}
	
	public void update() {
		switch (death) {
			case NO_DEATH:
				for (int i = 0; i < platforms.size(); ++i)
					platforms.get(i).update();
				for (int i = 0; i < nextSet.size(); ++i)
					nextSet.get(i).update();
				
				if (platformTime > 0) {
					platformTime -= Main.dtime;
					if (platformTime < 0)
						PlatformFactory.advanceLevel(platforms, nextSet);
				}
				else if (stage != PlatformFactory.MAX_STAGE && score - lastScore >= ADVANCE_LEVEL) {
					platformTime = PLATFORM_DELAY / timeScale;
					PlatformFactory.compareStage(platforms, nextSet, camera, ++stage);
					lastScore = score;
				}
				
				sacrificesAtATime += Main.dtime / 100;
				while (numSacrifices < (int) sacrificesAtATime)
					spawnSacrifice();
				
				player.update();
				for (int i = 0; i < sacrifices.size(); ++i) {
					sacrifices.get(i).update();
					if (sacrifices.get(i).shouldRemove()) {
						score += sacrifices.get(i).getPoints();
						sacrifices.remove(i--);
						--numSacrifices;
					}
				}
				boolean playerCollided = false;
				for (int i = 0; i < platforms.size(); ++i) {
					Platform curPlatform = platforms.get(i);
					if (!player.shouldFallThrough() && curPlatform.playerPassthrough(player) && curPlatform.getY() < player.getY() + player.getHeight()) {
						player.setY(curPlatform.getY() - player.getHeight());
						player.hitPlatform();
						playerCollided = true;
					}
					for (int j = 0; j < sacrifices.size(); ++j) {
						Sacrifice sacrifice = sacrifices.get(j);
						if (sacrifice instanceof Ram) {
							Ram ram = (Ram) sacrifice;
							if (!ram.shouldFallThrough() && curPlatform.ramPassthrough(ram) && curPlatform.getY() < ram.getY() + ram.getHeight()) {
								ram.setY(curPlatform.getY() - ram.getHeight());
								ram.hitPlatform();
							}
						}
					}
				}
				if (!playerCollided)
					player.inFreeFall();
				for (int i = 0; i < sacrifices.size(); ++i)
					player.attemptSacrifice(sacrifices.get(i));
				if (player.getX() < 0)
					player.setX(0);
				else if (player.getX() + player.getWidth() > camera.getWidth())
					player.setX(camera.getWidth() - player.getWidth());
				if (player.getY() < 0) {
					player.setY(0);
					player.setYVelocity(0);
				}
				else if (player.getY() + player.getHeight() > MAP_HEIGHT) {
					death = DEVIL_SNATCH;
					deathRect.setTexture(DEVIL_HAND);
					deathRect.set(player.getX(), MAP_HEIGHT - 24 * SCALE, 16 * SCALE, 24 * SCALE);
					attacks.deactivate();
				}
				camera.setY(player.getY() + (player.getHeight() - camera.getHeight()) / 2);
				if (camera.getY() < 0)
					camera.setY(0);
				else if (camera.getY() + camera.getHeight() > MAP_HEIGHT)
					camera.setY(MAP_HEIGHT - camera.getHeight());

				attackTime += Main.dtime * timeScale;
				if (attackTime > ATTACK_RATE) {
					attacks.triggerRandom();
					attackTime -= ATTACK_RATE;
				}
				
				timeScale += Main.dtime / 200;
				if (timeScale > 4)
					timeScale = 4;
				break;
			case DEVIL_SNATCH:
				deathTime += Main.dtime;
				if (deathTime > 1)
					deathRect.translate(0, (float) (32 * SCALE * Main.dtime), 0);
				if (deathRect.getY() >= MAP_HEIGHT)
					complete = true;
				break;
			case RAM_SMASHING:
				if (velocity.y < FALL) {
					if (velocity.y < 0)
						velocity.y += GRAVITY_UP * Main.dtime;
					else
						velocity.y += GRAVITY_DOWN * Main.dtime;
				}
				else
					velocity.y = FALL;

				deathRect.translate(velocity.mul((float) Main.dtime, new Vector3f()));
				if (deathRect.getX() >= camera.getWidth())
					complete = true;
				break;
			case INCINERATION:
				if (velocity.y < FALL) {
					if (velocity.y < 0)
						velocity.y += GRAVITY_UP * Main.dtime;
					else
						velocity.y += GRAVITY_DOWN * Main.dtime;
				}
				else
					velocity.y = FALL;

				deathRect.translate(velocity.mul((float) Main.dtime, new Vector3f()));
				if (deathRect.getY() >= MAP_HEIGHT)
					complete = true;
				break;
			case ELECTROCUTION:
				if (shockFrame < SHOCKED_ABRAHAM.length - 1) {
					deathTime += Main.dtime;
					shockFrame = (int) (deathTime / SHOCK_FRAME_LENGTH);
					if (shockFrame >= SHOCKED_ABRAHAM.length - 1) {
						shockFrame = SHOCKED_ABRAHAM.length - 1;
						velocity.set(0, -20 * SCALE, 0);
					}
					deathRect.setTexture(SHOCKED_ABRAHAM[shockFrame]);
				}
				else {
					if (velocity.y < FALL) {
						if (velocity.y < 0)
							velocity.y += GRAVITY_UP * Main.dtime;
						else
							velocity.y += GRAVITY_DOWN * Main.dtime;
					}
					else
						velocity.y = FALL;

					deathRect.translate(velocity.mul((float) Main.dtime, new Vector3f()));
					if (deathRect.getY() >= MAP_HEIGHT)
						complete = true;
				}
				break;
		}
		attacks.update();
		scoreDisplay.setValue(score);
	}
	
	public void spawnSacrifice() {
		boolean cegg = totalSacrifices % 10 == 0 && totalSacrifices > 0;
		if (totalSacrifices % BABY_RATE == 0 && totalSacrifices != 0)
			sacrifices.add(new SinusoidalBaby(camera, player.getY(), (int) Math.round(Math.random()) * 2 - 1, cegg));
		else {
			Platform platform = platforms.get((int) ((Math.random() * 100000) % platforms.size()));
			float min = platform.getLeft() - Ram.WIDTH + SCALE;
			float max = platform.getRight() - SCALE;
			sacrifices.add(new Ram(camera, (float) (min + (max - min) * Math.random()), platform.getY() - 64 * SCALE, cegg));
		}
		++numSacrifices;
		++totalSacrifices;
	}
	
	public void render() {
		background.render();
		for (int i = 0; i < nextSet.size(); ++i)
			nextSet.get(i).render();
		for (int i = 0; i < platforms.size(); ++i)
			platforms.get(i).render();
		if (death == NO_DEATH)
			player.render();
		else
			deathRect.render();
		for (int i = 0; i < sacrifices.size(); ++i)
			sacrifices.get(i).render();
		attacks.render();
		scoreText.render();
		scoreDisplay.render();
	}
	
	public void reload() {
		sacrifices.clear();
		stage = 0;
		PlatformFactory.loadStage(platforms, camera, 0);
		nextSet.clear();
		Platform platform = platforms.get((int) ((Math.random() * 100000) % platforms.size()));
		player.setX(platform.getCenter() - player.getWidth() / 2);
		player.setY(platform.getY() - player.getHeight());
		attacks.setActive();
		score = 0;
		timeScale = 1;
		complete = false;
		death = 0;
	}
	
	public boolean isComplete() {
		if (complete) {
			File file = new File("res/high.sco");
			if (file.exists()) {
				try {
					Scanner scanner = new Scanner(file);
					int highscore = scanner.nextInt();
					if (score > highscore) {
						PrintWriter writer = new PrintWriter(file);
						writer.write(Long.toString(score));
						writer.close();
					}
					scanner.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					PrintWriter writer = new PrintWriter(file);
					writer.write(Long.toString(score));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return complete;
	}
}
