package io.ikws4.weiju.page.home.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.ikws4.weiju.R;
import io.ikws4.weiju.util.Logger;
import io.ikws4.weiju.util.Strings;
import io.ikws4.weiju.util.UnitConverter;

public class ScriptListView extends RecyclerView {
    private Adapter mAdapter;
    private Callbacks mCallbacks = new EmptyCallbacks();

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
        LayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        setAdapter(mAdapter);
        setData(null, null);
    }

    public void setData(@Nullable List<ScriptItem> myScripts, @Nullable List<ScriptItem> availableScripts) {
        List<Item> items = new ArrayList<>();

        items.add(new Item(Item.MY_SCRIPTS_HEADER));
        if (myScripts == null) {
            items.add(new Item(Item.MY_SCRIPTS_LOADING_PLACEHOLDER));
        } else if (myScripts.isEmpty()) {
            items.add(new Item(Item.MY_SCRIPTS_EMPTY_PLACEHOLDER));
        } else {
            for (ScriptItem item : myScripts) {
                items.add(new Item(Item.MY_SCRIPT_ITEM, item.clone()));
            }
        }

        items.add(new Item(Item.AVAILABLE_SCRIPTS_HEADER));
        if (availableScripts == null) {
            items.add(new Item(Item.AVAILABLE_SCRIPTS_LOADING_PLACEHOLDER));
        } else if (availableScripts.isEmpty()) {
            items.add(new Item(Item.AVAILABLE_SCRIPTS_EMPTY_PLACEHOLDER));
        } else {
            for (ScriptItem item : availableScripts) {
                items.add(new Item(Item.AVAILABLE_SCRIPT_ITEM, item.clone()));
            }
        }

        mAdapter.submitList(items);
    }

    public void registerCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    private static final DiffUtil.ItemCallback<Item> CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.equals(newItem);
        }
    };

    class Adapter extends ListAdapter<Item, RecyclerView.ViewHolder> {

        protected Adapter() {
            super(CALLBACK);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case Item.MY_SCRIPTS_HEADER:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_header, parent, false);
                    return new MyScriptHeaderViewHolder(view);
                case Item.MY_SCRIPTS_LOADING_PLACEHOLDER:
                    view = new LoadingView(getContext(), 192);
                    return new ViewHolder(view);
                case Item.MY_SCRIPTS_EMPTY_PLACEHOLDER:
                    view = new EmptyView(getContext(), "click  button to create a new script", 192);
                    return new ViewHolder(view);
                case Item.MY_SCRIPT_ITEM:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_item, parent, false);
                    return new MyScriptItemViewHolder(view);

                case Item.AVAILABLE_SCRIPTS_HEADER:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_header, parent, false);
                    return new AvaiableScriptsHeaderViewHolder(view);
                case Item.AVAILABLE_SCRIPTS_LOADING_PLACEHOLDER:
                    view = new LoadingView(getContext(), 192);
                    return new ViewHolder(view);
                case Item.AVAILABLE_SCRIPTS_EMPTY_PLACEHOLDER:
                    view = new EmptyView(getContext(), "No more scripts available for this app", 96);
                    return new ViewHolder(view);
                case Item.AVAILABLE_SCRIPT_ITEM:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.script_item, parent, false);
                    return new AvaiableScriptItemViewHolder(view);

                default:
                    throw new IllegalStateException("Unexpected value: " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyScriptItemViewHolder) {
                ((MyScriptItemViewHolder) holder).bind(getItem(position).scriptItem);
            } else if (holder instanceof AvaiableScriptItemViewHolder) {
                ((AvaiableScriptItemViewHolder) holder).bind(getItem(position).scriptItem);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

        class MyScriptHeaderViewHolder extends RecyclerView.ViewHolder {

            public MyScriptHeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                TextView vTitle = itemView.findViewById(R.id.tv_title);
                vTitle.setText("My Scripts");

                ImageButton vHelp = itemView.findViewById(R.id.btn_icon);
                vHelp.setImageDrawable(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.ic_add_circle));
                vHelp.setOnClickListener((v) -> {
                    mCallbacks.onRequireCreateNewScripts();
                });
            }
        }

        class MyScriptItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView vIconLabel, vName, vAuthor, vDescription;
            private final ScriptStatusChipBar vChipBar;

            public MyScriptItemViewHolder(@NonNull View itemView) {
                super(itemView);
                vIconLabel = itemView.findViewById(R.id.tv_icon_label);
                vName = itemView.findViewById(R.id.tv_name);
                vAuthor = itemView.findViewById(R.id.tv_author);
                vDescription = itemView.findViewById(R.id.tv_description);
                vChipBar = itemView.findViewById(R.id.chip_bar);
            }

            public void bind(ScriptItem item) {
                itemView.setOnClickListener(v -> {
                    mCallbacks.onRequireGotoEditorFragment(item);
                });
                vIconLabel.setText(String.valueOf(item.name.charAt(0)));
                vName.setText(item.name);
                vAuthor.setText(item.author);
                vDescription.setText(item.description);
                vChipBar.setScriptStatus(item);

                itemView.setOnLongClickListener((v) -> {
                    showActionMenu(v, item);
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
                });
            }

            private void showActionMenu(View v, ScriptItem item) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.inflate(R.menu.my_script_item_actions);

                if (item.hasNewVersion) {
                    popup.getMenu().findItem(R.id.update_script).setVisible(true);
                }

                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(it -> {
                    int id = it.getItemId();
                    if (id == R.id.remove_from_my_scripts) {
                        mCallbacks.onRequireRemoveFromMyScripts(v, item);
                    } else if (id == R.id.update_script) {
                        mCallbacks.onRequireUpdateScript(item);
                    }
                    return true;
                });
                popup.show();
            }
        }

        class AvaiableScriptsHeaderViewHolder extends RecyclerView.ViewHolder {

            public AvaiableScriptsHeaderViewHolder(@NonNull View itemView) {
                super(itemView);
                TextView vTitle = itemView.findViewById(R.id.tv_title);
                vTitle.setText("Avaiable Scripts");

                ImageButton vHelp = itemView.findViewById(R.id.btn_icon);
                vHelp.setImageDrawable(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.ic_help));
                vHelp.setOnClickListener((v) -> {
                    Toast.makeText(v.getContext(), "Help", Toast.LENGTH_SHORT).show();
                });
            }
        }

        class AvaiableScriptItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView vIconLabel, vName, vAuthor, vDescription;
            private final ScriptStatusChipBar vChipBar;

            public AvaiableScriptItemViewHolder(@NonNull View itemView) {
                super(itemView);
                vIconLabel = itemView.findViewById(R.id.tv_icon_label);
                vName = itemView.findViewById(R.id.tv_name);
                vAuthor = itemView.findViewById(R.id.tv_author);
                vDescription = itemView.findViewById(R.id.tv_description);
                vChipBar = itemView.findViewById(R.id.chip_bar);
            }

            public void bind(ScriptItem item) {
                itemView.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), item.script, Toast.LENGTH_SHORT).show();
                });
                vIconLabel.setText(String.valueOf(item.name.charAt(0)));
                vName.setText(item.name);
                vAuthor.setText(item.author);
                vDescription.setText(item.description);
                vChipBar.setScriptStatus(item);

                itemView.setOnClickListener((v) -> {
                    showActionMenu(v, item);
                });
            }

            private void showActionMenu(View v, ScriptItem item) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.inflate(R.menu.available_script_item_actions);
                popup.setGravity(Gravity.END);
                popup.setOnMenuItemClickListener(it -> {
                    if (it.getItemId() == R.id.add_to_my_scripts) {
                        mCallbacks.onRequireAddToMyScripts(v, item);
                    } else if (it.getItemId() == R.id.more_about_this_script) {
                        Toast.makeText(v.getContext(), "TODO: More about this script", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
                popup.show();
            }
        }
    }

    private static class LoadingView extends FrameLayout {

        public LoadingView(Context context, int height) {
            super(context, null, 0, R.style.Widget_WeiJu_ProgressBar);
            setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, UnitConverter.dp(height), Gravity.CENTER));
            ProgressBar progressBar = new ProgressBar(context, null, 0, R.style.Widget_WeiJu_ProgressBar);
            addView(progressBar, new LayoutParams(UnitConverter.dp(64), UnitConverter.dp(64), Gravity.CENTER));
        }
    }

    private static class EmptyView extends androidx.appcompat.widget.AppCompatTextView {

        public EmptyView(Context context, String msg, int height) {
            super(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UnitConverter.dp(height));
            layoutParams.leftMargin = UnitConverter.dp(48);
            layoutParams.rightMargin = UnitConverter.dp(48);
            setLayoutParams(layoutParams);
            setGravity(Gravity.CENTER);
            Typeface font = ResourcesCompat.getFont(context, io.ikws4.weiju.editor.R.font.jetbrains_mono_regular);
            setTypeface(font);
            setText(msg);
            setTextColor(getContext().getResources().getColor(R.color.muted, null));
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
    }

    private static class Item {
        public static final int MY_SCRIPT_ITEM = 0;
        public static final int AVAILABLE_SCRIPT_ITEM = 1;
        public static final int AVAILABLE_SCRIPTS_HEADER = 2;
        public static final int MY_SCRIPTS_HEADER = 3;
        public static final int MY_SCRIPTS_EMPTY_PLACEHOLDER = 4;
        public static final int AVAILABLE_SCRIPTS_EMPTY_PLACEHOLDER = 5;
        public static final int MY_SCRIPTS_LOADING_PLACEHOLDER = 6;
        public static final int AVAILABLE_SCRIPTS_LOADING_PLACEHOLDER = 7;

        public final int type;
        public final ScriptItem scriptItem;

        public Item(int type) {
            this(type, null);
        }

        public Item(int type, @Nullable ScriptItem item) {
            this.type = type;
            scriptItem = item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return type == item.type && Objects.equals(scriptItem, item.scriptItem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, scriptItem);
        }
    }

    public static class ScriptItem implements Parcelable, Cloneable {
        private static final Globals GLOBALS = JsePlatform.standardGlobals();
        private static final Pattern META_DATA_PATTERN = Pattern.compile("^@metadata([\\s\\S]*)@end$", Pattern.MULTILINE);
        public static final ScriptItem EMPTY_ITEM = new ScriptItem("", "", "", "", "");
        public final String id;
        public final String name;
        public final String author;
        public final String version;
        public final String description;
        public final String script;
        public boolean hasNewVersion;
        public boolean isPackage;

        private ScriptItem(@NonNull String name, @NonNull String author, @NonNull String version, @NonNull String description, @NonNull String script) {
            this.id = Strings.join(".", author, name);
            this.name = name.isEmpty() ? " " : name;
            this.author = author;
            this.version = version;
            this.description = description;
            this.script = script;
        }

        protected ScriptItem(Parcel in) {
            id = in.readString();
            name = in.readString();
            author = in.readString();
            version = in.readString();
            description = in.readString();
            script = in.readString();
            hasNewVersion = in.readByte() != 0;
            isPackage = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(author);
            dest.writeString(version);
            dest.writeString(description);
            dest.writeString(script);
            dest.writeByte((byte) (hasNewVersion ? 1 : 0));
            dest.writeByte((byte) (isPackage ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ScriptItem> CREATOR = new Creator<ScriptItem>() {
            @Override
            public ScriptItem createFromParcel(Parcel in) {
                return new ScriptItem(in);
            }

            @Override
            public ScriptItem[] newArray(int size) {
                return new ScriptItem[size];
            }
        };

        public static ScriptItem from(String script) {
            try {
                Matcher metadataMatcher = META_DATA_PATTERN.matcher(script);
                if (metadataMatcher.find()) {
                    LuaTable metadata = GLOBALS.load(metadataMatcher.group(1)).call().checktable();
                    String name = metadata.get("name").checkjstring();
                    String author = metadata.get("author").checkjstring();
                    String version = metadata.get("version").checkjstring();
                    String description = metadata.get("description").checkjstring();

                    // Using the metadate to create ScriptItem
                    return new ScriptItem(name, author, version, description, script);
                }
            } catch (LuaError e) {
                Logger.d("WeiJu", e);
            }
            return EMPTY_ITEM;
        }

        private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]\\w*$");
        private static final Pattern VERINO_PATTERN = Pattern.compile("^(0|([1-9]\\d*))\\.(0|([1-9]\\d*))\\.(0|([1-9]\\d*))$");

        public String verify() {
            if (!NAME_PATTERN.matcher(name).matches()) {
                return "metadata.name is not valid";
            }
            if (!VERINO_PATTERN.matcher(version).matches()) {
                return "metadata.version is not valid";
            }
            if (author.isEmpty()) {
                return "meta.author is not valid";
            }
            return "";
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScriptItem item = (ScriptItem) o;
            return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(author, item.author) && Objects.equals(version, item.version) && Objects.equals(description, item.description) && Objects.equals(script, item.script) && Objects.equals(hasNewVersion, item.hasNewVersion) && Objects.equals(isPackage, item.isPackage);
        }

        public boolean metadataEquals(ScriptItem item) {
            if (this == item) return true;
            if (item == null) return false;
            return Objects.equals(id, item.id);
        }

        public int versionCompare(ScriptItem item) {
            if (this == item) return 0;
            if (item == null) return 1;


            String[] A = this.version.split("\\.");
            String[] B = item.version.split("\\.");

            // A and B's length should be 3
            for (int i = 0; i < A.length; i++) {
                int a = Integer.parseInt(A[i]);
                int b = Integer.parseInt(B[i]);
                if (a > b) return 1;
                if (a < b) return -1;
            }

            return 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, author, version, description, script, hasNewVersion, isPackage);
        }

        @NonNull
        @Override
        protected ScriptItem clone() {
            ScriptItem item = new ScriptItem(name, author, version, description, script);
            item.hasNewVersion = hasNewVersion;
            item.isPackage = isPackage;
            return item;
        }
    }

    public interface Callbacks {
        void onRequireAddToMyScripts(View v, ScriptItem item);

        void onRequireRemoveFromMyScripts(View v, ScriptItem item);

        void onRequireGotoEditorFragment(ScriptItem item);

        void onRequireCreateNewScripts();

        void onRequireUpdateScript(ScriptItem item);
    }

    public static class EmptyCallbacks implements Callbacks {

        @Override
        public void onRequireAddToMyScripts(View v, ScriptItem item) {
        }

        @Override
        public void onRequireRemoveFromMyScripts(View v, ScriptItem item) {
        }

        @Override
        public void onRequireGotoEditorFragment(ScriptItem item) {
        }

        @Override
        public void onRequireCreateNewScripts() {
        }

        @Override
        public void onRequireUpdateScript(ScriptItem item) {
        }
    }
}
