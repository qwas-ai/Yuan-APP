package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import com.example.myapplication.BaseActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // 加载 SettingsFragment
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.settings_container) == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.settings_container, new SettingsFragment());
            ft.commit();
        }
        // 设置返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}