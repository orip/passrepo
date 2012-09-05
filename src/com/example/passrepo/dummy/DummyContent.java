package com.example.passrepo.dummy;

import java.util.List;
import java.util.Map;

import com.example.passrepo.model.PasswordEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DummyContent {
    public static List<PasswordEntry> ITEMS = Lists.newArrayList();
    public static Map<String, PasswordEntry> ITEM_MAP = Maps.newHashMap();

    static {
        int counter = 0;
        addItem(new PasswordEntry(Integer.toString(counter++), "foo", "foo_user", "foo_pass"));
        addItem(new PasswordEntry(Integer.toString(counter++), "bar", "bar_user", "bar_pass"));
        addItem(new PasswordEntry(Integer.toString(counter++), "baz", "baz_user", "baz_pass"));
        addItem(new PasswordEntry(Integer.toString(counter++), "xxx", "xxx_user", "xxx_pass"));
        addItem(new PasswordEntry(Integer.toString(counter++), "yyy", "yyy_user", "yyy_pass"));
        addItem(new PasswordEntry(Integer.toString(counter++), "zzz", "zzz_user", "zzz_pass"));
    }

    private static void addItem(PasswordEntry passwordEntry) {
        ITEMS.add(passwordEntry);
        ITEM_MAP.put(passwordEntry.id, passwordEntry);
    }
}
