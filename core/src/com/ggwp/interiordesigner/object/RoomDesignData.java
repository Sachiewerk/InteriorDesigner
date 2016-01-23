package com.ggwp.interiordesigner.object;

public class RoomDesignData {

    private static final float DEFAULT_DIMENSION = 100f;

    private String backgroundImage;
    private float[] vertices;

    public static RoomDesignData getDefaultInstance(){
        RoomDesignData data = new RoomDesignData();
        data.setVertices(new float[]{
                -DEFAULT_DIMENSION, DEFAULT_DIMENSION, 0,
                0, DEFAULT_DIMENSION, 0,
                DEFAULT_DIMENSION, DEFAULT_DIMENSION, 0,
                DEFAULT_DIMENSION * 2, DEFAULT_DIMENSION, 0,
                -DEFAULT_DIMENSION, 0, 0,
                0, 0, 0,
                DEFAULT_DIMENSION, 0, 0,
                DEFAULT_DIMENSION * 2, 0, 0
        });
        return data;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }
}
