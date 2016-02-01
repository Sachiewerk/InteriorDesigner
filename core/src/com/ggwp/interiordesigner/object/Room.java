package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.utils.ToolUtils;

public class Room {

    private static final float SCALE_AMOUNT = 1f;
    private static final float MINIMUM_DIMENSION = 10f;
    private static final float DEFAULT_DIMENSION = 100f;

    private Array<Wall> walls = new Array<Wall>();

    private Wall leftWall;
    private Wall backWall;
    private Wall rightWall;

    private Material backWallMaterial;
    private Material sideWallMaterial;

    private ModelBuilder modelBuilder;
    private BlendingAttribute blendingAttribute;

    private boolean sideSelected = false;
    private boolean nearTop = false;
    private boolean nearLeft = false;

    private boolean defaultInstance = false;

    private int dropX = 0;
    private int dropY = 0;

    private int previousDragX = 0;
    private int previousDragY = 0;

    private Vector3 position = new Vector3();
    private Camera camera;

    private Vector3 scaleHolder = new Vector3();
    private Vector3 vectorHolder = new Vector3();
    private Quaternion quaternionHolder = new Quaternion();

    private RoomDesignData data;

    public Room(RoomDesignData data){
        this(null, data);
    }

    public Room(Camera camera){
        this(camera, null);
    }

    private Room(Camera camera, RoomDesignData data){
        this.camera = camera;
        this.data = data;

        if(data == null){
            defaultInstance = true;
            this.data = RoomDesignData.getDefaultInstance();
        }

        modelBuilder = new ModelBuilder();
        Color dodgerBlue = new Color(0.2f, 0.6f, 1f, 0.5f);
        Color emerald = new Color(0.15f, 0.68f, 0.38f, 0.5f);

        blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.4f;

        backWallMaterial = new Material(ColorAttribute.createDiffuse(dodgerBlue));
        backWallMaterial.set(blendingAttribute);

        sideWallMaterial = new Material(ColorAttribute.createDiffuse(emerald));
        sideWallMaterial.set(blendingAttribute);

        createLeftWall();
        createRightWall();
        createBackWall();

        if(data!=null) {
            if (data.getLeftWallVal() != null) {
                leftWall.transform.set(data.getLeftWallVal());
            }
            if (data.getRightWallVal() != null) {
                System.out.println(data.getRightWallVal());
                rightWall.transform.set(data.getRightWallVal());
            }
            if (data.getBackWallVal() != null) {
                backWall.transform.set(data.getBackWallVal());
            }
        }

    }

    public RoomDesignData toRoomDesignData(FileHandle fileHandle){
        RoomDesignData rdata = new RoomDesignData();
        System.out.println("Saving..");



        String name = "Room " + fileHandle.name().replace(".jpg", "").replace("room", "");
        rdata.setBackgroundImage(fileHandle.file().getAbsolutePath());
        rdata.setName(name);
        rdata.setLeftWallVal(leftWall.transform.getValues());
        rdata.setBackWallVal(backWall.transform.getValues());
        rdata.setRightWallVal(rightWall.transform.getValues());
        rdata.setVertices(data.getVertices());

        Object[][] tests = {{"title", "path"},
                {"message", rdata.getBackgroundImage()}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));

