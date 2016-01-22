package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.Arrays;
import java.util.List;

public class Room {

    private static final float SCALE_AMOUNT = 1;

    private Wall leftWall;
    private Wall backWall;
    private Wall rightWall;

    public Room(){
        ModelBuilder modelBuilder = new ModelBuilder();
        Color dodgerBlue = new Color(0.2f, 0.6f, 1f, 0.5f);

        BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        blendingAttribute.opacity = 0.5f;

        Material material = new Material(ColorAttribute.createDiffuse(dodgerBlue));
        material.set(blendingAttribute);

        float halfWallHeight = 50;
        float halfWallWidth = 50;

        createLeftWall(modelBuilder, material, halfWallHeight, halfWallWidth);
        createRightWall(modelBuilder, material, halfWallHeight, halfWallWidth);
        createBackWall(modelBuilder, blendingAttribute, material, halfWallHeight, halfWallWidth);
    }

    private void createBackWall(ModelBuilder modelBuilder, BlendingAttribute blendingAttribute, Material material, float halfWallHeight, float halfWallWidth) {
        blendingAttribute.opacity = 0.8f;
        material.set(blendingAttribute);

        Model backWallModel = modelBuilder.createRect(
                -halfWallWidth, 0, 0,
                halfWallWidth, 0, 0,
                halfWallWidth, halfWallHeight * 2, 0,
                -halfWallWidth, halfWallHeight * 2, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        backWall = new Wall(backWallModel);
    }

    private void createRightWall(ModelBuilder modelBuilder, Material material, float halfWallHeight, float halfWallWidth) {
        Model rightWallModel = modelBuilder.createRect(
                halfWallWidth, 0, 0,
                (halfWallWidth * 3), 0, 100,
                (halfWallWidth * 3), halfWallHeight * 2, 100,
                halfWallWidth, halfWallHeight * 2, 0,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        rightWall = new Wall(rightWallModel);
    }

    private void createLeftWall(ModelBuilder modelBuilder, Material material, float halfWallHeight, float halfWallWidth) {
        Model leftWallModel = modelBuilder.createRect(
                -(halfWallWidth * 3), 0, 100,
                -halfWallWidth, 0, 0,
                -halfWallWidth, halfWallHeight * 2, 0,
                -(halfWallWidth * 3), halfWallHeight * 2, 100,
                1, 1, 1,
                material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );

        leftWall = new Wall(leftWallModel);
    }

    public void onBackWallTopPartUpDrag(){
        System.out.println("TODO onBackWallTopPartUpDrag");

        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        float wallHeight = bounds.getHeight();
        float scalePercentage = 1f + (SCALE_AMOUNT / wallHeight);

        backWall.transform.scale(1, scalePercentage, 1);
        leftWall.transform.scale(1, scalePercentage, 1);
    }

    public void onBackWallTopPartDownDrag(){
        System.out.println("TODO onBackWallTopPartDownDrag");
    }

    public void onBackWallBottomPartUpDrag(){
        System.out.println("TODO onBackWallBottomPartUpDrag");
    }

    public void onBackWallBottomPartDownDrag(){
        System.out.println("TODO onBackWallBottomPartDownDrag");

        BoundingBox bounds = new BoundingBox();
        backWall.calculateBoundingBox(bounds).mul(backWall.transform);

        float wallHeight = bounds.getHeight();
        float scalePercentage = 1f + (SCALE_AMOUNT / wallHeight);

        System.out.println("Wall Height: " + wallHeight);
        System.out.println("Scale Percentage: " + scalePercentage);

        backWall.transform.scale(1, scalePercentage, 1).trn(0, -SCALE_AMOUNT, 0);
        leftWall.transform.scale(1, scalePercentage, 1).trn(0, -SCALE_AMOUNT, 0);
    }

    public void onBackWallLeftPartLeftDrag(){
        System.out.println("TODO onBackWallLeftPartLeftDrag");

        backWall.transform.translate(-SCALE_AMOUNT, 0, 0);
        leftWall.transform.translate(-SCALE_AMOUNT, 0, 0);
        rightWall.transform.translate(-SCALE_AMOUNT, 0, 0);
    }

    public void onBackWallLeftPartRightDrag(){
        System.out.println("TODO onBackWallLeftPartRightDrag");
    }

    public void onBackWallRightPartLeftDrag(){
        System.out.println("TODO onBackWallRightPartLeftDrag");
    }

    public void onBackWallRightPartRightDrag(){
        System.out.println("TODO onBackWallRightPartRightDrag");

        backWall.transform.translate(SCALE_AMOUNT, 0, 0);
        leftWall.transform.translate(SCALE_AMOUNT, 0, 0);
        rightWall.transform.translate(SCALE_AMOUNT, 0, 0);
    }

    public List<Wall> getWalls(){
        return Arrays.asList(leftWall, backWall, rightWall);
    }

    public Wall getLeftWall(){
        return leftWall;
    }

    public Wall getBackWall(){
        return backWall;
    }

    public Wall getRightWall(){
        return rightWall;
    }


}
