package com.example.passrepo;

public class PassRepoBaseSecurityException extends Exception {
    public PassRepoBaseSecurityException() {
    }

    public PassRepoBaseSecurityException(String detailMessage) {
        super(detailMessage);
    }

    public PassRepoBaseSecurityException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PassRepoBaseSecurityException(Throwable throwable) {
        super(throwable);
    }
}
