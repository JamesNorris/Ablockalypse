package com.github.jamesnorris.ablockalypse.aspect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;

import com.github.jamesnorris.ablockalypse.Ablockalypse;
import com.github.jamesnorris.ablockalypse.External;

public class PermanentAspect {
    public static final String[][] replacement = new String[][] { {"\\", ""}, {"/", ""}, {":", "="}, {"*", ""},
            {"?", ""}, {"\"", ""}, {"<", "("}, {">", ")"}, {"|", ""}};

    public static Object load(Class<?> cast, Map<String, Object> version) {
        try {
            Constructor<?> constr = cast.getConstructor(Map.class);
            if (constr == null) {
                Ablockalypse.getTracker().error("The PermanentAspect load constructor (" + cast.getSimpleName() + "(Map<String, Object> save)) was not found.", 10);
                return null;
            }
            if (!constr.isAccessible()) {
                constr.setAccessible(true);
            }
            return constr.newInstance(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String modifyForCompliance(String header) {
        String modHeader = header;
        for (int i = 0; i < replacement.length; i++) {
            modHeader = modHeader.replace(replacement[i][0], replacement[i][1]);
        }
        return modHeader;
    }

    public static File printData(PermanentAspect aspect) throws IOException {
        String header = modifyForCompliance(aspect.getHeader());
        File file = new File(Ablockalypse.getExternal().getPrintedSettingsFolder(), header + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        try {
            Map<String, Object> save = aspect.getSave();
            for (String key : save.keySet()) {
                Object value = save.get(key);
                writer.write(key + ": " + value + "\n");
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

    public static Map<String, Object> save(PermanentAspect aspect, File file) {
        try {
            Map<String, Object> save = aspect.getSave();
            save.put("saved_class_type", aspect.getClass().getName());
            External.save(save, file);
            return save;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final UUID uuid = UUID.randomUUID();

    public String getHeader() {
        return this.getClass().getSimpleName() + " <UUID: " + getUUID().toString() + ">";
    }

    public Map<String, Object> getSave() {
        return null;
    }

    public UUID getUUID() {
        return uuid;
    }
}
