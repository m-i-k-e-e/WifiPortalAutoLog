package org.mike.autolog.preferences;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Html;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.mike.autolog.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PrefsFragment extends PreferenceFragment implements Response.ErrorListener, Response.Listener<String> {

    private static final String NO_INTERNET = "Retry with a internet connection\n\n";
    private static final String JOKE_URL = "http://api.icndb.com/jokes/random";
    private AlertDialog alertDialog;

    public static PrefsFragment getInstance() {
        return new PrefsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, android.preference.Preference preference) {
        if (preference.hasKey()) {
            if ("pref_about".equals(preference.getKey())) {
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.copyright))
                        .setCancelable(true)
                        .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_info, null))
                        .create();

                alertDialog.show();
                Request request = new StringRequest(JOKE_URL, this, this);
                Volley.newRequestQueue(getActivity()).add(request);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        alertDialog.setMessage(NO_INTERNET + getString(R.string.copyright));
    }

    @Override
    public void onResponse(String response) {
        final Pattern pattern = Pattern.compile(".*joke\": \"(.*)\",");
        final Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                alertDialog.setMessage(Html.fromHtml(matcher.group(1), 0) + "\n\n" + getString(R.string.copyright));
            } else {
                alertDialog.setMessage(Html.fromHtml(matcher.group(1)) + "\n\n" + getString(R.string.copyright));
            }
        }
    }
}
