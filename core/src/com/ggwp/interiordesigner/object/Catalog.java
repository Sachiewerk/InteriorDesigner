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
import com.ggwp.interiordesigner.FurnitureSetupScreen;
import com.ggwp.interiordesigner.manager.SkinManager;

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
        furnituresContainer.add(grh).fillX();
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

        EventListener washingMachineClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createWashingMachineContainer());
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
                furnituresScrollPane = new ScrollPane(createTablesContainer());
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
                furnituresScrollPane = new ScrollPane(createSeatingsContainer());
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

        EventListener wallClockClickListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane.remove();
                furnituresScrollPane = new ScrollPane(createWallClockContainer());
                furnituresContainer.add(furnituresScrollPane);
            }
        };

        TextButton.TextButtonStyle defaultTextButtonStyle = SkinManager.getDefaultTextButtonStyle();
        categories.addActor(createCategoryContainer("Beds", "furnitures/categories/bed.png", defaultTextButtonStyle, bedsClickListener));
        categories.addActor(createCategoryContainer("Refrigerators", "furnitures/categories/refrigerator.png", defaultTextButtonStyle, refrigeratorClickListener));
        categories.addActor(createCategoryContainer("Ovens", "furnitures/categories/oven.png", defaultTextButtonStyle, ovenClickListener));
        categories.addActor(createCategoryContainer("Frames", "furnitures/categories/frame.png", defaultTextButtonStyle, framesClickListener));
        categories.addActor(createCategoryContainer("Tables", "furnitures/categories/sidetable.png", defaultTextButtonStyle, sideTableClickListener));
        categories.addActor(createCategoryContainer("Vase", "furnitures/categories/vase.png", defaultTextButtonStyle, vaseClickListener));
        categories.addActor(createCategoryContainer("Lamps", "furnitures/categories/lamp.png", defaultTextButtonStyle, lampClickListener));
        categories.addActor(createCategoryContainer("Cabinets", "furnitures/categories/dresser.png", defaultTextButtonStyle, dresserClickListener));
        categories.addActor(createCategoryContainer("Seatings", "furnitures/categories/sofa.png", defaultTextButtonStyle, sofaClickListener));
        categories.addActor(createCategoryContainer("Mirrors", "furnitures/categories/mirror.png", defaultTextButtonStyle, mirrorClickListener));
        categories.addActor(createCategoryContainer("Wall Clock", "furnitures/categories/wallclock.png", defaultTextButtonStyle, wallClockClickListener));
        categories.addActor(createCategoryContainer("TV", "furnitures/categories/tv.png", defaultTextButtonStyle, tvClickListener));
        categories.addActor(createCategoryContainer("Washing Machine", "furnitures/categories/washingmachine.png", defaultTextButtonStyle, washingMachineClickListener));
        categories.addActor(createCategoryContainer("Aircon/Electric Fan", "furnitures/categories/electricfan.png", defaultTextButtonStyle, electricFanClickListener));

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
                ((FurnitureSetupScreen) appScreen).addObject(modelName, model, gameObjectType);
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
        main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Bed with Quilt @ 6.5 x 4 x 9.5", "furnitures/bed/1/1.jpg", createListener("furnitures/bed/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Bed with Quilt @ 11.5 x 4 x 9.5", "furnitures/bed/2/2.jpg", createListener("furnitures/bed/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Alpine Bed @ 10 x 4.5 x 13", "furnitures/bed/3/3.jpg", createListener("furnitures/bed/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Alpine Bed @ 6.5 x 4.5 x 13", "furnitures/bed/4/4.jpg", createListener("furnitures/bed/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createFramesContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Frames", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Quadro Grunge @ 13.5 x 6", "furnitures/frames/1/1.jpg", createListener("furnitures/frames/1/1.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Tenor @ 8 x 5.5", "furnitures/frames/2/2.jpg", createListener("furnitures/frames/2/2.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("The Kissing Custom 11 x 7.5 ", "furnitures/frames/3/3.jpg", createListener("furnitures/frames/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Cyndi @ 7.42 x 3.43", "furnitures/frames/4/4.jpg", createListener("furnitures/frames/4/4.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Cindy @ 7.42 x 3.43\"", "furnitures/frames/5/5.jpg", createListener("furnitures/frames/5/5.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createRefrigeratorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Refrigerators", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();


        main.add(createFurnitureCard("LG Refrigerator @ 2.8 x 6.5 x 4", "furnitures/refrigerator/1/1.jpg", createListener("furnitures/refrigerator/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Duplex SMEG N210714 @ 3.8 x 10 x 5.5", "furnitures/refrigerator/2/2.jpg", createListener("furnitures/refrigerator/2/untitled.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Samsung RSA1STWP N161115 @ 3 x 11 x 3.5", "furnitures/refrigerator/3/3.jpg", createListener("furnitures/refrigerator/3/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createTVContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Televisions", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("TV with audio 48 @ 6.47 x 3.9 x 11.26\"", "furnitures/tv/1/1.jpg", createListener("furnitures/tv/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Samsung Smart LED 4K TV 42 @ 6.26 x 3.82 x 0.1\"", "furnitures/tv/2/2.jpg", createListener("furnitures/tv/2/model.g3db", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("TV 16:9 @ 8.68 x 8.79 x 4.34\"", "furnitures/tv/3/3.png", createListener("furnitures/tv/3/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createWashingMachineContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Washing Machines", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("LG WD-80302NUP @ 4.07 x 5.75 x 3.17", "furnitures/washingmachine/1/1.jpg", createListener("furnitures/washingmachine/1/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Dishwasher (white) @ 3.3 x 4 x 4", "furnitures/washingmachine/2/2.png", createListener("furnitures/washingmachine/2/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Dishwasher (black) @ 5 x 6.5 x 5.2", "furnitures/washingmachine/3/3.png", createListener("furnitures/washingmachine/3/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createVaseContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Vases", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Batur @ 2 x 4 x 2", "furnitures/vase/1/1.jpg", createListener("furnitures/vase/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Plain @ White 2 x 4 x 2", "furnitures/vase/2/2.jpg", createListener("furnitures/vase/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Silver @ 2 x 5 x 2", "furnitures/vase/3/3.jpg", createListener("furnitures/vase/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));

        return new Container(main);
    }

    private Container createElectricFanContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Electric Fans", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Wall fan 21 @ 3.34 x 6.26 x 2.28\"", "furnitures/electricfan/1/1.jpg", createListener("furnitures/electricfan/1/model.g3db", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Aircon @ 3.3 x 1.1 x 9\"", "furnitures/electricfan/2/2.jpg", createListener("furnitures/electricfan/2/2.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Fan Foil Aircon @ 3.86 x 3.96 x 2\"", "furnitures/electricfan/3/3.png", createListener("furnitures/electricfan/3/model.g3db", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Daikin 2 N020412 @ 6.47 x 3.9 x 11.26\"", "furnitures/electricfan/4/4.jpg", createListener("furnitures/electricfan/4/model.g3db", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Wall Fan @ 3.28 x 3.28 x 1.3\"", "furnitures/electricfan/5/5.png", createListener("furnitures/electricfan/5/model.g3db", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createOvenContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Ovens", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Oven @ 3.94 x 3.79 x 3.38\"", "furnitures/oven/1/1.png", createListener("furnitures/oven/1/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Double Oven @ 3.94 x 7.15 x 3.38\"", "furnitures/oven/2/2.png", createListener("furnitures/oven/2/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Oven (Black) @ 4.92 x 4.65 x 5.12\"", "furnitures/oven/3/3.png", createListener("furnitures/oven/3/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Small Oven @ 4.39 x 4.87 x 3.41\"", "furnitures/oven/4/4.png", createListener("furnitures/oven/4/model.g3db", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createWallClockContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Wall Clocks", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Grand Hotel 21 @ 0.96 x 0.52 x 0.11\"", "furnitures/wallclock/1/1.jpg", createListener("furnitures/wallclock/1/1.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Plain 29 @ 2.41 x 2.41 x 1.31\"", "furnitures/wallclock/2/2.jpg", createListener("furnitures/wallclock/2/2.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("cuckoo clock @ 4.21 x 7.6 x 4.21\"", "furnitures/wallclock/3/3.png", createListener("furnitures/wallclock/3/model.g3db", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createTablesContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Tables", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Walnut coffee table @ 6.5 x 2.2 x 3.5", "furnitures/tables/1/1.jpg", createListener("furnitures/tables/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Meja rias @ 6.5 x 9 x 3", "furnitures/tables/2/2.jpg", createListener("furnitures/tables/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("IKEA BUSKBO Coffee table @ 6.5 x 2.3 x 3.2", "furnitures/tables/3/3.jpg", createListener("furnitures/tables/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Vanity Table @ 7.3 x 8.3 x 3.5", "furnitures/tables/4/4.jpg", createListener("furnitures/tables/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Shaker-Style Side Table @ 7.7 x 4.5 x 2.7", "furnitures/tables/5/5.jpg", createListener("furnitures/tables/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Console Table @ 13.5 x 3.8 x 3.8", "furnitures/tables/6/6.jpg", createListener("furnitures/tables/6/6.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createLampContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Lamps", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Vintage @ 2 x 6.7 x 1.2", "furnitures/lamps/1/1.jpg", createListener("furnitures/lamps/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Giorgetti Planet @ 2.3 x 6 x 2.3", "furnitures/lamps/2/2.jpg", createListener("furnitures/lamps/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Wooden Wall Mounted Lamp @ 0.75 x 1 x 0.33", "furnitures/lamps/3/3.jpg", createListener("furnitures/lamps/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("", "Common/hud.png", null));
        return new Container(main);
    }

    private Container createSeatingsContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Seatings", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Long Black @ 9.7 x 3.2 x 3.2", "furnitures/sofa/1/1.jpg", createListener("furnitures/sofa/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Charcoal @ 7.8 x 3.2 x 4.6", "furnitures/sofa/2/2.jpg", createListener("furnitures/sofa/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Tufted couch @ 11.5 x 4.5 x 4.5", "furnitures/sofa/3/3.jpg", createListener("furnitures/sofa/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Soft Seat Lounge Reception Chair @ 3 x 2.5 x 2.5", "furnitures/sofa/4/4.jpg", createListener("furnitures/sofa/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Walnut Sofa Set @ 12 x 4 x 9", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Single Walnut Sofa @ 5.6 x 4 x 4.6", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/6/6.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Walnut Sofa @ 10.8 x 45 x 4.6", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/7/7.obj", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createDresserContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Cabinets", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Solid Wood @ 5 x 3 x 1.7", "furnitures/cabinets/1/1.jpg", createListener("furnitures/cabinets/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Solid Wood @ 5 x 6 x 1.7", "furnitures/cabinets/2/2.jpg", createListener("furnitures/cabinets/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Solid Wood @ 2.5 x 3 x 1.7", "furnitures/cabinets/3/3.jpg", createListener("furnitures/cabinets/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Cafelle Wood @ 6 x 3.5 x 1.5", "furnitures/cabinets/4/4.jpg", createListener("furnitures/cabinets/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Corner Dresser @ 5.3 x 7.5 x 4.3", "furnitures/cabinets/5/5.jpg", createListener("furnitures/cabinets/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Base Open @ 6 x 4.6 x 3", "furnitures/cabinets/6/6.jpg", createListener("furnitures/cabinets/6/6.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Book Shelf @ 4.7 x 5.5 x 1.6", "furnitures/cabinets/7/7.jpg", createListener("furnitures/cabinets/7/7.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Book Shelf @ 6.9 x 9.8 x 1.8", "furnitures/cabinets/8/8.jpg", createListener("furnitures/cabinets/8/8.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Book Shelf @ 14.5 x 5 x 1.3", "furnitures/cabinets/9/9.jpg", createListener("furnitures/cabinets/9/9.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createMirrorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Mirrors", SkinManager.getDefaultCatalogHeaderLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Romantic White @ 2.3 x 7 x 1.8", "furnitures/mirror/2/2.jpg", createListener("furnitures/mirror/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Wooden Frame @ 2 x 4 x 0.5", "furnitures/mirror/3/3.jpg", createListener("furnitures/mirror/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Wooden Frame @ 4 x 2 x 0.5", "furnitures/mirror/4/4.jpg", createListener("furnitures/mirror/4/4.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();

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

        String[] modelDetails = name.split("@");
        String modelName, size="";

        if(modelDetails.length==2){
            modelName = modelDetails[0].trim();
            size = modelDetails[1].trim();
        }
        else{
            modelName = modelDetails[0];
        }
        main.row();
        Table hg = new Table();
        hg.align(Align.left);
        Label l = new Label("Name: ", SkinManager.createLabelStyle(Color.GRAY));
        Label l1 = new Label(modelName, SkinManager.getDefaultLabelStyle());
        l1.setWidth(cardSize);
        l1.setWrap(true);
        hg.add(l).align(Align.topLeft);
        hg.add(l1).width(cardSize - 50f);
        hg.row();
        Label d = new Label("Size(inch): ", SkinManager.createLabelStyle(Color.GRAY));
        Label d1 = new Label(size, SkinManager.getDefaultLabelStyle());
        d1.setWidth(cardSize);
        d1.setWrap(true);
        hg.add(d).align(Align.topLeft).padTop(5f);
        hg.add(d1).width(cardSize - 50f);
        main.add(hg).padTop(10f).width(cardSize);

        Container container = new Container(main);
        return container;
    }
}
