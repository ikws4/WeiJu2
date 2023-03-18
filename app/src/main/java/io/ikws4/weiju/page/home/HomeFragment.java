package io.ikws4.weiju.page.home;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.topjohnwu.superuser.Shell;

import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.R;
import io.ikws4.weiju.WeiJu;
import io.ikws4.weiju.page.BaseFragment;
import io.ikws4.weiju.page.MainActivity;
import io.ikws4.weiju.page.editor.EditorFragment;
import io.ikws4.weiju.page.home.widget.AppListView;
import io.ikws4.weiju.page.home.widget.CreateScriptDialog;
import io.ikws4.weiju.page.home.widget.ScriptListView;
import io.ikws4.weiju.page.logcat.LogcatFragment;
import io.ikws4.weiju.page.setting.SettingFragment;
import io.ikws4.weiju.widget.searchbar.SearchBar;
import io.ikws4.weiju.widget.searchbar.UnselectedAppItemLoader;

public class HomeFragment extends BaseFragment {
    private boolean isDrag = false;
    private HomeViewModel vm;
    private Menu menu;

    public HomeFragment() {
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        AppListView vApps = view.findViewById(R.id.rv_item_list);
        ScriptListView vScripts = view.findViewById(R.id.rv_scripts);
        ViewFlipper vfScripts = view.findViewById(R.id.vf_scripts);
        SwipeRefreshLayout vRefresher = view.findViewById(R.id.refresher);

        vScripts.registerCallbacks(new ScriptListView.Callbacks() {
            @Override
            public void onRequireAddToMyScripts(View v, ScriptListView.ScriptItem item) {
                vm.removeFromAvaliableScripts(item);
                vm.addToMyScripts(item);
            }

            @Override
            public void onRequireRemoveFromMyScripts(View v, ScriptListView.ScriptItem item) {
                vm.removeFromMyScripts(item);
            }

            @Override
            public void onRequireGotoEditorFragment(ScriptListView.ScriptItem item) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", item);

                getMainActivity().startFragment(EditorFragment.class, MainActivity.FRAGMENT_FULL_SCREEN_DIALOG, bundle);
            }

            @Override
            public void onRequireCreateNewScripts(View v) {
                new CreateScriptDialog(getContext())
                    .setOnCreateListener(dialog -> {
                        ScriptListView.ScriptItem item = vm.createNewScriptAndAddToMyScripts(
                            dialog.getName(),
                            dialog.getAuthor(),
                            dialog.getDescription(),
                            dialog.getTemplate()
                        );
                        onRequireGotoEditorFragment(item);
                    })
                    .show();
            }

            @Override
            public void onRequireUpdateScript(ScriptListView.ScriptItem item) {
                vm.updateScript(item);
            }

            @Override
            public void onRequireCopyExample(ScriptListView.ScriptItem item) {
                ClipboardManager clipboard = WeiJu.getService(ClipboardManager.class);
                clipboard.setPrimaryClip(ClipData.newPlainText("Script Example", item.example));
            }
        });

        SearchBar searchBar = new SearchBar(getContext(), new UnselectedAppItemLoader());
        searchBar.setOnItemClickListener(item -> {
            AppListView.AppItem app = (AppListView.AppItem) item.userData;
            vm.switchApp(app);
            vm.addApp(app);
            return true;
        });

