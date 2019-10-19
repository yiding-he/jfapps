package com.hyd.jfapps.zkclient.config;

import java.util.*;
import java.util.prefs.Preferences;
import org.apache.commons.lang3.StringUtils;

public class UserPreferences {

    public static final String SPLIT = ";";

    private static Preferences getPreferences() {
        return Preferences.userRoot().node("com.hyd.zkclient");
    }

    public static void save(Config key, String value) {
        Preferences preferences = getPreferences();
        preferences.put(key.name(), value);
    }

    public static void append(Config key, String value) {
        Preferences preferences = getPreferences();
        String[] split = preferences.get(key.name(), "").split(SPLIT);
        if (split.length == 0) {
            preferences.put(key.name(), value);
        } else {
            List<String> values = new ArrayList<>(new HashSet<>(Arrays.asList(split)));
            if (!values.contains(value)) {
                values.add(value);
            }
            values.removeIf(StringUtils::isBlank);
            preferences.put(key.name(), String.join(SPLIT, values));
        }
    }

    public static List<String> get(Config key) {
        Preferences preferences = getPreferences();
        String[] split = preferences.get(key.name(), "").split(SPLIT);
        if (split.length == 0) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(split);
        }
    }
}
