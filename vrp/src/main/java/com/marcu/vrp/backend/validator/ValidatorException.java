package com.marcu.vrp.backend.validator;

import com.marcu.vrp.backend.utils.VrpException;

public class ValidatorException extends VrpException {
    public ValidatorException(String message) {
        super(message);
    }

    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidatorException(Throwable cause) {
        super(cause);
    }
}
