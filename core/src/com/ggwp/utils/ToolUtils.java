package com.ggwp.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 1/22/2016.
 */
public class ToolUtils {

    public static <T> T getParamValue(Object source,Class<T> cls,String paramName){
        if(source!=null&&source instanceof Map){
            Map map = (Map)source;
            if(map.containsKey(paramName)) {
                if (cls.isInstance(map.get(paramName))) return cls.cast(map.get(paramName));
            }
        }
        return null;
    }

    public static  Map<String,Object> createMapFromList(Object[][] arrays){
        HashMap<String,Object> obj = new HashMap<String, Object>();

        for (Object[] array:arrays
             ) {
            obj.put((String)array[0],array[1]);
        }

        return obj;

    }

    public static FileHandle findFileByAbsolutePath(String absolutePath){

          Object[][] tests = {{"title", "test error"},
                {"message", absolutePath}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        FileHandle tmplate = Gdx.files.absolute(absolutePath);
        /*File[] files = new File[tmplates.length];
        int i = 0;
        for (FileHandle fh:tmplates) {
            System.out.println(fh.file().getName());
            files[i++] = fh.file();
        }*/

        if(tmplate==null)
            return null;

        return tmplate;
    }

    public static FileHandle fetchLatestSnapshot(){
        System.out.println(Main.screenTemplateSaveDirectory);

        FileHandle[] tmplates = Gdx.files.absolute(Main.screenTemplateSaveDirectory).list();
        /*File[] files = new File[tmplates.length];
        int i = 0;
        for (FileHandle fh:tmplates) {
            System.out.println(fh.file().getName());
            files[i++] = fh.file();
        }*/

        if(tmplates==null)
            return null;

        if(tmplates.length==0){
            return null;
        }
        Arrays.sort(tmplates, new Comparator<FileHandle>() {
            public int compare(FileHandle f1, FileHandle f2) {
                // sort latest first
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        for (FileHandle fhx:tmplates) {
            System.out.println(fhx.file().getName());
        }
        return tmplates[0];
    }
}
