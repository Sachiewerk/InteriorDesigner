package com.ggwp.utils.Tweener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Dell on 1/24/2016.
 */
public class ImageOpacityAccessor implements TweenAccessor<Image> {

    // The following lines define the different possible tween types.
    // It's up to you to define what you need :-)

    public static final int ALPHA = 1;

    TweenManager manager;
    // TweenAccessor implementation

    @Override
    public int getValues(Image target, int tweenType, float[] returnValues) {
        returnValues[0] = target.getColor().a; return 1;
      /*  switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getX(); return 1;
            case POSITION_Y: returnValues[0] = target.getY(); return 1;
            case POSITION_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            default: assert false; return -1;
        }*/
    }

    @Override
    public void setValues(Image target, int tweenType, float[] newValues) {
        Color curColor = target.getColor();
        //System.out.println("SET VALUE:"+newValues[0]);
        target.setColor(curColor.r,curColor.g,curColor.b,newValues[0]);
        /*switch (tweenType) {
            case POSITION_X: target.setX(newValues[0]); break;
            case POSITION_Y: target.setY(newValues[0]); break;
            case POSITION_XY:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            default: assert false; break;
        }*/
    }
}