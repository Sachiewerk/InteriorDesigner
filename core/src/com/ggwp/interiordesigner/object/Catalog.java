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
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
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
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.RoomWithHUD;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.ToolUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Raymond on 1/22/2016.
 */
public class Catalog extends Window {

    protected Stage stage;
    protected AssetManager assets;
    protected Array<GameObject> furnitures;
    protected InputMultiplexer inputMultiplexer;
    protected AppScreen appScreen;
    private Catalog instance;

    private Table layoutTable;
    private Table furnituresContainer;
    private ScrollPane categoriesScrollPane;
    private ScrollPane furnituresScrollPane;

    public static Catalog construct(Stage stage, AssetManager assets, Array<GameObject> furnitures, InputMultiplexer inputMultiplexer, AppScreen appScreen){
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        Catalog catalog = new Catalog("",new Window.WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        catalog.stage = stage;
        catalog.assets = assets;
        catalog.furnitures = furnitures;
        catalog.inputMultiplexer = inputMultiplexer;
        catalog.appScreen = appScreen;
        catalog.instance = catalog;
        return catalog;
    }


    public Catalog(String title, WindowStyle style) {
        super(title, style);
        this.setFillParent(true);
        this.setModal(true);
        this.align(Align.left);
        initCategories();
    }

    private void initInputProcessors(){
        inputMultiplexer.getProcessors().clear();
        inputMultiplexer.addProcessor(appScreen);
        inputMultiplexer.addProcessor(stage);
    }


    private void initCategories(){
        VerticalGroup categories = new VerticalGroup();

        layoutTable = new Table();
        layoutTable.setFillParent(true);
        layoutTable.defaults().left();
        layoutTable.columnDefaults(0).width(250f);
        layoutTable.columnDefaults(1).top().width(Gdx.graphics.getWidth() - 250f);

        categoriesScrollPane = new ScrollPane(categories);
        //furnituresScrollPane = new ScrollPane(createBedsContainer());

        furnituresContainer = new Table();
        furnituresContainer.setFillParent(true);
        furnituresContainer.left();

        furnituresContainer.add(furnituresScrollPane);

        layoutTable.add(categoriesScrollPane);
        layoutTable.add(furnituresContainer);



/*
        EventListener bedsClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresContainer.clear();
                furnituresScrollPane = new ScrollPane(createBedsContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener framesClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresContainer.clear();
                furnituresScrollPane = new ScrollPane(createFramesContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };*/

        TextButton.TextButtonStyle defaultTextButtonStyle = SkinManager.getDefaultTextButtonStyle();


        FileHandle[] directories = Gdx.files.internal("furnitures").list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                //return new File(current, name).isDirectory();
                return Gdx.files.internal(current+"/"+name).isDirectory();
            }
        });

        Object[][] tests = {{"title", "test error"},
                {"message", directories.length}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        System.out.println(Arrays.toString(directories));
        for (final FileHandle category: directories
             ) {

            categories.addActor(createCategoryContainer(category.name(), "Common/no-image.png", defaultTextButtonStyle, new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
/*                    furnituresContainer.clear();
                    furnituresScrollPane = new ScrollPane(createFramesContainer());
                    furnituresContainer.add(furnituresScrollPane);*/
                    furnituresContainer.clear();
                    furnituresScrollPane = new ScrollPane(createFurnitureList(category.name()));
                    furnituresContainer.add(furnituresScrollPane);

                }
            }));


        }


      /*  categories.addActor(createCategoryContainer("Bed with pillow/mattresses", "Common/no-image.png", defaultTextButtonStyle, bedsClikListener));
        categories.addActor(createCategoryContainer("Frames","Common/no-image.png",defaultTextButtonStyle,framesClikListener));
        categories.addActor(createCategoryContainer("Side Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Vase","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Lamps", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Dresser Cabinets/Drawers", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Vanity Tables and Chairs", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Sofa Set/Couch", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Coffee Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Tv Rack","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Book Shelves","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Mirrors","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Dining Set","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Kitchen Cabinets","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Wall Clock", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("TV", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Washing Machine", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Electric Fan", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Aircon", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Refridgerator", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Oven", "Common/no-image.png", defaultTextButtonStyle, null));*/

        this.add(layoutTable);
    }

    List<FileHandle> filePath = new ArrayList<FileHandle>();
    private void fileList(FileHandle dir){
        //Get list of all files and folders in directory
        FileHandle[] files = Gdx.files.internal(dir.path()).list();
        Object[][] tests = {{"title", dir.file().getName()+":"+dir.path()},
                {"message", files.length}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        //For all files and folders in directory
        for (int i = 0; i < files.length; i++) {
            //Check if directory
            if (files[i].isDirectory())
                //Recursively call file list function on the new directory
                fileList(files[i]);
            else{
                //If not directory, print the file path
                if(files[i].file().getAbsolutePath().toLowerCase().endsWith(".obj")){
                    System.out.println(files[i].file().getAbsolutePath());
                    filePath.add(files[i]);
                }

            }
        }
    }

    private Container createFurnitureList(final String category){

        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        filePath.clear();
        fileList(Gdx.files.internal("furnitures/" + category));
        Object[][] tests = {{"title", "test error"},
                {"message", filePath.size()}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));

        int i = 0;

        for (final FileHandle categoryfolder : filePath
                ) {

//            assets.load(categoryfolder.path(), Model.class);
            //System.out.println(categoryfolder.path()+": loaded");
            main.add(createFurnitureCard(categoryfolder.name(), "Common/no-image.png", new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Model model = assets.get(categoryfolder.path(), Model.class);
                    if(appScreen instanceof RoomWithHUD){
                        ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                    }

                    stage.getActors().removeValue(instance,true);
                    initInputProcessors();
                }
            }));

            if (++i % 3 == 0) {
                main.row();
            }
        }



