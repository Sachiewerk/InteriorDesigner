package com.ggwp.interfaces;

/**
 * Created by Dell on 1/22/2016.
 */
public interface RequestResultListner {


    void OnRequestDone(Object result);

    AndroidOnlyInterface.RequestType getRequestType();
}
