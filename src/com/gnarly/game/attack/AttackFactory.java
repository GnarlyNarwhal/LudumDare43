package com.gnarly.game.attack;

import java.util.ArrayList;

import com.gnarly.engine.display.Camera;
import com.gnarly.game.play.Player;

public class AttackFactory {

	public static final int ATTACK_COUNT = 3;
	public static final int ANGER_RAM    = 0;
	public static final int FIREBALL     = 1;
	public static final int LIGHTNING    = 2;
	
	private Camera camera;
	private Player player;
	private boolean active;
	
	private ArrayList<Attack> attacks;
	
	private SmoteCallback callback;
	
	public AttackFactory(Camera camera, Player player, SmoteCallback callback) {
		this.camera = camera;
		this.player = player;
		attacks = new ArrayList<>();
		this.callback = callback;
		active = true;
	}
	
	public void update() {
		for (int i = 0; i < attacks.size(); ++i) {
			attacks.get(i).update();
			if (attacks.get(i).struckDown() && active) {
				callback.onSmote(attacks.get(i).id);
				active = false;
			}
			if (attacks.get(i).isComplete()) {
				attacks.remove(i--);
			}
		}
	}
	
	public void render() {
		for (int i = 0; i < attacks.size(); ++i)
			attacks.get(i).render();
	}
	
	public void triggerRandom() {
		triggerNew((int) (Math.random() * ATTACK_COUNT));
	}
	
	public void triggerNew(int type) {
		Attack addition = null;
		switch (type) {
			case ANGER_RAM:
				addition = new AngerRam(camera, player);
				addition.id = ANGER_RAM;
				break;
			case FIREBALL:
				addition = new Fireball(camera, player);
				addition.id = FIREBALL;
				break;
			case LIGHTNING:
				addition = new CloudStrike(camera, player);
				addition.id = LIGHTNING;
				break;
		}
		if (addition != null)
			attacks.add(addition);
	}
	
	public void deactivate() {
		active = false;
	}
	
	public void setActive() {
		active = true;
	}
}