/*        main.add(createFurnitureCard("b2", "Common/no-image.png", sofa2ClikListener));
        main.add(createFurnitureCard("b3", "Common/no-image.png", sofa3ClikListener));
        main.row();
        main.add(createFurnitureCard("b4", "Common/no-image.png", sofa4ClikListener));
        main.add(createFurnitureCard("b5", "Common/no-image.png", sofa5ClikListener));
        main.add(createFurnitureCard("b6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("b7", "Common/no-image.png", null));
        main.add(createFurnitureCard("b8", "Common/no-image.png", null));
        main.add(createFurnitureCard("b9", "Common/no-image.png", null));*/

        return new Container(main);

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
/*
    private Container createBedsContainer(){
        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        EventListener sofaClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Model model = assets.get("furnitures/tables/Table.obj", Model.class);
                if(appScreen instanceof RoomWithHUD){
                    ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                }

                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };

        EventListener sofa2ClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Model model = assets.get("furnitures/tables/CAB.obj", Model.class);
                if(appScreen instanceof RoomWithHUD){
                    ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                }

                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };
        EventListener sofa3ClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Model model = assets.get("furnitures/chair/chair2.obj", Model.class);
                if(appScreen instanceof RoomWithHUD){
                    ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                }

                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };
        EventListener sofa4ClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Model model = assets.get("furnitures/chair/chair3.obj", Model.class);
                if(appScreen instanceof RoomWithHUD){
                    ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                }

                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };

        EventListener sofa5ClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Model model = assets.get("furnitures/chair/chair4.obj", Model.class);
                if(appScreen instanceof RoomWithHUD){
                    ((RoomWithHUD) appScreen).addObject(model,GameObject.TYPE_FLOOR_OBJECT);
                }

                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };

        main.add(createFurnitureCard("b1", "Common/no-image.png", sofaClikListener));
        main.add(createFurnitureCard("b2", "Common/no-image.png", sofa2ClikListener));
        main.add(createFurnitureCard("b3", "Common/no-image.png", sofa3ClikListener));
        main.row();
        main.add(createFurnitureCard("b4", "Common/no-image.png", sofa4ClikListener));
        main.add(createFurnitureCard("b5", "Common/no-image.png", sofa5ClikListener));
        main.add(createFurnitureCard("b6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("b7", "Common/no-image.png", null));
        main.add(createFurnitureCard("b8", "Common/no-image.png", null));
        main.add(createFurnitureCard("b9", "Common/no-image.png", null));

        return new Container(main);
    }*/

   /* private Container createFramesContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Frames", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("f1", "Common/no-image.png", null));
        main.add(createFurnitureCard("f2", "Common/no-image.png", null));
        main.add(createFurnitureCard("f3", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("f4", "Common/no-image.png", null));
        main.add(createFurnitureCard("f5", "Common/no-image.png", null));
        main.add(createFurnitureCard("f6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("f7", "Common/no-image.png", null));
        main.add(createFurnitureCard("f8", "Common/no-image.png", null));
        main.add(createFurnitureCard("f9", "Common/no-image.png", null));

        return new Container(main);
    }*/

    private Container createFurnitureCard(String name,String img, EventListener listener){
        Table main = new Table();
        Image image = new Image((new Texture(img)));

        float cardSize = ((Gdx.graphics.getWidth()-250f) / (3f)) - 30f;

        main.add(image).width(cardSize).height(cardSize);
        if(listener != null){
            main.addListener(listener);
        }
        main.row();
        main.add(new Label(name, SkinManager.getDefaultLabelStyle()));
        return new Container(main);
    }
}
