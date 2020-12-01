package com.riversoforion.wrg;


import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Random;


public class RandomInputStream extends InputStream {

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 -_.!?\n";
    private final boolean continuous;
    private final long length;
    private long read = 0;
    private final Random rand = new SecureRandom();


    public RandomInputStream(boolean continuous, long length) {

        this.continuous = continuous;
        this.length = length;
    }


    @Override
    public int read() {

        if (continuous || read < length) {
            read++;
            return randomChar();
        }
        else {
            return -1;
        }
    }


    private int randomChar() {

        return CHARS.charAt(rand.nextInt(CHARS.length()));
    }
}
