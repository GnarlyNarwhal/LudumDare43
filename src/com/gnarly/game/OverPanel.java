package com.gnarly.game;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.ColRect;
import com.gnarly.engine.model.TexRect;
import com.gnarly.game.play.Map;
import com.gnarly.game.play.NumberDisplay;

public class OverPanel extends Panel {

	private static final float FADE_TIME = 1.5f;
	
	private Window window;
	private Camera camera;
	
	private TexRect background;
	private ColRect overlay;
	
	private Button playButton;
	
	private TexRect scoreText;
	private NumberDisplay scoreDisplay;
	
	private float time;
	
	public OverPanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		
		background = new TexRect(camera, "GameOverBackground.png", 0, 0, -0.5f, 1920, 1080, 0, true);
		overlay = new ColRect(camera, 0, 0, 0, 1920, 1080, 0, 0, 0, 1, true);
		
		playButton = new Button(window, camera, "AgainDefault.png", "AgainHovered.png", "AgainPressed.png", (camera.getWidth() - 480) / 2, camera.getHeight() / 2, -0.1f, 480, 240, true);

		scoreDisplay = new NumberDisplay(camera, camera.getWidth() / 2 - 300 + Map.SCALE * 4, camera.getHeight() / 2 + 20 + playButton.getHeight(), -0.01f, 93, NumberDisplay.JUSTIFICATION_LEFT, 0, 8);
		scoreText = new TexRect(camera, "Highscore.png", camera.getWidth() / 2 - 500 - Map.SCALE * 4, camera.getHeight() / 2 + 25 + playButton.getHeight(), -0.01f, 200, 83, 0, true);
		
		state = Main.OVER_PANEL;
		
		loadScore();
	}
	
	public void update() {
		if (time > 0) {
			time -= Main.dtime;
			overlay.setAlpha(time / FADE_TIME);
		}
		playButton.update();
		if (playButton.getState() == Button.RELEASED)
			state = Main.GAME_PANEL;
	}

	public void render() {
		background.render();
		playButton.render();
		scoreDisplay.render();
		scoreText.render();
		if (time > 0)
			overlay.render();
	}
	
	public void setActive() {
		loadScore();
		time = FADE_TIME;
	}
	
	public int checkState() {
		int state = this.state;
		this.state = Main.OVER_PANEL;
		return state;
	}
	
	public void loadScore() {
		File file = new File("res/high.sco");
		if (file.exists()) {
			try {
				Scanner scanner = new Scanner(file);
				scoreDisplay.setValue(scanner.nextInt());
				scanner.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