        vApps.registerCallbacks(new AppListView.Callbacks() {

            @Override
            public void onRequireSwitchApp(AppListView.AppItem app) {
                vm.switchApp(app);
            }

            @Override
            public void onRequireAddApp() {
                searchBar.show();
            }

            @Override
            public void onRequireRemoveApp(View v, float x, float y, int index, AppListView.AppItem item) {
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(v) {
                    @Override
                    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
                        outShadowTouchPoint.set((int) x, (int) y);
                    }
                };
                v.startDragAndDrop(ClipData.newPlainText("", ""), shadow, new Pair<>(index, item), 0);
            }
        });

        vfScripts.setOnDragListener((v, event) -> {
            var item = (Pair) event.getLocalState();
            int index = (int) item.first;
            AppListView.AppItem app = (AppListView.AppItem) item.second;
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    isDrag = true;
                    vm.removeApp(app);
                    actionBar.setSubtitle(R.string.home_drag_to_the_right);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    actionBar.setSubtitle(R.string.home_release_to_delete);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    actionBar.setSubtitle(R.string.home_drag_to_the_right);
                    return true;
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    isDrag = false;
                    if (event.getResult() == false) {
                        vm.addApp(index, app);
                    } else {
                        vm.updateSelectedAppAfterRemove(index, app);
                    }
                    actionBar.setSubtitle(null);
                    return true;
            }
            return false;
        });

        vRefresher.setOnRefreshListener(() -> {
            vm.refreshScripts();
        });

        vm.getSelectedApps().observe(getViewLifecycleOwner(), infos -> {
            if (isDrag == false && infos.isEmpty()) {
                vfScripts.setDisplayedChild(0);
            } else {
                vfScripts.setDisplayedChild(1);
            }

            vApps.scrollToSelectedPkgPosition();
            vApps.setData(infos);
        });

        vm.getAvaliableScripts().observe(getViewLifecycleOwner(), scripts -> {
            vScripts.setData(vm.getMyScripts().getValue(), scripts);
            vRefresher.setRefreshing(false);
        });

        vm.getMyScripts().observe(getViewLifecycleOwner(), scripts -> {
            vScripts.setData(scripts, vm.getAvaliableScripts().getValue());
        });

        vm.getCurrentSelectedAppPkg().observe(getViewLifecycleOwner(), pkg -> {
            // Hide launch button
            if (menu != null) {
                menu.findItem(R.id.launch_app).setVisible(!pkg.equals(BuildConfig.APPLICATION_ID));
            }
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        super.onCreateMenu(menu, menuInflater);
        this.menu = menu;
        menuInflater.inflate(R.menu.home_menu, menu);
        if (WeiJu.XPOSED_ENABLED) {
            menu.findItem(R.id.xposed_status).setVisible(false);
        }
        vm.getCurrentSelectedAppPkg().publish();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean isDisplayHomeAsUp() {
        return false;
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.xposed_status) {
            Toast.makeText(getContext(), R.string.home_status_not_activated_in_xposed, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.launch_app) {
            PackageManager pm = getContext().getPackageManager();

            String pkg = vm.getCurrentSelectedAppPkg().getValue();
            Intent launchAppIntent = pm.getLaunchIntentForPackage(pkg);

            String component = launchAppIntent.getComponent().flattenToShortString();
            if (!Shell.cmd("am force-stop " + pkg + " && am start -n " + component).exec().isSuccess()) {
                if (isAppRunning(pkg)) {
                    Toast.makeText(getContext(), R.string.home_status_can_not_force_stop, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", pkg, null));
                    startActivity(intent);
                } else {
                    startActivity(launchAppIntent);
                }
            }
        } else if (id == R.id.logcat) {
            getMainActivity().startFragment(LogcatFragment.class);
        } else if (id == R.id.restart) {
            if (!Shell.cmd("am force-stop io.ikws4.weiju && am start -n io.ikws4.weiju/.page.MainActivity").exec().isSuccess()) {
                Toast.makeText(getContext(), R.string.home_status_can_not_restart, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.setting) {
            getMainActivity().startFragment(SettingFragment.class);
        } else {
            return false;
        }
        return true;
    }

    private boolean isAppRunning(String pkg) {
        for (var it : getContext().getPackageManager().getInstalledPackages(0)) {
            if (!it.packageName.equals(pkg)) continue;

            if ((ApplicationInfo.FLAG_STOPPED & it.applicationInfo.flags) == 0) {
                return true;
            }
        }
        return false;
    }
}
