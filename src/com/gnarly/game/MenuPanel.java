package com.gnarly.game;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.TexRect;
import com.gnarly.game.play.Map;
import com.gnarly.game.play.NumberDisplay;

public class MenuPanel extends Panel {

	private Window window;
	private Camera camera;
	
	private TexRect background;
	
	private Button playButton;
	
	private TexRect scoreText;
	private NumberDisplay scoreDisplay;
	
	public MenuPanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		
		background = new TexRect(camera, "MenuBackground.png", 0, 0, -0.5f, 1920, 1080, 0, true);
		
		playButton = new Button(window, camera, "PlayDefault.png", "PlayHovered.png", "PlayPressed.png", (camera.getWidth() - 480) / 2, camera.getHeight() / 2 + 120, -0.1f, 480, 240, true);

		scoreDisplay = new NumberDisplay(camera, camera.getWidth() / 2 - 300 + Map.SCALE * 4, camera.getHeight() / 2 + 140 + playButton.getHeight(), 0, 93, NumberDisplay.JUSTIFICATION_LEFT, 0, 8);
		scoreText = new TexRect(camera, "Highscore.png", camera.getWidth() / 2 - 500 - Map.SCALE * 4, camera.getHeight() / 2 + 145 + playButton.getHeight(), 0, 200, 83, 0, true);
		
		state = Main.MENU_PANEL;
		
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
	
	public void update() {
		playButton.update();
		if (playButton.getState() == Button.RELEASED)
			state = Main.GAME_PANEL;
	}

	public void render() {
		background.render();
		playButton.render();
		scoreDisplay.render();
		scoreText.render();
	}
	
	public int checkState() {
		int state = this.state;
		this.state = Main.MENU_PANEL;
		return state;
	}
}
