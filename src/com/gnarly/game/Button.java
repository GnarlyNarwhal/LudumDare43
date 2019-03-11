package com.gnarly.game;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.TexRect;

public class Button {

	public static final int
		UNPRESSED = 0,
		RELEASED  = 1,
		PRESSED   = 2,
		HELD      = 3;
	
	private Window window;
	private Camera camera;
	
	private TexRect[] states;
	
	private float x, y, width, height;
	
	private int state, tex;
	
	public Button(Window window, Camera camera, String tex1, String tex2, String tex3, float x, float y, float depth, float width, float height, boolean gui) {
		this.window = window;
		this.camera = camera;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		states = new TexRect[3];
		states[0] = new TexRect(camera, tex1, x, y, depth, width, height, 0, gui);
		states[1] = new TexRect(camera, tex2, x, y, depth, width, height, 0, gui);
		states[2] = new TexRect(camera, tex3, x, y, depth, width, height, 0, gui);
		tex = 0;
		state = 0;
	}
	
	public void update() {
		if(contains(window.getMouseCoords(camera))) {
			if(window.mousePressed(GLFW_MOUSE_BUTTON_1) > Window.BUTTON_UNPRESSED) {
				tex = 2;
				if(state <= RELEASED)
					state = PRESSED;
				else
					state = HELD;
			}
			else {
				tex = 1;
				if(state >= PRESSED)
					state = RELEASED;
				else
					state = UNPRESSED;
			}
		}
		else {
			tex = 0;
			state = UNPRESSED;
		}
	}
	
	public void render() {
		states[tex].render();
	}
	
	public boolean contains(Vector3f coords) {
		return coords.x >= x && coords.y >= y && coords.x < x + width && coords.y < y + height;
	}
	
	public int getState() {
		return state;
	}
	
	public void setTex(int tex) {
		this.tex = tex;
	}
	
	public float getHeight() {
		return states[0].getHeight();
	}
}
