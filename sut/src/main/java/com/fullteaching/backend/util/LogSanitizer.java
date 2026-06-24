package com.fullteaching.backend.util;

/**
 * Utility to neutralize CR/LF characters in user-controlled values before they
 * are written to the application logs, preventing log forging/injection.
 */
public final class LogSanitizer {

    private LogSanitizer() {
    }

    public static String sanitize(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString().replaceAll("[\r\n]", "_");
    }
}
