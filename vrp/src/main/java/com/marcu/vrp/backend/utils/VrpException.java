package com.marcu.vrp.backend.utils;

public class VrpException extends RuntimeException {
    public VrpException(String message) {
        super(message);
    }

    public VrpException(String message, Throwable cause) {
        super(message, cause);
    }

    public VrpException(Throwable cause) {
        super(cause);
    }
}
