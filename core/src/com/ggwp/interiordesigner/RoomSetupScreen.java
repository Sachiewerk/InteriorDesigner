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
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.GameObject;
import com.ggwp.interiordesigner.object.Room;
import com.ggwp.interiordesigner.object.RoomDesignData;
import com.ggwp.utils.ToolUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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

    private GameObject box;
    private Vector3 boxPosition = new Vector3();
    private boolean computationBoxSetup = false;
    private boolean computationBoxDrag = false;
    private BlendingAttribute blendingAttribute;

    private DecimalFormat format = new DecimalFormat("#0.000");

    private Label computationLabel;

    private float previousDragX = 0f;
    private float previousDragY = 0f;

    private float ftHeight = -1f;
    private float ftWidth = -1f;
    private float ftDepth = -1f;

    private boolean prompted = false;
    private float _m = 3.432f;

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
        setupAutoComputationLabels();

        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (getTapCount() == 2) {
                    computationBoxSetup = !computationBoxSetup;
                    blendingAttribute.opacity = computationBoxSetup ? 0.5f : 0.0f;

                    for (Material material : box.materials) {
                        material.set(blendingAttribute);
                    }

                    for (Material material : box.model.materials) {
                        material.set(blendingAttribute);
                    }
                }
            }
        });

        format.setRoundingMode(RoundingMode.HALF_UP);
        computationBoxSetup = true;
        computeAreas();
    }

    private void setupAutoComputationBox(){
        blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.4f;

        Material boxMaterial = new Material(ColorAttribute.createDiffuse(Color.valueOf("#000000")));
        boxMaterial.set(blendingAttribute);

        ModelBuilder modelBuilder = new ModelBuilder();
        Model box = modelBuilder.createRect(
                0, 0, 0,
                25, 0, 0,
                25, 25, 0,
                0, 25, 0,
                1, 1, 1,
                boxMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        this.box = new GameObject(box, -1);
        instances.add(this.box);
    }

    private void setupAutoComputationLabels(){
        Table table = new Table();
        stage.addActor(table);
        table.setBounds(10, Gdx.graphics.getHeight() - 110, 200f, 100f);

        Label.LabelStyle style = SkinManager.getDefaultLabelStyle();
        computationLabel = new Label("Box Size: " + 0f, style);

        table.add(computationLabel).align(Align.left);
        table.row();
    }

    @Override
    public void render(float delta) {
        ftDepth = _m;

        if(!prompted){
            String message = "Please map the black box to any 8x8 object. " +
                    "Furniture sizes will not be accurate if you don't do this step properly.";
            Object[][] params = {{"title", message}, {"message", message}};
            Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.SHOW_MESSAGE, ToolUtils.createMapFromList(params));
            prompted = true;
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(background != null){
            spriteBatch.begin();
            spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.end();
        }

        modelBatch.begin(camera);

        if(!computationBoxSetup){
            modelBatch.render(room.getWalls());
        }

        modelBatch.render(instances, environment);
        modelBatch.end();

        String label = "Back Wall Height: " + format.format(ftHeight) + " ft.\n";
        label += "Back Wall Width: " + format.format(ftWidth) + " ft.\n";
        label += "Back Wall Area: " + format.format(ftWidth * ftHeight) + " ft.\n\n";
        label += "Floor Area: " + format.format(ftWidth * ftDepth) + " ft.\n";
        computationLabel.setText(label);

        stage.act();
        stage.draw();

        if(saveCurrentDesign){
            saveCurrentRoomDesign();
        }
    }

    private boolean saveCurrentDesign = false;

    private void saveCurrentRoomDesign(){
        RoomDesignData data = new RoomDesignData();

        System.out.println("Saving..");
        data.setBackgroundImage(fileHandle.name());

        String name = "Room " + fileHandle.name().replace(".jpg", "").replace("room", "");
        data.setVertices(room.getVertices());
        data.setName(name);
        data.setLeftWallVal(room.getLeftWall().transform.getValues());
        data.setBackWallVal(room.getBackWall().transform.getValues());
        data.setRightWallVal(room.getRightWall().transform.getValues());
        ToolUtils.saveEmptyRoomDataDesign(data);
        saveCurrentDesign = false;
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


        ImageButton doneButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/submitbtn.png"))));
        ImageButton backButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));

        backButton.setBounds(5f, 10f, Gdx.graphics.getWidth()/7, Gdx.graphics.getHeight()/5);
        doneButton.setBounds(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 7) - 5f, 10f, Gdx.graphics.getWidth() / 7, Gdx.graphics.getHeight() / 5);

        doneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Object[][] tests = {{"title", "Button Clicked"},
                        {"message", "File Handle"+fileHandle}};
                Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                        ToolUtils.createMapFromList(tests));
                Main.getInstance().setScreen(new FurnitureSetupScreen(camera, room.toRoomDesignData(fileHandle, ftWidth, ftHeight, ftDepth)));
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
        computationBoxDrag = isComputationBoxSelected(screenX, screenY);
        previousDragX = screenX;
        previousDragY = screenY;

        return false;
    }

    private boolean isComputationBoxSelected(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        box.transform.getTranslation(boxPosition);
        BoundingBox bb = new BoundingBox();
        box.calculateBoundingBox(bb).mul(box.transform);
        boxPosition.add(box.center);
        Boolean intersected = Intersector.intersectRayBounds(ray, bb, null);
        return intersected;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if(computationBoxSetup){
            handleComputationBoxAction(screenX, screenY);
        } else {
            room.onTouchDrag(screenX, screenY);
            computeAreas();
        }
        return true;
    }

    private void handleComputationBoxAction(int screenX, int screenY) {
        if(computationBoxDrag){
            onComputationBoxDrag(screenX, screenY);
        } else {
            onComputationBoxResize(screenX, screenY);
            computeAreas();
        }
    }

    private void onComputationBoxDrag(int screenX, int screenY){
        Ray ray = camera.getPickRay(screenX, screenY);
        final float distance = -ray.origin.z / ray.direction.z;

        BoundingBox boundingBox = new BoundingBox();
        box.calculateBoundingBox(boundingBox).mul(box.transform);

        boxPosition.set(ray.direction).scl(distance).add(ray.origin);
        float dimension = boundingBox.getWidth() / 2;
        boxPosition.x = boxPosition.x - dimension;
        boxPosition.y = boxPosition.y - dimension;

        box.transform.setTranslation(boxPosition);
    }

    private void onComputationBoxResize(int screenX, int screenY){
        boolean dragUp = screenY < previousDragY;
        boolean dragLeft = screenX < previousDragX;

        boolean nearTop = (Gdx.graphics.getHeight() / 2) > screenY;
        boolean nearLeft = (Gdx.graphics.getWidth() / 2) > screenX;


        if((nearTop && dragUp) || (nearLeft && dragLeft)){
            box.transform.scale(1.035f, 1.035f, 1.035f);
        } else {
            if(!isBelowMinimumSize()){
                box.transform.scale(0.965f, 0.965f, 0.965f);
            }
        }

        previousDragX = screenX;
        previousDragY = screenY;
    }

    private boolean isBelowMinimumSize(){
        BoundingBox currentComputationBoundingBox = new BoundingBox();
        box.calculateBoundingBox(currentComputationBoundingBox).mul(box.transform);
        return currentComputationBoundingBox.getHeight() <= 2f;
    }

    private void computeAreas(){
        BoundingBox wallBoundingBox = new BoundingBox();
        room.getBackWall().calculateBoundingBox(wallBoundingBox).mul(room.getBackWall().transform);

        BoundingBox computationBoundingBox = new BoundingBox();
        box.calculateBoundingBox(computationBoundingBox).mul(box.transform);

        float inchSizeOnScreen = computationBoundingBox.getHeight() / 8;
        ftHeight = (wallBoundingBox.getHeight() / inchSizeOnScreen) / 12;
        ftWidth = (wallBoundingBox.getWidth() / inchSizeOnScreen) / 12;

        Polygon floor = null;

        if(_m < 0){
            Ray bottomLeftCorner = camera.getPickRay(0, 0);
            Ray bottomRightCorner = camera.getPickRay(0, Gdx.graphics.getWidth());

            Vector3 bottomLeftVector = new Vector3();
            Vector3 bottomRightVector = new Vector3();

            final float bottomLeftDistance = -bottomLeftCorner.origin.y / bottomLeftCorner.direction.y;
            bottomLeftVector.set(bottomLeftCorner.direction).scl(bottomLeftDistance).add(bottomLeftCorner.origin);

            final float bottomRightDistance = -bottomRightCorner.origin.y / bottomRightCorner.direction.y;
            bottomRightVector.set(bottomRightCorner.direction).scl(bottomRightDistance).add(bottomRightCorner.origin);

            Vector3 leftCornerA = new Vector3();
            Vector3 leftCornerB = new Vector3();

            Vector3 rightCornerA = new Vector3();
            Vector3 rightCornerB = new Vector3();

            BoundingBox leftWallBoundingBox = new BoundingBox();
            BoundingBox rightWallBoundingBox = new BoundingBox();

            room.getLeftWall().calculateBoundingBox(leftWallBoundingBox).mul(room.getLeftWall().transform);
            room.getRightWall().calculateBoundingBox(rightWallBoundingBox).mul(room.getRightWall().transform);

            leftWallBoundingBox.getCorner001(leftCornerA);
            leftWallBoundingBox.getCorner100(leftCornerB);

            rightWallBoundingBox.getCorner000(rightCornerA);
            rightWallBoundingBox.getCorner101(rightCornerB);

            Vector2 leftIntersection = new Vector2();
            Intersector.intersectLines(leftCornerA.x, leftCornerA.z, leftCornerB.x, leftCornerB.z,
                    -1000, bottomLeftVector.z, 1000, bottomRightVector.z, leftIntersection);

            Vector2 rightIntersection = new Vector2();
            Intersector.intersectLines(rightCornerA.x, rightCornerA.z, rightCornerB.x, rightCornerB.z,
                    -1000, bottomLeftVector.z, 1000, bottomRightVector.z, rightIntersection);

            floor = new Polygon(new float[]{leftIntersection.x, leftIntersection.y, rightIntersection.x, rightIntersection.y,
                    rightCornerA.x, rightCornerA.z, leftCornerB.x, leftCornerB.z});
        }

        if(floor != null){
            ftDepth = floor.area() / ftWidth;
        }
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

}
