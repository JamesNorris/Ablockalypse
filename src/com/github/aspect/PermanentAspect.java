package com.github.aspect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;

import com.github.External;
import com.github.utility.serial.SavedVersion;

public class PermanentAspect {
    public static Object load(Class<?> cast, SavedVersion version) {
        try {
            Constructor<?> constr = cast.getConstructor(SavedVersion.class);
            if (!constr.isAccessible()) {
                constr.setAccessible(true);
            }
            return constr.newInstance(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File printData(PermanentAspect aspect) throws IOException {
        File file = new File(aspect.getHeader());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        try {
            Map<String, Object> save = aspect.getSave().getRawMap();
            for (String key : save.keySet()) {
                Object value = save.get(key);
                writer.write(key + ": " + value);
            }
        } catch (IOException e) {
            try {
                writer.write(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        writer.close();
        return file;
    }

    public static SavedVersion save(PermanentAspect aspect, File file) {
        try {
            SavedVersion save = aspect.getSave();
            External.save(save, file);
            return save;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final UUID uuid = UUID.randomUUID();

    public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID() + ">";
    }

    public SavedVersion getSave() {
        return null;
    }

    public UUID getUUID() {
        return uuid;
    }

    public File printData() {
        return null;
    }
}
