package com.ggwp.interiordesigner.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by Raymond on 1/22/2016.
 */
public class SkinManager {

    private static final Skin defaultSkin;

    static {
        defaultSkin= new Skin();
        Pixmap bloackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bloackPixmap.setColor(Color.BLACK);
        bloackPixmap.fill();

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.rgba8888(1f, 1f, 1f, .5f));
        whitePixmap.fill();

        FileHandle fontFile = Gdx.files.internal("data/arial.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 16;
        BitmapFont textFont = generator.generateFont(parameter);
        generator.dispose();

        defaultSkin.add("defaultButton", new Texture(whitePixmap));
        defaultSkin.add("defaultFont", textFont);

        bloackPixmap.dispose();
        whitePixmap.dispose();
    }

    public static TextButton.TextButtonStyle getDefaultTextButtonStyle(){
        TextButton.TextButtonStyle defaultTextButtonStyle = new TextButton.TextButtonStyle();
        defaultTextButtonStyle.font = defaultSkin.getFont("defaultFont");
        defaultTextButtonStyle.fontColor = Color.BLACK;

        return defaultTextButtonStyle;
    }

    public static Label.LabelStyle getDefaultLabelStyle(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = defaultSkin.getFont("defaultFont");
        style.fontColor = Color.BLACK;

        return style;
    }


}
