package com.cxd.exception;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 16:36
 **/
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
