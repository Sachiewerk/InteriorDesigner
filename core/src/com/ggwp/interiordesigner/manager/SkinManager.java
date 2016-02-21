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
import com.ggwp.interiordesigner.Main;

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
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 24;
        //parameter1.color = Color.WHITE;
        BitmapFont textFont = generator.generateFont(parameter);
        BitmapFont textFont1 = generator.generateFont(parameter1);
        generator.dispose();


        // buttons

        int buttonHeight = (int)(Gdx.graphics.getHeight()/10);
        int buttonWidth = (int)(Gdx.graphics.getWidth()/7);

        Pixmap cancelPixmap = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        cancelPixmap.setColor(Color.rgba8888(Color.RED.r, Color.RED.g, Color.RED.b, .5f));
        cancelPixmap.fill();


        Pixmap submitPixmap = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        submitPixmap.setColor(Color.rgba8888(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b, .5f));
        submitPixmap.fill();

        Pixmap clearPixmap = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        clearPixmap.setColor(Color.CLEAR);
        clearPixmap.fill();

        Pixmap optionPixmap = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        optionPixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.7f));
        optionPixmap.fill();

        Pixmap fillerPixmap = new Pixmap(100, buttonHeight, Pixmap.Format.RGBA8888);
        fillerPixmap.setColor(Color.argb8888(0f, 0f, 0f, 0f));
        fillerPixmap.fill();

        Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackPixmap.setColor(Color.valueOf("#34495e"));
        blackPixmap.fill();

        defaultSkin.add("blackBackground", new Texture(blackPixmap));
        defaultSkin.add("defaultButton", new Texture(whitePixmap));
        defaultSkin.add("defaultCancelButton", new Texture(cancelPixmap));
        defaultSkin.add("defaultSubmitButton", new Texture(submitPixmap));
        defaultSkin.add("defaultFillerSkin", new Texture(fillerPixmap));
        defaultSkin.add("clearTexture", new Texture(clearPixmap));
        defaultSkin.add("defaultFont", textFont);
        defaultSkin.add("defaultFont1", textFont1);
        defaultSkin.add("optionBackground", new Texture(optionPixmap));

        bloackPixmap.dispose();
        whitePixmap.dispose();
    }

    public static Skin getDefaultSkin(){
        return defaultSkin;
    }

    public static TextButton.TextButtonStyle getDefaultCancelTextButtonStyle(){
        TextButton.TextButtonStyle defaultTextButtonStyle = getDefaultTextButtonStyle();
        defaultTextButtonStyle.up = defaultSkin.getDrawable("defaultCancelButton");

        return defaultTextButtonStyle;
    }

    public static TextButton.TextButtonStyle getDefaultFillerButtonStyle(){
        TextButton.TextButtonStyle defaultTextButtonStyle = getDefaultTextButtonStyle();
        defaultTextButtonStyle.up = defaultSkin.getDrawable("defaultFillerSkin");
        defaultTextButtonStyle.font = defaultSkin.getFont("defaultFont1");
        return defaultTextButtonStyle;
    }

    public static TextButton.TextButtonStyle getDefaultSubmitTextButtonStyle(){
        TextButton.TextButtonStyle defaultTextButtonStyle = getDefaultTextButtonStyle();
        defaultTextButtonStyle.up = defaultSkin.getDrawable("defaultSubmitButton");
        return defaultTextButtonStyle;
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

    public static Label.LabelStyle createLabelStyle(Color col){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = defaultSkin.getFont("defaultFont");
        style.fontColor = col;

        return style;
    }

    public static Label.LabelStyle getDialogLabelStyle(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = defaultSkin.getFont("defaultFont1");
        style.fontColor = Color.WHITE;

        return style;
    }

    public static Label.LabelStyle getDefaultCatalogHeaderLabelStyle(){
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = defaultSkin.getFont("defaultFont");
        style.fontColor = Color.DARK_GRAY;

        return style;
    }





}
