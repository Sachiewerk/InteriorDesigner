package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Raymond on 1/20/2016.
 */
public class Furniture extends ModelInstance {

    public Shape shape;

    public Furniture(Model model){
        super(model);
    }

    public Furniture(Model model,String id){
        super(model,id);
    }

    public boolean isVisible(Camera cam) {
        return shape == null ? false : shape.isVisible(transform, cam);
    }

    /** @return -1 on no intersection, or when there is an intersection: the squared distance between the center of this
     * object and the point on the ray closest to this object when there is intersection. */
    public float intersects(Ray ray) {
        return shape == null ? -1f : shape.intersects(transform, ray);
    }
}
