package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen{

    private Skin skin;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundImage;
    private Texture gradient;
    final TextButton createNewBtn;
    //createNewBtn.setPosition(200, 200);
    final TextButton catalogBtn;
    //createNewBtn.setPosition(200, 150);
    final TextButton exitBtn;

    BitmapFont titleFont;
    BitmapFont textFont;

    public MenuScreen(){
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        createFonts();

        skin = new Skin();
        Pixmap pixmap = new Pixmap(10, 20, Pixmap.Format.RGBA8888);

        //Pixmap mask = new Pixmap(128, 128, Pixmap.Format.Alpha);

// Cut a rectangle of alpha value 0
        //mask.setBlending(Pixmap.Blending.None);
        //mask.setColor(new Color(0f, 0f, 0f, 0f));
        //mask.fillRectangle(0, 0, 32, 32);
        //pixmap.setColor(Color.rgba8888(255, 255, 255, 0.5f));
        //pixmap.setColor(Color.valueOf("#1abc9c"));
        //pixmap.fillCircle(50, 50, 50);
        //pixmap.drawPixmap(mask, pixmap.getWidth(), pixmap.getHeight());
        //mask.setBlending(Pixmap.Blending.SourceOver);
        skin.add("defaultButton", new Texture(pixmap));

        pixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.5f));
        pixmap.fill();

        skin.add("defaultButtonHover", new Texture(pixmap));

        skin.add("defaultFont", textFont);




        createNewBtn = new TextButton("CREATE NEW DESIGN", createButtonStyle(Color.valueOf("#2ecc71")));
        createNewBtn.pad(10);
        //createNewBtn.setPosition(200, 200);
        catalogBtn = new TextButton("CATALOG ", createButtonStyle(Color.valueOf("#f1c40f")));
        catalogBtn.pad(10);
        //createNewBtn.setPosition(200, 150);
        exitBtn = new TextButton("EXIT", createButtonStyle(Color.valueOf("#1abc9c")));
        exitBtn.pad(10);
        //createNewBtn.setPosition(200, 100);

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();

        table.row().pad(5);
        table.add(createNewBtn).align(Align.left);
        table.row().pad(5);
        table.add(catalogBtn).align(Align.left);
        table.row().pad(5);
        table.add(exitBtn).align(Align.left);

        //table.setFillParent(true);
        //table.setSize(30, 30);
        Container wrapper = new Container<Table>(table);

        //table.setBackground(skin.getDrawable("defaultButtonHover"));
        wrapper.setPosition(200, 120);

        System.out.println(table.getHeight());
        //wrapper.setFillParent(true);
        //wrapper.setBackground(skin.getDrawable("defaultButtonHover"));
        Container grad = new Container();
        stage.addActor(wrapper);
        backgroundImage = new Texture("menu2.jpg");
        gradient = new Texture("gradient-black.png");
    }

    private void addListeners(){

    }

    private TextButton.TextButtonStyle createButtonStyle(Color c){
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        //textButtonStyle.down = skin.getDrawable("defaultButton");
        //textButtonStyle.over = skin.getDrawable("defaultButtonHover");
        textButtonStyle.up = skin.getDrawable("defaultButtonHover");
        textButtonStyle.font = skin.getFont("defaultFont");
        textButtonStyle.fontColor = c;
        return textButtonStyle;
    }

    private void createFonts() {
        FileHandle fontFile = Gdx.files.internal("Data/Bernardo-Moda-Bold.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 20;
        textFont = generator.generateFont(parameter);
        parameter.size = 40;
        titleFont = generator.generateFont(parameter);
        generator.dispose();
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

        stage.getBatch().draw(gradient, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
