package com.ggwp.interfaces;

public interface RequestResultListener {

    void OnRequestDone(Object result);

    AndroidOnlyInterface.RequestType getRequestType();
}
