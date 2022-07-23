package io.ikws4.weiju.page.home.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.ikws4.weiju.R;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.UnitConverter;

public class AppListView extends RecyclerView {
    private Adapter mAdapter;
    private String mSelectedPackage;
    private Callbacks mCallbacks = new EmptyCallbask();

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

    public void registerCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public void setData(List<AppItem> data) {
        mAdapter.submitList(new ArrayList<>(data));
        if (!Preferences.getInstance(getContext()).get(Preferences.APP_LIST_SELECTED_PACKAGE, "").equals(mSelectedPackage)) {
            mAdapter.notifySelectedPkgPositionChanged();
            mSelectedPackage = Preferences.getInstance(getContext()).get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
            mAdapter.notifySelectedPkgPositionChanged();
        }
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

    private class Adapter extends ListAdapter<AppItem, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_ADD_APP = 1;

        public Adapter() {
            super(CALLBACK);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_TYPE_ITEM:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
                    return new ItemViewHolder(view);
                case VIEW_TYPE_ADD_APP:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_app_item, parent, false);
                    return new AddAppViewHodler(view);
                default:
                    throw new IllegalStateException("Unexpected value: " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemViewHolder) {
                ((ItemViewHolder) holder).bind(getItem(position));
            } else {
                ((AddAppViewHodler) holder).bind();
            }
        }

        @Override
        public int getItemCount() {
            return getRealItemCount() + 1;
        }

        public int getRealItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (position < getItemCount() - 1) return VIEW_TYPE_ITEM;
            return VIEW_TYPE_ADD_APP;
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

        private class ItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView vName;
            private final ShapeableImageView vIcon;
            private float x, y;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                vName = itemView.findViewById(R.id.tv_name);
                vIcon = itemView.findViewById(R.id.img_icon);
            }

            @SuppressLint("ClickableViewAccessibility")
            public void bind(AppItem item) {
                vName.setText(item.name);
                Glide.with(getContext())
                    .load(item.imgUri)
                    .into(vIcon);

                if (item.pkg.equals(mSelectedPackage)) {
                    vIcon.setStrokeWidth(UnitConverter.dp(2));
                    vIcon.setImageTintList(null);
                    vName.setTextColor(getContext().getColor(R.color.text));
                } else {
                    vIcon.setStrokeWidth(0);
                    vIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(R.color.subtle)));
                    vName.setTextColor(getContext().getColor(R.color.subtle));
                }

                itemView.setOnClickListener((v) -> {
                    if (mSelectedPackage.equals(item.pkg)) return;

                    mCallbacks.onRequireSwitchApp(item);
                    AppItem info = getItem(getLayoutPosition());
                    if (!info.pkg.equals(mSelectedPackage)) {
                        notifySelectedPkgPositionChanged();
                        notifyItemChanged(getLayoutPosition());
                        mSelectedPackage = info.pkg;
                        Preferences.getInstance(getContext()).put(Preferences.APP_LIST_SELECTED_PACKAGE, mSelectedPackage);
                    }
                });

                itemView.setOnTouchListener((v, event) -> {
                    x = event.getX();
                    y = event.getY();
                    return false;
                });

                itemView.setOnLongClickListener((v) -> {
                    mCallbacks.onRequireRemoveApp(vIcon, x, y, getLayoutPosition(), item);
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
                });
            }
        }

        private class AddAppViewHodler extends RecyclerView.ViewHolder {
            public AddAppViewHodler(@NonNull View itemView) {
                super(itemView);
            }

            public void bind() {
                itemView.setOnClickListener((v) -> {
                    mCallbacks.onRequireAddApp();
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

    public interface Callbacks {
        void onRequireSwitchApp(AppItem app);

        void onRequireAddApp();

        void onRequireRemoveApp(View v, float x, float y, int index, AppItem item);
    }

    static class EmptyCallbask implements Callbacks {

        @Override
        public void onRequireSwitchApp(AppItem app) {

        }

        @Override
        public void onRequireAddApp() {

        }

        @Override
        public void onRequireRemoveApp(View v, float x, float y, int index, AppItem item) {
        }
    }
}
