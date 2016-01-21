package com.ggwp.interiordesigner.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;

public class DesktopLauncher implements AndroidOnlyInterface{

	static DesktopLauncher launcher;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		launcher = new DesktopLauncher();
			new LwjglApplication(new Main( launcher), config);
	}

	@Override
	public void toast(String text) {
		Gdx.app.log("Desktop",text);
	}

	@Override
	public void notification(String title, String text) {
		Gdx.app.log("Desktop",text);
	}

	@Override
	public String takeSnapShot(String saveDirectory) {
	return "";
	}

	@Override
	public String getScreenTemplateDir() {
		return System.getProperty("user.home")+"/Pictures/interiordesign";
	}


}
