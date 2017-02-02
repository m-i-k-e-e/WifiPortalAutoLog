package org.mike.autolog.preferences;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.mike.autolog.R;

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, PrefsFragment.getInstance()).commit();
    }
}
