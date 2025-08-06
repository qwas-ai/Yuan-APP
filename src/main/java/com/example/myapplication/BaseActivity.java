package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.SettingsUtil;

/**
 * 所有 Activity 的基类，用于在 super.onCreate 之前应用夜间模式和主题颜色，
 * 确保用户切换设置后界面实时生效。
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在调用 super.onCreate 之前设置主题和夜间模式
        setTheme(SettingsUtil.getThemeResId(this));
        SettingsUtil.applyNightMode(this);
        super.onCreate(savedInstanceState);
    }
}