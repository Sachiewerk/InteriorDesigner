package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Catalog;
import com.ggwp.interiordesigner.object.GameObject;
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


            if(instances.get(userValue0).newlyAdded == false){
                instances.get(userValue0).collided = true;
            }
            if(instances.get(userValue1).newlyAdded == false){
                instances.get(userValue1).collided = true;
            }

            return true;
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

    private SpriteBatch spriteBatch;
    private Texture background;

    private Array<GameObject> instances = new Array<GameObject>();

    protected Stage stage;
    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    private Vector3 origPosition = new Vector3();
    private Quaternion origRotation = new Quaternion();


    private int selected = -1,selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    private int tranformTool = 0;


    private btCollisionConfiguration collisionConfig;
    private btDispatcher dispatcher;
    private MyContactListener contactListener;
    private btBroadphaseInterface broadphase;
    private btCollisionWorld collisionWorld;


    final static short GROUND_FLAG = 1<<8;
    final static short OBJECT_FLAG = 1<<9;
    final static short ALL_FLAG = -1;

    private float wallY = 0f;
    private float wallZ = 0f;

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

    public RoomWithHUD(){
        this(null, null, null);
    }

    private DebugDrawer debugDrawer;


    public RoomWithHUD(PerspectiveCamera camera, Array<Wall> walls, FileHandle backgroundSource){
        Bullet.init();
        this.camera = camera;
        stage = new Stage(new ScreenViewport());
        assets = new AssetManager();

        initEnvironment();
        initCamera();
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this, camController));

        initHUD();

        spriteBatch = new SpriteBatch();
        modelBatch = new ModelBatch();
        shapeRenderer = new ShapeRenderer();


        filePath.clear();
        fileList(Gdx.files.internal("furnitures"));
        Object[][] tests = {{"title", "test error"},
                {"message", filePath.size()}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));

        int i = 0;
        System.out.println("Loading assets ..");
        for (final FileHandle categoryfolder : filePath
                ) {
            System.out.println("Loading "+categoryfolder.path()+" ..");
            assets.load(categoryfolder.path(), Model.class);
        }

/*        assets.load("furnitures/tables/Table.obj", Model.class);
        assets.load("furnitures/chair/chair2.obj", Model.class);
        assets.load("furnitures/chair/chair3.obj", Model.class);
        assets.load("furnitures/chair/chair4.obj", Model.class);*/

