package com.ggwp.interfaces;

/**
 * Created by Dell on 1/21/2016.
 */
public interface AndroidOnlyInterface {

    public void toast(final String text);
    public void notification(final String title,final String text);
    public String takeSnapShot(final String saveDirectory);
    public String getScreenTemplateDir();

}
