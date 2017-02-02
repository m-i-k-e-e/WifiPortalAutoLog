package org.mike.autolog.preferences;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.mike.autolog.R;


public class PrefsFragment extends PreferenceFragmentCompat {

    public static PrefsFragment getInstance() {
        return new PrefsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.hasKey()) {
            if ("pref_about".equals(preference.getKey())) {
                new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Copyright Schenaerts Michaël")
                        .setCancelable(true)
                        .setIcon(ContextCompat.getDrawable(getContext(), android.R.drawable.ic_dialog_info))
                        .create().show();
            }
        }
        return super.onPreferenceTreeClick(preference);
    }
}
