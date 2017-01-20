package org.mike.autolog;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;


public class PrefsFragment extends PreferenceFragmentCompat {

    public static PrefsFragment getInstance(){
        return new PrefsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
