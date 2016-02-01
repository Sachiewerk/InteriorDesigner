package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.RoomWithHUD;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.ToolUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EmptyRoomSelector extends Window {

    protected Stage stage;
    private EmptyRoomSelector instance;

    private RoomDesignPanel selectedTemplate;
    private Map<String, RoomDesignPanel> templates = new HashMap<String, RoomDesignPanel>();


    public static EmptyRoomSelector construct(Stage stage) {
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        EmptyRoomSelector catalog = new EmptyRoomSelector("", new WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        catalog.stage = stage;
        catalog.instance = catalog;
        return catalog;
    }

    public EmptyRoomSelector(String title, WindowStyle style) {
        super(title, style);
        this.setFillParent(true);
        this.setModal(true);
        this.align(Align.left);
        initCategories();
    }

    private void initCategories() {
        Table layoutTable = new Table();
        layoutTable.setFillParent(true);
        layoutTable.defaults().left();
        layoutTable.columnDefaults(1).top().width(Gdx.graphics.getWidth());

        ScrollPane savedFilesScrollPane = new ScrollPane(createSavedFilesContainer());

        Table saveFilesContainer = new Table();
        saveFilesContainer.setFillParent(true);
        saveFilesContainer.left();
        saveFilesContainer.add(savedFilesScrollPane);
        saveFilesContainer.padTop(Gdx.graphics.getHeight() / 10);

        HorizontalGroup hg = new HorizontalGroup();

        Table table = new Table();
        table.defaults().left();
        table.columnDefaults(0).width(Gdx.graphics.getWidth() - 200f);

        ImageButton okButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/submitbtn.png"))));
        ImageButton cancelButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));
        okButton.setBackground(SkinManager.getDefaultSkin().getDrawable("defaultFillerSkin"));
        cancelButton.setBackground(SkinManager.getDefaultSkin().getDrawable("defaultFillerSkin"));

        TextButton tb = new TextButton("Select Empty Room", SkinManager.getDefaultFillerButtonStyle());
        tb.padLeft(10f);
        tb.getLabel().setAlignment(Align.left);

        table.add(tb).width(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 17) * 2);
        table.add(okButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);
        table.add(cancelButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedTemplate != null) {
                    stage.getActors().removeValue(instance, true);
                    Main.getInstance().getScreen().dispose();
                    FileHandle handle = Gdx.files.internal("Rooms/Images/" + selectedTemplate.data.getBackgroundImage());
                    RoomWithHUD roomWithHUD = new RoomWithHUD(null, selectedTemplate.data);
                    Main.getInstance().setScreen(roomWithHUD);
                }
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getActors().removeValue(instance, true);
                ToolUtils.initInputProcessors(stage);
            }
        });

        hg.addActor(table);
        layoutTable.add(hg);
        layoutTable.row();
        layoutTable.add(saveFilesContainer);
        hg.toFront();
        this.add(layoutTable);
    }

    private Container createSavedFilesContainer() {
        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.row();

        List<RoomDesignData> roomDataList = new ArrayList<RoomDesignData>();

        Gson gson = new GsonBuilder().serializeNulls().create();
        FileHandle dir = Gdx.files.internal("Rooms/Json/");
        if (dir.exists()) {
            for (FileHandle handle : dir.list()) {
                String json = handle.readString();
                RoomDesignData data = gson.fromJson(json, RoomDesignData.class);
                roomDataList.add(data);
            }
        }

        int i = 0;
        for (RoomDesignData data : roomDataList) {
            main.add(createRoomDesignData(data));
            if (++i % 3 == 0)
                main.row();
        }
        return new Container(main);
    }

    private Container createRoomDesignData(RoomDesignData data) {
        RoomDesignPanel main = new RoomDesignPanel(data);
        final String name = data.getBackgroundImage();

        main.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedTemplate != null){
                    selectedTemplate.setIsSelected(false);
                }
                selectedTemplate = templates.get(name);
                templates.get(name).setIsSelected(true);
            }
        });
        templates.put(data.getBackgroundImage(), main);
        return new Container(main);
    }

    private class RoomDesignPanel extends Table {

        private final RoomDesignData data;
        private boolean isSelected = false;

        public void setIsSelected(boolean isSelected) {
            if (isSelected != this.isSelected) {
                if (isSelected)
                    background(SkinManager.getDefaultSkin().getDrawable("defaultSubmitButton"));
                else
                    background(SkinManager.getDefaultSkin().getDrawable("clearTexture"));
            }
            this.isSelected = isSelected;
        }

        public RoomDesignPanel(RoomDesignData data) {
            super();
            this.data = data;

            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            float cardSize = ((Gdx.graphics.getWidth()) / (3f)) - 30f;

            FileHandle handle = Gdx.files.internal(Main.DEFAULT_EMPTY_ROOM_DIR + data.getBackgroundImage().toLowerCase());
            Image image = new Image((new Texture(handle)));

            add(image).width(cardSize).height(cardSize / (w / h));

            row();
            add(new Label(data.getBackgroundImage(), SkinManager.getDefaultLabelStyle()));
        }
    }
}
