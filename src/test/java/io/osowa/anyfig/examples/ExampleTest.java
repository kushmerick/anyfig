package io.osowa.anyfig.examples;

import org.junit.Test;

import io.osowa.anyfig.ConfigurationException;

public class ExampleTest {

    @Test(expected = ConfigurationException.class)
    public void testExampleFailsWithoutToken() throws Exception {
        Example.main(new String[0]);
    }

}
