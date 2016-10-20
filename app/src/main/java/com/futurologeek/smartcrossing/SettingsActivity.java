package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.futurologeek.smartcrossing.seekbar.SeekBarPreference;


public class SettingsActivity extends PreferenceActivity {
    public static SettingsActivity instance;
    CheckBoxPreference units;
    CheckBoxPreference camFocus;
    SeekBarPreference radius;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
        instance = this;
        SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);
        editor = preferences.edit();


       units    = (CheckBoxPreference) findPreference("unit");
       camFocus = (CheckBoxPreference) findPreference("cam_focus");
       radius = (SeekBarPreference) findPreference("radius");

       // units.setChecked(preferences.getBoolean("isKM",false));
        if(units.isChecked()){
            units.setSummary(getResources().getString(R.string.mile));
            radius.setMeasurementUnit(getResources().getString(R.string.mile));
            radius.setMaxValue(100);
            radius.setCurrentValue((int) (radius.getCurrentValue()/1.60));
        } else {
            units.setSummary(getResources().getString(R.string.km));
            radius.setMeasurementUnit(getResources().getString(R.string.km));
            radius.setMaxValue(160);
            radius.setCurrentValue((int) (radius.getCurrentValue()*1.60));
        }

       //camFocus.setChecked(preferences.getBoolean("isPoint",true));
        if(camFocus.isChecked()){
            camFocus.setSummary(getResources().getString(R.string.P_all));
        } else {
            camFocus.setSummary(getResources().getString(R.string.near_points));
        }

        radius.setCurrentValue(preferences.getInt("radius",30));

       radius.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                editor.putInt("radius", (int)newValue);
                editor.apply();
                Log.d("VALIU", String.valueOf(newValue));
                return true;
            }
                                             });
        units.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    units.setChecked(false);
                    units.setChecked(true);
                    preference.setSummary(getResources().getString(R.string.mile));
                    radius.setMeasurementUnit(getResources().getString(R.string.mile));
                    radius.setMaxValue(100);
                    radius.setCurrentValue((int) (radius.getCurrentValue()/1.60));
                    editor.putInt("radius", radius.getCurrentValue());
                    editor.apply();

                } else {
                    preference.setSummary(getResources().getString(R.string.km));
                    units.setChecked(true);
                    units.setChecked(false);
                    radius.setMeasurementUnit(getResources().getString(R.string.km));
                    radius.setMaxValue(160);
                    radius.setCurrentValue((int) (radius.getCurrentValue()*1.60));
                    editor.putInt("radius", radius.getCurrentValue());
                    editor.apply();
                }
                editor.putBoolean("isKM", !(Boolean) newValue);
                editor.commit();
                return true;
            }
        });

        camFocus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    preference.setSummary(getResources().getString(R.string.P_all));
                } else {
                    preference.setSummary(getResources().getString(R.string.near_points));
                }
                editor.putBoolean("isPoint", (Boolean) newValue);
                editor.commit();
            return true;
            }
        });
    }


}
