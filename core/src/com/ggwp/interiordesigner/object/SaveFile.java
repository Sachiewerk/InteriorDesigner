package com.ggwp.interiordesigner.object;

import java.util.ArrayList;
import java.util.List;

public class SaveFile {

    public class Object{
        public String assetName;
        public float[] positionMatrix;
        public int type;
    }

    public RoomDesignData roomDesignDataData;

    public List<Object> objects = new ArrayList<Object>();


    public void addObject(GameObject gameObject){

        Object obj = new Object();
        obj.assetName = gameObject.assetName;
        obj.positionMatrix = gameObject.transform.getValues();
        obj.type = gameObject.type;
        objects.add(obj);
    }


}
