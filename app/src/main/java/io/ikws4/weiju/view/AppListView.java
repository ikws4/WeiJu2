package io.ikws4.weiju.view;

import android.content.Context;
import android.content.res.ColorStateList;
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

import java.util.List;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.PreferenceStorage;
import io.ikws4.weiju.util.AppInfoListLoader;
import io.ikws4.weiju.util.UnitConverter;

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

  public AppListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    mSelectedPosition = PreferenceStorage.getInstance(getContext()).get(PreferenceStorage.APP_LIST_SELECTED_POSITION, 0);

    List<AppInfo> data = AppInfoListLoader.getUserApplications(getContext());
    // PackageManager pm = getContext().getPackageManager();
    // for (ApplicationInfo info : pm.getInstalledApplications(0)) {
    //     // Skip System Applications
    //     if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
    //     if (BuildConfig.DEBUG && data.size() > 5) break;
    //
    //     CharSequence name = info.loadLabel(pm);
    //     CharSequence pkg = info.packageName;
    //     Drawable icon = info.loadIcon(pm);
    //     data.add(new AppInfo(name, pkg, icon));
    // }
    setAdapter(mAdapter = new Adapter(data));
    setLayoutManager(new LinearLayoutManager(getContext()));
  }

  public void setOnItemClickListener(OnItemClickListener l) {
    mOnItemClickListener = l;
  }

  public CharSequence getSelectedPkg() {
    return mAdapter.data.get(mSelectedPosition).pkg;
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
        holder.bind(position);
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

      public void bind(int position) {
        AppInfo item = data.get(position);

        tvName.setText(item.name);
        imgIcon.setImageBitmap(item.icon);

        AppInfo lastSelectedItem = data.get(mSelectedPosition);
        if (item.pkg.equals(lastSelectedItem.pkg)) {
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
          notifyItemChanged(position);
          notifyItemChanged(mSelectedPosition);
          mSelectedPosition = position;
          PreferenceStorage.getInstance(getContext()).put(PreferenceStorage.APP_LIST_SELECTED_POSITION, position);
        });
      }
    }
  }

  public interface OnItemClickListener {
    void onClick(CharSequence pkg);
  }
}
