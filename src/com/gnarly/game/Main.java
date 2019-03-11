package com.gnarly.game;

import com.gnarly.engine.audio.ALManagement;
import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.shaders.Shader;

public class Main {
	
	public static long FPS = 999;
	public static double dtime;	
	
	public static final int
		NUM_PANELS = 3,
		GAME_PANEL = 0,
		MENU_PANEL = 1,
		OVER_PANEL = 2;
	
	private ALManagement al;
	
	private Window window;
	private Camera camera;
	
	private Panel[] panels;
	private int panel;
	
	public void start() {
		long curTime, pastTime, nspf = 1000000000 / FPS;
		init();
		pastTime = System.nanoTime();
		while(!window.shouldClose()) {
			curTime = System.nanoTime();
			if (curTime - pastTime > nspf) {
				dtime = (curTime - pastTime) / 1000000000d;
				update();
				render();
				pastTime = curTime;
			}
		}
		al.destroy();
		Window.terminate();
	}
	
	private void init() {
		al = new ALManagement();
		window = new Window("Abraham", true);
		//window = new Window(100, 100, "Abraham", true, true, true);
		camera = new Camera(1920, 1080);
		Shader.init();
		
		panels = new Panel[NUM_PANELS];
		panels[GAME_PANEL] = new GamePanel(window, camera);
		panels[MENU_PANEL] = new MenuPanel(window, camera);
		panels[OVER_PANEL] = new OverPanel(window, camera);
		panel = MENU_PANEL;
	}
	
	private void update() {
		window.update();
		int state = panels[panel].checkState();
		if (state != panel) {
			switch (state) {
				case GAME_PANEL:
					GamePanel game = (GamePanel) panels[GAME_PANEL];
					game.reset();
				case OVER_PANEL:
					OverPanel over = (OverPanel) panels[OVER_PANEL];
					over.setActive();
			}
			panel = state;
		}
		panels[panel].update();
		camera.update();
	}
	
	private void render() {
		window.clear();
		panels[panel].render();
		window.swap();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}
