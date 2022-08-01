package io.ikws4.weiju.page.home;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.R;
import io.ikws4.weiju.page.editor.EditorFragment;
import io.ikws4.weiju.page.home.widget.AppListView;
import io.ikws4.weiju.page.home.widget.ScriptListView;
import io.ikws4.weiju.widget.searchbar.SearchBar;
import io.ikws4.weiju.widget.searchbar.SelectedAppInfoItemLoader;

public class HomeFragment extends Fragment implements MenuProvider {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    private boolean isDrag = false;
    private HomeViewModel vm;

    public HomeFragment() {
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        vm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        AppListView vApps = view.findViewById(R.id.rv_item_list);
        ScriptListView vScripts = view.findViewById(R.id.rv_scripts);
        ViewFlipper vfScripts = view.findViewById(R.id.vf_scripts);

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

                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_top,
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_top
                    )
                    .add(R.id.fragment_container, EditorFragment.class, bundle)
                    .addToBackStack("home")
                    .commit();
            }

            @Override
            public void onRequireCreateNewScripts() {
                ScriptListView.ScriptItem item = vm.createNewScriptAndAddToMyScripts();
                onRequireGotoEditorFragment(item);
            }
        });

        SearchBar searchBar = new SearchBar(getContext(), new SelectedAppInfoItemLoader());
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
                    actionBar.setSubtitle("Drag to the right");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    actionBar.setSubtitle("Release to delete");
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    actionBar.setSubtitle("Drag to the right");
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
        });

        vm.getMyScripts().observe(getViewLifecycleOwner(), scripts -> {
            vScripts.setData(scripts, vm.getAvaliableScripts().getValue());
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();

        menuInflater.inflate(R.menu.home_menu, menu);
        if (XPOSED_ENABLED) {
            menu.findItem(R.id.xposed_status).setVisible(false);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.xposed_status) {
            Toast.makeText(getContext(), "WeiJu was not enabled in xposed.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.settings) {
            Toast.makeText(getContext(), "TODO: Settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.launch_app) {
            PackageManager pm = getContext().getPackageManager();

            Intent launchAppIntent = pm.getLaunchIntentForPackage(vm.getCurrentSelectedAppPkg().getValue());

            // FOR TEST: RESTART THE APP
            if (BuildConfig.DEBUG) {
                launchAppIntent = Intent.makeRestartActivityTask(launchAppIntent.getComponent());
            }

            startActivity(launchAppIntent);
        } else {
            return false;
        }
        return true;
    }
}
