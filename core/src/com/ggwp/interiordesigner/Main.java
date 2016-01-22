package com.ggwp.interiordesigner;

import com.badlogic.gdx.Game;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListner;
import com.ggwp.interiordesigner.object.AppScreens;

public class Main extends Game {



	public static AndroidOnlyInterface aoi;

	private static Main instance;

	public static String screenTemplateSaveDirectory;

	public static Main getInstance(){
		return instance;
	}

	public static void setScreen(AppScreens screen){
		try {
			Main.getInstance().setScreen(screen.getClazz().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Main(AndroidOnlyInterface pAoi){
		aoi = pAoi;

		aoi.addResultListener(new RequestResultListner() {
			@Override
			public void OnRequestDone(Object result) {
				if(result!=null && result instanceof String){
					screenTemplateSaveDirectory = (String)result;
				}
			}

			@Override
			public AndroidOnlyInterface.RequestType getRequestType() {
				return AndroidOnlyInterface.RequestType.GET_SCREEN_TEMPLATE_DIR;
			}
		});

		aoi.requestOnDevice(AndroidOnlyInterface.RequestType.GET_SCREEN_TEMPLATE_DIR, null);

	}

	@Override
	public void create () {
		instance = this;
		setScreen(new MenuScreen());
		dispose();
	}

}
