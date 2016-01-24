package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.EmptyRoomSelector;
import com.ggwp.interiordesigner.object.TutorialPanel;
import com.ggwp.utils.ToolUtils;
import com.ggwp.utils.Tweener.ImageOpacityAccessor;

import java.util.Arrays;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;

public class MenuScreen extends AppScreen {

    private Skin skin;
    private Stage stage;
    private Texture backgroundImage;
    private Image gradientA, gradientB;

    private Table newDesignOption;
    private ImageButton helpBtn;
    private TextButton createNewBtn;
    private TextButton catalogBtn;

    private BitmapFont titleFont;
    private BitmapFont textFont;
    private BitmapFont applicationNameFont;

    private TweenManager manager;
    private TextButton backButton;

    public MenuScreen() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        loadSkins();
        loadApplicationName();
        loadOptions();
        loadCreateNewOptions();
        loadHelpButton();
        loadTween();
        addListeners();
    }

    private void loadApplicationName(){
        Label label = new Label("Virtual Home Interior Design", skin, "applicationNameFont", Color.valueOf("#2ecc71"));
        label.setAlignment(Align.center);
        label.setBounds(0f, Gdx.graphics.getHeight() - 90f, Gdx.graphics.getWidth(), 80f);
        stage.addActor(label);
    }

    private void loadSkins() {
        backgroundImage = new Texture("menu.jpg");
        gradientA = new Image(new Texture("gradient-black.png"));
        gradientB = new Image(new Texture("gradient-black.png"));

        gradientA.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Color curColor = gradientB.getColor();
        gradientB.setColor(curColor.r, curColor.g, curColor.b, 0.0f);
        gradientB.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gradientB.setOrigin(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        gradientB.rotateBy(180);

        createFonts();
        skin = new Skin();
        skin.add("defaultFont", textFont);
        skin.add("defaultTitleFont", titleFont);
        skin.add("applicationNameFont", applicationNameFont);

        Pixmap optionPixmap = new Pixmap(100, 20, Pixmap.Format.RGBA8888);
        optionPixmap.setColor(Color.rgba8888(0f, 0f, 0f, 0.7f));
        optionPixmap.fill();
        skin.add("optionBackground", new Texture(optionPixmap));

        Window.WindowStyle style = new Window.WindowStyle();
        style.titleFont = skin.getFont("defaultFont");
        skin.add("default", style, Window.WindowStyle.class);

        Label.LabelStyle style1 = new Label.LabelStyle();
        style1.font = skin.getFont("defaultFont");
        skin.add("default", style1, Label.LabelStyle.class);
    }

    private void loadHelpButton() {
        helpBtn = new ImageButton(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal("Common/manual.png"), true))));
        helpBtn.setBounds(Gdx.graphics.getWidth() - 150, 10f, 100, 100);
        stage.addActor(helpBtn);
    }

    private void loadOptions() {
        createNewBtn = new TextButton("NEW PROJECT", createButtonStyle(Color.valueOf("#3498db")));
        createNewBtn.pad(20);

        catalogBtn = new TextButton("LOAD PROJECTS", createButtonStyle(Color.valueOf("#e74c3c")));
        catalogBtn.pad(20);

        Table table = new Table();
        table.row().pad(5);
        table.add(createNewBtn).align(Align.center).width(300f);

        table.row().pad(5);
        table.add(catalogBtn).align(Align.center).width(300f);

        Container wrapper = new Container<Table>(table);
        wrapper.setPosition(200, 100);
        stage.addActor(wrapper);
    }

    private void loadCreateNewOptions() {
        TextButton.TextButtonStyle subOption = new TextButton.TextButtonStyle();
        subOption.font = skin.getFont("defaultFont");
        subOption.fontColor = Color.WHITE;

        TextButton takePictureBtn = new TextButton("TAKE A PICTURE", subOption);
        takePictureBtn.setUserObject(generateImageButton("Common/camera.png"));

        TextButton fromGalleryBtn = new TextButton("FROM GALLERY", subOption);
        fromGalleryBtn.setUserObject(generateImageButton("Common/gallery.png"));

        TextButton emptyRoomBtn = new TextButton("EMPTY ROOM", subOption);
        emptyRoomBtn.setUserObject(generateImageButton("Common/room.png"));

        newDesignOption = new Table(skin);
        newDesignOption.setFillParent(true);

        newDesignOption.background(new SpriteDrawable(new Sprite(new Texture("gradient-overlay.png"))));

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        for(TextButton button : Arrays.asList(takePictureBtn, fromGalleryBtn, emptyRoomBtn)){
            ImageButton imageButton = (ImageButton) button.getUserObject();
            newDesignOption.add(imageButton).width(width / 6).height(height / 3);
        }

        newDesignOption.row();

        for(TextButton button : Arrays.asList(takePictureBtn, fromGalleryBtn, emptyRoomBtn)) {
            button.pad(10);
            newDesignOption.add(button).width(width / 3);
        }

        takePictureBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent il, float x, float y) {
                openDeviceCamera();
            }
        });

        fromGalleryBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent il, float x, float y) {
                FileHandle fileHandle = Gdx.files.internal("Rooms/Images/room1.jpg");
                Main.getInstance().setScreen(new RoomSetupScreen(fileHandle, false));
//                openDeviceGallery();
            }
        });

        emptyRoomBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final EmptyRoomSelector c = EmptyRoomSelector.construct(stage);
                ToolUtils.removeScreenInputProcessor(stage);
                stage.addActor(c);
            }
        });

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = SkinManager.getDefaultSkin().getFont("defaultFont");
        textButtonStyle.fontColor = Color.WHITE;
        backButton = new TextButton("BACK", textButtonStyle);
        backButton.setBounds((width / 2) - 50f, 5f, 100f, 50f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                newDesignOption.remove();
                backButton.remove();
            }
        });
    }

    private void loadTween() {
        manager = new TweenManager();
        Tween.registerAccessor(Image.class, new ImageOpacityAccessor());
        Tween.to(gradientA, ImageOpacityAccessor.ALPHA, 6f)
                .target(0.4f)
                .repeatYoyo(-1, 0)
                .ease(Bounce.INOUT)
                .start(manager);

        Tween.to(gradientB, ImageOpacityAccessor.ALPHA, 5f)
                .target(0.7f)
                .delay(3f)
                .repeatYoyo(-1, 0)
                .ease(Bounce.INOUT)
                .start(manager);
    }

    public static void openDeviceCamera() {
        Object[][] directory = {{"savedirectory", Main.screenTemplateSaveDirectory}};
        Main.getInstance().setScreen(new ImageSelectionScreen());
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.IMAGE_CAPTURE,
                ToolUtils.createMapFromList(directory));
    }

    public static void openDeviceGallery() {
        Main.getInstance().setScreen(new ImageSelectionScreen());
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.GET_IMAGE_FROM_GALLERY,
                null);
    }

    private void setDisableMenuButton(boolean disable) {
        createNewBtn.setDisabled(disable);
        catalogBtn.setDisabled(disable);
    }

    private void addListeners() {
        createNewBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent il, float x, float y) {
                stage.addActor(newDesignOption);
                stage.addActor(backButton);
                setDisableMenuButton(true);
            }
        });

        helpBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new TutorialPanel());
            }
        });
    }

    private TextButton.TextButtonStyle createButtonStyle(Color color) {
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("optionBackground");
        textButtonStyle.font = skin.getFont("defaultTitleFont");
        textButtonStyle.fontColor = color;
        return textButtonStyle;
    }

    private void createFonts() {
        FileHandle fontFile = Gdx.files.internal("data/Calibri.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 26;
        textFont = generator.generateFont(parameter);
        parameter.size = 28;
        titleFont = generator.generateFont(parameter);
        parameter.size = 55;
        applicationNameFont = generator.generateFont(parameter);
        generator.dispose();
    }

    private ImageButton generateImageButton(String imagePath){
        return new ImageButton(new SpriteDrawable(new Sprite(new Texture(Gdx.files.internal(imagePath), true))));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        manager.update(delta);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        gradientA.draw(stage.getBatch(), 1);
        gradientB.draw(stage.getBatch(), 1);
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
