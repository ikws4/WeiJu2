package io.ikws4.weiju.page.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.BaseFragment;
import io.ikws4.weiju.widget.ListTile;

public class AboutFragment extends BaseFragment {

    public AboutFragment() {
        super(R.layout.about_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");

        ListTile vGithub = view.findViewById(R.id.v_github);
        vGithub.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ikws4/WeiJu2"));
            startActivity(intent);
        });

        ListTile vBugReport = view.findViewById(R.id.v_bug_report);
        vBugReport.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ikws4/WeiJu2/issues/new"));
            startActivity(intent);
        });
    }
}
