package io.ikws4.jsitter;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Iterator;

public class TSQueryMatch implements Iterable<TSQueryCapture> {
    private int id;
    private int patternIndex;
    private TSQueryCapture[] captures;

    public TSQueryCapture getCapture(int index) {
        return captures[index];
    }

    public int captureCount() {
        return captures.length;
    }

    public int getId() {
        return id;
    }

    public int getPatternIndex() {
        return patternIndex;
    }

    @NonNull
    @Override
    public Iterator<TSQueryCapture> iterator() {
        return new Iterator<TSQueryCapture>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < captures.length;
            }

            @Override
            public TSQueryCapture next() {
                return captures[index++];
            }
        };
    }

    @Override
    public String toString() {
        return "TSQueryMatch{" +
                "id=" + id +
                ", captures=" + Arrays.toString(captures) +
                '}';
    }
}