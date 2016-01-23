package com.ggwp.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.object.RoomDesignData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

    private static final String SAVED_ROOM_DESIGN_DIRECTORY = "/savedrooms/";

    public static void saveRoomDataDesign(RoomDesignData data){
        try {
            System.out.println("Saving room data design..");
            FileHandle directory = Gdx.files.absolute(Main.aoi.getProjectDirectory() + SAVED_ROOM_DESIGN_DIRECTORY);
            createDirectoryIfNotExists(directory);

            String nextFileName = getRoomNextFileName(directory);
            System.out.println(nextFileName);

            FileHandle newFile = Gdx.files.absolute(Main.aoi.getProjectDirectory() + SAVED_ROOM_DESIGN_DIRECTORY + nextFileName);

//            if(newFile.exists()){
//                newFile.file().createNewFile();
//            }

//            newFile.file().createNewFile();

            System.out.println("Saving file..");
            JAXBContext jaxb = JAXBContext.newInstance(RoomDesignData.class);
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.setProperty(marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(data, newFile.file());
//
//            //Load file
            System.out.println("Loading file..");

            for(FileHandle fileHandle : directory.list()){
                try {
                    System.out.println(fileHandle.name());

                    Object[][] tests = {{"title", fileHandle.name()},
                            {"message", fileHandle.name()}};
                    Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                            ToolUtils.createMapFromList(tests));
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }

//            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
//            data = (RoomDesignData) unmarshaller.unmarshal(newFile.file());
//
//            for(Float f : data.getVertices()){
//                System.out.println(f);
//            }
//
//            i++;
//
//            FileHandle dir = Gdx.files.local("data/savedrooms/");
//            if(dir.exists() && dir.isDirectory()){
//                for(FileHandle h : dir.list()){
//                    System.out.println("Name: " + h.file().getName());
//                    System.out.println("Absolute Path: " + h.file().getName());
//                    System.out.println("- - -");
//                }
//            }

        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoomNextFileName(FileHandle directory){
        System.out.println("Getting room next file name..");
        Integer max = 0;

        for(FileHandle fileHandle : directory.list()){
            try {
                Integer id = Integer.parseInt(fileHandle.nameWithoutExtension());

                if(id > max){
                    max = id;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return String.valueOf(max + 1) + ".xml";
    }

    private static void createDirectoryIfNotExists(FileHandle directory) throws IOException {
        System.out.println("Checking directory..");
        if(directory.exists() == false){
            System.out.println("Creating directory..");
            directory.file().mkdirs();
        }
    }

}
