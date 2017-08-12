package com.pb.yabs.commons.exception;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public enum ErrorCode {
    UNKNOWN(42),
    ACCOUNT_NOT_FOUND(1),
    NOT_ENOUGH_MONEY(2),
    NEGATIVE_TRANSFER(3);

    private int value;
    ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
