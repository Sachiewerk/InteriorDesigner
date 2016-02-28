package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SaveFile {

    public RoomDesignData roomDesignData;
    public List<Object> objects = new ArrayList<Object>();
    public List<TilePaint> paintTiles = new ArrayList<TilePaint>();

    public void addObject(GameObject gameObject) {
        if(gameObject.assetName == null){
            return;
        }
        objects.add(serialize(gameObject));
    }

    public SaveFile.Object serialize(GameObject gameObject){
        Object obj = new Object();
        if(gameObject.assetName!=null)
            obj.assetName = gameObject.assetName;

        Vector3 trans = new Vector3();
        trans = gameObject.transform.getTranslation(trans);
        obj.translation = new float[]{trans.x, trans.y, trans.z};

        Vector3 scale = new Vector3();
        scale = gameObject.transform.getScale(scale);
        obj.scale = new float[]{scale.x, scale.y, scale.z};

        Quaternion quaternion = new Quaternion();
        quaternion = gameObject.transform.getRotation(quaternion);
        obj.rotation = new float[]{quaternion.x, quaternion.y, quaternion.z, quaternion.w};

        obj.type = gameObject.type;
        obj.val = gameObject.transform.val;
        return obj;
    }

    public class Object {
        public String assetName;
        public float[] translation;
        public float[] scale;
        public float[] rotation;
        public float[] val;
        public int type;
    }

    public static final class TilePaint {
        public String color;
        public int screenX,screenY;

        public TilePaint(String color,int screenX,int screenY){
            this.color = color;
            this.screenX = screenX;
            this.screenY = screenY;
        }

        @Override
        public String toString() {
            return "Color:"+color+":"+screenX+":"+screenY;
        }
    }


}
