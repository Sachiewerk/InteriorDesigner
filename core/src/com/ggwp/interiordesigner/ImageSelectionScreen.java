package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListener;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.utils.ToolUtils;

public class ImageSelectionScreen extends AppScreen{

    private FileHandle roomTemplateImageSource;

    private boolean flagImageUpdate = false;
    private boolean fromCamera = false;
    private ImageButton backButton;
    private Stage stage;
    private long startTime;

    public ImageSelectionScreen(){
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        flagImageUpdate = true;

        Main.aoi.addResultListener(new RequestResultListener() {
            @Override
            public void OnRequestDone(Object result) {
                String result1 = ToolUtils.getParamValue(result, String.class, "result");
                if (result1.equals("OK")) {
                    String path = ToolUtils.getParamValue(result, String.class, "path");
                    Object[][] tests = {{"title", "test error"},
                            {"message", "test message"}};
                    Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                            ToolUtils.createMapFromList(tests));

                    roomTemplateImageSource = ToolUtils.fetchLatestSnapshot();
                    flagImageUpdate = true;
                    fromCamera = true;
                }

            }

            @Override
            public AndroidOnlyInterface.RequestType getRequestType() {
                return AndroidOnlyInterface.RequestType.IMAGE_CAPTURE;
            }
        });


        Main.aoi.addResultListener(new RequestResultListener() {
            @Override
            public void OnRequestDone(Object result) {
                String result1 = ToolUtils.getParamValue(result, String.class, "imagepath");
                roomTemplateImageSource = ToolUtils.findFileByAbsolutePath(result1);
                flagImageUpdate = true;
                fromCamera = false;
            }

            @Override
            public AndroidOnlyInterface.RequestType getRequestType() {
                return AndroidOnlyInterface.RequestType.GET_IMAGE_FROM_GALLERY;
            }
        });


        Label l = new Label("Failed to load image file.. ", SkinManager.getDialogLabelStyle());
        l.setFontScale(1.5f);
        l.getStyle().fontColor = Color.WHITE;

        l.setBounds((Gdx.graphics.getWidth() / 2) - 200f, (Gdx.graphics.getHeight() / 2) + 50f, 100f, 50f);
        backButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));
        backButton.setBounds((Gdx.graphics.getWidth() / 2) - 50f, (Gdx.graphics.getHeight() / 2) - 50f, 100f, 50f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new MenuScreen());
                dispose();
            }
        });


        stage.addActor(l);
        stage.addActor(backButton);
    }

    @Override
    public void show() {
            startTime = TimeUtils.millis();
    }

    @Override
    public void render(float delta) {
        if(flagImageUpdate && roomTemplateImageSource != null){
            Main.getInstance().setScreen(new RoomSetupScreen(roomTemplateImageSource, fromCamera));
            dispose();
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        if (TimeUtils.millis()>(startTime+4000)) {
            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

            stage.getBatch().begin();
            stage.getBatch().end();

            stage.draw();
        }
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
