package com.ggwp.interiordesigner.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListener;
import com.ggwp.interiordesigner.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesktopLauncher implements AndroidOnlyInterface{

	static DesktopLauncher launcher;
	private List<RequestResultListener> listeners = new ArrayList<RequestResultListener>();

	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		double multiplier = 1;
		config.width = (int) Math.floor(1024 * multiplier);
		config.height = (int) (576 * multiplier);
		launcher = new DesktopLauncher();
		new LwjglApplication(new Main(launcher), config);
	}

	public void toast(String text) {
		Gdx.app.log("Desktop",text);
	}

	public void notification(String title, String text) {
		Gdx.app.log("Desktop", text);
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
				for (RequestResultListener r: listeners) {
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
			case LOG:
				System.out.println(params.get("message"));
				break;
		}
	}

	@Override
	public void addResultListener(RequestResultListener resultListner) {
		listeners.add(resultListner);
	}

	@Override
	public String getProjectDirectory() {
		return System.getProperty("user.home")+"/Pictures/interiordesign/";
	}

}
