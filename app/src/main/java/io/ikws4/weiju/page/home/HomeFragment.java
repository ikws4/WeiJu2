package io.ikws4.weiju.page.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.widget.dialog.searchbar.SearchBar;
import io.ikws4.weiju.widget.dialog.searchbar.SelectedAppInfoItemLoader;
import io.ikws4.weiju.widget.view.AppListView;
import io.ikws4.weiju.widget.view.ScriptListView;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeViewModel vm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        AppListView vAppList = view.findViewById(R.id.rv_item_list);
        ScriptListView vScripts = view.findViewById(R.id.rv_scripts);

        vScripts.registerCallbacks(new ScriptListView.Callbacks() {
            @Override
            public void onAddToMyScripts(View v, ScriptListView.ScriptItem item) {
                vm.removeFromAvaliableScripts(item);
                vm.addToMyScript(item);
            }

            @Override
            public void onRemoveFromMyScripts(View v, ScriptListView.ScriptItem item) {
                vm.removeFromMyScripts(item);
            }
        });

        SearchBar searchBar = new SearchBar(getContext(), new SelectedAppInfoItemLoader());
        searchBar.setOnItemClickListener(item -> {
            vm.selectApp((AppInfo) item.userData);
            vAppList.scrollToBottom();
            return true;
        });

        vAppList.setOnItemClickListener(app -> {
            vm.switchApp(app.pkg);
        });

        vAppList.setOnAddAppClickListener(v -> {
            searchBar.show();
        });

        vm.getSelectedAppInfos().observe(getViewLifecycleOwner(), infos -> {
            vAppList.setData(infos);
            vAppList.scrollToSelectedPkgPosition();
        });

        vm.getAvaliableScripts().observe(getViewLifecycleOwner(), scripts -> {
            vScripts.setData(vm.getMyScripts().getValue(), scripts);
        });

        vm.getMyScripts().observe(getViewLifecycleOwner(), scripts -> {
            vScripts.setData(scripts, vm.getAvaliableScripts().getValue());
        });
    }
}
