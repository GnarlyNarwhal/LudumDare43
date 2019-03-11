package com.gnarly.game.platforms;

import java.util.ArrayList;

import com.gnarly.engine.display.Camera;
import com.gnarly.game.play.Map;

public class PlatformFactory {

	public static final int MAX_STAGE = 4;
	
	public static Platform makePlatform(Camera camera, float x, float y, int width) {
		if (y < camera.getHeight())
			return new HeavenPlatform(camera, x, y, width);
		else if (y < camera.getHeight() * 2)
			return new EarthPlatform(camera, x, y, width);
		else
			return new HellPlatform(camera, x, y, width);
	}
	
	public static void loadStage(ArrayList<Platform> platforms, Camera camera, int stage) {
		platforms.clear();		
		switch (stage) {
			case 0:
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT     / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT     / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 2 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 2 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 3 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 3 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 4 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 4 / 5, 3));
				break;
			case 1:
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT     / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 2 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth() * 2 / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 3 / 5, 3));
				platforms.add(makePlatform(camera, camera.getWidth()     / 3 - Platform.WIDTH * 1.5f, Map.MAP_HEIGHT * 4 / 5, 3));
				break;
			case 2:
				platforms.add(PlatformFactory.makePlatform(camera, (camera.getWidth() - Platform.WIDTH * 4) / 2, camera.getHeight()     / 2, 4));
				platforms.add(PlatformFactory.makePlatform(camera, (camera.getWidth() - Platform.WIDTH * 4) / 2, camera.getHeight() * 3 / 2, 4));
				platforms.add(PlatformFactory.makePlatform(camera, (camera.getWidth() - Platform.WIDTH * 4) / 2, camera.getHeight() * 5 / 2, 4));
				break;
			case 3:
				platforms.add(PlatformFactory.makePlatform(camera, (camera.getWidth() - Platform.WIDTH * 6) / 2, camera.getHeight() * 5 / 2, 6));
				break;
			case 4:
				platforms.add(PlatformFactory.makePlatform(camera, (camera.getWidth() - Platform.WIDTH * 2) / 2, camera.getHeight() * 5 / 2, 2));
				break;
		}
	}
	
	public static void advanceLevel(ArrayList<Platform> platforms, ArrayList<Platform> nextSet) {
		for (int i = 0; i < platforms.size(); ++i)
			if (platforms.get(i).getFlag() == Platform.FLAG_DELETE)
				platforms.remove(i--);
		while (nextSet.size() > 0) {
			nextSet.get(0).flagCreate(false);
			platforms.add(nextSet.get(0));
			nextSet.remove(0);
		}
	}
	
	public static void compareStage(ArrayList<Platform> platforms, ArrayList<Platform> nextSet, Camera camera, int stage) {
		loadStage(nextSet, camera, stage);
		for (int i = 0; i < platforms.size(); ++i)
			platforms.get(i).flagDelete(true);
		for (int i = 0; i < nextSet.size(); ++i)
			nextSet.get(i).flagCreate(true);
		for (int i = 0; i < platforms.size(); ++i) {
			for (int j = 0; j < nextSet.size(); ++j) {
				if (platforms.get(i).compare(nextSet.get(j))) {
					platforms.get(i).flagDelete(false);
					nextSet.remove(j--);
				}
			}
		}
	}
}
