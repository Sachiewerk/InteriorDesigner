package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.RoomWithHUD;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.ToolUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Raymond on 1/22/2016.
 */
public class EmptyRoomSelector extends Window {

    protected Stage stage;
    protected Array<ModelInstance> furnitures;
    protected InputMultiplexer inputMultiplexer;
    protected AppScreen appScreen;
    private EmptyRoomSelector instance;

    private Table layoutTable;
    private Table furnituresContainer;
    private ScrollPane categoriesScrollPane;
    private ScrollPane furnituresScrollPane;

    private RoomDesignPanel selectedTemplate;
    private Map<String,RoomDesignPanel> templates = new HashMap<String,RoomDesignPanel>();

    public static EmptyRoomSelector construct(Stage stage){
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        EmptyRoomSelector catalog = new EmptyRoomSelector("",new WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
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


    private void initCategories(){
        VerticalGroup categories = new VerticalGroup();

        layoutTable = new Table();
        layoutTable.setFillParent(true);
        layoutTable.defaults().left();
        //layoutTable.columnDefaults(0).width(250f);
        layoutTable.columnDefaults(1).top().width(Gdx.graphics.getWidth());

        categoriesScrollPane = new ScrollPane(categories);
        furnituresScrollPane = new ScrollPane(createBedsContainer());

        furnituresContainer = new Table();
        furnituresContainer.setFillParent(true);
        furnituresContainer.left();

        furnituresContainer.add(furnituresScrollPane);

        HorizontalGroup hg = new HorizontalGroup();

        Table t = new Table();

        t.defaults().left();
        t.columnDefaults(0).width(Gdx.graphics.getWidth()-200f);
        //t.columnDefaults(1).top().width(Gdx.graphics.getWidth());

        //table.add(image).width(40f).height(40f);
        //table.add(button).width(180f).height(40f);
        Label lbl1 = new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle());
        TextButton tb1 = new TextButton("OK",SkinManager.getDefaultSubmitTextButtonStyle());
        TextButton tb2 = new TextButton("Cancel",SkinManager.getDefaultCancelTextButtonStyle());

        t.add(lbl1);
        t.add(tb1).width(100);
        t.add(tb2).width(100).fillY();

        tb2.addListener(

                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        stage.getActors().removeValue(instance, true);
                        ToolUtils.initInputProcessors(stage);
                    //selectedTemplate
                    }
                });

        tb1.addListener(

                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(selectedTemplate!=null) {
                            stage.getActors().removeValue(instance, true);
                            Main.getInstance().getScreen().dispose();
                            Main.getInstance().setScreen(new RoomWithHUD(Gdx.files.internal(selectedTemplate.rdata.getBackgroundImage())));

                        }
                    }
                });

        //t.defaults().left().pad(20f).padRight(0);
//        t.columnDefaults(2).setActorX(Gdx.graphics.getWidth() - 300);
//        t.columnDefaults(3).setActorX(Gdx.graphics.getWidth()-200);
        //t.setFillParent(true);
        hg.addActor(t);

        layoutTable.add(hg);

        layoutTable.row();
        //layoutTable.add(categoriesScrollPane);
        layoutTable.add(furnituresContainer);

        hg.toFront();




        this.add(layoutTable);
    }

    private Table createCategoryContainer(String label, String img, TextButton.TextButtonStyle buttonStyle, EventListener listener) {
        Table table = new Table();
        Image image = new Image((new Texture(img)));
        TextButton button = new TextButton(label,buttonStyle);
        button.getLabel().setColor(Color.WHITE);
        button.getLabel().setAlignment(Align.left);

        if(listener != null){
            button.addListener(listener);
        }

        table.columnDefaults(0).center().pad(10f);
        table.columnDefaults(1).left().padRight(10f);

        table.add(image).width(40f).height(40f);
        table.add(button).width(180f).height(40f);
        return table;
    }

    private Container createBedsContainer(){
        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        //main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        EventListener sofaClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        };


        List<RoomDesignData> rdatalist = new ArrayList<RoomDesignData>();

        rdatalist.add(new RoomDesignData("f1", "Rooms/Images/room1.jpg"));
        rdatalist.add(new RoomDesignData("f2", "Rooms/Images/room2.jpg"));
        rdatalist.add(new RoomDesignData("f3", "Rooms/Images/room3.jpg"));
        rdatalist.add(new RoomDesignData("f4", "Rooms/Images/room4.jpg"));
        rdatalist.add(new RoomDesignData("f5", "Rooms/Images/room5.jpg"));
        rdatalist.add(new RoomDesignData("f6", "Rooms/Images/room6.jpg"));
        rdatalist.add(new RoomDesignData("f7", "Rooms/Images/room7.jpg"));
        rdatalist.add(new RoomDesignData("f8", "Rooms/Images/room8.jpg"));
        rdatalist.add(new RoomDesignData("f9", "Rooms/Images/room9.jpg"));

        int i = 0;
        for (RoomDesignData rdata:
                rdatalist) {
            main.add(createRoomDesignData(rdata));
            if(++i%3 == 0)
                main.row();
        }
        return new Container(main);
    }



    private Container createRoomDesignData(RoomDesignData rdata){


        RoomDesignPanel main = new RoomDesignPanel(rdata);

        final String name = rdata.getName();

        main.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //System.out.println("Selected:" + name);
                System.out.println("Selected:" + templates.get(name));
                if(selectedTemplate!=null)
                    selectedTemplate.setIsSelected(false);
                selectedTemplate = templates.get(name);
                templates.get(name).setIsSelected(true);
            }
        });


        templates.put(rdata.getName(),main);
        return new Container(main);
    }

    private class RoomDesignPanel extends Table{
        private final RoomDesignData rdata;

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            if(isSelected!=this.isSelected){
                if(isSelected)
                    background(SkinManager.getDefaultSkin().getDrawable("defaultSubmitButton"));
                else
                    background(SkinManager.getDefaultSkin().getDrawable("clearTexture"));
            }
            this.isSelected = isSelected;
        }

        private boolean isSelected = false;

        public RoomDesignPanel(RoomDesignData rdata){
            super();
            this.rdata = rdata;

            float w=Gdx.graphics.getWidth();
            float h=Gdx.graphics.getHeight();
            float cardSize = ((Gdx.graphics.getWidth()) / (3f)) - 30f;

            Image image = new Image((new Texture(rdata.getBackgroundImage())));

            add(image).width(cardSize).height(cardSize / (w / h));

            row();
            add(new Label(rdata.getName(), SkinManager.getDefaultLabelStyle()));

        }



    }
}
