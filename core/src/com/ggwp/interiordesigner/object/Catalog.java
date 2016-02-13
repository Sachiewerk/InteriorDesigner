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
        categories.addActor(createCategoryContainer("Beds", "furnitures/categories/bed.png", defaultTextButtonStyle, bedsClickListener));
        categories.addActor(createCategoryContainer("Refrigerators", "furnitures/categories/refrigerator.png", defaultTextButtonStyle, refrigeratorClickListener));
//        categories.addActor(createCategoryContainer("Ovens", "furnitures/categories/oven.png", defaultTextButtonStyle, ovenClickListener));
        categories.addActor(createCategoryContainer("Frames", "furnitures/categories/frame.png", defaultTextButtonStyle, framesClickListener));
        categories.addActor(createCategoryContainer("Tables", "furnitures/categories/sidetable.png", defaultTextButtonStyle, sideTableClickListener));
        categories.addActor(createCategoryContainer("Vase", "furnitures/categories/vase.png", defaultTextButtonStyle, vaseClickListener));
        categories.addActor(createCategoryContainer("Lamps", "furnitures/categories/lamp.png", defaultTextButtonStyle, lampClickListener));
        categories.addActor(createCategoryContainer("Cabinets", "furnitures/categories/dresser.png", defaultTextButtonStyle, dresserClickListener));
        categories.addActor(createCategoryContainer("Seatings", "furnitures/categories/sofa.png", defaultTextButtonStyle, sofaClickListener));
        categories.addActor(createCategoryContainer("Mirrors", "furnitures/categories/mirror.png", defaultTextButtonStyle, mirrorClickListener));
        categories.addActor(createCategoryContainer("Dining Set", "furnitures/categories/dining.png", defaultTextButtonStyle, diningClickListener));
        categories.addActor(createCategoryContainer("Wall Clock", "furnitures/categories/wallclock.png", defaultTextButtonStyle, wallClockClickListener));
        categories.addActor(createCategoryContainer("TV", "furnitures/categories/tv.png", defaultTextButtonStyle, tvClickListener));
        categories.addActor(createCategoryContainer("Washing Machine", "furnitures/categories/washingmachine.png", defaultTextButtonStyle, washingMachineClickListener));
        categories.addActor(createCategoryContainer("Electric Fan", "furnitures/categories/electricfan.png", defaultTextButtonStyle, electricFanClickListener));
