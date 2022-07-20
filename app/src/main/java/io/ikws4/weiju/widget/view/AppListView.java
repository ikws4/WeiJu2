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

import java.util.List;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.UnitConverter;
import io.ikws4.weiju.widget.view.recyclerview.VerticalSpacingItemDecorator;

public class AppListView extends RecyclerView {
    private OnItemClickListener mOnItemClickListener;
    private View.OnClickListener mOnAddAppClickListener;
    private Adapter mAdapter;
    private String mSelectedPackage;

    public AppListView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSelectedPackage = Preferences.getInstance(getContext()).get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        mAdapter = new Adapter();
        setAdapter(mAdapter);
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new VerticalSpacingItemDecorator(UnitConverter.dp(16)));
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public void setOnAddAppClickListener(OnClickListener onAddAppClickListener) {
        mOnAddAppClickListener = onAddAppClickListener;
    }

    public void setData(List<AppInfo> data) {
        mAdapter.submitList(data);
        mAdapter.notifySelectedPkgPositionChanged();
        mSelectedPackage = Preferences.getInstance(getContext()).get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        mAdapter.notifySelectedPkgPositionChanged();
    }

    public void scrollToBottom() {
        smoothScrollToPosition(mAdapter.getItemCount());
    }

    public void scrollToSelectedPkgPosition() {
        int i = mAdapter.getSelectedPkgPosition();
        if (i != -1) smoothScrollToPosition(i);
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
                holder.bind(getItem(position));
            } else {
                holder.itemView.setOnClickListener(mOnAddAppClickListener);
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

        public void notifySelectedPkgPositionChanged() {
            int i = getSelectedPkgPosition();
            if (i != -1) notifyItemChanged(i);
        }

        public int getSelectedPkgPosition() {
            for (int i = 0; i < getRealItemCount(); i++) {
                if (getItem(i).pkg.equals(mSelectedPackage)) {
                    return i;
                }
            }
            return -1;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final AppCompatTextView tvName;
            private final ShapeableImageView imgIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                imgIcon = itemView.findViewById(R.id.img_icon);
            }

            public void bind(AppInfo item) {
                tvName.setText(item.name);
                Glide.with(getContext())
                    .load(item.imgUri)
                    .into(imgIcon);

                if (item.pkg.equals(mSelectedPackage)) {
                    imgIcon.setStrokeWidth(UnitConverter.dp(2));
                    imgIcon.setImageTintList(null);
                    tvName.setTextColor(getContext().getColor(R.color.text));
                } else {
                    imgIcon.setStrokeWidth(0);
                    imgIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(R.color.subtle)));
                    tvName.setTextColor(getContext().getColor(R.color.subtle));
                }

                itemView.setOnClickListener((v) -> {
                    if (mSelectedPackage.equals(item.pkg)) return;

                    if (mOnAddAppClickListener != null) {
                        mOnItemClickListener.onClick(item);
                    }
                    AppInfo info = getItem(getLayoutPosition());
                    if (!info.pkg.equals(mSelectedPackage)) {
                        notifySelectedPkgPositionChanged();
                        notifyItemChanged(getLayoutPosition());
                        mSelectedPackage = info.pkg;
                        Preferences.getInstance(getContext()).put(Preferences.APP_LIST_SELECTED_PACKAGE, mSelectedPackage);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(AppInfo pkg);
    }
}
