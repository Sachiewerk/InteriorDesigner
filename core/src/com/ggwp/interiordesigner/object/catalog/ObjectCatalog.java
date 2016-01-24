package com.ggwp.interiordesigner.object.catalog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by Dell on 1/21/2016.
 */
public class ObjectCatalog{

    private static ObjectCatalog instance = null;

    final Container container;

    public static ObjectCatalog getCurrentInstance(){
        return instance;
    }

    public static void init(String title, Skin skin){
        instance = new ObjectCatalog(title,skin);
    }



    private ObjectCatalog(String title, Skin skin) {


        Label titleLbl = new Label("test", skin);
        Label titleLbl1 = new Label("test", skin);
        Label titleLbl2 = new Label("test", skin);
        Label titleLbl3 = new Label("test", skin);

        Table tbl = new Table();
        tbl.add(titleLbl).width(50);
        tbl.add(titleLbl1).expand().bottom().fillX();
        tbl.row();
        tbl.add(titleLbl2);
        tbl.add(titleLbl3);

        tbl.setFillParent(true);

        container = new Container<Table>(tbl);

        container.setFillParent(true);
    }

    public void show(Stage stage){
        stage.addActor(container);
    }
    public void hide(){
        container.remove();
    }

}
