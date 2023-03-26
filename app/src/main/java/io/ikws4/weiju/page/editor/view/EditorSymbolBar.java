package io.ikws4.weiju.page.editor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.events.StartChatEvent;

public class EditorSymbolBar extends RecyclerView {
    private Callbacks mCallbacks = new EmptyCallbask();

    public EditorSymbolBar(Context context) {
        super(context);
        init();
    }

    public EditorSymbolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        String[] symbols = getContext().getResources().getStringArray(R.array.editor_symbols);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new Adapter(Arrays.asList(symbols)));
    }

    public void attach(Editor editor) {
        registerCallbacks(new EditorSymbolBar.Callbacks() {
            @Override
            public void onClickSymbol(String s) {
                if (s.equals("AI")) {
                    EventBus.getDefault().post(new StartChatEvent());
                } else {
                    SymbolPairMatch pariMathces = editor.getEditorLanguage().getSymbolPairs();
                    List<SymbolPairMatch.SymbolPair> pairs = pariMathces.matchBestPairList(s.charAt(0));

                    if (pairs.isEmpty()) {
                        editor.commitText(s);
                    } else {
                        SymbolPairMatch.SymbolPair pair = pairs.get(0);
                        Content content = editor.getText();
                        Cursor cursor = editor.getCursor();
                        char afterChar = content.charAt(cursor.getRight());
                        if (afterChar == s.charAt(0)) {
                            editor.moveSelectionRight();
                        } else {
                            if (pair != null) {
                                editor.commitText(pair.open + pair.close);
                                editor.moveSelectionLeft();
                            } else {
                                editor.commitText(s);
                            }
                        }
                    }
                }
            }
        });
    }

    private void registerCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private final List<String> mData;

        public Adapter(List<String> symbols) {
            mData = symbols;
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_symbol_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            holder.bind(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView vSymbol;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vSymbol = itemView.findViewById(R.id.tv_symbol);
            }

            public void bind(String item) {
                vSymbol.setText(item);
                vSymbol.setOnClickListener((v) -> {
                    mCallbacks.onClickSymbol(item);
                });
            }
        }
    }

    public interface Callbacks {
        void onClickSymbol(String s);
    }

    static class EmptyCallbask implements Callbacks {
        @Override
        public void onClickSymbol(String s) {
        }
    }
}
