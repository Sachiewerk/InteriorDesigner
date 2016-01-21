package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.object.Box;
import com.ggwp.interiordesigner.object.Furniture;


public class RectangleTest extends InputAdapter implements Screen {

    private Environment environment;
    private PerspectiveCamera camera;
    private CameraInputController cameraInputController;
    private ShapeRenderer shapeRenderer;

    private ModelBatch modelBatch;
    private Furniture sofa;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Array<ModelInstance> walls = new Array<ModelInstance>();
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    public AssetManager assets;
    public boolean loading;

    private int selected = -1, selecting = -1;
    private Vector3 position = new Vector3();

    private Material selectionMaterial;
    private Material originalMaterial;

    public RectangleTest () {
        spriteBatch = new SpriteBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 50f, 100f);
        camera.lookAt(0, 50, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        assets = new AssetManager();
        assets.load("sofa.obj", Model.class);
        loading = true;

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(this));
//        Gdx.input.setInputProcessor(this);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model square = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instances.add(new ModelInstance(square));

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();

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
                -(halfWallWidth * 3), 0, 100,
                -halfWallWidth, 0, 0,
                -halfWallWidth, halfWallHeight * 2, 0,
                -(halfWallWidth * 3), halfWallHeight * 2, 100,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance leftWallModelInstance = new ModelInstance(leftWallModel);
        walls.add(leftWallModelInstance);
        instances.add(leftWallModelInstance);

        Model rightWallModel = modelBuilder.createRect(
                halfWallWidth, 0, 0,
                (halfWallWidth * 3), 0, 100,
                (halfWallWidth * 3), halfWallHeight * 2, 100,
                halfWallWidth, halfWallHeight * 2, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance rightWallModelInstance = new ModelInstance(rightWallModel);
        walls.add(rightWallModelInstance);
        instances.add(rightWallModelInstance);

        blendingAttribute.opacity = 0.8f;
        material.set(blendingAttribute);

        Model backWallModel = modelBuilder.createRect(
                -halfWallWidth, 0, 0,
                halfWallWidth, 0, 0,
                halfWallWidth, halfWallHeight * 2, 0,
                -halfWallWidth, halfWallHeight * 2, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        ModelInstance backWallModelInstance = new ModelInstance(backWallModel);
        walls.add(backWallModelInstance);
        instances.add(backWallModelInstance);
    }

    private void doneLoading() {
        sofa = new Furniture(assets.get("sofa.obj", Model.class));

        sofa.transform.rotate(Vector3.X, -90);
//        sofa.transform.scale(3, 3, 3);

        sofa.calculateTransforms();
        BoundingBox bounds = new BoundingBox();
        sofa.calculateBoundingBox(bounds);
        sofa.shape = new Box(bounds);

//        sofa.transform.setToTranslation(0f,bounds.getHeight() - 50f ,0f);

        instances.add(sofa);

        background = new Texture(Gdx.files.internal("Rooms/room2.jpg"));
        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update()){
            doneLoading();
        }

//        cameraInputController.update();

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
        modelBatch.render(instances, environment);
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
//        Ray ray = camera.getPickRay(screenX, screenY);
//        float distanceY = -ray.origin.y / ray.direction.y;
//        float distanceX = -ray.origin.x / ray.direction.x;
//        float distanceZ = -ray.origin.z / ray.direction.z;
//
//        System.out.println("X: " + distanceX);
//        System.out.println("Y: " + distanceY);
//        System.out.println("Z: " + distanceZ);
//
//        return super.touchDown(screenX, screenY, pointer, button);

//        System.out.println("x = " + screenX);
//        System.out.println("y = " + screenY);
        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (selecting < 0) return false;
        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);


            final float distance = -ray.origin.y / ray.direction.y;

            System.out.println(distance);

            position.set(ray.direction).scl(distance).add(ray.origin);

//            if(instances.get(selected) instanceof  Furniture){
//                Furniture furniture = (Furniture) instances.get(selected);
//                BoundingBox bounds = new BoundingBox();
//                furniture.calculateBoundingBox(bounds);
//                position.set(position.x,bounds.getHeight() - 50f,position.z);
//            }


            instances.get(selected).transform.setTranslation(position);

        }
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY)) setSelected(selecting);
            selecting = -1;
            return true;
        }
        return false;
    }

    public void setSelected (int value) {
        if (selected == value) return;
        if (selected >= 0) {
            for(Material mat : instances.get(selected).materials){
                mat.clear();
                mat.set(originalMaterial);
            }
        }
        selected = value;
        if (selected >= 0) {
            for(Material mat : instances.get(selected).materials){
                originalMaterial.clear();
                originalMaterial.set(mat);
                mat.clear();
                mat.set(selectionMaterial);
            }

        }
    }

    public int getObject (int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < instances.size; ++i) {
            if(instances.get(i) instanceof  Furniture){
                final Furniture instance = (Furniture) instances.get(i);
                float dist2 = instance.intersects(ray);
                if (dist2 >= 0 && (distance < 0f || dist2 < distance)) {
                    result = i;
                    distance = dist2;
                }
            }
        }
        return result;
    }
}
