package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class RectangleTest extends InputAdapter implements Screen {

    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController cameraInputController;
    private ShapeRenderer shapeRenderer;

    private ModelBatch modelBatch;
    private ModelInstance sofa;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Array<ModelInstance> walls = new Array<ModelInstance>();
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    public AssetManager assets;
    public boolean loading;

    public RectangleTest () {
        spriteBatch = new SpriteBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 100f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        assets = new AssetManager();
        assets.load("sofa.obj", Model.class);
        loading = true;

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(this);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model square = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instances.add(new ModelInstance(square));

        createWalls(modelBuilder);
    }

    private void createWalls(ModelBuilder modelBuilder) {
        Color dodgerBlue = new Color(0.2f, 0.6f, 1f, 0.5f);

        BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.5f;

        Material material = new Material(ColorAttribute.createDiffuse(dodgerBlue));
        material.set(blendingAttribute);

        float halfWallHeight = 50;
        float halfWallWidth = 50;

        Model leftWallModel = modelBuilder.createRect(
                -(halfWallWidth * 3), -halfWallHeight, 100,
                -halfWallWidth, -halfWallHeight, 0,
                -halfWallWidth, halfWallHeight, 0,
                -(halfWallWidth * 3), halfWallHeight, 100,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance leftWallModelInstance = new ModelInstance(leftWallModel);
        walls.add(leftWallModelInstance);
        instances.add(leftWallModelInstance);

        Model rightWallModel = modelBuilder.createRect(
                halfWallWidth, -halfWallHeight, 0,
                (halfWallWidth * 3), -halfWallHeight, 100,
                (halfWallWidth * 3), halfWallHeight, 100,
                halfWallWidth, halfWallHeight, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance rightWallModelInstance = new ModelInstance(rightWallModel);
        walls.add(rightWallModelInstance);
        instances.add(rightWallModelInstance);

        blendingAttribute.opacity = 0.8f;
        material.set(blendingAttribute);

        Model backWallModel = modelBuilder.createRect(
                -halfWallWidth, -halfWallHeight, 0,
                halfWallWidth, -halfWallHeight, 0,
                halfWallWidth, halfWallHeight, 0,
                -halfWallWidth, halfWallHeight, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance backWallModelInstance = new ModelInstance(backWallModel);
        walls.add(backWallModelInstance);
        instances.add(backWallModelInstance);
    }

    private void doneLoading() {
        sofa = new ModelInstance(assets.get("sofa.obj", Model.class));
        sofa.transform.setToRotation(Vector3.Y, 180).trn(0, 0, 6f);
        instances.add(sofa);

        background = new Texture(Gdx.files.internal("Rooms/room2.jpg"));
        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update()){
            doneLoading();
        }

        cameraInputController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(0.2f, 0.6f, 1f, 1f);
//        shapeRenderer.rect(-25, -25, 50, 50);
//        float points[] = new float[]{
//                -25, -25,
//                25, -25,
//                25, 25,
//                -25, 25
//        };
//        shapeRenderer.polygon(points);
//        shapeRenderer.end();

        if(background != null){
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }


        modelBatch.begin(camera);
        modelBatch.render(walls, environment);
        modelBatch.end();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        walls.clear();
        spriteBatch.dispose();
        assets.dispose();
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
        Ray ray = camera.getPickRay(screenX, screenY);
        float distanceY = -ray.origin.y / ray.direction.y;
        float distanceX = -ray.origin.x / ray.direction.x;
        float distanceZ = -ray.origin.z / ray.direction.z;

        System.out.println("X: " + distanceX);
        System.out.println("Y: " + distanceY);
        System.out.println("Z: " + distanceZ);

        return super.touchDown(screenX, screenY, pointer, button);
    }
}
