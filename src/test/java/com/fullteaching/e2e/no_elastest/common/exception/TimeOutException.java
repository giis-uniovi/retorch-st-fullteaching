package com.fullteaching.e2e.no_elastest.common.exception;

public class TimeOutException extends Exception {

    private static final long serialVersionUID = 9200938683074869086L;

    public TimeOutException() {
        super();
    }

    public TimeOutException(String message) {
        super(message);
    }
}
