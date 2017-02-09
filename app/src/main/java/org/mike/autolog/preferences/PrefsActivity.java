package org.mike.autolog.preferences;

import android.app.Activity;
import android.os.Bundle;

import org.mike.autolog.R;

public class PrefsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getFragmentManager().beginTransaction().replace(R.id.container, PrefsFragment.getInstance()).commit();
    }
}
