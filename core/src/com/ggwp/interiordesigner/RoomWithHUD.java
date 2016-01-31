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
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
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
import java.util.List;

/**
 * Created by Raymond on 1/19/2016.
 */
public class RoomWithHUD extends AppScreen  {

    final static class TransformTool {
        static final int MOVE = 0;
        static final int ROTATE = 1;
    }

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {

            GameObject obj0 = instances.get(userValue0);
            GameObject obj1 = instances.get(userValue1);

            instances.get(userValue0).collided = true;
            instances.get(userValue1).collided = true;

            if(obj0.type == GameObject.TYPE_WALL){
                setWallMaterial((Wall) obj0);
            }

            if(obj1.type == GameObject.TYPE_WALL){
                setWallMaterial((Wall) obj1);
            }


            if(!collidedInstances.contains(obj0, true)){
                collidedInstances.add(obj0);
            }
            if(!collidedInstances.contains(obj1, true)){
                collidedInstances.add(obj1);
            }



            System.out.println("contact");
            return true;
        }

        @Override
        public void onContactEnded(int userValue0, int userValue1) {
            GameObject obj0 = instances.get(userValue0);
            GameObject obj1 = instances.get(userValue1);

            if(obj0.type == GameObject.TYPE_WALL){
                removeWallTexture((Wall) obj0);
            }

            if(obj1.type == GameObject.TYPE_WALL){
                removeWallTexture((Wall) obj1);
            }

            collidedInstances.removeValue(obj0, false);
            collidedInstances.removeValue(obj1,false);

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


    private int selected = -1,selecting = -1;

    private int tranformTool = 0;


    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private MyContactListener contactListener;
    private btBroadphaseInterface broadphase;
    private btCollisionWorld collisionWorld;


    final static short WALL_FLAG = 1<<8;
    final static short FLOOR_OBJECT_FLAG = 1<<9;
    final static short WALL_OBJECT_FLAG = 1<<10;
    final static short ALL_FLAG = -1;

    private float wallY = 0f;
    private float wallZ = 0f;
    private float backWallHeight = 0f;

    private Vector3 pointAtWall = new Vector3();
    private Quaternion wallQuaternion = new Quaternion();

    private BlendingAttribute wallBlendingAttrib;
    private Material origWallMaterial;

    private RoomDesignData designData;
    private boolean back = false;
    private float toolsPanelYBounds;

    //To limit left and right moving of floor object
    private float minPosX;
    private float maxPosX;
//    private DebugDrawer debugDrawer;

    private void initEnvironment(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(Color.WHITE, 40f, 65f, -50f));
        environment.add(new DirectionalLight().set(Color.WHITE, -25f, 40f, 20f));
        environment.add(new DirectionalLight().set(Color.WHITE, 90f, 40f, 20f));
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

    }

    private void removeScreenInputProcessor(){
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor(stage);
    }

    public RoomWithHUD(){
        this(null, null);
    }



