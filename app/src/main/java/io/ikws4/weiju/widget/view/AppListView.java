package io.ikws4.weiju.widget.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.UnitConverter;
import io.ikws4.weiju.widget.VerticalSpacingItemDecorator;
import io.ikws4.weiju.widget.dialog.SearchBar;

public class AppListView extends RecyclerView {
    private OnItemClickListener mOnItemClickListener;
    private Adapter mAdapter;
    private int mSelectedPosition;

    public AppListView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSelectedPosition = Preferences.getInstance(getContext()).get(Preferences.APP_LIST_SELECTED_POSITION, 0);
        mAdapter = new Adapter();
        setAdapter(mAdapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new VerticalSpacingItemDecorator(UnitConverter.dp(16)));
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public CharSequence getSelectedPkg() {
        return mAdapter.getSelectedPkg();
    }

    public void setData(List<AppInfo> data) {
        // mAdapter.addData(data);
        mAdapter.submitList(data);
    }

    private static final DiffUtil.ItemCallback<AppInfo> CALLBACK = new DiffUtil.ItemCallback<AppInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull AppInfo oldItem, @NonNull AppInfo newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull AppInfo oldItem, @NonNull AppInfo newItem) {
            return oldItem.equals(newItem);
        }
    };

    private class Adapter extends ListAdapter<AppInfo, Adapter.ViewHolder> {

        public Adapter() {
            super(CALLBACK);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resId = R.layout.app_item;
            if (viewType == 1) resId = R.layout.app_add_item;

            View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < getRealItemCount()) {
                holder.bind(position);
            } else {
                holder.itemView.setOnClickListener((v -> {
                    List<SearchBar.Item> items = new ArrayList<>();
                    for (int i = 0; i < getRealItemCount(); i++) {
                        AppInfo info = getItem(i);
                        items.add(new SearchBar.Item(info.name, info.imgUri));
                    }
                    new SearchBar.Builder(getContext())
                        .setItems(items)
                        .onItemClick(item -> true)
                        .show();
                }));
            }
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() + 1;
        }

        public int getRealItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (position < getItemCount() - 1) return 0;
            return 1;
        }

        public CharSequence getSelectedPkg() {
            if (0 <= mSelectedPosition && mSelectedPosition < getRealItemCount()) {
                return getItem(mSelectedPosition).pkg;
            } else {
                return "";
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final AppCompatTextView tvName;
            private final ShapeableImageView imgIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                imgIcon = itemView.findViewById(R.id.img_icon);
            }

            public void bind(int position) {
                AppInfo item = getItem(position);

                tvName.setText(item.name);
                Glide.with(getContext())
                    .load(item.imgUri)
                    .into(imgIcon);

                if (mSelectedPosition < getRealItemCount() && item.pkg.equals(getItem(mSelectedPosition).pkg)) {
                    imgIcon.setStrokeWidth(UnitConverter.dp(2));
                    imgIcon.setImageTintList(null);
                    tvName.setTextColor(getContext().getColor(R.color.text));
                } else {
                    imgIcon.setStrokeWidth(0);
                    imgIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(R.color.subtle)));
                    tvName.setTextColor(getContext().getColor(R.color.subtle));
                }

                itemView.setOnClickListener((v) -> {
                    mOnItemClickListener.onClick(item.pkg);
                    if (position != mSelectedPosition) {
                        notifyItemChanged(position);
                        notifyItemChanged(mSelectedPosition);
                        mSelectedPosition = position;
                        Preferences.getInstance(getContext()).put(Preferences.APP_LIST_SELECTED_POSITION, position);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(CharSequence pkg);
    }

    // >>> Dialog

    // <<< Dialog
}
