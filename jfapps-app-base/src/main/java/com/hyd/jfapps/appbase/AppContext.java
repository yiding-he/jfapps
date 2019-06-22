package com.hyd.jfapps.appbase;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    private static final Map<String, Object> DATA = new HashMap<String, Object>();

    public static void put(String key, Object value) {
        DATA.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) DATA.get(key);
    }
}
