package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListner;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.utils.ToolUtils;

public class ImageSelectionScreen extends AppScreen{

    private FileHandle roomTemplateImageSource;

    private boolean flagImageUpdate = false;
    private boolean fromCamera = false;

    public ImageSelectionScreen(){
        flagImageUpdate = true;

        Main.aoi.addResultListener(new RequestResultListner() {
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


        Main.aoi.addResultListener(new RequestResultListner() {
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
    }

    @Override
    public boolean OnBackPressed() {
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(flagImageUpdate && roomTemplateImageSource != null){
            dispose();
            Main.getInstance().setScreen(new RoomSetupScreen(roomTemplateImageSource, fromCamera));
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
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
