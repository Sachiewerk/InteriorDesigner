package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by Raymond on 1/17/2016.
 */
public class RoomSetupScreen implements Screen {

    private Viewport viewport;
    private PerspectiveCamera  camera;
    private SpriteBatch sprithBatch;
    private ModelBatch modelBatch;
    private Texture roomImage;

    private ModelInstance wallLeft;
    private ModelInstance wallRight;
    private ModelInstance wallBack;
    private ModelInstance floor;
    private CameraInputController cameraController;


    private AssetManager assets;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    public RoomSetupScreen(){
        sprithBatch = new SpriteBatch();
        modelBatch = new ModelBatch();

        viewport = new FitViewport(800, 480, camera);
        roomImage = new Texture(Gdx.files.internal("Rooms/room2.jpg"));


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

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        cameraController.update();
        sprithBatch.begin();
        sprithBatch.draw(roomImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
