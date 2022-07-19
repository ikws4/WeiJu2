package io.ikws4.weiju.widget.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.ikws4.weiju.R;

public class ScriptListView extends RecyclerView {
    private Adapter mAdapter;

    public ScriptListView(@NonNull Context context) {
        super(context);
        init();
    }

    public ScriptListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mAdapter = new Adapter();
        setAdapter(mAdapter);

        List<ScriptItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            items.add(new ScriptItem("System variables", "ikws4", "Change the variables like your phone model", ""));
        }
        mAdapter.submitList(items);
    }


    private static final DiffUtil.ItemCallback<ScriptItem> CALLBACK = new DiffUtil.ItemCallback<ScriptItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScriptItem oldItem, @NonNull ScriptItem newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScriptItem oldItem, @NonNull ScriptItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    class Adapter extends ListAdapter<ScriptItem, Adapter.ViewHolder> {

        protected Adapter() {
            super(CALLBACK);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView vIconLabel, vName, vAuthor, vDescription;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vIconLabel = itemView.findViewById(R.id.tv_icon_label);
                vName = itemView.findViewById(R.id.tv_name);
                vAuthor = itemView.findViewById(R.id.tv_author);
                vDescription = itemView.findViewById(R.id.tv_description);
            }

            public void bind(ScriptItem item) {
                itemView.setOnClickListener(v -> {
                });
                vIconLabel.setText(String.valueOf(item.name.charAt(0)));
                vName.setText(item.name);
                vAuthor.setText(item.author);
                vDescription.setText(item.description);
            }
        }
    }

    public static class ScriptItem {
        public final String name;
        public final String author;
        public final String description;
        public final String script;

        public ScriptItem(@NonNull String name, @NonNull String author, @NonNull String description, @NonNull String script) {
            this.name = name;
            this.author = author;
            this.description = description;
            this.script = script;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScriptItem item = (ScriptItem) o;
            return name.equals(item.name) && author.equals(item.author) && description.equals(item.description) && script.equals(item.script);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, author, description, script);
        }
    }
}
