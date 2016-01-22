package com.ggwp.interiordesigner.object;

import com.ggwp.interiordesigner.MenuScreen;
import com.ggwp.interiordesigner.RoomSetupScreen;
import com.ggwp.interiordesigner.RoomWithHUD;

/**
 * Created by Dell on 1/21/2016.
 */
public enum AppScreens {

    Menus(MenuScreen.class),
    RoomSetup(RoomSetupScreen.class),
    EmptyRoom(RoomWithHUD.class);

    private final Class<? extends AppScreen> clazz;

    AppScreens(Class<? extends AppScreen> clazz){
        this.clazz = clazz;
    }

    public Class<? extends AppScreen> getClazz(){
        return clazz;
    }
}
