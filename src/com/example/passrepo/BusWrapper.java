package com.example.passrepo;

import com.squareup.otto.Bus;

public class BusWrapper {
    // TODO: inject this with Dagger instead
    public static final Bus globalBus = new Bus();
}
