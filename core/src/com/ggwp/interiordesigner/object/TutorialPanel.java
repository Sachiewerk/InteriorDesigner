package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.MenuScreen;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.Tweener.ImageButtonAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;


/**
 * Created by Raymond on 1/22/2016.
 */
public class TutorialPanel extends AppScreen {

    protected Stage stage;
    protected AppScreen appScreen;
    private TutorialPanel instance;

    private Table layoutTable;
    private Table furnituresContainer;

    private TweenManager manager;
    private Image a,b;
    private int curPos = 0;
    private  TextButton backButton;
    private FileHandle[] slides = null;


    public TutorialPanel() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        //initCategories();

        slides = Gdx.files.internal("Tutorial").list();

        if(slides.length==0)
            return;

        a = new Image(new SpriteDrawable(new Sprite(new Texture(slides[0]))));
        a.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //a.setFillParent(true);
        stage.addActor(a);
        if(slides.length==1)
            return;

        b = new Image(new SpriteDrawable(new Sprite(new Texture(slides[1]))));
        b.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //a.setFillParent(true);
        stage.addActor(b);
        a.toFront();
        manager = new TweenManager();

        Tween.registerAccessor(Image.class, new ImageButtonAccessor());
        /*Tween.from(b, ImageButtonAccessor.POSITION_X, 1f)
                .target(500, 0)
                .ease(Cubic.INOUT)
                .delay(1.0f)
                .start(manager);*/

        a.addListener(new ActorGestureListener() {
            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                super.fling(event, velocityX, velocityY, button);
                processSlide(velocityX,velocityY,b);
            }
        });
        b.addListener(new ActorGestureListener() {
            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                super.fling(event, velocityX, velocityY, button);
                processSlide(velocityX, velocityY, a);
            }
        });


        //new SpriteDrawable(new Sprite(new Texture("Rooms/room1.jpg")))
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = SkinManager.getDefaultSkin().newDrawable("defaultButton");
        textButtonStyle.down = SkinManager.getDefaultSkin().newDrawable("defaultButton");
        textButtonStyle.font = SkinManager.getDefaultSkin().getFont("defaultFont");
        textButtonStyle.fontColor= Color.BLACK;
        backButton = new TextButton("BACK", textButtonStyle);
        backButton.setBounds(5f, 5f, 70f, 40f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new MenuScreen());
                dispose();
            }
        });

        stage.addActor(backButton);

    }

    private boolean doneAnimating =true;
    public void processSlide( float velocityX, float velocityY, Image a){

        System.out.println(curPos+":"+slides[curPos]);
        if(doneAnimating)
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX < 0) {

                if(curPos>=slides.length-1)
                    return;
                a.setDrawable(new SpriteDrawable(new Sprite(new Texture(slides[++curPos]))));

                a.toFront();
                backButton.toFront();
                Tween.from(a, ImageButtonAccessor.POSITION_X, 1f)
                        .target(Gdx.graphics.getWidth(), 0)
                        .ease(Cubic.INOUT)
                        .start(manager).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        doneAnimating = true;
                    }
                });
                doneAnimating=false;
            } else if (velocityX > 0) {
                //
                if(curPos<=0)
                    return;
                a.setDrawable(new SpriteDrawable(new Sprite(new Texture(slides[--curPos]))));
                a.toFront();
                backButton.toFront();
                Tween.from(a, ImageButtonAccessor.POSITION_X, 1f)
                        .target(-Gdx.graphics.getWidth(), 0)
                        .ease(Cubic.INOUT)
                        .start(manager).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        doneAnimating = true;
                    }
                });
                doneAnimating=false;
            } else {
            }
        } else {
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        //test tween

        manager.update(delta);

        //

        stage.getBatch().begin();
        //stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //sage.getBatch().draw(gradient, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.draw();


    }






    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }



}
