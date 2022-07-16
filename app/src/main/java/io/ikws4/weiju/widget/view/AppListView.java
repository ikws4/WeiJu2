package io.ikws4.weiju.widget.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.UnitConverter;
import io.ikws4.weiju.widget.VerticalSpacingItemDecorator;
import io.ikws4.weiju.widget.dialog.SearchListDialog;

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

  public void addData(List<AppInfo> data) {
    mAdapter.addData(data);
  }

  private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private final List<AppInfo> mData = new ArrayList<>();

    public Adapter() {
      mData.add(null);
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
      if (position < mData.size() - 1) {
        holder.bind(position);
      } else {
        holder.itemView.setOnClickListener((v -> {
          Toast.makeText(v.getContext(), "Add app", Toast.LENGTH_SHORT).show();
          SearchListDialog searchListDialog = new SearchListDialog(getContext());
          searchListDialog.setItems(
              mData.stream()
                  .filter(Objects::nonNull)
                  .map(item -> new SearchListDialog.Item(item.name, new ColorDrawable(Color.WHITE)))
                  .collect(Collectors.toList())
          );
          searchListDialog.show();
        }));
      }
    }

    @Override
    public int getItemCount() {
      return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
      if (position < mData.size() - 1) return 0;
      return 1;
    }

    public void addData(List<AppInfo> data) {
      int insertPoint = mData.size() - 1;
      mData.addAll(insertPoint, data);
      notifyItemRangeInserted(insertPoint, data.size());
    }

    public CharSequence getSelectedPkg() {
      return mData.get(mSelectedPosition).pkg;
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
        AppInfo item = mData.get(position);

        tvName.setText(item.name);
        Glide.with(getContext())
            .load(item.info)
            .into(imgIcon);

        if (mSelectedPosition < mData.size() && item.pkg.equals(mData.get(mSelectedPosition).pkg)) {
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
