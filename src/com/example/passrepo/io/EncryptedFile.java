package com.example.passrepo.io;

import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;

public class EncryptedFile {
    public ScryptParameters scryptParameters;
    public CipherText cipherText;

    public EncryptedFile(ScryptParameters scryptParameters, CipherText cipherText) {
        this.scryptParameters = scryptParameters;
        this.cipherText = cipherText;
    }
}
