package com.aware.plugin.questionnaire;

import com.aware.Aware;
import com.aware.plugin.questionnaire.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String STATUS_PLUGIN_QUESTIONNAIRE = "status_plugin_esm_questionnaire";
    public static final String QUESTIONNAIRES_PLUGIN_ESM_QUESTIONNAIRE = "questionnaires_plugin_esm_questionnaire";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        syncSettings();
    }

    private void syncSettings() {
        CheckBoxPreference active = (CheckBoxPreference) findPreference(STATUS_PLUGIN_QUESTIONNAIRE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = (Preference) findPreference(key);

        if( preference.getKey().equals(STATUS_PLUGIN_QUESTIONNAIRE)) {
            boolean is_active = sharedPreferences.getBoolean(key, false);
            Aware.setSetting(getApplicationContext(), key, is_active);
            if( is_active ) {
                Aware.startPlugin(getApplicationContext(), getPackageName());
            } else {
                Aware.stopPlugin(getApplicationContext(), getPackageName());
            }
        }

        Intent apply = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(apply);
    }
}

