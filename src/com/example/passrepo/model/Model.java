package com.example.passrepo.model;

import java.util.List;
import java.util.Map;

import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.google.common.collect.Maps;

public class Model {
    public List<PasswordEntry> passwordEntries;
    public transient byte[] key;
    public transient ScryptParameters scryptParameters;
    
    public Map<String, PasswordEntry> idsToPasswordEntriesMap;

    public Model(byte[] key, ScryptParameters scryptParameters, List<PasswordEntry> passwordEntries) {
        this.key = key;
        this.scryptParameters = scryptParameters;
        this.passwordEntries = passwordEntries;
        this.idsToPasswordEntriesMap = Maps.newHashMap();
        populateIdsToPasswordEntriesMap();
    }

    // TODO: Change to private.
    public void populateIdsToPasswordEntriesMap() {
        long index = 0;
        for (PasswordEntry passwordEntry : passwordEntries) {
            passwordEntry.id = Long.toString(index++);
            idsToPasswordEntriesMap.put(passwordEntry.id, passwordEntry);
        }
    }
    
    public PasswordEntry getPasswordEntry(String id) {
        return idsToPasswordEntriesMap.get(id);
    }
    
    public static Model currentModel = null;
}