/*        assets.load("furnitures/chair/chair1.obj", Model.class);
        assets.load("furnitures/chair/chair2.obj", Model.class);
        assets.load("furnitures/chair/chair3.obj", Model.class);
        assets.load("furnitures/chair/chair4.obj", Model.class);
        assets.load("furnitures/chair/chair5.obj", Model.class);
        assets.load("furnitures/chair/chair6.obj", Model.class);
        assets.load("sofa.obj", Model.class);*/
        loading = true;



        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        contactListener = new MyContactListener();

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);

        collisionWorld.setDebugDrawer(debugDrawer);

        if(walls != null){
            for(Wall wall : walls){


                Vector3 pos = new Vector3();
                wall.transform.getTranslation(pos);
//                wall.transform.setToTranslation(pos.x,0,pos.z);
                wallY = pos.y;

                if(wall.side){
                    if(pos.z != 0){
                        wallZ = pos.z;
                    }
                }

                wall.body = new btCollisionObject();
                wall.body.setCollisionShape(createConvexHullShape(wall.model,true));



                wall.collided = false;
                wall.body.setWorldTransform(wall.transform);
                wall.body.setUserValue(instances.size);
                wall.body.setCollisionFlags(wall.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
                instances.add(wall);
                collisionWorld.addCollisionObject(wall.body, GROUND_FLAG, OBJECT_FLAG);

                instances.add(wall);
            }
        }
        background = new Texture(backgroundSource);

        BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.5f;
        selectionMaterial = new Material(ColorAttribute.createDiffuse(new Color(1f, 0.647f, 0f,0.6f)));
        selectionMaterial.set(blendingAttribute);
        originalMaterial = new Material();
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

    private void initHUD(){
        ImageButton addButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/add.png"))));
        ImageButton removeButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/remove.png"))));
        ImageButton moveButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/move.png"))));
        ImageButton rotateButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/rotate.png"))));

        addButton.setBounds(0, 10f, 40f, 40f);
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

/*    private float volumeOfMesh(Mesh mesh) {
        float vols = from t in mesh.Triangles
        select SignedVolumeOfTriangle(t.P1, t.P2, t.P3);
        return Math.Abs(vols.Sum());
    }*/

    public static float signedVolumeOfTriangle(Vector3 p1, Vector3 p2, Vector3 p3)
    {
        return p1.dot(p2.crs(p3)) / 6.0f;
    }

    public void addObject(Model model, int type){
        BoundingBox bounds = new BoundingBox();
        model.calculateBoundingBox(bounds);



        Vector3 dimension = new Vector3();
        bounds.getDimensions(dimension);

        dimension.x -= (dimension.x / 2f);
        dimension.y -= (dimension.y / 2f);
        dimension.z -= (dimension.z / 2f);

        GameObject object = new GameObject(model,new btBoxShape(dimension),type);
        object.transform.translate(camera.position.x, wallY + (bounds.getHeight() / 2), (camera.position.z / 2));
//        object.transform.setToScaling(20f,20f,20f);
//        object.calculateTransforms();

        //System.out.println(signedVolumeOfTriangle(model.meshParts.));
        object.transform.scale(0.5f,0.5f,0.5f);

        object.newlyAdded = true;

        BoundingBox bb = new BoundingBox();
        object.calculateBoundingBox(bb);

        bb.getCenter(object.center);
        bb.getDimensions(object.dimensions);

        object.collided = false;
        object.body.setWorldTransform(object.transform);
        object.body.setUserValue(instances.size);
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(object);
        collisionWorld.addCollisionObject(object.body, OBJECT_FLAG, ALL_FLAG);
    }

    private void doneLoading () {
//        for(int i = 1; i < 2; i++){
//            Model m = assets.get("sofa.obj", Model.class);
//
//            BoundingBox bounds = new BoundingBox();
//            m.calculateBoundingBox(bounds);
//
//            Vector3 dimension = new Vector3();
//            bounds.getDimensions(dimension);
//
//            dimension.x -= (dimension.x / 2f);
//            dimension.y -= (dimension.y / 2f);
//            dimension.z -= (dimension.z / 2f);
//
//            GameObject sofa = new GameObject(m,new btBoxShape(dimension),GameObject.TYPE_FLOOR_OBJECT);
//            sofa.transform.translate((i * 40f), wallY + (bounds.getHeight() / 2), ((bounds.getDepth() / 2) + 1));
//            sofa.calculateTransforms();
//
//            sofa.newlyAdded = true;
//            BoundingBox bb = new BoundingBox();
//            sofa.calculateBoundingBox(bb);
//
//            bb.getCenter(sofa.center);
//            bb.getDimensions(sofa.dimensions);
//
//            sofa.collided = false;
//            sofa.body.setWorldTransform(sofa.transform);
//            sofa.body.setUserValue(instances.size);
//            sofa.body.setCollisionFlags(sofa.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
//            instances.add(sofa);
//            collisionWorld.addCollisionObject(sofa.body,OBJECT_FLAG,ALL_FLAG);
//        }

        loading = false;
    }

    @Override
    public void render(float delta) {
        if (loading && assets.update()){
            doneLoading();
        }

        collisionWorld.performDiscreteCollisionDetection();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        if(background != null){
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        debugDrawer.begin(camera);
        collisionWorld.debugDrawWorld();
        debugDrawer.end();

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
        stage.act();
        stage.draw();

    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        selecting = getSelectedObject(screenX, screenY);
        if(selecting >= 0) {
            instances.get(selecting).transform.getTranslation(origPosition);
            instances.get(selecting).transform.getRotation(origRotation);
        }

        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (selected < 0) return false;
        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);

            float level = wallY + (instances.get(selected).dimensions.y / 2);
            final float distance = (level - ray.origin.y) / ray.direction.y;

            position.set(ray.direction).scl(distance).add(ray.origin);

            if(tranformTool == TransformTool.MOVE){
                instances.get(selected).transform.setTranslation(position);
            }else{
                if (ray.direction.x > ray.origin.x){
                    instances.get(selected).transform.rotate(Vector3.Y,-1f);
                }else{
                    instances.get(selected).transform.rotate(Vector3.Y,1f);
                }
            }
            instances.get(selected).body.setWorldTransform(instances.get(selected).transform);
        }

        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
//            if (selecting == selected){
                setSelected(selecting);
                if(selected >= 0){
                    if(instances.get(selected).collided) {
                        if(tranformTool == TransformTool.MOVE){
//                if(instances.get(selected).collided) {
                            instances.get(selected).transform.setTranslation(origPosition);
                            instances.get(selected).collided = false;
//                }
                        }else{
//                if(instances.get(selected).collided) {
                            instances.get(selected).transform.setToTranslation(origPosition);
                            instances.get(selected).transform.rotate(origRotation.x, origRotation.y, origRotation.z, origRotation.w);
                            instances.get(selected).collided = false;
//                }
                        }
                    }else{
                        if(instances.get(selected).newlyAdded){
                            instances.get(selected).newlyAdded = false;
                        }
                    }

                    instances.get(selected).body.setWorldTransform(instances.get(selected).transform);
                }
//            }

            selecting = -1;
            return true;
        }
        return false;
    }

    private void setSelected(int value){
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


    public int getSelectedObject(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);
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