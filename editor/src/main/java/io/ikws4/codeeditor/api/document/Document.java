package io.ikws4.codeeditor.api.document;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.ikws4.codeeditor.api.document.markup.Markup;

public class Document extends SpannableStringBuilder {
    private List<Markup> mMarkups;
    private boolean mUpdating = false;
    private int mStart;
    private int mEnd;

    public Document(CharSequence source) {
        super(source);
        mMarkups = new ArrayList<>();
    }

    public  void setMarkupSource(@NonNull List<Markup> markups) {
        Objects.requireNonNull(markups);
        mMarkups = markups;
        notifyVisibleRangeChanged(mStart, mEnd, true);
    }

    public void setMarkup(Markup markup) {
        setSpan(markup, markup.getStart(), markup.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Notify the visible area was changed, in order to refresh the markups
     */
    public void notifyVisibleRangeChanged(int start, int end, boolean updateAll) {
        if (mUpdating) return;

        mUpdating = true;
        if (updateAll) {
            removeMarkups(mStart, mEnd);
            addMarkups(start, end);
        } else {
            if (start >= mStart) {
                if (start != 0) {
                    removeMarkups(mStart, start);
                }
                addMarkups(mEnd, end);
            } else {
                addMarkups(start, mStart);
                removeMarkups(end, mEnd);
            }
        }
        mStart = start;
        mEnd = end;
        mUpdating = false;
    }

    public void notifyTextChanged(int start, int offset) {
        if (mUpdating) return;

        mUpdating = true;
        shiftMarkups(start, offset);
        mUpdating = false;
    }

    /**
     * Shift markups by given start and offset.
     */
    private void shiftMarkups(int start, int offset) {
        for (int i = getMarkupIndex(start); i < mMarkups.size(); i++) {
            mMarkups.get(i).shift(offset);
        }
    }

    /**
     * Remove markups by given range (start, end)
     */
    private void removeMarkups(int start, int end) {
        Markup[] markups = getSpans(start, end, Markup.class);
        for (Markup markup : markups) {
            removeSpan(markup);
        }
    }

    /**
     * Add markups by given range (start, end)
     */
    private void addMarkups(int start, int end) {
        int startIndex = getMarkupIndex(start);
        int endIndex = Math.min(mMarkups.size() - 1, getMarkupIndex(end));
        for (int i = startIndex; i <= endIndex; i++) {
            Markup markup = mMarkups.get(i);
            setMarkup(markup);
        }
    }

    private int getMarkupIndex(int start) {
        int l = 0, r = mMarkups.size(), m;
        while (l < r) {
            m = l + (r - l) / 2;
            if (mMarkups.get(m).getStart() < start) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l;
    }

    public static class Factory extends Editable.Factory {
        private static final Document.Factory sInstance = new Document.Factory();

        public static Document.Factory getInstance() {
            return sInstance;
        }

        @Override
        public Editable newEditable(CharSequence source) {
            return new Document(source);
        }
    }
}
