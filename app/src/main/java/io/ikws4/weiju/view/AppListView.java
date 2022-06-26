package io.ikws4.weiju.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.R;

public class AppListView extends RecyclerView {
    private OnItemClickListener mOnItemClickListener;
    private CharSequence mSelectedPkg;

    public AppListView(@NonNull Context context) {
        super(context);
        init();
    }

    public AppListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        List<AppInfo> data = new ArrayList<>();
        PackageManager pm = getContext().getPackageManager();
        for (ApplicationInfo info : pm.getInstalledApplications(0)) {
            // Skip System Applications
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
            if (BuildConfig.DEBUG && data.size() > 5) break;

            CharSequence name = info.loadLabel(pm);
            CharSequence pkg = info.packageName;
            Drawable icon = info.loadIcon(pm);
            data.add(new AppInfo(name, pkg, icon));
        }
        setAdapter(new Adapter(data));
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public CharSequence getSelectedPkg() {
        return mSelectedPkg;
    }

    public void setmSelectedPkg(CharSequence mSelectedPkg) {
        this.mSelectedPkg = mSelectedPkg;
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<AppInfo> data;

        public Adapter(List<AppInfo> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int resId = R.layout.item_app;
            if (viewType == 1) resId = R.layout.item_add_app;

            View view = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < data.size() - 1) {
                holder.bind(data.get(position));
            } else {
                holder.itemView.setOnClickListener((v -> {
                    Toast.makeText(v.getContext(), "Add app", Toast.LENGTH_SHORT).show();
                }));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position < data.size() - 1) return 0;
            return 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final AppCompatTextView tvName;
            private final ShapeableImageView imgIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                imgIcon = itemView.findViewById(R.id.img_icon);
            }

            public void bind(AppInfo item) {
                itemView.setOnClickListener((v) -> {
                    mOnItemClickListener.onClick(item.pkg);
                    mSelectedPkg = item.pkg;
                });
                tvName.setText(item.name);
                imgIcon.setImageDrawable(item.icon);
            }
        }
    }

    public interface OnItemClickListener {
        void onClick(CharSequence pkg);
    }

    static class AppInfo {
        CharSequence name;
        CharSequence pkg;
        Drawable icon;

        public AppInfo(CharSequence name, CharSequence pkg, Drawable icon) {
            this.name = name;
            this.pkg = pkg;
            this.icon = icon;
        }
    }
}
