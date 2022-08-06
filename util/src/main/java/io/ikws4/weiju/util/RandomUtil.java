package io.ikws4.weiju.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class RandomUtil {
    private static final Random mRandom = new Random();
    private static String[] words;

    static {
        InputStream in = RandomUtil.class.getResourceAsStream("words");
        try {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            words = new String(bytes).split("\n");
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    public static String nextWords(String delimeter, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(words[nextInt(words.length)]);
            if (i < n - 1) sb.append(delimeter);
        }
        return sb.toString();
    }

    public static void setSeed(long seed) {
        mRandom.setSeed(seed);
    }

    public static void nextBytes(byte[] bytes) {
        mRandom.nextBytes(bytes);
    }

    public static int nextInt() {
        return mRandom.nextInt();
    }

    public static int nextInt(int bound) {
        return mRandom.nextInt(bound);
    }

    public static long nextLong() {
        return mRandom.nextLong();
    }

    public static boolean nextBoolean() {
        return mRandom.nextBoolean();
    }

    public static float nextFloat() {
        return mRandom.nextFloat();
    }

    public static double nextDouble() {
        return mRandom.nextDouble();
    }

    public static double nextGaussian() {
        return mRandom.nextGaussian();
    }
}