//        categories.addActor(createCategoryContainer("Aircon", "furnitures/categories/aircon.png", defaultTextButtonStyle, airconClickListener));

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

        main.add(createFurnitureCard("Bed with Quilt 6.5x4x9.5", "furnitures/bed/1/1.jpg", createListener("furnitures/bed/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Bed with Quilt 11.5x4x9.5", "furnitures/bed/2/2.jpg", createListener("furnitures/bed/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Alpine Bed 10x4.5x13", "furnitures/bed/3/3.jpg", createListener("furnitures/bed/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Alpine Bed 6.5x4.5x13", "furnitures/bed/4/4.jpg", createListener("furnitures/bed/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createFramesContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Frames", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Quadrp Grunge 13.5x6", "furnitures/frames/1/1.jpg", createListener("furnitures/frames/1/1.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Tenor 8x5.5", "furnitures/frames/2/2.jpg", createListener("furnitures/frames/2/2.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("The Kissing Custom 11x7.5 ", "furnitures/frames/3/3.jpg", createListener("furnitures/frames/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Cyndi", "furnitures/frames/4/4.jpg", createListener("furnitures/frames/4/4.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createRefrigeratorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Refrigerators", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();


        main.add(createFurnitureCard("LG Refrigerator ", "furnitures/refrigerator/1/1.jpg", createListener("furnitures/refrigerator/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        return new Container(main);
    }

    private Container createTVContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Televisions", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("TV with audio", "furnitures/tv/1/1.jpg", createListener("furnitures/tv/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Samsung Smart LED 4K TV", "furnitures/tv/2/2.jpg", createListener("furnitures/tv/2/2.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createWashingMachineContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Washing Machines", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("LG WD-80302NUP", "furnitures/washingmachine/1/1.jpg", createListener("furnitures/washingmachine/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        return new Container(main);
    }

    private Container createVaseContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Vases", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Batur 2x4x2", "furnitures/vase/1/1.jpg", createListener("furnitures/vase/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Plain White 2x4x2", "furnitures/vase/2/2.jpg", createListener("furnitures/vase/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Silver 2x5x2", "furnitures/vase/3/3.jpg", createListener("furnitures/vase/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();

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

        main.add(createFurnitureCard("Wall fan", "furnitures/electricfan/1/1.jpg", createListener("furnitures/electricfan/1/1.obj", GameObject.TYPE_WALL_OBJECT)));

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


        main.add(createFurnitureCard("Grand Hotel", "furnitures/wallclock/1/1.jpg", createListener("furnitures/wallclock/1/1.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Plain", "furnitures/wallclock/2/2.jpg", createListener("furnitures/wallclock/2/2.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createTablesContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Tables", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Walnut coffee table 6.5x2.2x3.5", "furnitures/tables/1/1.jpg", createListener("furnitures/tables/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Meja rias 6.5x9x3", "furnitures/tables/2/2.jpg", createListener("furnitures/tables/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("IKEA BUSKBO Coffee table 6.5x2.3x3.2", "furnitures/tables/3/3.jpg", createListener("furnitures/tables/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Vanity Table 7.3x8.3x3.5", "furnitures/tables/4/4.jpg", createListener("furnitures/tables/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Shaker-Style Side Table 7.7x4.5x2.7", "furnitures/tables/5/5.jpg", createListener("furnitures/tables/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Console Table 13.5x3.8x3.8", "furnitures/tables/6/6.jpg", createListener("furnitures/tables/6/6.obj", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createLampContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Lamps", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Vintage 2x6.7x1.2", "furnitures/lamps/1/1.jpg", createListener("furnitures/lamps/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Giorgetti Planet 2.3x6x2.3", "furnitures/lamps/2/2.jpg", createListener("furnitures/lamps/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Wooden Wall Mounted Lamp 0.75x1x0.33", "furnitures/lamps/3/3.jpg", createListener("furnitures/lamps/3/3.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }

    private Container createSeatingsContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Seatings", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Long Black", "furnitures/sofa/1/1.jpg", createListener("furnitures/sofa/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Charcoal", "furnitures/sofa/2/2.jpg", createListener("furnitures/sofa/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Tufted couch", "furnitures/sofa/3/3.jpg", createListener("furnitures/sofa/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Soft Seat Lounge Reception Chair", "furnitures/sofa/4/4.jpg", createListener("furnitures/sofa/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Walnut Sofa Set", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Single Walnut Sofa", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/6/6.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Walnut Sofa", "furnitures/sofa/5/5.jpg", createListener("furnitures/sofa/7/7.obj", GameObject.TYPE_FLOOR_OBJECT)));

        return new Container(main);
    }

    private Container createDresserContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Cabinets", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Solid Wood 5x3x1.7", "furnitures/cabinets/1/1.jpg", createListener("furnitures/cabinets/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Solid Wood 5x6x1.7", "furnitures/cabinets/2/2.jpg", createListener("furnitures/cabinets/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Solid Wood 2.5x3x1.7", "furnitures/cabinets/3/3.jpg", createListener("furnitures/cabinets/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Cafelle Wood 6x3.5x1.5", "furnitures/cabinets/4/4.jpg", createListener("furnitures/cabinets/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Corner Dresser 5.3x7.5x4.3", "furnitures/cabinets/5/5.jpg", createListener("furnitures/cabinets/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Base Open 6x4.6x3", "furnitures/cabinets/6/6.jpg", createListener("furnitures/cabinets/6/6.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Book Shelf 4.7x5.5x1.6", "furnitures/cabinets/7/7.jpg", createListener("furnitures/cabinets/7/7.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Book Shelf 6.9x9.8x1.8", "furnitures/cabinets/8/8.jpg", createListener("furnitures/cabinets/8/8.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Book Shelf 14.5x5x1.3", "furnitures/cabinets/9/9.jpg", createListener("furnitures/cabinets/9/9.obj", GameObject.TYPE_WALL_OBJECT)));

        return new Container(main);
    }



    private Container createBookShelfContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Book Shelves", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();


        main.add(createFurnitureCard("Small", "furnitures/bookshelves/1/1.jpg", createListener("furnitures/bookshelves/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Tall", "furnitures/bookshelves/2/2.jpg", createListener("furnitures/bookshelves/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Wall Mounted", "furnitures/bookshelves/3/3.jpg", createListener("furnitures/bookshelves/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        return new Container(main);
    }

    private Container createMirrorContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Mirrors", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();


        main.add(createFurnitureCard("Ribbon Mirror", "furnitures/mirror/1/1.jpg", createListener("furnitures/mirror/1/1.obj", GameObject.TYPE_WALL_OBJECT)));
        main.add(createFurnitureCard("Romantic White ", "furnitures/mirror/2/2.jpg", createListener("furnitures/mirror/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Wooden Frame(Portrait)", "furnitures/mirror/3/3.jpg", createListener("furnitures/mirror/3/3.obj", GameObject.TYPE_WALL_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Wooden Frame(Landscape)", "furnitures/mirror/4/4.jpg", createListener("furnitures/mirror/4/4.obj", GameObject.TYPE_WALL_OBJECT)));
        return new Container(main);
    }

    private Container createDiningContainer(){
        Table main = new Table();
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Dining Sets", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("Restaurant Table Setting(4)", "furnitures/dining/1/1.jpg", createListener("furnitures/dining/1/1.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Restaurant Table Setting(6)", "furnitures/dining/2/2.jpg", createListener("furnitures/dining/2/2.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Picnic Table", "furnitures/dining/3/3.jpg", createListener("furnitures/dining/3/3.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.row();
        main.add(createFurnitureCard("Fine Dining", "furnitures/dining/4/4.jpg", createListener("furnitures/dining/4/4.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Round Dining", "furnitures/dining/5/5.jpg", createListener("furnitures/dining/5/5.obj", GameObject.TYPE_FLOOR_OBJECT)));
        main.add(createFurnitureCard("Terrace Dining", "furnitures/dining/6/6.jpg", createListener("furnitures/dining/6/6.obj", GameObject.TYPE_FLOOR_OBJECT)));

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
