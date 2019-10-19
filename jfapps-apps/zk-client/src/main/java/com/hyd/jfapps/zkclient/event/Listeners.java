package com.hyd.jfapps.zkclient.event;

import java.util.*;

public class Listeners {

    private static Map<Class, List<Listener>> listeners = new HashMap<>();

    @FunctionalInterface
    public interface Listener<T> {
        void onEvent(T event) throws Exception;
    }

    public static class EventException extends RuntimeException {

        EventException(Throwable cause) {
            super(cause);
        }
    }

    public static <T> void addListener(Class<T> eventType, Listener<T> listener) {
        listeners
            .computeIfAbsent(eventType, _e -> new ArrayList<>())
            .add(listener);
    }

    @SuppressWarnings("unchecked")
    public static <T> void publish(Object event) {
        listeners
            .getOrDefault(event.getClass(), Collections.emptyList())
            .forEach(listener -> {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    throw new EventException(e);
                }
            });
    }
}
