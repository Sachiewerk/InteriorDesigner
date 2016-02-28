package com.ggwp.interiordesigner.manager;

import com.badlogic.gdx.Gdx;

public class ViewportManager {

    public static int VIRTUAL_WIDTH = 1280;
    public static int VIRTUAL_HEIGHT = 720;
    public static int VIRTUAL_POS_X = 0;
    public static int VIRTUAL_POS_Y = 0;

    static {
        computeScreenSize();
    }

    private static void computeScreenSize(){
        double deviceWidth = Gdx.graphics.getWidth();
        double deviceHeight = Gdx.graphics.getHeight();

        VIRTUAL_WIDTH = (int) deviceWidth;
        VIRTUAL_HEIGHT = (int) deviceHeight;

        if(getCurrentAspectRatio() < getDefaultAspectRatio()){
            VIRTUAL_HEIGHT = (VIRTUAL_WIDTH * 9) / 16;
        } else {
            VIRTUAL_WIDTH = (VIRTUAL_HEIGHT * 16) / 9;
        }

        VIRTUAL_POS_Y = (int) (deviceHeight - VIRTUAL_HEIGHT) / 2;
        VIRTUAL_POS_X = (int) (deviceWidth - VIRTUAL_WIDTH) / 2;
    }

    public static float getCurrentAspectRatio(){
        return (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;
    }

    private static final float getDefaultAspectRatio(){
        return 16f / 9f;
    }



}
