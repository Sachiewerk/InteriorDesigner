package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Raymond on 1/20/2016.
 */
public class Furniture extends ModelInstance implements Disposable {

    public final btCollisionObject body;
    public boolean moving;

    public Furniture (Model model, String node, btCollisionShape shape) {
        super(model, node);
        body = new btCollisionObject();
        body.setCollisionShape(shape);
    }

    @Override
    public void dispose () {
        body.dispose();
    }

    public static class Constructor implements Disposable {
        public final Model model;
        public final String node;
        public final btCollisionShape shape;

        public Constructor (Model model, String node, btCollisionShape shape) {
            this.model = model;
            this.node = node;
            this.shape = shape;
        }

        public Furniture construct () {
            return new Furniture(model, node, shape);
        }

        @Override
        public void dispose () {
            shape.dispose();
        }
    }
}
