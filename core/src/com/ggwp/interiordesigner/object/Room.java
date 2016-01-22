package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Room {

    private static final float SCALE_AMOUNT = 1f;
    private static final float MINIMUM_DIMENSION = 10f;

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

    private int dropX = 0;
    private int dropY = 0;

    private int previousDragX = 0;
    private int previousDragY = 0;

    private float defaultHeight = 100f;
    private float defaultWidth = 100f;
    private float defaultDepth = 100f;

    private Vector3 position = new Vector3();
    private Camera camera;

    public Room(Camera camera){
        Preferences prefs = Gdx.app.getPreferences("Test");
        System.out.println(prefs.getFloat("screen_w", 0f));
        System.out.println(prefs.getFloat("screen_h", 0f));

        prefs.putFloat("screen_w", Gdx.graphics.getWidth());
        prefs.putFloat("screen_h", Gdx.graphics.getHeight());
        prefs.flush();

        RoomDesignData data = new RoomDesignData();
        data.setBackgroundImage("Rooms/room2.jpg");
        data.setVertices(new float[]{
                -100, 100, 0,
                0, 100, 0,
                100, 100, 0,
                200, 100, 0,
                -100, 0, 0,
                0, 0, 0,
                100, 0, 0,
                200, 0, 0
        });

        try {
            FileHandle handle = Gdx.files.local("data/default-room.xml");

//            if(handle.exists() == false){
//                System.out.println("Creating file..");
//                handle.file().createNewFile();
//            }

            //Save file
//            System.out.println("Saving file..");
            JAXBContext jaxb = JAXBContext.newInstance(RoomDesignData.class);
//            Marshaller marshaller = jaxb.createMarshaller();
//            marshaller.setProperty(marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(data, handle.file());

            //Load file
            System.out.println("Loading file..");
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            RoomDesignData d = (RoomDesignData) unmarshaller.unmarshal(handle.file());

            for(Float f : d.getVertices()){
                System.out.println(f);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }



        this.camera = camera;

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
    }

    private void createBackWall() {
        blendingAttribute.opacity = 0.8f;
        backWallMaterial.set(blendingAttribute);

        Model backWallModel = modelBuilder.createRect(
                0, 0, 0,
                defaultWidth, 0, 0,
                defaultWidth, defaultHeight, 0,
                0, defaultHeight, 0,
                1, 1, 1,
                backWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        backWall = new Wall(backWallModel, false);
        walls.add(backWall);
    }

    private void createRightWall() {
        Model rightWallModel = modelBuilder.createRect(
                defaultWidth, 0, 0,
                defaultWidth * 2, 0, defaultDepth,
                defaultWidth * 2, defaultHeight, defaultDepth,
                defaultWidth, defaultHeight, 0,
                1, 1, 1,
                sideWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        rightWall = new Wall(rightWallModel, true);

//        TODO Nagloloko transform pag may ganto
//        rightWall.transform
//                .translate(defaultWidth, defaultHeight / 2, 0)
//                .rotateRad(0, defaultHeight / 2, 0, (float) Math.toRadians(-30))
//                .translate(-defaultWidth, -(defaultHeight / 2), 0);

        walls.add(rightWall);
    }

    private void createLeftWall() {
        Model leftWallModel = modelBuilder.createRect(
                -defaultWidth, 0, defaultDepth,
                0, 0, 0,
                0, defaultHeight, 0,
                -defaultWidth, defaultHeight, defaultDepth,
                1, 1, 1,
                sideWallMaterial, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        leftWall = new Wall(leftWallModel, true);

//        TODO Nagloloko transform pag may ganto
//        TODO Option: REMOVE + CREATE WALL AGAIN
//        leftWall.transform.rotateRad(0, defaultHeight / 2, 0, (float) Math.toRadians(30));

        walls.add(leftWall);
    }

    private void onBackWallTopPartUpDrag(){
        scaleWallsY(1);
    }

    private void onBackWallTopPartDownDrag(){
        scaleWallsY(-1);
    }

    private void onBackWallBottomPartUpDrag(){
        scaleAndMoveWallsY(1);
    }

    private void onBackWallBottomPartDownDrag(){
        scaleAndMoveWallsY(-1);
    }

    private void onBackWallLeftPartLeftDrag(){
        scaleAndMoveWallsX(-1);
    }

    private void onBackWallLeftPartRightDrag(){
        scaleAndMoveWallsX(1);
    }

    private void onBackWallRightPartLeftDrag(){
        scaleWallsX(-1);
    }

    private void onBackWallRightPartRightDrag(){
        scaleWallsX(1);
    }

    private void onLeftWallUpDrag(){
        leftWallDrag(SCALE_AMOUNT);
    }

    private void onLeftWallDownDrag(){
        leftWallDrag(-SCALE_AMOUNT);
    }

    private void onRightWallUpDrag(){
        rightWallDrag(-SCALE_AMOUNT);
    }

    private void onRightWallDownDrag() {
        rightWallDrag(SCALE_AMOUNT);
    }

    private void leftWallDrag(float degrees) {
        leftWall.transform
                .translate(0, defaultHeight / 2, 0)
                .rotate(0, defaultHeight / 2, 0 , degrees)
                .translate(0, -(defaultHeight / 2), 0);
    }

    private void rightWallDrag(float degrees){
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        rightWall.transform
                .translate(defaultWidth, defaultHeight / 2, 0)
                .rotate(0, defaultHeight / 2, 0, degrees)
                .translate(-defaultWidth, -(defaultHeight / 2), 0);
    }

    private void scaleWallsX(float multiplier) {
        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        if(bounds.getWidth() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getWidth()) * multiplier);
            backWall.transform.scale(scalePercentage, 1f, 1f);
            rightWall.transform.translate(SCALE_AMOUNT * multiplier, 0f, 0f);
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

        if(bounds.getWidth() > MINIMUM_DIMENSION){
            float scalePercentage = 1f + ((SCALE_AMOUNT / bounds.getWidth()) * -(multiplier));
            backWall.transform.scale(scalePercentage, 1f, 1f).trn(SCALE_AMOUNT * multiplier, 0f, 0f);
            leftWall.transform.translate(SCALE_AMOUNT * multiplier, 0f, 0f);
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
                sideSelected = wall.side;
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

}
