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
import com.ggwp.interiordesigner.FurnitureSetupScreen;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.ToolUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SaveFileLoader extends Window {

    protected Stage stage;
    private SaveFileLoader instance;

    private SaveFilePanel selectedFile;
    private Map<String, SaveFilePanel> templates = new HashMap<String, SaveFilePanel>();

    public static SaveFileLoader construct(Stage stage) {
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        SaveFileLoader catalog = new SaveFileLoader("", new WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        catalog.stage = stage;
        catalog.instance = catalog;
        return catalog;
    }

    public SaveFileLoader(String title, WindowStyle style) {
        super(title, style);
        this.setFillParent(true);
        this.setModal(true);
        this.align(Align.left);
        loadSavedFiles();
    }

    private void loadSavedFiles() {
        Table layoutTable = new Table();
        layoutTable.setFillParent(true);
        layoutTable.defaults().left();
        layoutTable.columnDefaults(1).top().width(Gdx.graphics.getWidth());

        ScrollPane savedFilesScrollPane = new ScrollPane(createSavedFilesContainer());

        Table savedFilesContainer = new Table();
        savedFilesContainer.setFillParent(true);
        savedFilesContainer.left();
        savedFilesContainer.add(savedFilesScrollPane);
        savedFilesContainer.padTop(Gdx.graphics.getHeight() / 10);

        HorizontalGroup horizontalGroup = new HorizontalGroup();

        Table table = new Table();

        table.defaults().left();
        table.columnDefaults(2).fillX();

        ImageButton okButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/submitbtn.png"))));
        ImageButton cancelButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));
        ImageButton deleteButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/remove.png"))));

        TextButton textButton = new TextButton("Select Save File", SkinManager.getDefaultFillerButtonStyle());

        textButton.padLeft(10f);
        textButton.getLabel().setAlignment(Align.left);

        table.add(textButton).width(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 17) * 3);
        table.add(okButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);
        table.add(cancelButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);
        table.add(deleteButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadSelectedSavedFile();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getActors().removeValue(instance, true);
                ToolUtils.initInputProcessors(stage);
            }
        });

        deleteButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(selectedFile != null){
                    templates.remove(selectedFile.getName());
                    selectedFile.data.delete();
                    stage.getActors().removeValue(instance, true);
                    ToolUtils.initInputProcessors(stage);
                }
            }
        });

        horizontalGroup.addActor(table);
        layoutTable.add(horizontalGroup);
        layoutTable.row();
        layoutTable.add(savedFilesContainer);
        horizontalGroup.toFront();
        this.add(layoutTable);
    }

    private void loadSelectedSavedFile() {
        if (selectedFile != null) {
            stage.getActors().removeValue(instance, true);
            Main.getInstance().getScreen().dispose();

            Gson gson = new GsonBuilder().serializeNulls().create();

            String json = selectedFile.data.readString();
            SaveFile data = gson.fromJson(json, SaveFile.class);

            List<SaveFile.Object> objs = data.objects;

            List<SaveFile.TilePaint> pTiles = data.paintTiles;

            FurnitureSetupScreen furnitureSetupScreen = new FurnitureSetupScreen(null, data.roomDesignData);
            Main.getInstance().setScreen(furnitureSetupScreen);

            if (objs != null) {
                for (SaveFile.Object obj : objs) {
                    if (obj.assetName != null) {
                        furnitureSetupScreen.addObject(obj);
                    }
                }
            }

            if (pTiles != null) {
                for (SaveFile.TilePaint obj : pTiles) {
                        furnitureSetupScreen.paintSelectedTile(obj);
                }
            }
        }
    }

    private Container createSavedFilesContainer() {
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(30f);
        main.row();

        List<FileHandle> saveFileDataList = new ArrayList<FileHandle>();

        FileHandle dir = Gdx.files.absolute(ToolUtils.getSaveFileDirAbsolutePath());
        if (dir.exists()) {
            for (FileHandle handle : dir.list()) {
                saveFileDataList.add(handle);
            }
        }

        int i = 0;
        for (FileHandle data : saveFileDataList) {
            main.add(createSaveFileData(data));
            if (++i % 3 == 0)
                main.row();
        }
        return new Container(main);
    }

    private Container createSaveFileData(final FileHandle data) {
        SaveFilePanel main = new SaveFilePanel(data);

        main.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedFile != null) {
                    selectedFile.setIsSelected(false);
                }
                selectedFile = templates.get(data.name());
                templates.get(data.name()).setIsSelected(true);
            }
        });

        templates.put(data.name(), main);

        if(selectedFile == null){
            selectedFile = templates.get(data.name());
            selectedFile.setIsSelected(true);
        }

        return new Container(main);
    }

    private class SaveFilePanel extends Table {

        private final FileHandle data;
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

        public SaveFilePanel(FileHandle data) {
            super();
            this.data = data;
            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            float cardSize = ((Gdx.graphics.getWidth()) / (3f)) - 30f;

            //FileHandle handle = Gdx.files.internal("Rooms/Images/" + data.getBackgroundImage());
            Image image;
            if (data == null) {
                image = new Image((new Texture("Common/empty-thumb.png")));
            } else {
                image = new Image((new Texture("Common/room.png")));
            }

            add(image).width(cardSize).height(cardSize / (w / h));

            if (data != null) {
                row();
                add(new Label(data.name().replace(".dat", ""), SkinManager.getDefaultLabelStyle()));
            }
        }
    }
}

