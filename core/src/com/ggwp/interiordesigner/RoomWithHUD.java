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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Box;
import com.ggwp.interiordesigner.object.Furniture;

/**
 * Created by Raymond on 1/19/2016.
 */
public class RoomWithHUD extends AppScreen  {

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

    protected PerspectiveCamera cam;
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

    private Array<ModelInstance> walls = new Array<ModelInstance>();
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    protected Stage stage;
    private Vector3 position = new Vector3();

    private int selected = -1, selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    private Texture hudBackgroud;
    private Window catalogWindow;

    private void initEnvironment(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initCamera(){
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 7f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
    }

    private void initInputProcessors(){
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    private void removeScreenInputProcessor(){
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor(stage);
    }

    private Skin defaultSkin;
    private TextButton.TextButtonStyle defaultTextButtonStyle;
    private Texture whiteTexture;
    private Texture blackTexture;

    private int tranformTool = 0;

    public RoomWithHUD(){
        initEnvironment();
        initCamera();
        initHUD();

        spriteBatch = new SpriteBatch();

        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 50f, 100f);
        cam.lookAt(0, 50, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        assets = new AssetManager();
        assets.load("sofa.obj", Model.class);
        loading = true;

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
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

    private void initSkins(){
        defaultSkin = new Skin();
        Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackPixmap.setColor(Color.BLACK);
        blackPixmap.fill();
        blackTexture = new Texture(blackPixmap);

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.rgba8888(1f, 1f, 1f, .5f));
        whitePixmap.fill();
        whiteTexture = new Texture(whitePixmap);


        defaultSkin.add("defaultButton", new Texture(whitePixmap));
        BitmapFont bitmapFont = new BitmapFont();
        defaultSkin.add("defaultFont", bitmapFont);
        blackPixmap.dispose();
        whitePixmap.dispose();
        defaultTextButtonStyle = new TextButton.TextButtonStyle();
//        defaultTextButtonStyle.up = defaultSkin.newDrawable("defaultButton");
//        defaultTextButtonStyle.down = defaultSkin.newDrawable("defaultButton");
        defaultTextButtonStyle.font = defaultSkin.getFont("defaultFont");
        defaultTextButtonStyle.fontColor = Color.BLACK;
        defaultSkin.add("default", defaultTextButtonStyle);
    }

    private void initHUD(){
        stage = new Stage();
        initSkins();

        hudBackgroud = whiteTexture;

        ImageButton addButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/add.png"))));
        ImageButton removeButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/remove.png"))));
        ImageButton moveButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/move.png"))));
        ImageButton rotateButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/rotate.png"))));

        addButton.setBounds(0,10f,40f,40f);
        removeButton.setBounds(60f,10f,40f,40f);
        moveButton.setBounds(120,10f,40f,40f);
        rotateButton.setBounds(180f,10f,40f,40f);


        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeScreenInputProcessor();
                stage.addActor(catalogWindow);
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

        moveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tranformTool = TransformTool.MOVE;
            }
        });

        rotateButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tranformTool = TransformTool.ROTATE;
            }
        });

        Table tools = new Table();
        tools.setDebug(true);
        tools.setBackground(new SpriteDrawable(new Sprite(whiteTexture)));
        tools.setBounds(0, Gdx.graphics.getHeight() - 60f, Gdx.graphics.getWidth(), 60f);

        tools.defaults().pad(10f);
        tools.defaults().width(40f).height(40f);
        tools.columnDefaults(2).width(40f).height(40f);
        tools.columnDefaults(3).width(40f).height(40f);

        tools.addActor(addButton);
        tools.addActor(removeButton);
        tools.addActor(moveButton);
        tools.addActor(rotateButton);

        stage.addActor(tools);
//        stage.addActor(removeFurniture);
//        stage.addActor(moveButton);
//        stage.addActor(rotateButton);
        initCatalogWindow();
    }

    private Container createCategoryContainer(String label, String img, TextButton.TextButtonStyle buttonStyle, EventListener listener){
        Table table = new Table();
        Image image = new Image((new Texture(img)));
        TextButton button = new TextButton(label,buttonStyle);
        button.getLabel().setColor(Color.WHITE);
        button.getLabel().setAlignment(Align.left);
        if(listener != null){
            button.addListener(listener);
        }

        table.columnDefaults(0).center().pad(10f);
        table.columnDefaults(1).left().padRight(10f);

        table.add(image).width(40f).height(40f);
        table.add(button).width(180f).height(40f);
        table.setDebug(true);
        Container container = new Container(table);
        return  container;
    }

    private void initCatalogWindow(){
        TextButton closeWindow = new TextButton("Close", defaultTextButtonStyle);

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);

        Window.WindowStyle windowStyle = new Window.WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture)));
        catalogWindow = new Window("", windowStyle);
        catalogWindow.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        catalogWindow.setModal(true);

        VerticalGroup categories = new VerticalGroup();
        categories.align(Align.topLeft);
        categories.setDebug(true);

        EventListener sofaClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Furniture sofa = new Furniture(assets.get("sofa.obj", Model.class));
                sofa.transform.rotate(Vector3.X, -90);
                sofa.calculateTransforms();
                BoundingBox bounds = new BoundingBox();
                sofa.calculateBoundingBox(bounds);
                sofa.shape = new Box(bounds);
                instances.add(sofa);
                stage.getActors().removeValue(catalogWindow, true);
                initInputProcessors();
            }
        };

        categories.addActor(createCategoryContainer("Frames", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Bed with pillow/mattresses","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Side Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Vase","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Lamps", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Dresser Cabinets/Drawers", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Vanity Tables and Chairs", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Sofa Set/Couch", "Common/no-image.png", defaultTextButtonStyle, sofaClikListener));
        categories.addActor(createCategoryContainer("Coffee Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Tv Rack","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Book Shelves","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Mirrors","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Dining Set","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Kitchen Cabinets","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Wall Clock", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("TV","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Washing Machine","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Electric Fan","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Aircon","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Refridgerator","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Oven","Common/no-image.png",defaultTextButtonStyle,null));

        catalogWindow.align(Align.left);

        ScrollPane scrollPane = new ScrollPane(categories);

        Table table = new Table();
        table.setFillParent(true);
        table.defaults().left();

        table.columnDefaults(0).width(250f);
        table.columnDefaults(1).width(Gdx.graphics.getWidth() - 250f);

        table.add(scrollPane);
        table.add(closeWindow);

        catalogWindow.add(table);

        closeWindow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getActors().removeValue(catalogWindow, true);
                initInputProcessors();
            }
        });
    }

    private void doneLoading () {
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
//            spriteBatch.draw(hudBackgroud, 0, Gdx.graphics.getHeight() - 60f, Gdx.graphics.getWidth(), 60f);
            spriteBatch.end();
        }


        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        stage.draw();

    }

    protected boolean isVisible (final Camera cam, final GameObject instance) {
        instance.transform.getTranslation(position);
        position.add(instance.center);
        return cam.frustum.sphereInFrustum(position, instance.radius);
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
            Ray ray = cam.getPickRay(screenX, screenY);
            final float distance = -ray.origin.y / ray.direction.y;
            position.set(ray.direction).scl(distance).add(ray.origin);
            if(tranformTool == TransformTool.MOVE){
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
        Ray ray = cam.getPickRay(screenX, screenY);
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
        walls.clear();
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