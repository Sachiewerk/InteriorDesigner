package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Transform;
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
        initCategories();
    }

    private void initCategories() {
        Table layoutTable = new Table();
        layoutTable.setFillParent(true);
        layoutTable.defaults().left();
        layoutTable.columnDefaults(1).top().width(Gdx.graphics.getWidth());

        ScrollPane furnituresScrollPane = new ScrollPane(createBedsContainer());

        Table furnituresContainer = new Table();
        furnituresContainer.setFillParent(true);
        furnituresContainer.left();
        furnituresContainer.add(furnituresScrollPane);

        HorizontalGroup hg = new HorizontalGroup();

        Table table = new Table();

        table.defaults().left();
        table.columnDefaults(2).fillX();

        ImageButton okButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/submitbtn.png"))));

        //okButton.background(SkinManager.getDefaultSubmitTextButtonStyle().up);

        //new ImageButton("OK", SkinManager.getDefaultSubmitTextButtonStyle());
        ImageButton cancelButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));
        //cancelButton.background(SkinManager.getDefaultCancelTextButtonStyle().up);


        //table.setWidth(Gdx.graphics.getWidth());
        table.add(new TextButton("", SkinManager.getDefaultFillerButtonStyle())).width(Gdx.graphics.getWidth() - (Gdx.graphics.getWidth() / 17) * 2);
        table.add(okButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);
        table.add(cancelButton).width(Gdx.graphics.getWidth() / 17).height(Gdx.graphics.getHeight() / 10);
        //table.pad(10);
        //table.background(SkinManager.getDefaultSkin().getDrawable("optionBackground"));
        //table.setWidth(500);

        okButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (selectedFile != null) {

                            stage.getActors().removeValue(instance, true);
                            Main.getInstance().getScreen().dispose();

                            Gson gson = new GsonBuilder().serializeNulls().create();

                            System.out.println("reading..");
                            String json = new String(selectedFile.data.readString());
                            SaveFile data = gson.fromJson(json, SaveFile.class);

                            //HashMap<String,Object> map= new HashMap<String,Object>();
                            //List<Map<String,Object>> objs = (List<Map<String, Object>>) data.get("objects");
                            List<SaveFile.Object> objs = data.objects;

                            RoomWithHUD roomWithHUD = new RoomWithHUD(null, data.roomDesignDataData);
                            Main.getInstance().setScreen(roomWithHUD);

                            if (objs != null) {
                                for (SaveFile.Object obj :
                                        objs) {

                                    if (obj.assetName != null) {
                                        System.out.println("loading.." + obj.assetName);
                                        roomWithHUD.addObject(obj);
                                    }


                                }
                            }

//                    data.setName(data.getBackgroundImage());
                            // roomDataList.add(data);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                        }
                    }
                });

        cancelButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.getActors().removeValue(instance, true);
                        ToolUtils.initInputProcessors(stage);
                    }
                });

        hg.addActor(table);
        //hg.setFillParent(true);
        //hg.setWidth(500);
        layoutTable.add(hg);
        layoutTable.row();
        layoutTable.add(furnituresContainer);
        hg.toFront();
        this.add(layoutTable);
    }

    private Container createBedsContainer() {
        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        //main.setHeight(Gdx.graphics.getHeight());
        //main.setFillParent(true);
        main.row();

        List<FileHandle>  saveFileDataList = new ArrayList<FileHandle>();

        //Gson gson = new GsonBuilder().serializeNulls().create();
        FileHandle dir = Gdx.files.absolute(ToolUtils.getSaveFileDirAbsolutePath());
        if (dir.exists()) {
            for (FileHandle handle : dir.list()) {
//                try {
                    //String json = new String(handle.readString());
                    //RoomDesignData data = gson.fromJson(json, RoomDesignData.class);
//                    data.setName(data.getBackgroundImage());
                     saveFileDataList.add(handle);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

        int i = 0;
        for (FileHandle data : saveFileDataList) {
            main.add(createSaveFileData(data));
            if (++i % 3 == 0)
                main.row();
        }
        while(i<9){
            main.add(createEmptyData());
            if (++i % 3 == 0)
                main.row();
        }


        return new Container(main);
    }

    private Container createEmptyData() {
        SaveFilePanel main = new SaveFilePanel(null);

//        final String name = data.getName();


//        templates.put(data.getName(), main);
        return new Container(main);
    }


    private Container createSaveFileData(final FileHandle data) {
        SaveFilePanel main = new SaveFilePanel(data);

//        final String name = data.getName();

        main.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Selected:" + templates.get(data.name()));

                if (selectedFile != null) {
                    selectedFile.setIsSelected(false);
                }

                selectedFile = templates.get(data.name());
                templates.get(data.name()).setIsSelected(true);
            }
        });

//        templates.put(data.getName(), main);
        templates.put(data.name(), main);
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
            if(data==null){
                image = new Image((new Texture("Common/empty-thumb.png")));
            }
            else {
                image = new Image((new Texture("Common/room.png")));
            }

            add(image).width(cardSize).height(cardSize / (w / h));


            if(data!=null) {
                row();
                add(new Label(data.name(), SkinManager.getDefaultLabelStyle()));
            }
//            add(new Label(data.getName(), SkinManager.getDefaultLabelStyle()));
        }
    }
}

