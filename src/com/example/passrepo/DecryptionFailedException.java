package com.example.passrepo;

public class DecryptionFailedException extends PassRepoBaseSecurityException {
    public DecryptionFailedException() {
    }

    public DecryptionFailedException(String detailMessage) {
        super(detailMessage);
    }

    public DecryptionFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DecryptionFailedException(Throwable throwable) {
        super(throwable);
    }
}
