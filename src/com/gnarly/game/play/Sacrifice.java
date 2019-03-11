package com.gnarly.game.play;

public interface Sacrifice {
	
	public void update();
	public void render();
	public float getX();
	public float getY();
	public float getWidth();
	public float getHeight();
	public void sacrifice();
	public boolean shouldRemove();
	public int getPoints();
}
