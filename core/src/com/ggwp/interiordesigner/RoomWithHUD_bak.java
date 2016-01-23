package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Box;
import com.ggwp.interiordesigner.object.Catalog;
import com.ggwp.interiordesigner.object.EmptyRoomSelector;
import com.ggwp.interiordesigner.object.Furniture;
import com.ggwp.interiordesigner.object.Wall;

/**
 * Created by Raymond on 1/19/2016.
 */
public class RoomWithHUD_bak extends AppScreen  {

    final static class TransformTool {
        static final int MOVE = 0;
        static final int ROTATE = 1;
    }

    public static class GameObject extends ModelInstance {
        public final Vector3 center = new Vector3();
        public final Vector3 dimensions = new Vector3();
        public final float radius;

        private final static BoundingBox bounds = new BoundingBox();

        public GameObject(Model model){
            super(model);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }

        public GameObject (Model model, String rootNode, boolean mergeTransform) {
            super(model, rootNode, mergeTransform);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }
    }

    protected PerspectiveCamera camera;
    protected CameraInputController camController;
    protected SpriteBatch batch;
    protected ModelBatch modelBatch;
    protected AssetManager assets;
    protected Environment environment;
    protected boolean loading;
    private ShapeRenderer shapeRenderer;
    private Furniture sofa;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    protected Stage stage;
    private Vector3 position = new Vector3();

    private int selected = -1, selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    private Window catalogWindow;

    private void initEnvironment(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initCamera(){
        if(camera == null){
            camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(50f, 50f, 100f);
            camera.lookAt(50, 50, 0);
            camera.near = 1f;
            camera.far = 300f;
            camera.update();
        }

        camController = new CameraInputController(camera);
    }

    private void removeScreenInputProcessor(){
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor(stage);
    }

    private int tranformTool = 0;

    public RoomWithHUD_bak(){
        this(null, null);
    }

    public RoomWithHUD_bak(PerspectiveCamera camera, Array<Wall> walls){
        this.camera = camera;
        stage = new Stage(new ScreenViewport());
        assets = new AssetManager();

        initEnvironment();
        initCamera();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        initHUD();

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        assets.load("sofa.obj", Model.class);
        loading = true;

        ModelBuilder modelBuilder = new ModelBuilder();
        Model square = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instances.add(new ModelInstance(square));

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();

        if(walls != null){
            instances.addAll(walls);
        }

        final EmptyRoomSelector c = EmptyRoomSelector.construct(stage);

        removeScreenInputProcessor();
        stage.addActor(c);
    }

    private void initHUD(){
        ImageButton addButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/add.png"))));
        ImageButton removeButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/remove.png"))));
        ImageButton moveButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/move.png"))));
        ImageButton rotateButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/rotate.png"))));

        addButton.setBounds(0,10f,40f,40f);
        removeButton.setBounds(60f, 10f, 40f, 40f);
        moveButton.setBounds(120, 10f, 40f, 40f);
        rotateButton.setBounds(180f, 10f, 40f, 40f);

       final Catalog c = Catalog.construct(stage,assets,instances, (InputMultiplexer) Gdx.input.getInputProcessor(),this);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeScreenInputProcessor();
                stage.addActor(c);
            }
        });

        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected > -1) {
                    ModelInstance instance = instances.get(selected);
                    if (instance != null) {
                        instances.removeIndex(selected);
                        selected = -1;
                    }
                }
            }
        });

        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tranformTool = TransformTool.MOVE;
            }
        });

        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tranformTool = TransformTool.ROTATE;
            }
        });

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.rgba8888(1f, 1f, 1f, .5f));
        whitePixmap.fill();

        Table tools = new Table();
        tools.setBackground(new SpriteDrawable(new Sprite(new Texture(whitePixmap))));
        tools.setBounds(0, Gdx.graphics.getHeight() - 60f, Gdx.graphics.getWidth(), 60f);

        tools.defaults().pad(10f);
        tools.defaults().width(40f).height(40f);
        tools.columnDefaults(2).width(40f).height(40f);
        tools.columnDefaults(3).width(40f).height(40f);

        tools.addActor(addButton);
        tools.addActor(removeButton);
        tools.addActor(moveButton);
        tools.addActor(rotateButton);

        whitePixmap.dispose();
        stage.addActor(tools);
    }

    private void doneLoading () {
        sofa = new Furniture(assets.get("sofa.obj", Model.class));
//        sofa.transform.rotate(Vector3.X, -90);
//        sofa.transform.scale(1f, .1f, 1f);

        sofa.calculateTransforms();
        BoundingBox bounds = new BoundingBox();
        sofa.calculateBoundingBox(bounds);
        System.out.println(bounds.getHeight());
        sofa.transform.setToTranslation(0, bounds.getHeight(), 0);

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
//            spriteBatch.draw(hudBackgroud, 0, Gdx.graphics.getHeight() - 60f, Gdx.graphics.getWidth(), 60f);
            spriteBatch.end();
        }


        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
        stage.act();
        stage.draw();

    }

    protected boolean isVisible (final Camera camera, final GameObject instance) {
        instance.transform.getTranslation(position);
        position.add(instance.center);
        return camera.frustum.sphereInFrustum(position, instance.radius);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (selecting < 0)
            return false;
        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);
            final float distance = -ray.origin.y / ray.direction.y;
            position.set(ray.direction).scl(distance).add(ray.origin);
            if(tranformTool == TransformTool.MOVE){
                BoundingBox boundingBox = new BoundingBox();
                instances.get(selected).calculateBoundingBox(boundingBox);

                position.y = boundingBox.getHeight();
                instances.get(selected).transform.setTranslation(position);
            }else{
                if(ray.direction.x > ray.origin.x){
                    instances.get(selected).transform.rotate(Vector3.Z,-3f);
                }else{
                    instances.get(selected).transform.rotate(Vector3.Z,3f);
                }
            }

        }
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY))
                setSelected(selecting);
            selecting = -1;
            return true;
        }
        return false;
    }

    public void setSelected (int value) {
        if (selected == value) return;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            mat.clear();
            mat.set(originalMaterial);
        }
        selected = value;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
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

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        spriteBatch.dispose();
        assets.dispose();
    }

    @Override
    public void show() {

    }


    @Override
    public void resize (int width, int height) {
//        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void hide() {

    }
}