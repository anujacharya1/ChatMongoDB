package com.anuj.monsub;

/**
 * Created by anujacharya on 3/11/16.
 */
public class MonSubException extends RuntimeException {

    private String message;
    private Integer errorCode;

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public static MonSubException build(Integer errorCode, String message){
        MonSubException monSubException =  new MonSubException();
        monSubException.setErrorCode(errorCode);
        monSubException.setMessage(message);
        return monSubException;
    }
}
