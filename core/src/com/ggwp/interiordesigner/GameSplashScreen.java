package com.ggwp.interiordesigner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.Screen;

/**
 * Created by bytexe on 1/28/2016.
 */
public class GameSplashScreen  implements Screen{

    private SpriteBatch batch;
    private Game myGame;
    private Texture texture;
    private OrthographicCamera camera;
    private long startTime;
    private int rendCount;

    public GameSplashScreen  (Game g) // ** constructor called initially **//
    {
        Gdx.app.log("my Spash Screen", "constructor called");
        myGame = g; // ** get Game parameter **//
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 512, 512);
        batch = new SpriteBatch();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        //batch.draw(texture, (Gdx.graphics.getWidth()/2)+(192/2), ((Gdx.graphics.getHeight()/2)+(192/2)));//Gdx.graphics.getWidth()/2+(192/2), Gdx.graphics.getHeight()/2+(192/2));
        batch.draw(texture,150,150);
        //System.out.println((Gdx.graphics.getWidth()/2)+":"+ ((Gdx.graphics.getHeight()/2)+(192/2)));
        batch.end();
        rendCount++;
        if (TimeUtils.millis()>(startTime+5000)) myGame.setScreen(new MenuScreen());

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Gdx.app.log("my Splash Screen", "show called");
        texture = new Texture(Gdx.files.internal("Common/ic_launcher.png")); //** texture is now the splash image **//
        startTime = TimeUtils.millis();
    }

    @Override
    public void hide() {
        Gdx.app.log("my Splash Screen", "hide called");
        Gdx.app.log("my Splash Screen", "rendered " + rendCount + " times.");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
    }

}

