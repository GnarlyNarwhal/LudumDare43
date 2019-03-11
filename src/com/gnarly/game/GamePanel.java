package com.gnarly.game;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.game.play.Map;

public class GamePanel extends Panel {

	private Window window;
	private Camera camera;
	
	private Map map;
	
	public GamePanel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
	
		map = new Map(window, camera);
		
		state = Main.GAME_PANEL;
	}
	
	public void update() {
		map.update();
		if (map.isComplete())
			state = Main.OVER_PANEL;
	}
	
	public void render() {
		map.render();
	}
	
	public void reset() {
		map = new Map(window, camera);
	}
	
	public int checkState() {
		int state = this.state;
		this.state = Main.GAME_PANEL;
		return state;
	}
}
