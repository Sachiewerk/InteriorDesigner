package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SaveFile {

    public class Object{
        public String assetName;
        public float[] translation;
        public float[] scale;
        public float[] rotation;
        public float[] val;
        public int type;

    }

    public RoomDesignData roomDesignDataData;

    public List<Object> objects = new ArrayList<Object>();

    public void addObject(GameObject gameObject){

        Object obj = new Object();
        obj.assetName = gameObject.assetName;
        if(gameObject.assetName==null){
            return;
        }
        Vector3 trans = new Vector3();
        trans = gameObject.transform.getTranslation(trans);
        obj.translation = new float[]{trans.x,trans.y,trans.z};
        System.out.println("Saving translation.. "+obj.translation[0]+":"+obj.translation[1]+":"+obj.translation[2]);
        Vector3 scale = new Vector3();
        scale = gameObject.transform.getScale(scale);
        obj.scale = new float[]{scale.x,scale.y,scale.z};
        System.out.println("Saving scale.. "+obj.scale[0]+":"+obj.scale[1]+":"+obj.scale[2]);
        Quaternion quaternion = new Quaternion();
        quaternion = gameObject.transform.getRotation(quaternion);
        obj.rotation = new float[]{quaternion.x,quaternion.y,quaternion.z,quaternion.w};
        System.out.println("Saving rotation.. "+obj.rotation[0]+":"+obj.rotation[1]+":"+obj.rotation[2]+":"+obj.rotation[3]);
        obj.type = gameObject.type;
        obj.val = gameObject.transform.val;
        objects.add(obj);
    }


}
