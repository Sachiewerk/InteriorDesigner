package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
import com.ggwp.interiordesigner.RoomWithHUD;
import com.ggwp.interiordesigner.manager.SkinManager;

import sun.awt.HorizBagLayout;


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
        furnituresScrollPane = new ScrollPane(createBedsContainer());

        furnituresContainer = new Table();
        furnituresContainer.setFillParent(true);
        furnituresContainer.left();

        Table grh = new Table();
        ImageButton cancelButton = new ImageButton(new SpriteDrawable(new Sprite(new Texture("Common/cancelbtn.png"))));
        TextButton tbuton = new TextButton("Furnitures", SkinManager.getDefaultFillerButtonStyle());

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getActors().removeValue(instance, true);
                initInputProcessors();
            }
        });

        grh.add(tbuton).width(Gdx.graphics.getWidth() - 250f - ((Gdx.graphics.getWidth() - 250f) / 13)).height(Gdx.graphics.getHeight() / 10);
        grh.add(cancelButton).width((Gdx.graphics.getWidth() - 250f) / 13).height(Gdx.graphics.getHeight()/10);
        furnituresContainer.add(grh).fillX();//.width(Gdx.graphics.getWidth()-250f-((Gdx.graphics.getWidth()-250f)/17)).height(Gdx.graphics.getHeight() / 10);
        //furnituresContainer.add(cancelButton).width((Gdx.graphics.getWidth()-250f)/17).height(Gdx.graphics.getHeight() / 10).left();
        furnituresContainer.row();
        furnituresContainer.add(furnituresScrollPane);

        layoutTable.add(categoriesScrollPane);
        layoutTable.add(furnituresContainer);

        EventListener bedsClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createBedsContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener framesClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createFramesContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener refrigeratorClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createRefrigeratorContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener ovenClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createOvenContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener tvClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createTVContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener tvRackClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createTVRackContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener washingMachineClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createWashingMachineContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener airconClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createAirconContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener vaseClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createVaseContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener electricFanClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createElectricFanContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener sideTableClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createSideTableContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener lampClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createLampContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener dresserClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createDresserContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener sofaClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createSofaContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener coffeeTableClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createCoffeeTableContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener vanityTableClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createVanityTableContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener bookShelfClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createBookShelfContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener mirrorClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createMirrorContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener diningClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createDiningContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener kitchenClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createKitchenContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        EventListener wallClockClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createWallClockContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        TextButton.TextButtonStyle defaultTextButtonStyle = SkinManager.getDefaultTextButtonStyle();
        categories.addActor(createCategoryContainer("Bed with pillows", "furnitures/categories/bed.png", defaultTextButtonStyle, bedsClickListener));
        categories.addActor(createCategoryContainer("Refrigerators", "furnitures/categories/refrigerator.png", defaultTextButtonStyle, refrigeratorClickListener));
        categories.addActor(createCategoryContainer("Ovens", "furnitures/categories/oven.png", defaultTextButtonStyle, ovenClickListener));
        categories.addActor(createCategoryContainer("Frames", "furnitures/categories/frame.png", defaultTextButtonStyle, framesClickListener));
        categories.addActor(createCategoryContainer("Side Tables", "furnitures/categories/sidetable.png", defaultTextButtonStyle, sideTableClickListener));
        categories.addActor(createCategoryContainer("Vase", "furnitures/categories/vase.png", defaultTextButtonStyle, vaseClickListener));
        categories.addActor(createCategoryContainer("Lamps", "furnitures/categories/lamp.png", defaultTextButtonStyle, lampClickListener));
        categories.addActor(createCategoryContainer("Dresser Cabinets/Drawers", "furnitures/categories/dresser.png", defaultTextButtonStyle, dresserClickListener));
        categories.addActor(createCategoryContainer("Vanity Tables and Chairs", "furnitures/categories/vanitytable.png", defaultTextButtonStyle, vanityTableClickListener));
        categories.addActor(createCategoryContainer("Sofa Set/Couch", "furnitures/categories/sofa.png", defaultTextButtonStyle, sofaClickListener));
        categories.addActor(createCategoryContainer("Coffee Tables", "furnitures/categories/coffeetable.png", defaultTextButtonStyle, coffeeTableClickListener));
        categories.addActor(createCategoryContainer("Tv Rack", "furnitures/categories/tvrack.png", defaultTextButtonStyle, tvRackClickListener));
        categories.addActor(createCategoryContainer("Book Shelves", "furnitures/categories/book.png", defaultTextButtonStyle, bookShelfClickListener));
        categories.addActor(createCategoryContainer("Mirrors", "furnitures/categories/mirror.png", defaultTextButtonStyle, mirrorClickListener));
        categories.addActor(createCategoryContainer("Dining Set", "furnitures/categories/dining.png", defaultTextButtonStyle, diningClickListener));
        categories.addActor(createCategoryContainer("Kitchen Cabinets", "furnitures/categories/kitchen.png", defaultTextButtonStyle, kitchenClickListener));
        categories.addActor(createCategoryContainer("Wall Clock", "furnitures/categories/wallclock.png", defaultTextButtonStyle, wallClockClickListener));
        categories.addActor(createCategoryContainer("TV", "furnitures/categories/tv.png", defaultTextButtonStyle, tvClickListener));
        categories.addActor(createCategoryContainer("Washing Machine", "furnitures/categories/washingmachine.png", defaultTextButtonStyle, washingMachineClickListener));
        categories.addActor(createCategoryContainer("Electric Fan", "furnitures/categories/electricfan.png", defaultTextButtonStyle, electricFanClickListener));
        categories.addActor(createCategoryContainer("Aircon", "furnitures/categories/aircon.png", defaultTextButtonStyle, airconClickListener));

        this.add(layoutTable);
    }

    private Table createCategoryContainer(String label, String img, TextButton.TextButtonStyle buttonStyle, EventListener listener) {
        Table table = new Table();
        table.setBackground(SkinManager.getDefaultFillerButtonStyle().up);
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

    private ClickListener createListener(final String modelName, final int gameObjectType){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!assets.isLoaded(modelName)){
                    assets.load(modelName, Model.class);
                }

                assets.finishLoadingAsset(modelName);
                Model model = assets.get(modelName, Model.class);
                ((RoomWithHUD) appScreen).addObject(modelName,model, gameObjectType);
                stage.getActors().removeValue(instance, true);
                initInputProcessors();
            }
        };
    }

    private Container createBedsContainer(){
        Table main = new Table();

        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        ClickListener listener = createListener("furnitures/bed/bed1/bed1.obj", GameObject.TYPE_FLOOR_OBJECT);
        main.add(createFurnitureCard("b1", "Common/no-image.png", createListener("furnitures/bed/bed1/bed1.obj",GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("b2", "Common/no-image.png", createListener("furnitures/bed/bed2/bed2.obj",GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("b3", "Common/no-image.png", listener));
        main.row();
        main.add(createFurnitureCard("b4", "Common/no-image.png", listener));
        main.add(createFurnitureCard("b5", "Common/no-image.png", listener));
        main.add(createFurnitureCard("b6", "Common/no-image.png", listener));
        main.row();
        main.add(createFurnitureCard("b7", "Common/no-image.png", listener));
        main.add(createFurnitureCard("b8", "Common/no-image.png", listener));
        main.add(createFurnitureCard("b9", "Common/no-image.png", listener));

        return new Container(main);
    }

    private Container createFramesContainer(){
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
    }

    private Container createRefrigeratorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Refrigerators", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Refrigerator " + i, "furnitures/refrigerator/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createTVContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Televisions", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("TV " + i, "furnitures/tv/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createTVRackContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("TV Racks", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("TV Rack " + i, "furnitures/tvrack/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createWashingMachineContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Washing Machines", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Washing Machine " + i, "furnitures/washingmachine/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createVaseContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Vases", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Vase " + i, "furnitures/vase/" + i + ".png",
                    createListener("furnitures/vase/1/1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createAirconContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Aircons", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Aircon " + i, "furnitures/aircon/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createElectricFanContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Electric Fans", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Electric Fan " + i, "furnitures/electricfan/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createOvenContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Ovens", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Oven " + i, "furnitures/oven/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createWallClockContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Wall Clocks", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Wall Clock " + i, "furnitures/wallclock/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createSideTableContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Side Tables", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Side Table " + i, "furnitures/sidetable/" + i + ".png",
                    createListener("furnitures/tables/1/1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createLampContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Lamps", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Lamp " + i, "furnitures/lamps/1.jpg",
                    createListener("furnitures/lamps/1/1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createSofaContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Sofa/Couches", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Sofa " + i, "furnitures/sofa/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createDresserContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Dressers", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Dresser " + i, "furnitures/dresser/" + i + ".png",
                    createListener("furnitures/dresser/1/1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createCoffeeTableContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Coffee Tables", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Coffee Table " + i, "furnitures/coffeetable/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createVanityTableContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Vanity Tables", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Vanity Table " + i, "furnitures/vanitytable/" + i + ".png", createListener("furnitures/chair/chair"+i+".obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createBookShelfContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Book Shelfs", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Book Shelf " + i, "furnitures/wallclock/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createMirrorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Mirrors", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Mirror " + i, "furnitures/mirror/" + i + ".png",
                    createListener("furnitures/mirror/1/1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createDiningContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Dinings", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Dining " + i, "furnitures/dining/" + i + ".png",
                    createListener("furnitures/tables/tb1/tb1.obj",GameObject.TYPE_FLOOR_OBJECT)));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createKitchenContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Kitchens", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        for(int i = 1; i <= 9; i++){
            main.add(createFurnitureCard("Kitchen " + i, "furnitures/kitchen/" + i + ".png", null));

            if(i % 3 == 0){
                main.row();
            }
        }
        return new Container(main);
    }

    private Container createFurnitureCard(String name, String img, EventListener listener){
        Table main = new Table();
        Image image = new Image((new Texture(img)));

        float cardSize = ((Gdx.graphics.getWidth()-250f) / (3f)) - 30f;

        main.add(image).width(cardSize).height(cardSize);
        if (listener != null) {
            main.addListener(listener);
        }
        main.row();
        main.add(new Label(name, SkinManager.getDefaultLabelStyle())).padTop(10f);
        Container container = new Container(main);
        return container;
    }
}
