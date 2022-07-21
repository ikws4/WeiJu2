package io.ikws4.weiju.page.home.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.Rect;
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
import java.util.Objects;

import io.ikws4.weiju.R;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.UnitConverter;

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

    public void setData(List<AppItem> data) {
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

    private static final DiffUtil.ItemCallback<AppItem> CALLBACK = new DiffUtil.ItemCallback<AppItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull AppItem oldItem, @NonNull AppItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    private class Adapter extends ListAdapter<AppItem, Adapter.ViewHolder> {

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

            public void bind(AppItem item) {
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
                    AppItem info = getItem(getLayoutPosition());
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

    public static class AppItem {
        public final String name;
        public final String pkg;
        public final String imgUri;
        public final boolean isSystemApp;

        public AppItem(@NonNull String name, @NonNull String pkg, boolean isSystemApp) {
            this.name = name;
            this.pkg = pkg;
            this.imgUri = "pkg:" + pkg;
            this.isSystemApp = isSystemApp;
        }

        public static boolean isSystemApp(ApplicationInfo info) {
            return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AppItem info = (AppItem) o;
            return isSystemApp == info.isSystemApp && Objects.equals(name, info.name) && Objects.equals(pkg, info.pkg) && Objects.equals(imgUri, info.imgUri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, pkg, imgUri, isSystemApp);
        }
    }

    static class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration {
        private final int mSpacing;

        public VerticalSpacingItemDecorator(int spacing) {
            mSpacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpacing;
            }
            outRect.bottom = mSpacing;
        }
    }


    public interface OnItemClickListener {
        void onClick(AppItem pkg);
    }


}
