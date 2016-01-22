package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.Furniture;
import com.ggwp.interiordesigner.object.Room;


public class RectangleTest extends AppScreen {

    private Environment environment;
    private PerspectiveCamera camera;

    private ModelBatch modelBatch;

    private SpriteBatch spriteBatch;
    private Texture background;

    private Room room;
    private Array<ModelInstance> instances = new Array<ModelInstance>();

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

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 50f, 100f);
        camera.lookAt(0, 50, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        Gdx.input.setInputProcessor(this);

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();

        room = new Room();

        background = new Texture(Gdx.files.internal("Rooms/room2.jpg"));
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
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        room.getWalls().clear();
        spriteBatch.dispose();
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
        lastTouchX = screenX;
        lastTouchY = screenY;

        System.out.println("Width: "+  Gdx.graphics.getWidth());
        System.out.println("Height: "+  Gdx.graphics.getHeight());
        System.out.println("Center: " + room.getBackWall().center.x + " " + room.getBackWall().center.y + " " + room.getBackWall().center.z);
        System.out.println("Dim: " + room.getBackWall().dimensions.x + " " + room.getBackWall().dimensions.y + " " + room.getBackWall().dimensions.z);

        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        dragWall(screenX, screenY);

        if (selecting < 0) return false;
        if (selected == selecting) {
            Ray ray = camera.getPickRay(screenX, screenY);
            final float distance = -ray.origin.y / ray.direction.y;
            position.set(ray.direction).scl(distance).add(ray.origin);
            instances.get(selected).transform.setTranslation(position);
        }
        return true;
    }

    private Integer lastTouchX = null;
    private Integer lastTouchY = null;

    private Boolean dragWall(int screenX, int screenY){
        if(lastTouchY != null){
            if(screenY < lastTouchY){
                room.onBackWallTopPartUpDrag();
            } else if(screenY > lastTouchY){
                room.onBackWallBottomPartDownDrag();
            }
        }

        if(lastTouchX != null){
            if(screenX < lastTouchX){
                room.onBackWallLeftPartLeftDrag();
            } else if(screenX > lastTouchX){
                room.onBackWallRightPartRightDrag();
            }
        }

        lastTouchX = screenX;
        lastTouchY = screenY;
        return false;
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
