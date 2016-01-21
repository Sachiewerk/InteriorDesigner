package com.ggwp.interiordesigner.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.test.ShapeTest2;

import org.omg.CORBA.Environment;

import java.io.Console;

public class DesktopLauncher implements AndroidOnlyInterface{

	static DesktopLauncher launcher;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

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
