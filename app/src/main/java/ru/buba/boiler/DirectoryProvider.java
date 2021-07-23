package ru.buba.boiler;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DirectoryProvider {

    public static ArrayList<String> listofRaw() {
        Field[] fields = R.raw.class.getFields();
        ArrayList<String> list = new ArrayList<String>();
        for (Field field : fields) {
            list.add(field.getName() + ".mp3");
        }
        return list;
    }

}
