package io.ikws4.weiju.widget.dialog.SearchBar;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.ikws4.weiju.R;
import io.ikws4.weiju.util.Logger;

public class SearchBar extends Dialog {
    private OnItemClickListener mOnItemClickListener;
    private final List<Item> mSourceItems = new ArrayList<>();
    private List<Item> mDisplayItems;
    private final Adapter mAdapter;

    private final EditText vInput;
    private final ImageButton vSearch;
    private final ProgressBar vLoading;
    private final MaterialDivider vDivider;

    private ItemLoader mItemLoader;
    private ItemLoader.Callback mItemLoaderCallback;

    public SearchBar(@NonNull Context context, @NonNull ItemLoader itemLoader) {
        super(context);
        setContentView(R.layout.search_list_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // bluring
        getWindow().setDimAmount(0.3f);
        //  48% height
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.85);
        getWindow().setAttributes(lp);

        RecyclerView rv = findViewById(R.id.rv_item_list);
        mAdapter = new Adapter();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        vInput = findViewById(R.id.et_input);
        vSearch = findViewById(R.id.btn_search);
        vDivider = findViewById(R.id.divider);
        vLoading = findViewById(R.id.pb_loading);

        vInput.setOnEditorActionListener((v, a, e) -> true);
        vInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s);
            }
        });

        mItemLoader = itemLoader;
        mItemLoaderCallback = new ItemLoader.Callback() {
            @Override
            public void send(List<Item> items) {
                mSourceItems.addAll(items);
                filter("");
            }

            @Override
            public void error(Throwable e) {
                Logger.e(e);
            }

            @Override
            public void finish() {
                vLoading.setVisibility(View.GONE);
                vSearch.setVisibility(View.VISIBLE);
            }
        };
        mItemLoader.setContext(context);
    }

    private void filter(CharSequence s) {
        mDisplayItems = mSourceItems.stream().filter((item) -> isSubsequence(s, item.title)).collect(Collectors.toList());
        updateDividerVisibility();
        mAdapter.submitList(mDisplayItems);
    }

    private void updateDividerVisibility() {
        if (mDisplayItems == null || mDisplayItems.size() == 0) {
            vDivider.setVisibility(View.GONE);
        } else {
            vDivider.setVisibility(View.VISIBLE);
        }
    }

    // check a if the subsequence of b
    private boolean isSubsequence(CharSequence a, CharSequence b) {
        int i = 0, j = 0;
        while (i < a.length()) {
            while (j < b.length() && Character.toLowerCase(b.charAt(j)) != Character.toLowerCase(a.charAt(i))) j++;
            if (j == b.length()) break;
            i++;
        }
        return i == a.length();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void show() {
        mSourceItems.clear();
        filter("");

        if (mItemLoader != null) {
            mItemLoader.load(mItemLoaderCallback);
            vSearch.setVisibility(View.GONE);
            vLoading.setVisibility(View.VISIBLE);
        } else {
            vSearch.setVisibility(View.VISIBLE);
            vLoading.setVisibility(View.GONE);
        }

        super.show();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mItemLoader.clear();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        vInput.setText("");
    }

    private static final DiffUtil.ItemCallback<Item> CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.equals(newItem);
        }
    };

    private class Adapter extends ListAdapter<Item, Adapter.ViewHolder> {

        protected Adapter() {
            super(CALLBACK);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView vIcon;
            TextView vTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vIcon = itemView.findViewById(R.id.img_icon);
                vTitle = itemView.findViewById(R.id.tv_title);
            }

            public void bind(Item item) {
                vTitle.setText(item.title);
                Glide.with(getContext())
                    .load(item.iconUri)
                    .into(vIcon);
                itemView.setOnClickListener((v) -> {
                    if (mOnItemClickListener != null) {
                        if (mOnItemClickListener.onClick(item)) {
                            dismiss();
                        }
                    }
                });
            }
        }

    }

    public static class Item {
        public final CharSequence title;
        public final String iconUri;
        public final Object userData;

        public Item(@NonNull CharSequence title, @NonNull String iconUri) {
            this(title, iconUri, null);
        }

        public Item(@NonNull CharSequence title, @NonNull String iconUri, @Nullable Object userData) {
            this.title = title;
            this.iconUri = iconUri;
            this.userData = userData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return title.equals(item.title) && iconUri.equals(item.iconUri) && Objects.equals(userData, item.userData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, iconUri, userData);
        }
    }

    public interface OnItemClickListener {
        /**
         * @return true dismiss the search bar
         */
        boolean onClick(Item item);
    }

    public static abstract class ItemLoader {
        private Context mContext;

        protected abstract void load(Callback callback);

        protected void clear() {}

        void setContext(Context context) {
            mContext = context;
        }

        public Context getContext() {
            return mContext;
        }

        public interface Callback {
            void send(List<Item> items);

            void error(Throwable e);

            void finish();
        }
    }

    // public static class Builder {
    //     private final SearchBar mSearchBar;
    //
    //     public Builder(Context context) {
    //         mSearchBar = new SearchBar(context);
    //     }
    //
    //     public Builder setItems(List<Item> items) {
    //         mSearchBar.setItems(items);
    //         return this;
    //     }
    //
    //     public Builder onItemClick(OnItemClickListener l) {
    //         mSearchBar.setOnItemClickListener(l);
    //         return this;
    //     }
    //
    //     public SearchBar build() {
    //         return mSearchBar;
    //     }
    // }
}