package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.util.SettingsUtil;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 应用夜间模式设置
        SettingsUtil.applyNightMode(this);
        
        // 应用主题颜色
        setTheme(SettingsUtil.getThemeResId(this));
    }
}