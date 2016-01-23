package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
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
import com.ggwp.interiordesigner.manager.SkinManager;


/**
 * Created by Raymond on 1/22/2016.
 */
public class EmptyRoomSelector_BAK extends Window {

    protected Stage stage;
    protected AssetManager assets;
    protected Array<ModelInstance> furnitures;
    protected InputMultiplexer inputMultiplexer;
    protected AppScreen appScreen;
    private EmptyRoomSelector_BAK instance;

    private Table layoutTable;
    private Table furnituresContainer;
    private ScrollPane categoriesScrollPane;
    private ScrollPane furnituresScrollPane;

    private RoomDesignData selectedTemplate;

    public static EmptyRoomSelector_BAK construct(Stage stage, AssetManager assets, InputMultiplexer inputMultiplexer, AppScreen appScreen){
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        EmptyRoomSelector_BAK catalog = new EmptyRoomSelector_BAK("",new WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        catalog.stage = stage;
        catalog.assets = assets;
        catalog.inputMultiplexer = inputMultiplexer;
        catalog.appScreen = appScreen;
        catalog.instance = catalog;
        return catalog;
    }


    public EmptyRoomSelector_BAK(String title, WindowStyle style) {
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

        tb1.addListener(

        new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                /*Furniture sofa = new Furniture(assets.get("sofa.obj", Model.class));
                sofa.transform.rotate(Vector3.X, -90);
                sofa.calculateTransforms();
                BoundingBox bounds = new BoundingBox();
                sofa.calculateBoundingBox(bounds);
                sofa.shape = new Box(bounds);
                furnitures.add(sofa);
                stage.getActors().removeValue(instance,true);
                initInputProcessors();*/
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
        };

        TextButton.TextButtonStyle defaultTextButtonStyle = SkinManager.getDefaultTextButtonStyle();
        categories.addActor(createCategoryContainer("Bed with pillow/mattresses", "Common/no-image.png", defaultTextButtonStyle, bedsClikListener));
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
        categories.addActor(createCategoryContainer("Oven", "Common/no-image.png", defaultTextButtonStyle, null));

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

        main.add(createFurnitureCard("b1", "Common/no-image.png", null));
        main.add(createFurnitureCard("b2", "Common/no-image.png", sofaClikListener));
        main.add(createFurnitureCard("b3", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("b4", "Common/no-image.png", null));
        main.add(createFurnitureCard("b5", "Common/no-image.png", null));
        main.add(createFurnitureCard("b6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("b7", "Common/no-image.png", null));
        main.add(createFurnitureCard("b8", "Common/no-image.png", null));
        main.add(createFurnitureCard("b9", "Common/no-image.png", null));

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

    private Container createFurnitureCard(String name,String img, EventListener listener){
        Table main = new Table();
        Image image = new Image((new Texture(img)));

        float w=Gdx.graphics.getWidth();
        float h=Gdx.graphics.getHeight();
        float cardSize = ((Gdx.graphics.getWidth()) / (3f)) - 30f;

//System.out.println(w+":"+h+":"+cardSize+":"+cardSize/(w/h)+":"+(w/h));
        main.add(image).width(cardSize).height(cardSize / (w / h));
        if(listener != null){
            main.addListener(listener);
        }

        main.row();
        main.add(new Label(name, SkinManager.getDefaultLabelStyle()));
        return new Container(main);
    }
}
