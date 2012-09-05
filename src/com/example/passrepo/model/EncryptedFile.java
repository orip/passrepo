package com.example.passrepo.model;

import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;

public class EncryptedFile {
    public ScryptParameters scryptParameters;
    public byte[] encrypted;

    public EncryptedFile(ScryptParameters scryptParameters, byte[] encrypted) {
        this.scryptParameters = scryptParameters;
        this.encrypted = encrypted;
    }
}
