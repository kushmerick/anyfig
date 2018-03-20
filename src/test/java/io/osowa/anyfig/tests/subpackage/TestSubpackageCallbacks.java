package io.osowa.anyfig.tests.subpackage;

import io.osowa.anyfig.Configurable;

// this test fixture is in a strange package because is used for testing package callbacks

public class TestSubpackageCallbacks {

    @Configurable(literal = true, value = "4")
    public static int field = 1;

}
