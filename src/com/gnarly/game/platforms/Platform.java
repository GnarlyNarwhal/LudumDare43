package com.gnarly.game.platforms;

import com.gnarly.game.play.Map;
import com.gnarly.game.play.Player;
import com.gnarly.game.play.Ram;

public abstract class Platform {
	
	protected static final float PULSE_SPEED = 2;
	
	public static final float WIDTH = 32 * Map.SCALE;
	
	public static final int FLAG_NONE   = 0;
	public static final int FLAG_DELETE = 1;
	public static final int FLAG_CREATE = 2;
	
	protected float left;
	protected float right;
	protected float y;
	
	protected int flag;
	protected float time;
	
	public Platform(float left, float right, float y) {
		this.left = left;
		this.right = right;
		this.y = y;
		flag = FLAG_NONE;
	}
	
	public float getY() {
		return y;
	}
	
	public float getLeft() {
		return left;
	}
	
	public float getRight() {
		return right;
	}
	
	public float getCenter() {
		return (left + right) / 2;
	}
	
	public boolean playerPassthrough(Player player) {
		return (player.getPastY() + player.getHeight() <= y && player.getY() + player.getHeight() > y && player.getX() + 3 * Map.SCALE <= right && player.getX() + player.getWidth() - 3 * Map.SCALE >= left);
	}
	
	public boolean ramPassthrough(Ram ram) {
		return (ram.getPastY() + ram.getHeight() <= y && ram.getY() + ram.getHeight() > y && ram.getX() + 3 * Map.SCALE <= right && ram.getX() + ram.getWidth() - 3 * Map.SCALE >= left);
	}
	
	public void flagDelete(boolean delete) {
		if (delete)
			flag = FLAG_DELETE;
		else
			flag = FLAG_NONE;
	}
	
	public void flagCreate(boolean create) {
		if (create)
			flag = FLAG_CREATE;
		else
			flag = FLAG_NONE;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public boolean compare(Platform platform) {
		return (platform.left == left && platform.right == right && platform.y == y);
	}
	
	public abstract void update();
	public abstract void render();
}
