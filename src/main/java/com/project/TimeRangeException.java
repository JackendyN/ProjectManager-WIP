package com.project;

import java.io.Serializable;

public class TimeRangeException extends RuntimeException implements Serializable {
    public TimeRangeException() {
        super("Invalid Time Range entered.");
    }
}
