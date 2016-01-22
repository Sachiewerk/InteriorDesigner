package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Wall extends ModelInstance {

    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public boolean side = false;

    private final static BoundingBox bounds = new BoundingBox();

    public Wall(Model model, boolean side){
        super(model);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        this.side = side;
    }

}
