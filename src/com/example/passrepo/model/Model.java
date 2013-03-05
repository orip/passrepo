package com.example.passrepo.model;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Model {
    public transient PasswordHasher.Keys keys;
    public transient ScryptParameters scryptParameters;

    private List<PasswordEntry> passwordEntries;
    private long lastIndex;
    private Map<String, PasswordEntry> idsToPasswordEntriesMap;

    public Model(PasswordHasher.Keys keys, ScryptParameters scryptParameters, List<PasswordEntry> passwordEntries) {
        this.keys = keys;
        this.scryptParameters = scryptParameters;
        this.passwordEntries = passwordEntries;
        this.idsToPasswordEntriesMap = Maps.newHashMap();
        populateIdsToPasswordEntriesMap();
    }

    // TODO: Change to private.
    public void populateIdsToPasswordEntriesMap() {
        idsToPasswordEntriesMap.clear();
        lastIndex = 0;
        for (PasswordEntry passwordEntry : passwordEntries) {
            passwordEntry.id = Long.toString(lastIndex++);
            idsToPasswordEntriesMap.put(passwordEntry.id, passwordEntry);
        }
    }
    
    public PasswordEntry getPasswordEntry(String id) {
        return idsToPasswordEntriesMap.get(id);
    }
    
    public static Model currentModel = null;

    public List<PasswordEntry> getPasswordEntries() {
        return Collections.unmodifiableList(passwordEntries);
    }

    public void addPasswordEntry(PasswordEntry passwordEntry) {
        passwordEntry.id = Long.toString(lastIndex++);
        passwordEntries.add(passwordEntry);
        idsToPasswordEntriesMap.put(passwordEntry.id, passwordEntry);
    }
}
