package com.gnarly.game.attack;

public abstract class Attack {
	
	public int id;
	
	public abstract void update();
	public abstract void render();
	public abstract boolean struckDown();
	public abstract boolean isComplete();
}
