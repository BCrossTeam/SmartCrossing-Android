package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.futurologeek.smartcrossing.seekbar.SeekBarPreference;

import org.json.JSONException;
import org.json.JSONObject;


public class SettingsActivity extends PreferenceActivity {
    public static SettingsActivity instance;
    CheckBoxPreference units;
    CheckBoxPreference camFocus;
    SeekBarPreference radius;
    SharedPreferences.Editor editor;
    Preference signOut;
    Preference libs;
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
        instance = this;
        SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);
        editor = preferences.edit();


        signOut = (Preference) findPreference("log_out");
        units    = (CheckBoxPreference) findPreference("unit");
        camFocus = (CheckBoxPreference) findPreference("cam_focus");
        radius = (SeekBarPreference) findPreference("radius");
        libs = (Preference) findPreference("libs");

        libs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, LicensesActivity.class);
                startActivity(i);
                return true;
            }
        });

        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new signOut().execute();
                return true;
            }
        });

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

    class signOut extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceDelete(Constants.auth_url+UserInfo.token);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);

                    if(jsonObj.has("success")){
                        SettingsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DBHandler db = new DBHandler(SettingsActivity.this);
                                db.deleteAll();
                                db.close();
                                UserInfo.token = "";
                                UserInfo.uid = -1;
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SettingsActivity.this, LoadingActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                SettingsActivity.this.sendBroadcast(new Intent("finish_activity"));
                                finish();
                            }
                        });
                    } else {
                        SettingsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.logged_out_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
    }


}
