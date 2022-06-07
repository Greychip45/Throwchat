package com.example.friendsbook;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;


public class RootPreferences extends PreferenceActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        Load_setting();
    }

    private void Load_setting(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean check_night = sp.getBoolean("night",false);



        if(check_night){
            getListView().setBackgroundColor(Color.parseColor("#222222"));

        } else{
            getListView().setBackgroundColor(Color.parseColor("#ffffff"));
        }

        SwitchPreference check_night_instant = (SwitchPreference) findPreference("night");
        check_night_instant.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(android.preference.Preference preference, Object object) {

                boolean yes = (boolean)object;
                if(yes){
                    getListView().setBackgroundColor(Color.parseColor("#222222"));
                } else{
                    getListView().setBackgroundColor(Color.parseColor("#ffffff"));
                }


                return true;
            }
        });

        ListPreference lp = (ListPreference) findPreference("bubbles");



    }

    @Override
    protected void onResume() {
        Load_setting();
        super.onResume();
    }
}
