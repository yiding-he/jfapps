package com.hyd.jfapps.zkclient.event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Listeners {

    private static Map<Class, List<Listener>> listeners = new ConcurrentHashMap<>();

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

        // 复制一份 List 给当前线程使用，以免出现并发异常
        List<Listener> listenerList = new ArrayList<>(
            listeners.getOrDefault(event.getClass(), Collections.emptyList())
        );

        listenerList.forEach(listener -> {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                throw new EventException(e);
            }
        });
    }
}
