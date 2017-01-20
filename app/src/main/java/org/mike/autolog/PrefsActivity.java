package org.mike.autolog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrefsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, PrefsFragment.getInstance()).commit();
    }
}
