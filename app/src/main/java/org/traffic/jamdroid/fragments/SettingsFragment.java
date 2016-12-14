package org.traffic.jamdroid.fragments;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.traffic.jamdroid.R;

/**
 * Created by Daniel on 14.12.2016.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
