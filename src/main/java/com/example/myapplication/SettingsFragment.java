package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.myapplication.util.SettingsUtil;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference aboutPref = findPreference("pref_about");
        if (aboutPref != null) {
            aboutPref.setOnPreferenceClickListener(this);
        }

        // 注册首选项变更监听
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("pref_about".equals(preference.getKey())) {
            Intent intent = new Intent(requireContext(), AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消注册监听
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SettingsUtil.KEY_NIGHT_MODE.equals(key)) {
            // 夜间模式优先
            SettingsUtil.applyNightMode(requireContext());
            requireActivity().recreate();
        } else if (SettingsUtil.KEY_THEME_COLOR.equals(key)) {
            // 主题颜色
            SettingsUtil.applyThemeColor(requireContext());
            requireActivity().recreate();
        }
    }
}