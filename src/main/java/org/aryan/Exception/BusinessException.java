package org.aryan.Exception;

public class BusinessException extends Exception{
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

    public BusinessException() {
    }
}
