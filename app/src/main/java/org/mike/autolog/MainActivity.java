package org.mike.autolog;

import android.content.Context;
import android.content.Intent;
import android.net.CaptivePortal;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

    private WebView web;
    private Network net;
    private CaptivePortal captivePortal;
    private static final String URL = "http://clients3.google.com/generate_204";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN.equals(intent.getAction())) {
            net = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK);
            captivePortal = intent.getParcelableExtra(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);
        }
        web = (WebView) findViewById(R.id.webView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (net!=null && captivePortal != null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            cm.bindProcessToNetwork(net);
            final RequestQueue queue = Volley.newRequestQueue(this);

            final WebViewClient webClient = new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    view.evaluateJavascript("document.getElementById('user.username').value='"+ PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_user_id", "") +"';" +
                            "document.getElementById('user.password').value='"+ PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_password", "") +"';" +
                            "document.getElementById('aupAccepted').click();" +
                            "document.getElementById('ui_login_signon_button').click();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                    captivePortal.reportCaptivePortalDismissed();
                    finish();
                }
            };


            StringRequest checkConnectionRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            captivePortal.reportCaptivePortalDismissed();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse.statusCode == 302) {
                                web.getSettings().setJavaScriptEnabled(true);
                                web.setWebViewClient(webClient);
                                web.loadUrl(error.networkResponse.headers.get("Location"));
                            }
                        }
                    });

            queue.add(checkConnectionRequest);
        }
    }
}

