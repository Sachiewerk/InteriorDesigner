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
import com.ggwp.interiordesigner.object.AppScreen;

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


    private AssetManager assets;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    public RoomSetupScreen(){
        sprithBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        font = new BitmapFont();
        viewport = new FitViewport(800, 480, camera);

        System.out.println(Main.aoi.getScreenTemplateDir());

        FileHandle[] tmplates = Gdx.files.absolute(Main.aoi.getScreenTemplateDir()).list();
        /*File[] files = new File[tmplates.length];
        int i = 0;
        for (FileHandle fh:tmplates) {
            System.out.println(fh.file().getName());
            files[i++] = fh.file();
        }*/

        Arrays.sort(tmplates, new Comparator<FileHandle>() {
            public int compare(FileHandle f1, FileHandle f2) {
                // sort latest first
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        for (FileHandle fhx:tmplates) {
            System.out.println(fhx.file().getName());
        }

        if(tmplates.length==0) {
            roomImage = new Texture(Gdx.files.internal("Rooms/room2.jpg"));
        }
        else{
            roomImage = new Texture(tmplates[0]);
        }

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
