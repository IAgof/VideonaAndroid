package com.videonasocialmedia.videona.eventbus.events.error;

/**
 * Created by Veronica Lago Fominaya on 26/08/2015.
 */
public abstract class ErrorEvent {
    public final String message;
    public final Throwable exception;

    public ErrorEvent(Throwable exception, String message) {
        this.exception = exception;
        this.message = message;
    }
}
