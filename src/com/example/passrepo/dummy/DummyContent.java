package com.example.passrepo.dummy;

import java.util.List;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.model.Model;
import com.example.passrepo.model.PasswordEntry;
import com.google.common.collect.Lists;

public class DummyContent {
    public static Model model;

    public static final PasswordHasher.Keys dummyKeys;

    static {
        byte salt[] = new byte[]{}; // TODO: re-generate salt on every save?
        ScryptParameters scryptParameters = new ScryptParameters(salt);
        dummyKeys = PasswordHasher.hash("foo", scryptParameters);

        List<PasswordEntry> passwordEntries = Lists.newArrayList(
                new PasswordEntry("foo1", "foo_user", "foo_pass"),
                new PasswordEntry("foo2", "foo_user", "foo_pass"),
                new PasswordEntry("foo3", "foo_user", "foo_pass"),
                new PasswordEntry("foo4", "foo_user", "foo_pass"),
                new PasswordEntry("foo5", "foo_user", "foo_pass"),
                new PasswordEntry("foo6", "foo_user", "foo_pass"),
                new PasswordEntry("foo7", "foo_user", "foo_pass"),
        new PasswordEntry("bar", "bar_user", "bar_pass"),
        new PasswordEntry("baz", "baz_user", "baz_pass"),
        new PasswordEntry("xxx", "xxx_user", "xxx_pass"),
        new PasswordEntry("yyy", "yyy_user", "yyy_pass"),
        new PasswordEntry("zzz", "zzz_user", "zzz_pass"));
        
        model = new Model(dummyKeys, scryptParameters, passwordEntries);
    }
}
