package com.ggwp.interiordesigner.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListner;
import com.ggwp.interiordesigner.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesktopLauncher implements AndroidOnlyInterface{

	static DesktopLauncher launcher;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 854;
		config.height = 480;
		launcher = new DesktopLauncher();
			new LwjglApplication(new Main( launcher), config);
	}

	public void toast(String text) {
		Gdx.app.log("Desktop",text);
	}

	public void notification(String title, String text) {
		Gdx.app.log("Desktop",text);
	}

	public String takeSnapShot(String saveDirectory) {
	return "";
	}

	public String getScreenTemplateDir() {
		return getProjectDirectory()+"cam-snapshots/";

	}


	@Override
	public void requestOnDevice(RequestType rType, Map<String,Object> params) {

		switch (rType){
			case GET_SCREEN_TEMPLATE_DIR:
				for (RequestResultListner r: listeners) {
					if(r.getRequestType()==rType){
						r.OnRequestDone(getScreenTemplateDir());
					}
				}
				break;
			case SHOW_MESSAGE:
				String msg = params.get("message").toString();
				toast(msg);
				break;
			case SHOW_NOTIFICATION:
				String title = params.get("title").toString();
				String msg1 = params.get("message").toString();

				notification(title, msg1);
				break;
			case IMAGE_CAPTURE:
				String saveDirectory = params.get("savedirectory").toString();
				takeSnapShot(saveDirectory);
				break;
		}


	}

	private List<RequestResultListner> listeners = new ArrayList<RequestResultListner>();


	@Override
	public void addResultListener(RequestResultListner resultListner) {
		listeners.add(resultListner);
	}

	@Override
	public void removeResultListener(RequestResultListner resultListner) {
		listeners.remove(resultListner);
	}

	@Override
	public String getProjectDirectory() {
		return System.getProperty("user.home")+"/Pictures/interiordesign/";
	}
}
