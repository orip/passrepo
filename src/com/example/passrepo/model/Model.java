package com.example.passrepo.model;

import java.util.List;

import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;

public class Model {
    public List<PasswordEntry> passwordEntries;
    public byte[] key;
    public ScryptParameters scryptParameters;

    public Model(byte[] key, ScryptParameters scryptParameters, List<PasswordEntry> passwordEntries) {
        this.key = key;
        this.scryptParameters = scryptParameters;
        this.passwordEntries = passwordEntries;
    }
}
