package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Raymond on 1/20/2016.
 */
public class FurnitureContactListener extends com.badlogic.gdx.physics.bullet.collision.ContactListener {

    public Array<Furniture> instances;

    @Override
    public boolean onContactAdded (int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
//        if(instances == null){
//            return false;
//        }
//        instances.get(userValue0).collided = false;
//        instances.get(userValue1).collided = false;
        return true;
    }
}
