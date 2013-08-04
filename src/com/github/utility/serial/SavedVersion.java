package com.github.utility.serial;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SavedVersion implements Serializable, Map<String, Object> {
    private static final long serialVersionUID = -6129147135279870132L;
    private final String title, className;
    private final Map<String, Object> serialization;

    public SavedVersion(String title, Map<String, Object> serialization, Class<?> thisClass) {
        this.title = title;
        this.serialization = serialization;
        className = thisClass.getCanonicalName();
    }

    @Override public void clear() {
        serialization.clear();
    }

    @Override public boolean containsKey(Object key) {
        return serialization.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return serialization.containsValue(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) @Override public Set entrySet() {
        return serialization.entrySet();
    }

    @Override public Object get(Object key) {
        return serialization.get(key);
    }

    public Map<String, Object> getRawMap() {
        return serialization;
    }

    public String getTitle() {
        return title;
    }

    public Class<?> getVersionClass() {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public boolean isEmpty() {
        return serialization.isEmpty();
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) @Override public Set keySet() {
        return serialization.keySet();
    }

    @Override public Object put(String key, Object value) {
        return serialization.put(key, value);
    }

    @Override public void putAll(Map<? extends String, ? extends Object> m) {
        serialization.putAll(m);
    }

    @Override public Object remove(Object key) {
        return serialization.remove(key);
    }

    public void replace(String key, Object value) {
        serialization.remove(key);
        serialization.put(key, value);
    }

    @Override public int size() {
        return serialization.size();
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) @Override public Collection values() {
        return serialization.values();
    }
}
