package com.gnarly.game;

public abstract class Panel {
	
	protected int state;
	
	public abstract void update();
	public abstract void render();
	public abstract int checkState();
}
