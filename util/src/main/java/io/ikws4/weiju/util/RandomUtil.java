package io.ikws4.weiju.util;

import java.util.Random;

public class RandomUtil {
    private static final Random mRandom = new Random();

    public static String nextName(int n) {
        StringBuilder sb = new StringBuilder();
        int wordsSize = WordsKt.getWords().size();
        for (int i = 0; i < n; i++) {
            sb.append(WordsKt.getWords().get(nextInt(wordsSize)));
            if (i < n -1 ) sb.append(" ");
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
