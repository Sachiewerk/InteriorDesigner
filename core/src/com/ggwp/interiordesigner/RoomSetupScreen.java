package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Room;

public class RoomSetupScreen extends AppScreen {

    private Environment environment;
    private PerspectiveCamera camera;

    private ModelBatch modelBatch;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Room room;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private Stage stage;

    public RoomSetupScreen(FileHandle fileHandle) {
        stage = new Stage(new ScreenViewport());

        spriteBatch = new SpriteBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(50f, 50f, 100f);
        camera.lookAt(50, 50, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        room = new Room(camera);
        background = new Texture(fileHandle);
        initOptions();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(background != null){
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        modelBatch.begin(camera);
        modelBatch.render(room.getWalls());
        modelBatch.render(instances, environment);
        modelBatch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        room.getWalls().clear();
        spriteBatch.dispose();
    }

    private void initOptions(){
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(70, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.5f));
        pixmap.fill();

        skin.add("defaultButton", new Texture(pixmap));
        skin.add("defaultFont", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("defaultButton");
        textButtonStyle.down = skin.newDrawable("defaultButton");
        textButtonStyle.font = skin.getFont("defaultFont");

        TextButton backToGallery = new TextButton("BACK", textButtonStyle);
        backToGallery.setBounds(5f, 5f, 70f, 40f);

        TextButton doneButton = new TextButton("DONE", textButtonStyle);
        doneButton.setBounds(80f, 5f, 70f, 40f);

        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new RoomWithHUD(camera, room.getWalls()));
                dispose();
            }
        });

        backToGallery.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Back to Gallery..");
                MenuScreen.openDeviceGallery();
                dispose();
            }
        });

        stage.addActor(doneButton);
        stage.addActor(backToGallery);
    }

    @Override
    public void show() {

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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        room.onTouchDown(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        room.onTouchDrag(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

}
