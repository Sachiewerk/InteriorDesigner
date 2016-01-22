package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListner;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.AppScreens;
import com.ggwp.utils.ToolUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * Created by Raymond on 1/17/2016.
 */
public class RoomSetupScreen extends AppScreen{

    private Viewport viewport;
    private PerspectiveCamera  camera;
    private SpriteBatch sprithBatch;
    private ModelBatch modelBatch;
    private Texture roomImage;
    public BitmapFont font;
    private ModelInstance wallLeft;
    private ModelInstance wallRight;
    private ModelInstance wallBack;
    private ModelInstance floor;
    private CameraInputController cameraController;
    private FileHandle roomTemplateImageSource;


    private AssetManager assets;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    public RoomSetupScreen(){
        sprithBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        font = new BitmapFont();
        viewport = new FitViewport(800, 480, camera);



        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 30f, 1f);
        camera.lookAt(0, 0, 0);
//        camera.near = 0.1f;
//        camera.far = 600f;
        camera.update();

        cameraController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraController);

//        ModelBuilder modelBuilder = new ModelBuilder();

        assets = new AssetManager();
        assets.load("sofa.obj", Model.class);
        assets.finishLoading();

        Model model = assets.get("sofa.obj", Model.class);
        ModelInstance inst = new ModelInstance(model);
        inst.transform.rotate(Vector3.Y, 270f);
        inst.transform.rotate(Vector3.X, 180f);
        inst.transform.rotate(Vector3.Z, 180f);
//        inst.transform.translate(-10f,0,0f);

        instances.add(inst);

//        Model modelWallLeft =  modelBuilder.createBox(.5f, 10f, 10f,
//                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//
//        Model modelWallRight =  modelBuilder.createBox(.5f, 10f, 10f,
//                new Material(ColorAttribute.createDiffuse(Color.GRAY)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


//        wallLeft = new ModelInstance(modelWallLeft);
//        wallLeft.transform.setToRotation(Vector3.Y, 30f);
//        wallLeft.transform.setToTranslation(0f, 0f, 10f);
//        wallRight = new ModelInstance(modelWallRight);
//        wallRight.transform.setToRotation(Vector3.Y, 60f);
//        wallRight.transform.setToTranslation(0f, 0f, 0f);

//        wallRight = new ModelInstance(model);
//
//
//
//        floor = new ModelInstance(model);

        flagImageUpdate=true;
        updateImage();

        Main.aoi.addResultListener(new RequestResultListner() {
            @Override
            public void OnRequestDone(Object result) {
                        /*paramValues.put("path",path);
                        paramValues.put("result",result);*/
                String result1 = ToolUtils.getParamValue(result, String.class, "result");
                if (result1.equals("OK")) {
                    String path = ToolUtils.getParamValue(result, String.class, "path");
                    Object[][] tests = {{"title", "test error"},
                            {"message", "test message"}};
                    Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                            ToolUtils.createMapFromList(tests));

                    roomTemplateImageSource = ToolUtils.fetchLatestSnapshot();
                    flagImageUpdate = true;
                }

            }

            @Override
            public AndroidOnlyInterface.RequestType getRequestType() {
                return AndroidOnlyInterface.RequestType.IMAGE_CAPTURE;
            }
        });


        /*Object[][] tests = {{"savedirectory", Main.screenTemplateSaveDirectory}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.IMAGE_CAPTURE,
                ToolUtils.createMapFromList(tests));*/

        Main.aoi.addResultListener(new RequestResultListner() {
            @Override
            public void OnRequestDone(Object result) {
                        /*paramValues.put("path",path);
                        paramValues.put("result",result);*/
                String result1 = ToolUtils.getParamValue(result, String.class, "imagepath");

                roomTemplateImageSource = ToolUtils.findFileByAbsolutePath(result1);
                flagImageUpdate = true;

            }

            @Override
            public AndroidOnlyInterface.RequestType getRequestType() {
                return AndroidOnlyInterface.RequestType.GET_IMAGE_FROM_GALLERY;
            }
        });


        /*Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.GET_IMAGE_FROM_GALLERY,
                null);*/

        //ObjectCatalog.getCurrentInstance().show(stage);

    }

    boolean flagImageUpdate = false;
    private void updateImage(){


        if(flagImageUpdate==false){
            return;
        }

        Object[][] tests = {{"title", "test error"},
                {"message", "updating image"}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
       /* Object[][] tests = {{"title", "test error"},
                {"message", tmplates[0]}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));*/

        if(roomTemplateImageSource==null) {
            roomImage = new Texture(Gdx.files.internal("Rooms/room2.jpg"));
        }
        else{
            roomImage = new Texture(roomTemplateImageSource);
        }
        flagImageUpdate = false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        updateImage();
        Object[][] tests = {{"title", "test error"},
                {"message", "rendering.."}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cameraController.update();
        sprithBatch.begin();
        sprithBatch.draw(roomImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.draw(sprithBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 20, 30);
        sprithBatch.end();

        modelBatch.begin(camera);
        modelBatch.render(instances);
        modelBatch.end();


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