    private void init(PerspectiveCamera camera,FileHandle backgroundSource, Array<Wall> walls){
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

//        debugDrawer = new DebugDrawer();
//        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
//        collisionWorld.setDebugDrawer(debugDrawer);

        wallBlendingAttrib = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        wallBlendingAttrib.opacity = 0f;

        if(walls != null){
            origWallMaterial = new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.6f, 1f, 0.5f)));
            BlendingAttribute ba = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            ba.opacity = 0.4f;


            origWallMaterial.set(ba);

            for(Wall wall : walls){
                BoundingBox boundingBox = new BoundingBox();
                wall.calculateBoundingBox(boundingBox).mul(wall.transform);
                Vector3 pos = new Vector3();

                boundingBox.getCorner001(pos);
                wallY = pos.y;

                if(wall.location == Wall.LEFT){
                    minPosX = pos.x;
                }
                if(wall.location == Wall.RIGHT){
                    maxPosX = pos.x;
                }

                if(wall.isSide()){
                    if(pos.z != 0){
                        wallZ = pos.z;
                    }
                }else{
                    backWallHeight = wall.dimensions.y;
                }

                removeWallTexture(wall);

                wall.body = new btCollisionObject();
                wall.body.setCollisionShape(createConvexHullShape(wall.model,false));
                wall.collided = false;
                wall.body.setWorldTransform(wall.transform);
                wall.body.setUserValue(instances.size);
                wall.body.setCollisionFlags(wall.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
                instances.add(wall);
                collisionWorld.addCollisionObject(wall.body, WALL_FLAG, FLOOR_OBJECT_FLAG);

                instances.add(wall);
            }
        }
        background = new Texture(backgroundSource);
    }

    public RoomWithHUD(PerspectiveCamera camera, Array<Wall> walls,FileHandle backgroundSource){

        init(camera, backgroundSource, walls);
    }

    public RoomWithHUD(PerspectiveCamera camera, RoomDesignData rdata){

        designData = rdata;
        Room room = new Room(rdata);
        //System.out.println("TEST:"+rdata.getBackgroundImage());
        FileHandle backgroundSource;
        if(rdata.getBackgroundImage().contains(Main.DEFAULT_EMPTY_ROOM_DIR)){
            backgroundSource= Gdx.files.internal(rdata.getBackgroundImage());
        }
        else if(rdata.getBackgroundImage().contains("/")){
            backgroundSource= Gdx.files.absolute(rdata.getBackgroundImage());
        }
        else{
            backgroundSource= Gdx.files.internal(Main.DEFAULT_EMPTY_ROOM_DIR+rdata.getBackgroundImage());
        }



        init(camera,backgroundSource,room.getWalls());
    }

    private void removeWallTexture(Wall wall){
        if(wall != null){
            for(Material mat : wall.materials){
                mat.set(wallBlendingAttrib);
            }
            for(Material mat : wall.model.materials){
                mat.set(wallBlendingAttrib);
            }
        }
    }

    private void setWallMaterial(Wall wall){
        if(wall != null){
            for(Material mat : wall.materials){
                mat.clear();
                mat.set(origWallMaterial);
            }
            for(Material mat : wall.model.materials){
                mat.clear();
                mat.set(origWallMaterial);
            }
        }
    }

    List<FileHandle> filePath = new ArrayList<FileHandle>();
    private void fileList(FileHandle dir){
        //Get list of all files and folders in directory
        FileHandle[] files = Gdx.files.internal(dir.path()).list();
        Object[][] tests = {{"title", dir.file().getName()+":"+dir.path()},
                {"message", files.length}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        //For all files and folders in directory
        for (int i = 0; i < files.length; i++) {
            //Check if directory
            if (files[i].isDirectory())
                //Recursively call file list function on the new directory
                fileList(files[i]);
            else{
                //If not directory, print the file path
                if(files[i].file().getAbsolutePath().toLowerCase().endsWith(".obj")){
                    System.out.println(files[i].file().getAbsolutePath());
                    filePath.add(files[i]);
                }

            }
        }
    }

    public static btConvexHullShape createConvexHullShape (final Model model, boolean optimize) {
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

    private void removeGameObjects(Array<GameObject> objects){
        instances.removeAll(objects, true);
    }

    private void initHUD(){
        ImageButton addButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/add.png"))));
        ImageButton removeButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/remove.png"))));
        ImageButton moveButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/move.png"))));
        ImageButton rotateButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/rotate.png"))));

        ImageButton clearButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/clear.png"))));
        ImageButton saveButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/save.png"))));
        ImageButton cancellButon = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancel.png"))));


        addButton.setBounds(0, 0, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        removeButton.setBounds(Gdx.graphics.getWidth() / 10, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        moveButton.setBounds(Gdx.graphics.getWidth() / 10 * 2, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        rotateButton.setBounds(Gdx.graphics.getWidth() / 10 * 3, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);

        clearButton.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 10 * 3, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);


        saveButton.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 10 * 2, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);
        cancellButon.setBounds(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 10, 0f, Gdx.graphics.getWidth() / 10, Gdx.graphics.getHeight() / 10);

        final Catalog c = Catalog.construct(stage,assets,instances, (InputMultiplexer) Gdx.input.getInputProcessor(),this);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Add Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                removeScreenInputProcessor();
                stage.addActor(c);
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
                tranformTool = TransformTool.MOVE;
            }
        });

        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "Rotate Button Clicked"}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                tranformTool = TransformTool.ROTATE;
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
                        for (GameObject g :
                                instances) {
                            gobjs[i++] = g;
                        }
                        ToolUtils.saveRoomSetup(text + ".dat", gobjs, designData);

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


                Label l = new Label("Save? ", SkinManager.getDialogLabelStyle());
                l.setFontScale(2);

                Table hgroup = new Table();
                hgroup.pad(Gdx.graphics.getHeight() / 30);
                hgroup.add(l);
                hgroup.add(btnYes).height(Gdx.graphics.getHeight() / 10)
                        .width(Gdx.graphics.getWidth() / 17);
                hgroup.add(btnNo).height(Gdx.graphics.getHeight() / 10)
                        .width(Gdx.graphics.getWidth() / 17);
                confirmDialog.add(hgroup);

                confirmDialog.show(stage);
            }
        });

        cancellButon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new MenuScreen());
                dispose();
            }
        });

        Pixmap whitePixmap = new Pixmap(1, Gdx.graphics.getHeight() / 10, Pixmap.Format.RGBA8888);
        Color col = Color.WHITE;//Color.valueOf("#3498db");
        whitePixmap.setColor(Color.argb8888(col.r, col.g, col.b, 0.7f));
        whitePixmap.fill();

        Table tools = new Table();
        tools.setBackground(new SpriteDrawable(new Sprite(new Texture(whitePixmap))));
        tools.setBounds(0, (Gdx.graphics.getHeight() - (Gdx.graphics.getHeight() / 10)), Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 10);

        toolsPanelYBounds = (Gdx.graphics.getHeight() / 10);
