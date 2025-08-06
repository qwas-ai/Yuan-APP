package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class SettingsUtil {
    // 键名常量
    public static final String KEY_FONT_SIZE = "pref_font_size";
    public static final String KEY_LINE_SPACING = "pref_line_spacing";
    public static final String KEY_THEME_COLOR = "pref_theme_color";
    public static final String KEY_NIGHT_MODE = "pref_night_mode";
    
    // 默认值
    public static final String DEFAULT_FONT_SIZE = "16";
    public static final String DEFAULT_LINE_SPACING = "1.4";
    public static final String DEFAULT_THEME_COLOR = "default";
    public static final boolean DEFAULT_NIGHT_MODE = false;
    
    // 获取字体大小
    public static float getFontSize(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSizeStr = prefs.getString(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        try {
            return Float.parseFloat(fontSizeStr);
        } catch (NumberFormatException e) {
            return Float.parseFloat(DEFAULT_FONT_SIZE);
        }
    }
    
    // 获取行间距
    public static float getLineSpacing(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lineSpacingStr = prefs.getString(KEY_LINE_SPACING, DEFAULT_LINE_SPACING);
        try {
            return Float.parseFloat(lineSpacingStr);
        } catch (NumberFormatException e) {
            return Float.parseFloat(DEFAULT_LINE_SPACING);
        }
    }
    
    // 获取主题颜色
    public static String getThemeColor(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(KEY_THEME_COLOR, DEFAULT_THEME_COLOR);
    }
    
    // 是否启用夜间模式
    public static boolean isNightModeEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_NIGHT_MODE, DEFAULT_NIGHT_MODE);
    }
    
    // 应用夜间模式设置
    public static void applyNightMode(Context context) {
        boolean isNightMode = isNightModeEnabled(context);
        int nightMode = isNightMode ? 
                AppCompatDelegate.MODE_NIGHT_YES : 
                AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
    
    // 获取主题资源ID
    public static int getThemeResId(Context context) {
        String themeColor = getThemeColor(context);
        switch (themeColor) {
            case "blue":
                return com.example.myapplication.R.style.Theme_Blue;
            case "green":
                return com.example.myapplication.R.style.Theme_Green;
            case "red":
                return com.example.myapplication.R.style.Theme_Red;
            case "purple":
                return com.example.myapplication.R.style.Theme_Purple;
            default:
                return com.example.myapplication.R.style.Theme_MyApplication;
        }
    }
    
    // 应用主题颜色（应用级，可在 Activity 重建前调用）
    public static void applyThemeColor(Context context) {
        int themeId = getThemeResId(context);
        Context appCtx = context.getApplicationContext();
        if (appCtx instanceof android.app.Application) {
            ((android.app.Application) appCtx).setTheme(themeId);
        }
    }
}