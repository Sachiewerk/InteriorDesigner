package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.GameObject;
import com.ggwp.interiordesigner.object.Room;
import com.ggwp.interiordesigner.object.RoomDesignData;
import com.ggwp.utils.ToolUtils;

public class RoomSetupScreen extends AppScreen {

    private Environment environment;
    private PerspectiveCamera camera;

    private ModelBatch modelBatch;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Room room;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private Stage stage;
    private Boolean fromCamera;

    private FileHandle fileHandle;

    private GameObject computationBox;
    private float backWallArea = 0f;
    private Vector3 computationBoxPosition = new Vector3();
    private boolean computationBoxSetup = false;
    private BlendingAttribute blendingAttribute;

    public RoomSetupScreen(FileHandle fileHandle, Boolean fromCamera) {
        this.fileHandle = fileHandle;

        this.fromCamera = fromCamera;
        stage = new Stage(new ScreenViewport());

        spriteBatch = new SpriteBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(50f, 50f, 100f);
        camera.lookAt(50, 50, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));

        room = new Room(camera);
        background = new Texture(fileHandle);
        initOptions();
        setupAutoComputationBox();
        setAutoComputationLabels();

        stage.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if(getTapCount() == 2){
                    computationBoxSetup = !computationBoxSetup;
                    blendingAttribute.opacity = computationBoxSetup ? 0.8f : 0.4f;

                    for(Material material : computationBox.materials){
                        material.set(blendingAttribute);
                    }

                    for(Material material : computationBox.model.materials){
                        material.set(blendingAttribute);
                    }

                    System.out.println("Double Tapped. Setup Mode: " + computationBoxSetup);
                }
            }
        });
    }

    private GameObject xyPlane;

    private void setupAutoComputationBox(){
        blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.4f;

        Material boxMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("#000000")));
        boxMaterial.set(blendingAttribute);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createRect(
                25, 25, 25,
                75, 25, 25,
                75, 75, 25,
                25, 75, 25,
                1, 1, 1,
                boxMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        computationBox = new GameObject(box, -1);
        instances.add(computationBox);

        blendingAttribute.opacity = 0.1f;
        boxMaterial.set(blendingAttribute);
        Model xyPlaneModel = modelBuilder.createRect(
                -1000, -1000, 0,
                1000, -1000, 0,
                1000, 1000, 0,
                -1000, 1000, 0,
                1, 1, 1,
                boxMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        xyPlane = new GameObject(xyPlaneModel, -1);
        instances.add(xyPlane);
    }

    private void setAutoComputationLabels(){
        Table table = new Table();
        stage.addActor(table);
        table.setPosition(200, 65);

        Label.LabelStyle style = SkinManager.getDefaultLabelStyle();
        Label backWallAreaLabel = new Label("Box Size: " + 50f, style);
        backWallAreaLabel.setFontScale(1.5f);

        table.add(backWallAreaLabel);
        table.row();
        table.pack();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(background != null){
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        modelBatch.begin(camera);
        modelBatch.render(room.getWalls());
        modelBatch.render(instances, environment);
        modelBatch.end();

        stage.act();
        stage.draw();
    }

    private void saveCurrentRoomDesign(){
        RoomDesignData data = new RoomDesignData();
        data.setBackgroundImage(fileHandle.name());

        String name = "Room " + fileHandle.name().replace(".jpg", "").replace("room", "");
        data.setVertices(room.getVertices());
        data.setName(name);
        ToolUtils.saveRoomDataDesign(data);
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        room.getWalls().clear();
        spriteBatch.dispose();
    }

    private void initOptions(){
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(70, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.5f));
        pixmap.fill();

        skin.add("defaultButton", new Texture(pixmap));
        skin.add("defaultFont", new BitmapFont());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("defaultButton");
        textButtonStyle.down = skin.newDrawable("defaultButton");
        textButtonStyle.font = skin.getFont("defaultFont");

        TextButton backButton = new TextButton("BACK", textButtonStyle);
        backButton.setBounds(5f, 5f, 70f, 40f);

        TextButton doneButton = new TextButton("DONE", textButtonStyle);
        doneButton.setBounds(80f, 5f, 70f, 40f);

        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new RoomWithHUD(camera, room.getWalls(), fileHandle));
                dispose();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (fromCamera) {
                    MenuScreen.openDeviceCamera();
                } else {
                    MenuScreen.openDeviceGallery();
                }
                dispose();
            }
        });

        stage.addActor(doneButton);
        stage.addActor(backButton);
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
        if(computationBoxSetup == false){
            room.onTouchDown(screenX, screenY);
        }
        return false;
    }

    private boolean isComputationBoxSelected(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        computationBox.transform.getTranslation(computationBoxPosition);
        BoundingBox bb = new BoundingBox();
        computationBox.calculateBoundingBox(bb);
        computationBoxPosition.add(computationBox.center);
        Boolean intersected = Intersector.intersectRayBounds(ray, bb, null);
        System.out.println("Intersected: " + intersected);
        return intersected;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        System.out.println("Dragging..");
        if(computationBoxSetup){
            if(isComputationBoxSelected(screenX, screenY)){
                System.out.println("Dragging mini box..");
                onComputationBoxDrag(screenX, screenY);
            } else {
                System.out.println("Resizing mini box..");
                onComputationBoxResize(screenX, screenY);
            }

        } else {
            room.onTouchDrag(screenX, screenY);
        }
        return true;
    }

    private void onComputationBoxDrag(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
//        final float distance = -ray.origin.z / ray.direction.z;
//
//        computationBoxPosition.set(ray.direction).scl(distance).add(ray.origin);
//        computationBox.transform.setTranslation(computationBoxPosition);
//
//        gameObject.transform.set(pointAtWall, wallQuaternion);
//        computationBox.transform.setTranslation(screenX, screenY, 0);

//        gameObject.transform.set(pointAtWall, wallQuaternion);

        BoundingBox boundingBox = new BoundingBox();
        xyPlane.calculateBoundingBox(boundingBox);
        Intersector.intersectRayBounds(ray, boundingBox, computationBoxPosition);
        computationBox.transform.setTranslation(computationBoxPosition);
    }

    private void onComputationBoxResize(int screenX, int screenY){

    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

}
