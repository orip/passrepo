package com.example.passrepo.model;

import java.util.List;
import java.util.Map;

import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.google.common.collect.Maps;

public class Model {
    public List<PasswordEntry> passwordEntries;
    public transient byte[] key;
    public transient ScryptParameters scryptParameters;
    
    private Map<String, PasswordEntry> idsToPasswordEntriesMap;

    public Model(byte[] key, ScryptParameters scryptParameters, List<PasswordEntry> passwordEntries) {
        this.key = key;
        this.scryptParameters = scryptParameters;
        this.passwordEntries = passwordEntries;
        this.idsToPasswordEntriesMap = Maps.newHashMap();
        populateIdsToPasswordEntriesMap(passwordEntries);
    }

    private void populateIdsToPasswordEntriesMap(List<PasswordEntry> passwordEntries) {
        long counter = 0;
        for (PasswordEntry passwordEntry : passwordEntries) {
            passwordEntry.id = Long.toString(counter++);
            idsToPasswordEntriesMap.put(passwordEntry.id, passwordEntry);
        }
    }
    
    public PasswordEntry getPasswordEntry(String id) {
        return idsToPasswordEntriesMap.get(id);
    }
}
