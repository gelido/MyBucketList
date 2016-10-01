package com.rafaelcarvalho.mybucketlist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rafaelcarvalho.mybucketlist.R;
import com.rafaelcarvalho.mybucketlist.activities.SettingsActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String COLOR_LIST_KEY = "theme_color";

    private ListPreference mColorPreference;
    private String mOldValue;

    public SettingsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mColorPreference = (ListPreference) findPreference(COLOR_LIST_KEY);
        mOldValue = mColorPreference.getValue();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        String summary = getResources().getString(R.string.blue);
        if(prefs != null) {
            onSharedPreferenceChanged(prefs,COLOR_LIST_KEY);
        }

        mColorPreference.setSummary(summary);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        onSharedPreferenceChanged(prefs,COLOR_LIST_KEY);

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(COLOR_LIST_KEY.equals(key)) {
            String newValue = mColorPreference.getValue();
            String colorValue = sharedPreferences.getString(key,"");
            String color;
            switch (colorValue){
                case "0":
                    color = getResources().getString(R.string.blue);
                    break;
                case "1":
                    color = getResources().getString(R.string.orange);
                    break;
                default:
                    color = getResources().getString(R.string.choose_color_summary);
            }
            mColorPreference.setSummary(color);
            getActivity().getIntent().putExtra(SettingsActivity.IS_MODIFIED, true);
            if(!this.mOldValue.equals(newValue)){
                getActivity().recreate();
            }
        }
    }


}
