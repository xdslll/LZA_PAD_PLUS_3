package com.lza.pad.exception;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 15/2/6.
 */
public class DeviceNotFound extends RuntimeException {

    public DeviceNotFound() {
    }

    public DeviceNotFound(String detailMessage) {
        super(detailMessage);
    }
}
