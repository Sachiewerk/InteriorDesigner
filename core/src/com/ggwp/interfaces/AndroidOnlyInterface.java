package com.ggwp.interfaces;

import org.omg.CORBA.Request;

import java.awt.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 1/21/2016.
 */
public interface AndroidOnlyInterface {

    public static enum RequestType{
        SHOW_MESSAGE(1),SHOW_NOTIFICATION(2),IMAGE_CAPTURE(3),GET_IMAGE_FROM_GALLERY(4),GET_SCREEN_TEMPLATE_DIR(5),
        LOG(6)
        ;

        private final int requestCode;
        RequestType(int requestCode){
            this.requestCode = requestCode;
        }

        public int getRequestCode(){
            return requestCode;
        }
    }


/*    public void toast(final String text);
    public void notification(final String title,final String text);
    public String takeSnapShot(final String saveDirectory);
    public String getScreenTemplateDir();*/

    public void requestOnDevice(RequestType rType, Map<String,Object> params);

    public void addResultListener(RequestResultListner resultListner);

}
