package com.hyd.jfapps.launcher;

public class AppLoadingException extends RuntimeException {

    public AppLoadingException() {
    }

    public AppLoadingException(String message) {
        super(message);
    }

    public AppLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppLoadingException(Throwable cause) {
        super(cause);
    }
}
