package com.gnarly.game.play;

import org.joml.Vector2f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.model.TexRect;
import com.gnarly.engine.texture.Texture;

public class NumberDisplay {
	
	public static final int
		JUSTIFICATION_LEFT   = 0,
		JUSTIFICATION_CENTER = 1,
		JUSTIFICATION_RIGHT  = 2;						

	public static Texture[] nums;
	
	private static final float DIMENSION_RATIO = 1;
	
	private Camera camera;
	
	private long num;
	private int minDigits;
	
	private int justification;
	private float x, y, height;
	
	private TexRect display;
	
	public NumberDisplay(Camera camera, float x, float y, float z, float height, int justification, long number, int minDigits) {
		this.camera = camera;
		this.num = number;
		this.minDigits = minDigits;
		this.justification = justification;
		this.x = x;
		this.y = y;
		this.height = height;
		if(nums == null) {
			nums = new Texture[10];
			for (int i = 0; i < 10; ++i)
				nums[i] = new Texture(i + "digit.png");
		}
		display = new TexRect(camera, nums[0], 0, 0, z, 0, 0, 0, true);
	}
	
	public void render() {
		float width = height * DIMENSION_RATIO;
		float x = this.x;
		String text = Long.toString(num);
		int numDigits = Math.max(minDigits, text.length());
		if(justification == JUSTIFICATION_CENTER)
			x -= (numDigits / 2.0f) * width;
		else if(justification == JUSTIFICATION_RIGHT)
			x -= numDigits * width;
		display.setX(x);
		display.setY(y);
		display.setHeight(height);
		display.setWidth(width);
		for(int i = 0; i < numDigits; ++i) {
			int index = 0;
			if(i + text.length() - numDigits > -1) {
				if(text.charAt(i + text.length() - numDigits) == 'r')
					index = 16;
				else if(text.charAt(i + text.length() - numDigits) == 'o')
					index = 17;
				else if(text.charAt(i + text.length() - numDigits) >= 'a')
					index = text.charAt(i + text.length() - numDigits) - 87;
				else
					index = text.charAt(i + text.length() - numDigits) - 48;
			}
			display.setTexture(nums[index]);
			display.render();
			display.translate(width, 0, 0);
		}
	}
	
	public long getValue() {
		return num;
	}
	
	public void setValue(long num) {
		this.num = num;
	}
	
	public void add(long num) {
		this.num += num;
	}
	
	public void sub(long num) {
		this.num -= num;
	}
	
	public void increment() {
		++num;
	}
	
	public void decrement() {
		--num;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public void setDigitWidth(float width) {
		this.height = width / DIMENSION_RATIO;
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public Vector2f getPosition() {
		return new Vector2f(x, y);
	}
	
	public Vector2f getPosition(int justification) {
		return new Vector2f(x, y);
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}	
}