//        tools.defaults().pad(10f);
        tools.defaults().width(Gdx.graphics.getWidth()/10).height(Gdx.graphics.getHeight()/10);
        tools.columnDefaults(2).width(Gdx.graphics.getWidth()/10).height(Gdx.graphics.getHeight() / 10);
        tools.columnDefaults(3).width(Gdx.graphics.getWidth()/10).height(Gdx.graphics.getHeight()/10);

        tools.addActor(addButton);
        tools.addActor(removeButton);
        tools.addActor(moveButton);
        tools.addActor(rotateButton);
        tools.addActor(clearButton);
        tools.addActor(saveButton);
        tools.addActor(cancellButon);

        whitePixmap.dispose();
        stage.addActor(tools);
    }



/*    private float volumeOfMesh(Mesh mesh) {
        float vols = from t in mesh.Triangles
        select SignedVolumeOfTriangle(t.P1, t.P2, t.P3);
        return Math.Abs(vols.Sum());
    }*/

    public static float signedVolumeOfTriangle(Vector3 p1, Vector3 p2, Vector3 p3)
    {
        return p1.dot(p2.crs(p3)) / 6.0f;
    }

    public void addObject(SaveFile.Object obj){
        if(!assets.isLoaded(obj.assetName)){
            assets.load(obj.assetName, Model.class);
        }

        assets.finishLoadingAsset(obj.assetName);
        Model model = assets.get(obj.assetName, Model.class);
        System.out.println("done loading asset.."+obj.assetName);
        addObject(obj.assetName, model, obj.type,obj.translation,obj.scale,obj.rotation,obj.val);
    }

    public void addObject(Model model, int type){
        addObject(null,model, type,null,null,null,null);
    }
    public void addObject(String assetName,Model model, int type){
        addObject(assetName,model, type,null,null,null,null);
    }


    public void addObject(String assetName,Model model, int type,float[] translation,
                          float[] scale,
                          float[] rotation,float[] val){
        BoundingBox bounds = new BoundingBox();
        model.calculateBoundingBox(bounds);

        Vector3 dimension = new Vector3();
        bounds.getDimensions(dimension);

        dimension.x -= (dimension.x / 2f);
        dimension.y -= (dimension.y / 2f);
        dimension.z -= (dimension.z / 2f);
        GameObject object = new GameObject(model,new btBoxShape(dimension),type,assetName);




        if(type == GameObject.TYPE_WALL_OBJECT){
            object.transform.translate(camera.position.x, wallY + (backWallHeight / 2), dimension.z);
        }else{
            object.transform.translate(camera.position.x, wallY + (bounds.getHeight() / 2f), dimension.z + 1);
        }

        System.out.println(dimension.y + (dimension.y * 2));


        if(val!=null){
            object.transform.set(val);
        }




//        object.newlyAdded = true;

        BoundingBox bb = new BoundingBox();
        object.calculateBoundingBox(bb);

        bb.getCenter(object.center);
        bb.getDimensions(object.dimensions);

        object.collided = false;
        object.body.setWorldTransform(object.transform);
        object.body.setUserValue(instances.size);
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(object);
        if(type == GameObject.TYPE_WALL_OBJECT){
            collisionWorld.addCollisionObject(object.body, FLOOR_OBJECT_FLAG, FLOOR_OBJECT_FLAG);
        }else{
            collisionWorld.addCollisionObject(object.body, FLOOR_OBJECT_FLAG, ALL_FLAG);
        }

        tranformTool = TransformTool.MOVE;
    }


    private void doneLoading () {
        loading = false;
    }

    @Override
    public void render(float delta) {
        if ( back)
        {
            Main.getInstance().setScreen(new MenuScreen());
            //dispose();
            back = false;
        }

        if (loading && assets.update()){
            doneLoading();
        }
        /*GameObject ga = null;
        for (GameObject c:
             instances) {
            if(c.assetName!=null){
                ga=c;
                break;
            }

        }*/
        //ga.transform.setTranslation(122,32,21);
        //ga.transform.setToRotation(122,32,21,3);
        collisionWorld.performDiscreteCollisionDetection();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        if(background != null){

            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

//        debugDrawer.begin(camera);
//        collisionWorld.debugDrawWorld();
//        debugDrawer.end();



        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
        stage.act();
        stage.draw();

    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if(screenY <= toolsPanelYBounds){
            return  super.touchDown(screenX,screenY,pointer,button);
        }
        selecting = getSelectedObject(screenX, screenY);
        selected = selecting;
        if(selecting >= 0) {
            instances.get(selecting).transform.getTranslation(origPosition);
            instances.get(selecting).transform.getRotation(origRotation);
        }

        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if(screenY <= toolsPanelYBounds){
            return super.touchDragged(screenX, screenY, pointer);
        }
        if (selected < 0) return false;

        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);
            GameObject gameObject = instances.get(selected);

            if(gameObject.type == GameObject.TYPE_WALL_OBJECT){
                int selectedWallIndex = getSelectedWall(screenX, screenY);


                handleWallObjectDrag(gameObject, ray);

                Vector3 pos = new Vector3();
                gameObject.transform.getTranslation(pos);
                Wall selectedWall = null;
                if(selectedWallIndex >= 0 && instances.get(selectedWallIndex) != null) {
                    selectedWall = (Wall) instances.get(selectedWallIndex);

                    float halfz = (gameObject.dimensions.z / 2);
                    if(selectedWall.location == Wall.BACK){
                        pos.z += halfz;
                    }else if(selectedWall.location == Wall.LEFT){
                        pos.x += halfz;
                    }else{
                        pos.x -= halfz;
                    }
                }

                if(wallY > (pos.y - (gameObject.dimensions.y / 2))){
                    pos.y = wallY + (gameObject.dimensions.y / 2);
                }
                if(backWallHeight < (pos.y + (gameObject.dimensions.y / 2))){
                    pos.y = wallY - (gameObject.dimensions.y / 2);
                }
                gameObject.transform.setTranslation(pos);

//                System.out.println("x = " + pos.x + " y = " + pos.y + " z = " + pos.z);
            } else {
                handleFloorObjectDrag(gameObject, ray);
            }


            gameObject.body.setWorldTransform(gameObject.transform);
        }

        return true;
    }

    private void checkColission(GameObject gameObject){
        if(gameObject != null && gameObject.type != GameObject.TYPE_WALL && collidedInstances != null){
            if(collidedInstances.contains(gameObject,true)){
                moveToPreviousLocation(gameObject);
            }else{

            }

//
//            if(gameObject.collided) {
//                if(tranformTool == TransformTool.MOVE){
//                    gameObject.transform.setTranslation(origPosition);
//                    gameObject.collided = false;
//                }else{
//                    gameObject.transform.setToTranslation(origPosition);
//                    gameObject.transform.rotate(origRotation.x, origRotation.y, origRotation.z, origRotation.w);
//                    gameObject.collided = false;
//                }
//            }else{
//                if(gameObject.newlyAdded){
//                    gameObject.newlyAdded = false;
//                }
//            }
            gameObject.body.setWorldTransform(gameObject.transform);
        }
    }

    private void moveToPreviousLocation(GameObject gameObject){
        if(gameObject != null){
            if(gameObject.collided) {
                if(tranformTool == TransformTool.MOVE){
                    gameObject.transform.setTranslation(origPosition);
                    gameObject.collided = false;
                }else{
                    gameObject.transform.setToTranslation(origPosition);
                    gameObject.transform.rotate(origRotation.x, origRotation.y, origRotation.z, origRotation.w);
                    gameObject.collided = false;
                }
            }
            gameObject.body.setWorldTransform(gameObject.transform);
        }
    }

    public int getSelectedWall(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;

        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);
            if(instance.type == GameObject.TYPE_WALL){
                Wall wall = (Wall) instance;

                BoundingBox boundingBox = new BoundingBox();
                wall.calculateBoundingBox(boundingBox).mul(wall.transform);


                Vector3 cornerA = new Vector3();
                Vector3 cornerB = new Vector3();
                Vector3 cornerC = new Vector3();
                Vector3 cornerD = new Vector3();

                if(wall.location == Wall.RIGHT){
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
        if(tranformTool == TransformTool.MOVE){
            gameObject.transform.set(pointAtWall, wallQuaternion);
        }

    }

    private void handleFloorObjectDrag(GameObject gameObject, Ray ray) {
        float level = wallY + (gameObject.dimensions.y / 2f);
        final float distance = (level - ray.origin.y) / ray.direction.y;

        position.set(ray.direction).scl(distance).add(ray.origin);

        if(tranformTool == TransformTool.MOVE){
            if(((position.z - (gameObject.dimensions.z / 2))) < 1 ){
                position.z = gameObject.dimensions.z;
            }
            if((position.x - (gameObject.dimensions.x / 2)) < minPosX){
                position.x = minPosX + (gameObject.dimensions.x / 2);
            }
            if((position.x + (gameObject.dimensions.x / 2)) > maxPosX){
                position.x = maxPosX - (gameObject.dimensions.x / 2);
            }
            gameObject.transform.setTranslation(position);
        } else {
            if (ray.direction.x > ray.origin.x){
                gameObject.transform.rotate(Vector3.Y, -1f);
            } else {
                gameObject.transform.rotate(Vector3.Y, 1f);
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(screenY <=toolsPanelYBounds){
            return  super.touchUp(screenX, screenY, pointer, button);
        }
       /* Object[][] tests = {{"title", "Message"},
                {"message", "Touch up!"+instances.size}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));*/
        if(selected >= 0){

            /*Object[][] tests2 = {{"title", "Message"},
                    {"message", "collision checked!"+instances.size}};
            Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                    ToolUtils.createMapFromList(tests2));*/
            checkColission(instances.get(selected));
        }
        return super.touchUp(screenX, screenY, pointer, button);
    }

    private void setSelected(int value){
        if (selected == value) return;
        selected = value;
    }

    public int getSelectedObject(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);

            if(instance.type != GameObject.TYPE_WALL){
                float dist2 = -1f;
                instance.transform.getTranslation(position).add(instance.center);
                if (Intersector.intersectRayBoundsFast(ray, position, instance.dimensions)) {
                    final float len = ray.direction.dot(position.x-ray.origin.x, position.y-ray.origin.y, position.z-ray.origin.z);
                    dist2 = position.dst2(ray.origin.x+ray.direction.x*len, ray.origin.y+ray.direction.y*len, ray.origin.z+ray.direction.z*len);
                }

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
        collidedInstances.clear();
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