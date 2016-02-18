package com.ggwp.interfaces;

import java.util.Map;

public interface AndroidOnlyInterface {

    enum RequestType{
        SHOW_MESSAGE(1), SHOW_NOTIFICATION(2), IMAGE_CAPTURE(3), GET_IMAGE_FROM_GALLERY(4), GET_SCREEN_TEMPLATE_DIR(5),
        LOG(6), SAVE_FILE(7), SET_ACTIVE_SCREEN(9);

        private final int requestCode;
        RequestType(int requestCode){
            this.requestCode = requestCode;
        }

        public int getRequestCode(){
            return requestCode;
        }
    }

    void requestOnDevice(RequestType rType, Map<String,Object> params);

    void addResultListener(RequestResultListener resultListner);

    String getProjectDirectory();


}
