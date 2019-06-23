package com.hyd.jfapps.appbase;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    public static final String APP_ICON = "AppIcon";

    private final Map<String, Object> DATA = new HashMap<String, Object>();

    public void put(String key, Object value) {
        DATA.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) DATA.get(key);
    }
}
