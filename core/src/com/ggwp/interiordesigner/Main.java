package com.ggwp.interiordesigner;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggwp.interfaces.AndroidOnlyInterface;

public class Main extends Game {



	public static AndroidOnlyInterface aoi;

	private static Main instance;

	public static Main getInstance(){
		return instance;
	}

	public Main(AndroidOnlyInterface pAoi){
		aoi = pAoi;
	}

	@Override
	public void create () {
		instance = this;
		setScreen(new MenuScreen());
		dispose();
	}

}
