package com.riversoforion.wrg;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;


class RandomInputStreamTest {

    @Test
    void readContinuously() throws IOException {

        InputStream is = new RandomInputStream(true, 0);
        for (int i = 0; i < 300; i++) {
            Assertions.assertTrue(is.read() >= 0);
        }
        is.close();
    }


    @Test
    void readSpecifiedNumberOfChars() throws IOException {

        InputStream is = new RandomInputStream(false, 20);
        for (int i = 0; i < 20; i++) {
            Assertions.assertTrue(is.read() >= 0);
        }
        Assertions.assertEquals(-1, is.read());
        is.close();
    }
}