        return rdata;
    }

    private void createBackWall() {
        blendingAttribute.opacity = 0.8f;
        backWallMaterial.set(blendingAttribute);

        Model backWallModel = modelBuilder.createRect(
                data.getVertices()[15], data.getVertices()[16], data.getVertices()[17],
                data.getVertices()[18], data.getVertices()[19], data.getVertices()[20],
                data.getVertices()[6], data.getVertices()[7], data.getVertices()[8],
                data.getVertices()[3], data.getVertices()[4], data.getVertices()[5],
                1, 1, 1,
                backWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        backWall = new Wall(backWallModel, Wall.BACK);
        walls.add(backWall);
    }

    private void createRightWall() {
        Model rightWallModel = modelBuilder.createRect(
                data.getVertices()[18], data.getVertices()[19], data.getVertices()[20],
                data.getVertices()[21], data.getVertices()[22], data.getVertices()[23],
                data.getVertices()[9], data.getVertices()[10], data.getVertices()[11],
                data.getVertices()[6], data.getVertices()[7], data.getVertices()[8],
                1, 1, 1,
                sideWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        rightWall = new Wall(rightWallModel, Wall.RIGHT);
        if(defaultInstance){
            rightWall.transform
                .translate(DEFAULT_DIMENSION, DEFAULT_DIMENSION / 2, 0)
                .rotateRad(0, DEFAULT_DIMENSION / 2, 0, (float) Math.toRadians(-45))
                .translate(-DEFAULT_DIMENSION, -(DEFAULT_DIMENSION / 2), 0);
        }

        walls.add(rightWall);
    }

    private void createLeftWall() {
        Model leftWallModel = modelBuilder.createRect(
                data.getVertices()[12], data.getVertices()[13], data.getVertices()[14],
                data.getVertices()[15], data.getVertices()[16], data.getVertices()[17],
                data.getVertices()[3], data.getVertices()[4], data.getVertices()[5],
                data.getVertices()[0], data.getVertices()[1], data.getVertices()[2],
                1, 1, 1,
                sideWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        leftWall = new Wall(leftWallModel, Wall.LEFT);

        if(defaultInstance){
            float radians = (float) Math.toRadians(45);
            leftWall.transform.setToRotationRad(0, DEFAULT_DIMENSION / 2, 0, radians);
        }

        walls.add(leftWall);
    }

    private void onBackWallTopPartUpDrag(){
//        System.out.println("b wall top part up");
        scaleWallsY(1);
    }

    private void onBackWallTopPartDownDrag(){
//        System.out.println("b wall top part down");
        scaleWallsY(-1);
    }

    private void onBackWallBottomPartUpDrag(){
//        System.out.println("b wall bot part up");
        scaleAndMoveWallsY(1);
    }

    private void onBackWallBottomPartDownDrag(){
//        System.out.println("b wall bot part down");
        scaleAndMoveWallsY(-1);
    }

    private void onBackWallLeftPartLeftDrag(){
//        System.out.println("b wall left part left");
        scaleAndMoveWallsX(-1);
    }

    private void onBackWallLeftPartRightDrag(){
//        System.out.println("b wall left part right");
        scaleAndMoveWallsX(1);
    }

    private void onBackWallRightPartLeftDrag(){
//        System.out.println("b wall right part left");
        scaleWallsX(-1);
    }

    private void onBackWallRightPartRightDrag(){
//        System.out.println("b wall right part right");
        scaleWallsX(1);
    }

    private void onLeftWallUpDrag(){
//        System.out.println("left wall up");
        leftWallDrag(SCALE_AMOUNT);
    }

    private void onLeftWallDownDrag(){
//        System.out.println("left wall down");
        leftWallDrag(-SCALE_AMOUNT);
    }

    private void onRightWallUpDrag(){
//        System.out.println("right wall up");
        rightWallDrag(-SCALE_AMOUNT);
    }

    private void onRightWallDownDrag() {
//        System.out.println("right wall down");
        rightWallDrag(SCALE_AMOUNT);
    }

    private void leftWallDrag(float degrees) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        leftWall.transform
                .translate(0, bounds.getHeight() / 2, 0)
                .rotate(0, bounds.getHeight() / 2, 0 , degrees)
                .translate(0, -(bounds.getHeight() / 2), 0);

        BoundingBox leftBox = new BoundingBox();
        leftWall.calculateBoundingBox(leftBox);
        Quaternion quaternion = new Quaternion();
        leftWall.transform.getRotation(quaternion, true);

        if(quaternion.getYaw() < 0 || quaternion.getAngle() > 90){
            leftWall.transform
                    .translate(0, bounds.getHeight() / 2, 0)
                    .rotate(0, bounds.getHeight() / 2, 0 , -degrees)
                    .translate(0, -(bounds.getHeight() / 2), 0);
        }
    }

    private void rightWallDrag(float degrees){
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        rightWall.transform
                .translate(DEFAULT_DIMENSION, bounds.getHeight() / 2, 0)
                .rotate(0, bounds.getHeight() / 2, 0, degrees)
                .translate(-DEFAULT_DIMENSION, -(bounds.getHeight() / 2), 0);

        BoundingBox rightBox = new BoundingBox();
        rightWall.calculateBoundingBox(rightBox);
        Quaternion quaternion = new Quaternion();
        rightWall.transform.getRotation(quaternion, true);

        if(quaternion.getYaw() > 0 || quaternion.getAngle() > 90){
            rightWall.transform
                    .translate(DEFAULT_DIMENSION, bounds.getHeight() / 2, 0)
                    .rotate(0, bounds.getHeight() / 2, 0, -degrees)
                    .translate(-DEFAULT_DIMENSION, -(bounds.getHeight() / 2), 0);
        }
    }

    private void scaleWallsX(float multiplier) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        rightWall.transform.getScale(scaleHolder);
        rightWall.transform.getTranslation(vectorHolder);
        rightWall.transform.getRotation(quaternionHolder, true);

        if(bounds.getWidth() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getWidth()) * multiplier);
            backWall.transform.scale(scalePercentage, 1f, 1f);

            vectorHolder.x = vectorHolder.x + (SCALE_AMOUNT * multiplier);
            rightWall.transform.set(vectorHolder, quaternionHolder, scaleHolder);
        }
    }
 
    private void scaleWallsY(float multiplier) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        if(bounds.getHeight() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getHeight()) * multiplier);

            for(Wall wall : getWalls()){
                wall.transform.scale(1f, scalePercentage, 1f);
            }
        }
    }

    private void scaleAndMoveWallsX(float multiplier) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        leftWall.transform.getScale(scaleHolder);
        leftWall.transform.getTranslation(vectorHolder);
        leftWall.transform.getRotation(quaternionHolder, true);

        if(bounds.getWidth() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getWidth()) * -(multiplier));
            backWall.transform.scale(scalePercentage, 1f, 1f).trn(SCALE_AMOUNT * multiplier, 0f, 0f);

            vectorHolder.x = vectorHolder.x + (SCALE_AMOUNT * multiplier);
            leftWall.transform.set(vectorHolder, quaternionHolder, scaleHolder);
        }
    }

    private void scaleAndMoveWallsY(float multiplier) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        if(bounds.getHeight() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getHeight()) * -(multiplier));
            for(Wall wall : getWalls()){
                wall.transform.scale(1, scalePercentage, 1).trn(0, SCALE_AMOUNT * multiplier, 0);
            }
        }
    }

    public Array<Wall> getWalls(){
        return walls;
    }

    public Wall getBackWall(){
        return backWall;
    }

    public Wall getRightWall(){
        return rightWall;
    }

    public Wall getLeftWall(){
        return leftWall;
    }

    private void handleSideWallDrag(boolean dragUp) {
        if(nearLeft){
            if(dragUp){
                this.onLeftWallUpDrag();
            } else {
                this.onLeftWallDownDrag();
            }
        } else {
            if(dragUp){
                this.onRightWallUpDrag();
            } else {
                this.onRightWallDownDrag();
            }
        }
    }

    private void handleBackWallDrag(int screenX, int screenY, boolean dragUp, boolean dragLeft) {
        int deltaX = dropX - screenX;
        int deltaY = dropY - screenY;

        boolean vertical = Math.abs(deltaX) < Math.abs(deltaY);

        if(vertical){
            if(nearTop){
                if(dragUp){
                    this.onBackWallTopPartUpDrag();
                } else {
                    this.onBackWallTopPartDownDrag();
                }
            } else {
                if(dragUp){
                    this.onBackWallBottomPartUpDrag();
                } else {
                    this.onBackWallBottomPartDownDrag();
                }
            }
        } else {
            if(nearLeft){
                if(dragLeft){
                    this.onBackWallLeftPartLeftDrag();
                } else {
                    this.onBackWallLeftPartRightDrag();
                }
            } else {
                if(dragLeft){
                    this.onBackWallRightPartLeftDrag();
                } else {
                    this.onBackWallRightPartRightDrag();
                }
            }
        }
    }

    private void checkSelectedWall(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);

        sideSelected = false;

        float distance = -1;
        for (Wall wall : getWalls()) {
            wall.transform.getTranslation(position);

            BoundingBox bb = new BoundingBox();
            wall.calculateBoundingBox(bb);

            position.add(wall.center);
            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance) continue;


            if (Intersector.intersectRayBounds(ray, bb, null)) {
                sideSelected = wall.isSide();
                distance = dist2;
            }
        }
    }

    public void onTouchDown(int screenX, int screenY) {
        checkSelectedWall(screenX, screenY);

        dropX = screenX;
        dropY = screenY;

        previousDragX = screenX;
        previousDragY = screenY;

        nearTop = (Gdx.graphics.getHeight() / 2) > screenY;
        nearLeft = (Gdx.graphics.getWidth() / 2) > screenX;
    }

    public void onTouchDrag(int screenX, int screenY){
        boolean dragUp = screenY < previousDragY;
        boolean dragLeft = screenX < previousDragX;

        if(sideSelected){
            handleSideWallDrag(dragUp);
        } else {
            handleBackWallDrag(screenX, screenY, dragUp, dragLeft);
        }

        previousDragX = screenX;
        previousDragY = screenY;
    }

    public float[] getVertices(){
        BoundingBox leftBox = new BoundingBox();
        BoundingBox rightBox = new BoundingBox();

        leftWall.calculateBoundingBox(leftBox).mul(leftWall.transform);
        rightWall.calculateBoundingBox(rightBox).mul(rightWall.transform);

        Vector3 leftV100 = new Vector3();
        Vector3 leftV110 = new Vector3();
        Vector3 leftV001 = new Vector3();
        Vector3 leftV011 = new Vector3();

        Vector3 rightV000 = new Vector3();
        Vector3 rightV010 = new Vector3();
        Vector3 rightV101 = new Vector3();
        Vector3 rightV111 = new Vector3();

        leftBox.getCorner100(leftV100);
        leftBox.getCorner110(leftV110);
        leftBox.getCorner001(leftV001);
        leftBox.getCorner011(leftV011);

        rightBox.getCorner000(rightV000);
        rightBox.getCorner010(rightV010);
        rightBox.getCorner101(rightV101);
        rightBox.getCorner111(rightV111);

        float[] vertices = new float[]{
                leftV011.x, leftV011.y, leftV011.z,
                leftV110.x, leftV110.y, leftV110.z,
                rightV010.x, rightV010.y, rightV010.z,
                rightV111.x, rightV111.y, rightV111.z,

                leftV001.x, leftV001.y, leftV001.z,
                leftV100.x, leftV100.y, leftV100.z,
                rightV000.x, rightV000.y, rightV000.z,
                rightV101.x, rightV101.y, rightV101.z,
        };

        return vertices;
    }

}
