package com.repomanager.models.exceptions;

import static com.repomanager.consts.ErrorResponseConsts.GENERIC_ERROR_RESPONSE;

public class GenericException extends RuntimeException {

    public GenericException() {

        super(GENERIC_ERROR_RESPONSE);

    }

    public GenericException(String message) {

        super(message);

    }

}
