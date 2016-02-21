package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
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
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Catalog;
import com.ggwp.interiordesigner.object.GameObject;
import com.ggwp.interiordesigner.object.Room;
import com.ggwp.interiordesigner.object.RoomDesignData;
import com.ggwp.interiordesigner.object.SaveFile;
import com.ggwp.interiordesigner.object.Wall;
import com.ggwp.utils.ToolUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FurnitureSetupScreen extends AppScreen {

    final static class TransformTool {
        static final int MOVE = 0;
        static final int ROTATE = 1;
        static final int PAINT = 2;
    }

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {

            GameObject obj0 = instances.get(userValue0);
            GameObject obj1 = instances.get(userValue1);

            instances.get(userValue0).collided = true;
            instances.get(userValue1).collided = true;

            if (obj0.type == GameObject.TYPE_WALL) {
                setWallMaterial((Wall) obj0);
            }

            if (obj1.type == GameObject.TYPE_WALL) {
                setWallMaterial((Wall) obj1);
            }

            if (!collidedInstances.contains(obj0, true)) {
                collidedInstances.add(obj0);
            }
            if (!collidedInstances.contains(obj1, true)) {
                collidedInstances.add(obj1);
            }

            System.out.println("contact");
            return true;
        }

        @Override
        public void onContactEnded(int userValue0, int userValue1) {
            GameObject obj0 = instances.get(userValue0);
            GameObject obj1 = instances.get(userValue1);

            if (obj0.type == GameObject.TYPE_WALL) {
                removeWallTexture((Wall) obj0);
            }

            if (obj1.type == GameObject.TYPE_WALL) {
                removeWallTexture((Wall) obj1);
            }

            collidedInstances.removeValue(obj0, false);
            collidedInstances.removeValue(obj1, false);

            System.out.println("ended");
        }
    }

    protected PerspectiveCamera camera;
    protected ModelBatch modelBatch;
    protected AssetManager assets;
    protected Environment environment;
    protected boolean loading;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Array<GameObject> instances = new Array<GameObject>();
    private Array<GameObject> collidedInstances = new Array<GameObject>();

    protected Stage stage;
    private Vector3 position = new Vector3();
    private Vector3 origPosition = new Vector3();
    private Quaternion origRotation = new Quaternion();

    private int selected = -1, selecting = -1;
    private int transformTool = 0;

    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private MyContactListener contactListener;
    private btBroadphaseInterface broadphase;
    private btCollisionWorld collisionWorld;

    final static short WALL_FLAG = 1 << 8;
    final static short FLOOR_OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;

    private float wallY = 0f;
    private float backWallHeight = 0f;

    private Vector3 pointAtWall = new Vector3();
    private Quaternion wallQuaternion = new Quaternion();

    private BlendingAttribute wallBlendingAttrib;
    private Material origWallMaterial;

    private RoomDesignData designData;
    private boolean back = false;
    private float toolsPanelYBounds;

    private float minPosX;
    private float maxPosX;

    private ModelBuilder builder = new ModelBuilder();
    private BlendingAttribute paintBlendingAttribute = new BlendingAttribute();
    private Color selectedColor;
    private Wall backWall;
    private Array<GameObject> paintableTiles = new Array<GameObject>();

    private HashMap<GameObject,SaveFile.TilePaint> paintedTiles;

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE, 40f, 65f, -50f));
        environment.add(new DirectionalLight().set(Color.WHITE, -25f, 40f, 20f));
        environment.add(new DirectionalLight().set(Color.WHITE, 90f, 40f, 20f));
    }

    private void initCamera() {
        if (camera == null) {
            camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            camera.position.set(50f, 50f, 100f);
            camera.lookAt(50, 50, 0);
            camera.near = 1f;
            camera.far = 300f;
            camera.update();
        }
    }

    private void removeScreenInputProcessor() {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor(stage);
    }
    DebugDrawer debugDrawer;
    private void init(PerspectiveCamera camera, FileHandle backgroundSource, Array<Wall> walls) {
        Bullet.init();
        this.camera = camera;
        stage = new Stage(new ScreenViewport());
        assets = new AssetManager();

        initEnvironment();
        initCamera();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));

        initHUD();

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();

        filePath.clear();
        loading = true;

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contactListener = new MyContactListener();

debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw   .DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        collisionWorld.setDebugDrawer(debugDrawer);
        wallBlendingAttrib = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        wallBlendingAttrib.opacity = 0f;

        if (walls != null) {
            origWallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.6f, 1f, 0.5f)));
            BlendingAttribute ba = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            ba.opacity = 0.4f;

            origWallMaterial.set(ba);

            for (Wall wall : walls) {
                addWall(wall);
            }
        }
        background = new Texture(backgroundSource);

        setupOverlay();
        setupPaintableTiles();
    }

    public FurnitureSetupScreen(PerspectiveCamera camera, RoomDesignData roomDesignData) {
        designData = roomDesignData;
        FileHandle backgroundSource;

        if (roomDesignData.getBackgroundImage().startsWith("C")) {
            backgroundSource = Gdx.files.absolute(roomDesignData.getBackgroundImage());
        } else if (roomDesignData.getBackgroundImage().contains(Main.DEFAULT_EMPTY_ROOM_DIR)) {
            backgroundSource = Gdx.files.internal(roomDesignData.getBackgroundImage());
        } else if (roomDesignData.getBackgroundImage().contains("/")) {
            backgroundSource = Gdx.files.absolute(roomDesignData.getBackgroundImage());
        } else {
            backgroundSource = Gdx.files.internal(Main.DEFAULT_EMPTY_ROOM_DIR + roomDesignData.getBackgroundImage());
        }

        init(camera, backgroundSource, new Room(roomDesignData).getWalls());
    }

    private void removeWallTexture(Wall wall) {
        if (wall != null) {
            for (Material mat : wall.materials) {
                mat.set(wallBlendingAttrib);
            }
            for (Material mat : wall.model.materials) {
                mat.set(wallBlendingAttrib);
            }
        }
    }

    private void setWallMaterial(Wall wall) {
        if (wall != null) {
            for (Material mat : wall.materials) {
                mat.clear();
                mat.set(origWallMaterial);
            }
            for (Material mat : wall.model.materials) {
                mat.clear();
                mat.set(origWallMaterial);
            }
        }
    }

    List<FileHandle> filePath = new ArrayList<FileHandle>();

    public static btConvexHullShape createConvexHullShape(final Model model, boolean optimize) {
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
        if (!optimize) return shape;

        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);

        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
    }

    private void removeGameObjects(Array<GameObject> objects) {
        instances.removeAll(objects, true);
    }

    private void initHUD() {
        Pixmap whitePixmap = new Pixmap(1, Gdx.graphics.getHeight() / 10, Pixmap.Format.RGBA8888);
        Color col = Color.WHITE;
        whitePixmap.setColor(Color.argb8888(col.r, col.g, col.b, 0.7f));
        whitePixmap.fill();

        Table tools = new Table();
        tools.setBackground(new SpriteDrawable(new Sprite(new Texture(whitePixmap))));
        tools.setBounds(0, (Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 10)), Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 10);

        toolsPanelYBounds = (Gdx.graphics.getHeight() / 10);
        tools.defaults().width(Gdx.graphics.getWidth() / 10).height(Gdx.graphics.getHeight() / 10);
        tools.columnDefaults(2).width(Gdx.graphics.getWidth() / 10).height(Gdx.graphics.getHeight() / 10);
        tools.columnDefaults(3).width(Gdx.graphics.getWidth() / 10).height(Gdx.graphics.getHeight() / 10);

        ImageButton addButton = createAndAddImageButtonTool(0, "Common/add.png", tools);
        ImageButton removeButton = createAndAddImageButtonTool(1, "Common/remove.png", tools);
        ImageButton moveButton = createAndAddImageButtonTool(2, "Common/move.png", tools);
        ImageButton rotateButton = createAndAddImageButtonTool(3, "Common/rotate.png", tools);
        ImageButton clearButton = createAndAddImageButtonTool(4, "Common/clear.png", tools);
        ImageButton paintButton = createAndAddImageButtonTool(5, "Common/roller.png", tools);
        ImageButton paletteButton = createAndAddImageButtonTool(6, "Common/palette.png", tools);
        ImageButton saveButton = createAndAddImageButtonTool(7, "Common/save.png", tools);
        ImageButton cancelButton = createAndAddImageButtonTool(8, "Common/cancel.png", tools);

        final Catalog catalog = Catalog.construct(stage, assets, instances, (InputMultiplexer) Gdx.input.getInputProcessor(), this);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Add Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                removeScreenInputProcessor();
                stage.addActor(catalog);
            }
        });

        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Remove Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));

                if (selected > -1) {
                    GameObject object = instances.get(selected);
                    if (object != null && object.type != GameObject.TYPE_WALL) {
                        instances.removeIndex(selected);
                        collisionWorld.removeCollisionObject(object.body);
                        selected = -1;
                    }
                }
            }
        });

        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Move Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                transformTool = TransformTool.MOVE;
            }
        });

        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Rotate Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                transformTool = TransformTool.ROTATE;
            }
        });

        paletteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addActor(overlay);
            }
        });

        paintButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transformTool = TransformTool.PAINT;
            }
        });

        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Clear Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                Array<GameObject> objects = new Array<GameObject>();
                for (GameObject gameObject : instances) {
                    if (gameObject.type != GameObject.TYPE_WALL) {
                        objects.add(gameObject);
                        collisionWorld.removeCollisionObject(gameObject.body);
                    }
                }
                selected = -1;
                removeGameObjects(objects);
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSaveButtonClicked();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new MenuScreen());
                dispose();
            }
        });

        whitePixmap.dispose();
        stage.addActor(tools);
    }

    private void onSaveButtonClicked() {
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        Color col = Color.valueOf("#3498db");
        whitePixmap.setColor(Color.argb8888(col.r, col.g, col.b, 0.7f));
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        final Dialog confirmDialog = new Dialog("", new Window.WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        confirmDialog.setModal(true);
        confirmDialog.setSize(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        ImageButton btnYes = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/submitbtn.png"))));
        ImageButton btnNo = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));

        final TextInputListener te = new TextInputListener() {
            @Override
            public void input(String text) {

                GameObject[] gobjs = new GameObject[instances.size];
                int i = 0;
                for (GameObject g : instances) {
                    gobjs[i++] = g;
                }
                ToolUtils.saveRoomSetup(text + ".dat", gobjs, designData,paintedTiles.values());

                Object[][] tests = {{"title", "Message"},
                        {"message", "File Saved."}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));

                back = true;
                confirmDialog.hide();
            }

            @Override
            public void canceled() {
                confirmDialog.hide();
            }
        };

        btnYes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.getTextInput(te, "File Name", "Room", "");
                        /*Main.getInstance().setScreen(new MenuScreen());
                        dispose();*/
            }
        });

        btnNo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmDialog.hide();
            }
        });

        Label label = new Label("Save? ", SkinManager.getDialogLabelStyle());
        label.setFontScale(2);

        Table group = new Table();
        group.pad(Gdx.graphics.getHeight() / 30);
        group.add(label);
        group.add(btnYes).height(Gdx.graphics.getHeight() / 10)
                .width(Gdx.graphics.getWidth() / 17);
        group.add(btnNo).height(Gdx.graphics.getHeight() / 10)
                .width(Gdx.graphics.getWidth() / 17);
        confirmDialog.add(group);

        confirmDialog.show(stage);
    }

    private ImageButton createAndAddImageButtonTool(Integer index, String icon, Table tools) {
        ImageButton imageButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture(icon))));
        float xPoint = index * (Gdx.graphics.getWidth() / 10);
        imageButton.setBounds(xPoint, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        tools.addActor(imageButton);
        return imageButton;
    }

    public void addObject(SaveFile.Object obj) {
        if (!assets.isLoaded(obj.assetName)) {
            assets.load(obj.assetName, Model.class);
        }

        assets.finishLoadingAsset(obj.assetName);
        Model model = assets.get(obj.assetName, Model.class);
        addObject(obj.assetName, model, obj.type, obj.val);
    }

    public void addWall(Wall wall){
        BoundingBox boundingBox = new BoundingBox();
        wall.calculateBoundingBox(boundingBox).mul(wall.transform);
        Vector3 pos = new Vector3();

        boundingBox.getCorner001(pos);
        wallY = pos.y;

        if (wall.location == Wall.LEFT) {
            minPosX = pos.x;
        }
        if (wall.location == Wall.RIGHT) {
            maxPosX = pos.x;
        }

        if (!wall.isSide()) {
            backWallHeight = wall.dimensions.y;
        }

        removeWallTexture(wall);

        wall.body = new btCollisionObject();
        wall.body.setCollisionShape(createConvexHullShape(wall.model, false));
        wall.collided = false;
        wall.body.setWorldTransform(wall.transform);
        wall.body.setUserValue(instances.size);
        wall.body.setCollisionFlags(wall.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(wall);
        collisionWorld.addCollisionObject(wall.body, WALL_FLAG, FLOOR_OBJECT_FLAG);

        instances.add(wall);

        if (wall.isSide() == false) {
            backWall = wall;
        }
    }

    public void addObject(String assetName, Model model, int type) {
        addObject(assetName, model, type, null);
    }

    float scaleFactor = 3f;

    public void addObject(String assetName, Model model, int type, float[] val) {
        BoundingBox bounds = new BoundingBox();
        model.calculateBoundingBox(bounds);

        Vector3 dimension = new Vector3();
        bounds.getDimensions(dimension);

        //Scale collision shape
       // dimension.scl(scaleFactor, scaleFactor, scaleFactor);

        dimension.x = dimension.x / 2f;
        dimension.y = dimension.y / 2f;
        dimension.z = dimension.z / 2f;

        GameObject object = new GameObject(model, new btBoxShape(dimension), type, assetName);

        if (type == GameObject.TYPE_WALL_OBJECT) {
            object.transform.translate(camera.position.x, wallY + (backWallHeight / 2), dimension.z);
        } else {
            object.transform.translate(camera.position.x, wallY + (bounds.getHeight() / 2f), dimension.z + 1);
        }

        if (val != null) {
            object.transform.set(val);
        }

        //Scale for actual size
        //object.transform.scale(scaleFactor, scaleFactor, scaleFactor);

        for(int i= 0 ;i< object.nodes.size;i++){
            Vector3 v3 = object.nodes.get(i).scale;
            object.nodes.get(i).scale.set(v3.x*scaleFactor, v3.y*scaleFactor, v3.z*scaleFactor);
        }
        object.calculateTransforms();

        BoundingBox bb = new BoundingBox();
        object.calculateBoundingBox(bb);

        bb.getCenter(object.center);
        bb.getDimensions(object.dimensions);

        //Scale for selection
        //object.dimensions.scl(scaleFactor, scaleFactor, scaleFactor);

        object.collided = false;
        object.body.setWorldTransform(object.transform);
        object.body.setUserValue(instances.size);
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);

        instances.add(object);

        if (type == GameObject.TYPE_WALL_OBJECT) {
            collisionWorld.addCollisionObject(object.body, FLOOR_OBJECT_FLAG, FLOOR_OBJECT_FLAG);
        } else {
            collisionWorld.addCollisionObject(object.body, FLOOR_OBJECT_FLAG, ALL_FLAG);
        }

        transformTool = TransformTool.MOVE;
    }


    private void doneLoading() {
        loading = false;
    }

    @Override
    public void render(float delta) {
        if (back) {
            Main.getInstance().setScreen(new MenuScreen());
            //dispose();
            back = false;
        }

        if (loading && assets.update()) {
            doneLoading();
        }

        collisionWorld.performDiscreteCollisionDetection();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (background != null) {
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        debugDrawer.begin(camera);
        collisionWorld.debugDrawWorld();
        debugDrawer.end();

        modelBatch.begin(camera);

        for (GameObject object : instances) {
            if (object instanceof Wall) {
                modelBatch.render(object);
            } else {
                modelBatch.render(object, environment);
            }
        }
        modelBatch.render(paintableTiles);
        modelBatch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenY <= toolsPanelYBounds) {
            return super.touchDown(screenX, screenY, pointer, button);
        }

        if (transformTool == TransformTool.PAINT) {
            paintSelectedTile(screenX, screenY);
        } else {
            selecting = getSelectedObject(screenX, screenY);

            System.out.println("Selecting: " + selecting);

            selected = selecting;
            if (selecting >= 0) {
                instances.get(selecting).transform.getTranslation(origPosition);
                instances.get(selecting).transform.getRotation(origRotation);
            }
        }
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screenY <= toolsPanelYBounds) {
            return super.touchDragged(screenX, screenY, pointer);
        }

        if (transformTool == TransformTool.PAINT) {
            paintSelectedTile(screenX, screenY);
            return super.touchDragged(screenX, screenY, pointer);
        }

        if (selected < 0) return false;

        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);
            GameObject gameObject = instances.get(selected);

            if (gameObject.type == GameObject.TYPE_WALL_OBJECT) {
                int selectedWallIndex = getSelectedWall(screenX, screenY);

                handleWallObjectDrag(gameObject, ray);

                Vector3 pos = new Vector3();
                gameObject.transform.getTranslation(pos);
                Wall selectedWall;

                if (selectedWallIndex >= 0 && instances.get(selectedWallIndex) != null) {
                    selectedWall = (Wall) instances.get(selectedWallIndex);

                    float halfz = (gameObject.dimensions.z / 2);
                    if (selectedWall.location == Wall.BACK) {
                        pos.z += halfz;
                    } else if (selectedWall.location == Wall.LEFT) {
                        pos.x += halfz;
                    } else {
                        pos.x -= halfz;
                    }
                }

                if (wallY > (pos.y - (gameObject.dimensions.y / 2))) {
                    pos.y = wallY + (gameObject.dimensions.y / 2);
                }
                if (backWallHeight < (pos.y + (gameObject.dimensions.y / 2))) {
                    pos.y = wallY - (gameObject.dimensions.y / 2);
                }
                gameObject.transform.setTranslation(pos);
            } else {
                handleFloorObjectDrag(gameObject, ray);
            }
            gameObject.body.setWorldTransform(gameObject.transform);
        }
        return true;
    }

    private void checkCollision(GameObject gameObject) {
        if (gameObject != null && gameObject.type != GameObject.TYPE_WALL && collidedInstances != null) {
            if (collidedInstances.contains(gameObject, true)) {
                moveToPreviousLocation(gameObject);
            }
            gameObject.body.setWorldTransform(gameObject.transform);
        }
    }

    private void moveToPreviousLocation(GameObject gameObject) {
        if (gameObject != null) {
            if (gameObject.collided) {
                if (transformTool == TransformTool.MOVE) {
                    gameObject.transform.setTranslation(origPosition);
                    gameObject.collided = false;
                } else {
                    gameObject.transform.setToTranslation(origPosition);
                    gameObject.transform.rotate(origRotation.x, origRotation.y, origRotation.z, origRotation.w);
                    gameObject.collided = false;
                }
            }
            gameObject.body.setWorldTransform(gameObject.transform);
        }
    }

    public int getSelectedWall(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;

        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);
            if (instance.type == GameObject.TYPE_WALL) {
                Wall wall = (Wall) instance;

                BoundingBox boundingBox = new BoundingBox();
                wall.calculateBoundingBox(boundingBox).mul(wall.transform);

                Vector3 cornerA = new Vector3();
                Vector3 cornerB = new Vector3();
                Vector3 cornerC = new Vector3();
                Vector3 cornerD = new Vector3();

                if (wall.location == Wall.RIGHT) {
                    boundingBox.getCorner000(cornerA);
                    boundingBox.getCorner101(cornerB);
                    boundingBox.getCorner111(cornerC);
                    boundingBox.getCorner010(cornerD);
                } else {
                    boundingBox.getCorner001(cornerA);
                    boundingBox.getCorner100(cornerB);
                    boundingBox.getCorner110(cornerC);
                    boundingBox.getCorner011(cornerD);
                }

                List<Vector3> triangles = new ArrayList<Vector3>();
                triangles.add(cornerA);
                triangles.add(cornerB);
                triangles.add(cornerC);

                triangles.add(cornerC);
                triangles.add(cornerD);
                triangles.add(cornerA);

                if (Intersector.intersectRayTriangles(ray, triangles, pointAtWall)) {
                    wall.transform.getRotation(wallQuaternion);
                    return i;
                }
            }
        }
        return result;
    }

    private void handleWallObjectDrag(GameObject gameObject, Ray ray) {
        if (transformTool == TransformTool.MOVE) {
            gameObject.transform.set(pointAtWall, wallQuaternion);
        }
    }

    private void handleFloorObjectDrag(GameObject gameObject, Ray ray) {
        float level = wallY + (gameObject.dimensions.y / 2f);
        final float distance = (level - ray.origin.y) / ray.direction.y;

        position.set(ray.direction).scl(distance).add(ray.origin);

        if (transformTool == TransformTool.MOVE) {
            if (((position.z - (gameObject.dimensions.z / 2))) < 1) {
                position.z = gameObject.dimensions.z;
            }
            if ((position.x - (gameObject.dimensions.x / 2)) < minPosX) {
                position.x = minPosX + (gameObject.dimensions.x / 2);
            }
            if ((position.x + (gameObject.dimensions.x / 2)) > maxPosX) {
                position.x = maxPosX - (gameObject.dimensions.x / 2);
            }
            gameObject.transform.setTranslation(position);
        } else {
            if (ray.direction.x > ray.origin.x) {
                gameObject.transform.rotate(Vector3.Y, -1f);
            } else {
                gameObject.transform.rotate(Vector3.Y, 1f);
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (screenY <= toolsPanelYBounds) {
            return super.touchUp(screenX, screenY, pointer, button);
        }

        if (selected >= 0) {
            checkCollision(instances.get(selected));
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    public int getSelectedObject(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float previousDistanceHolder = -1;

        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);

            if (instance.type != GameObject.TYPE_WALL) {
                instance.transform.getTranslation(position).add(instance.center);
                if (Intersector.intersectRayBoundsFast(ray, position, instance.dimensions)) {
                    if(previousDistanceHolder < 0 || previousDistanceHolder < instance.dimensions.z){
                        result = i;
                        previousDistanceHolder = instance.dimensions.z;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        collidedInstances.clear();
        spriteBatch.dispose();
        assets.dispose();
    }

    @Override
    public void show() {

    }


    @Override
    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
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

    private Table overlay;

    private void setupOverlay() {
        overlay = new Table(SkinManager.getDefaultSkin());
        overlay.setFillParent(true);
        overlay.background(new SpriteDrawable(new Sprite(new Texture("gradient-overlay.png"))));

        Table table = new Table();

        String[] hexes = new String[]{
                "#1abc9c", "#2ecc71", "#3498db", "#9b59b6", "#34495e",
                "#16a085", "#27ae60", "#2980b9", "#8e44ad", "#2c3e50",
                "#f1c40f", "#e67e22", "#e74c3c", "#ecf0f1", "#95a5a6",
                "#f39c12", "#d35400", "#c0392b", "#bdc3c7", "#7f8c8d"
        };

        float width = Gdx.graphics.getWidth() / 10;
        float height = Gdx.graphics.getHeight() / 8;

        for (int i = 0; i < hexes.length; i++) {
            final Color color = Color.valueOf(hexes[i]);
            TextButton button = new TextButton("", createButtonStyle(color));
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedColor = color;
                    overlay.remove();
                }
            });

            table.add(button).align(Align.center).width(width).height(height).pad(10f);

            if ((i + 1) % 5 == 0) {
                table.row();
            }
        }

        overlay.add(table);
    }

    private TextButton.TextButtonStyle createButtonStyle(Color color) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        Pixmap map = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        map.setColor(color);
        map.fill();
        SkinManager.getDefaultSkin().add(color.toString(), new Texture(map));
        textButtonStyle.up = SkinManager.getDefaultSkin().getDrawable(color.toString());
        textButtonStyle.font = SkinManager.getDefaultSkin().getFont("defaultFont");
        return textButtonStyle;
    }

    private void setupPaintableTiles() {
        float horizontalTileCount = 8;
        float verticalTileCount = 8;

        paintedTiles = new HashMap<GameObject,SaveFile.TilePaint>();

        BoundingBox box = new BoundingBox();
        backWall.calculateBoundingBox(box).mul(backWall.transform);

        float tileHeight = box.getHeight() / verticalTileCount;
        float tileWidth = box.getWidth() / horizontalTileCount;

        for (int h = 0; h < horizontalTileCount; h++) {
            for (int v = 0; v < verticalTileCount; v++) {

                Vector3 corner = new Vector3();
                box.getCorner000(corner);

                float startH = tileWidth * h + corner.x;
                float startV = tileHeight * v + corner.y;

                Material material = new Material(ColorAttribute.createDiffuse(Color.CLEAR));
                paintBlendingAttribute.opacity = 0f;
                material.set(paintBlendingAttribute);

                Model model = builder.createRect(
                        startH, startV, backWall.center.z,
                        startH + tileWidth, startV, backWall.center.z,
                        startH + tileWidth, startV + tileHeight, backWall.center.z,
                        startH, startV + tileHeight, backWall.center.z,
                        1, 1, 1,
                        material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                );
                GameObject object = new GameObject(model, -1);
                paintableTiles.add(object);
            }
        }
    }

    public int paintSelectedTile(SaveFile.TilePaint tpaint) {
        selectedColor = Color.valueOf(tpaint.color);
        return paintSelectedTile(tpaint.screenX,tpaint.screenY);
    }

    public int paintSelectedTile(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;

        for (int i = 0; i < paintableTiles.size; i++) {
            GameObject instance = paintableTiles.get(i);
            BoundingBox box = new BoundingBox();
            instance.calculateBoundingBox(box);

            if (Intersector.intersectRayBoundsFast(ray, box)) {
                result = i;
                for (Material material : instance.materials) {
                    material.set(ColorAttribute.createDiffuse(selectedColor));
                    paintBlendingAttribute.opacity = 0.6f;
                    material.set(paintBlendingAttribute);
                }
                SaveFile.TilePaint tilePaint = new SaveFile.TilePaint(selectedColor.toString(),screenX,screenY);
                paintedTiles.put(instance, tilePaint);
            }
        }

        if (result < 0) {
            int wallIndex = getSelectedWall(screenX, screenY);

            if (wallIndex >= 0) {
                Wall wall = (Wall) instances.get(wallIndex);

                for (Material material : wall.materials) {
                    material.set(ColorAttribute.createDiffuse(selectedColor));
                    paintBlendingAttribute.opacity = 0.6f;
                    material.set(paintBlendingAttribute);
                }
                SaveFile.TilePaint tilePaint = new SaveFile.TilePaint(selectedColor.toString(),screenX,screenY);
                paintedTiles.put(wall, tilePaint);
            }
        }
        return result;
    }

}