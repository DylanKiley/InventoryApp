package com.kylecorry.inventory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kyle on 11/3/14.
 */
public class Settings extends AppCompatActivity {
    static Preference emailPref;
    static SharedPreferences sharedPreferences;

    public static final String MAIN_EMAIL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.blank);
        getWindow().setBackgroundDrawable(null);
        // put the settings on the screen
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // add all of the settings
            addPreferencesFromResource(R.xml.prefs);
            sharedPreferences = getPreferenceManager().getSharedPreferences();
            // set the email preference summary to the email
            String email = sharedPreferences.getString("email", MAIN_EMAIL);
            if (email.isEmpty()) {
                email = MAIN_EMAIL;
            }
            emailPref = getPreferenceScreen().findPreference("email");
            emailPref.setSummary(email);

            // monitor changes in the settings
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // set the email preference summary to the email when changed
            if (key.contentEquals("email")) {
                String userMessage = sharedPreferences.getString("email", MAIN_EMAIL);
                if (userMessage.isEmpty()) {
                    userMessage = MAIN_EMAIL;
                }
                emailPref.setSummary(userMessage);
            }
        }
    }
}
