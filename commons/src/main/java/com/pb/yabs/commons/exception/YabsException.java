package com.pb.yabs.commons.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class YabsException extends RuntimeException {

    private final ErrorCode errorCode;

    public YabsException(ErrorCode errorCode, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
