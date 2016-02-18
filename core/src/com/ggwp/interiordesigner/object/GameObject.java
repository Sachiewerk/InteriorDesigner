package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class GameObject extends ModelInstance implements  Disposable{

    public static final int TYPE_WALL = 0;
    public static final int TYPE_WALL_OBJECT = 1;
    public static final int TYPE_FLOOR_OBJECT = 2;

    public Vector3 center = new Vector3();
    public Vector3 dimensions = new Vector3();
    public btCollisionObject body;
    public boolean collided;
    public int type;
    public String assetName;

    public GameObject(Model model,int type){
        this(model,null,type);
    }

    public GameObject(Model model, btCollisionShape shape,int type,String assetName){
        this(model,shape,type);
        this.assetName = assetName;
    }

    public GameObject(Model model, btCollisionShape shape,int type){
        super(model);
        this.type = type;
        if(shape != null) {
            body = new btCollisionObject();
            body.setCollisionShape(shape);
        }
    }

    @Override
    public void dispose(){
        body.dispose();
    }

}
