package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interfaces.RequestResultListner;
import com.ggwp.interiordesigner.Main;
import com.ggwp.utils.ToolUtils;

/**
 * Created by Dell on 1/21/2016.
 */
public abstract class AppScreen extends InputAdapter implements Screen{

    //private final Main main;


    public AppScreen(){
        //this.main = main;
        /*Object[][] tests = {{"activescreen", this}
                };
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.SET_ACTIVE_SCREEN,
                ToolUtils.createMapFromList(tests));*/
    }

/*
    public Main getMain(){
        return main;
    }
*/

    public boolean OnBackPressed(){return false;}

    public void back(){
        if(OnBackPressed()){
            //dispose();
        }
    }

}
