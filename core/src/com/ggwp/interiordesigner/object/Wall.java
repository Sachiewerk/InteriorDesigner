package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Wall extends GameObject {

    public static final int LEFT = 0;
    public static final int BACK = 1;
    public static final int RIGHT = 2;

    private final static BoundingBox bounds = new BoundingBox();

    public int location;

    public Wall(Model model, int location){
        super(model,GameObject.TYPE_WALL);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        this.location = location;
    }

    public boolean isSide(){
        return location != BACK;
    }


}
