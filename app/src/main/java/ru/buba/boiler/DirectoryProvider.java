package ru.buba.boiler;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DirectoryProvider {

    public static ArrayList<String> listofRaw() {
        Field[] fields = R.raw.class.getFields();
        ArrayList<String> list = new ArrayList<String>();
        for (Field field : fields) {
            list.add(field.getName()+ ".mp3");
        }
        return list;
    }

}
