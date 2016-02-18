package com.ggwp.utils.Tweener;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenManager;

public class ImageOpacityAccessor implements TweenAccessor<Image> {

    public static final int ALPHA = 1;

    @Override
    public int getValues(Image target, int tweenType, float[] returnValues) {
        returnValues[0] = target.getColor().a; return 1;
    }

    @Override
    public void setValues(Image target, int tweenType, float[] newValues) {
        Color curColor = target.getColor();
        target.setColor(curColor.r,curColor.g,curColor.b,newValues[0]);
    }
}