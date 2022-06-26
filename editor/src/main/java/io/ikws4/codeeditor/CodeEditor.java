package io.ikws4.codeeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.document.Document;
import io.ikws4.codeeditor.api.editor.Editor;
import io.ikws4.codeeditor.api.editor.LayoutModel;
import io.ikws4.codeeditor.api.editor.ScaleModel;
import io.ikws4.codeeditor.api.editor.ScrollingModel;
import io.ikws4.codeeditor.api.editor.SelectionModel;
import io.ikws4.codeeditor.api.editor.component.Component;
import io.ikws4.codeeditor.api.editor.listener.ScaleListener;
import io.ikws4.codeeditor.api.editor.listener.SelectionListener;
import io.ikws4.codeeditor.api.editor.listener.VisibleAreaListener;
import io.ikws4.codeeditor.api.language.Language;
import io.ikws4.codeeditor.component.Gutter;
import io.ikws4.codeeditor.component.TextArea;
import io.ikws4.codeeditor.component.Toolbar;
import io.ikws4.codeeditor.configuration.Configuration;
import io.ikws4.codeeditor.language.java.JavaLanguage;
import io.ikws4.codeeditor.widget.HScrollView;
import io.ikws4.codeeditor.widget.VScrollView;

@SuppressLint("ClickableViewAccessibility")
public class CodeEditor extends FrameLayout implements Editor, ScaleGestureDetector.OnScaleGestureListener,
        SelectionModel, ScrollingModel, ScaleModel, LayoutModel {
    static {
        System.loadLibrary("jsitter");
    }

    private static final String TAG = "Editor";

    private Configuration mConfiguration;
    private Language mLanguage;

    private final List<SelectionListener> mSelectionListeners;
    private final List<VisibleAreaListener> mVisibleAreaListeners;
    private final List<ScaleListener> mScaleListeners;

    private final HScrollView mHScrollView;
    private final VScrollView mVScrollView;
    private final TextArea mTextArea;
    private final Toolbar mToolbar;
    private final Gutter mGutter;

    private final ScaleGestureDetector mScaleGestureDetector;

    // visible area
    private int mScrollX;
    private int mScrollY;
    private int mWidth;
    private int mHeight;
    private Rect mVisibleArea = new Rect();

    // selection
    private int mSelStart;
    private int mSelEnd;

    // scale
    private float mScaleFactor = 1.0f;

    private final InputMethodManager mIMM;


    public CodeEditor(@NonNull Context context) {
        this(context, null);
    }

    public CodeEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.editor, this, true);

        mConfiguration = new Configuration();
        mLanguage = new JavaLanguage();

        mScaleListeners = new ArrayList<>();
        mSelectionListeners = new ArrayList<>();
        mVisibleAreaListeners = new ArrayList<>();

        mHScrollView = findViewById(R.id.hScrollView);
        mVScrollView = findViewById(R.id.vScrollView);
        mTextArea = findViewById(R.id.textArea);
        mToolbar = findViewById(R.id.toolbar);
        mGutter = findViewById(R.id.gutter);

        // delegate scroll and scale event
        mHScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            mScrollX = scrollX;
            notifyVisibleAreaChanged();
        });
        mVScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            mScrollY = scrollY;
            notifyVisibleAreaChanged();
        });
        mHScrollView.setOnTouchListener((v, event) -> onTouchEvent(event));
        mVScrollView.setOnTouchListener((v, event) -> onTouchEvent(event));
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        // selection event
        mTextArea.setOnSelectionChangedListener(this::notifySelectionChanged);

        // for view model
        if (isViwer()) {
            mTextArea.setEnabled(false);
            mTextArea.setFocusable(false);
            mTextArea.setMovementMethod(null);
            mToolbar.setVisibility(GONE);
        }

        mIMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        bindComponent(this);
    }

    private void bindComponent(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof Component) {
                addComponent((Component) child);
            }
            if (child instanceof ViewGroup) {
                bindComponent((ViewGroup) child);
            }
        }
    }

    private void addComponent(Component component) {
        Objects.requireNonNull(component);
        component.onAttachEditor(this);
    }

    private void notifyVisibleAreaChanged() {
        Rect visibleArea = new Rect(mScrollX, mScrollY, mScrollX + mWidth, mScrollY + mHeight);
        for (VisibleAreaListener l : mVisibleAreaListeners) {
            l.onVisibleAreaChanged(visibleArea, mVisibleArea);
        }
        mVisibleArea = visibleArea;
    }

    private void notifySelectionChanged(int start, int end) {
        for (SelectionListener l : mSelectionListeners) {
            l.onSelectionChanged(start, end, mSelStart, mSelEnd);
        }
        mSelStart = start;
        mSelEnd = end;
    }

    private void notifyScaleChanged(float factor) {
        mScaleFactor = Math.max(0.5f, Math.min(1.5f, mScaleFactor * factor));
        for (ScaleListener l : mScaleListeners) {
            l.onScaleChanged(mScaleFactor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        notifyScaleChanged(detector.getScaleFactor());
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        notifyVisibleAreaChanged();
    }

    @NonNull
    @Override
    public Document getDocument() {
        return (Document) mTextArea.getText();
    }

    @NonNull
    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public void setConfiguration(@NonNull Configuration configuration) {
        Objects.requireNonNull(configuration);
        mConfiguration = configuration;
    }

    @NonNull
    @Override
    public ColorScheme getColorScheme() {
        return mConfiguration.getColorScheme();
    }

    @NonNull
    public Language getLanguage() {
        return mLanguage;
    }

    @NonNull
    @Override
    public Component findComponentById(int id) {
        Component component = findViewById(id);
        Objects.requireNonNull(component, "Component not found, id: " + id);
        return component;
    }

    @Override
    public boolean isViwer() {
        return false;
    }

    @NonNull
    @Override
    public SelectionModel getSelectionModel() {
        return this;
    }

    @NonNull
    @Override
    public ScrollingModel getScrollingModel() {
        return this;
    }

    @NonNull
    @Override
    public ScaleModel getScacleModel() {
        return this;
    }

    @NonNull
    @Override
    public LayoutModel getLayoutModel() {
        return this;
    }

    @Override
    public void showSoftInput() {
        mIMM.showSoftInput(mTextArea, 0);
    }

    @Override
    public void hideSoftInput() {
        mIMM.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    ///////////////////////////////////////////////////////////////////////////
    // ScrollingModel
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void scrollToCaret() {
        mTextArea.scrollTo(getScrollX(), getLineBaseline(getCurrentLine()));
    }

    @Override
    public void scrollTo(int x, int y) {
        mVScrollView.scrollTo(0, y);
        mHScrollView.scrollTo(x, 0);
    }

    @Override
    public void scrollBy(int x, int y) {
        mVScrollView.scrollBy(0, y);
        mHScrollView.scrollBy(x, 0);
    }

    @NonNull
    @Override
    public Rect getVisibleArea() {
        return mVisibleArea;
    }

    @Override
    public void addVisibleAreaListener(@NonNull VisibleAreaListener l) {
        Objects.requireNonNull(l);
        mVisibleAreaListeners.add(l);
    }

    @Override
    public void removeVisibleAreaListener(@NonNull VisibleAreaListener l) {
        Objects.requireNonNull(l);
        mVisibleAreaListeners.remove(l);
    }

    ///////////////////////////////////////////////////////////////////////////
    // SelectionModel
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public int getSelectionStart() {
        return mTextArea.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        return mTextArea.getSelectionEnd();
    }

    @NonNull
    @Override
    public CharSequence getSelectionText() {
        return mTextArea.getText().subSequence(getSelectionStart(), getSelectionEnd());
    }

    @Override
    public boolean hasSelection() {
        return mTextArea.hasSelection();
    }

    @Override
    public void setSelection(int startOffset, int endOffset) {
        mTextArea.setSelection(startOffset, endOffset);
    }

    @Override
    public void removeSelection() {
        Selection.removeSelection(mTextArea.getText());
    }

    @Override
    public void addSelectionListener(@NonNull SelectionListener l) {
        Objects.requireNonNull(l);
        mSelectionListeners.add(l);
    }

    @Override
    public void removeSelectionListener(@NonNull SelectionListener l) {
        Objects.requireNonNull(l);
        mSelectionListeners.remove(l);
    }

    @Override
    public void selectionLineAtCaret() {
        Layout layout = mTextArea.getLayout();
        if (layout != null) {
            int line = mTextArea.getCurrentLine();
            int startOffset = layout.getLineStart(line);
            int endOffset = layout.getLineEnd(line);
            mTextArea.setSelection(startOffset, endOffset);
        }
    }

    @Override
    public void moveUp() {
        focusIfNot();
        mTextArea.caretMoveUp();
    }

    @Override
    public void moveDown() {
        focusIfNot();
        mTextArea.caretMoveDown();
    }

    @Override
    public void moveLeft() {
        focusIfNot();
        mTextArea.caretMoveLeft();
    }

    @Override
    public void moveRight() {
        focusIfNot();
        mTextArea.caretMoveRight();
    }

    /**
     * If the {@link TextArea} is not focus, then request focus.
     */
    private void focusIfNot() {
        if (!mTextArea.hasFocus()) {
            mTextArea.requestFocus();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ScaleModel
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public float getScaleFactor() {
        return mScaleFactor;
    }

    @Override
    public void addScaleListener(@NonNull ScaleListener l) {
        Objects.requireNonNull(l);
        mScaleListeners.add(l);
    }

    @Override
    public void removeScaleListener(@NonNull ScaleListener l) {
        Objects.requireNonNull(l);
        mScaleListeners.remove(l);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Layout
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public int getLineBaseline(int line) {
        return mTextArea.getLayout().getLineBaseline(line);
    }

    @Override
    public int getCurrentLine() {
        return mTextArea.getCurrentLine();
    }

    @Override
    public int getTopLine() {
        return mTextArea.getTopLine();
    }

    @Override
    public int getBottomLine() {
        return mTextArea.getBottomLine();
    }

    @Override
    public int getLineCount() {
        return mTextArea.getLineCount();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Others
    ///////////////////////////////////////////////////////////////////////////
    public void setLangauge(@NonNull Language langauge) {
        Objects.requireNonNull(langauge);
        mLanguage = langauge;
    }

    public void cut() {
        mTextArea.cut();
    }

    public void paste() {
        mTextArea.paste();
    }

    public void undo() {
        mTextArea.undo();
    }

    public void redo() {
        mTextArea.redo();
    }

    public void replace() {
        mTextArea.replace();
    }

    public void format() {
        mTextArea.format();
    }

    public void setDocument(@NonNull Document document) {
        Objects.requireNonNull(document);
        mTextArea.setText(document);
    }
}
