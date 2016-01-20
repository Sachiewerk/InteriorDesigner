package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen{

    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundImage;

    public MenuScreen(){
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.5f));
        pixmap.fill();

        skin.add("defaultButton", new Texture(pixmap));
        BitmapFont bitmapFont = new BitmapFont();
        skin.add("defaultFont", bitmapFont);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("defaultButton");
        textButtonStyle.down = skin.newDrawable("defaultButton");
        textButtonStyle.font = skin.getFont("defaultFont");

        skin.add("default", textButtonStyle);

        final TextButton textButton = new TextButton("PLAY", textButtonStyle);
        textButton.setPosition(200, 200);

        final TextButton textButton2 = new TextButton("PLAY 2 ", textButtonStyle);
        textButton.setPosition(200, 150);
        final TextButton textButton3 = new TextButton("PLAY 3 ", textButtonStyle);
        textButton.setPosition(200, 100);

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();

        table.add(textButton);
        table.add(textButton2);
        table.add(textButton3);
        table.setFillParent(true);
        stage.addActor(table);

        backgroundImage = new Texture("menu.jpg");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
