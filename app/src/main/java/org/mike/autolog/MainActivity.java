package org.mike.autolog;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends Activity implements Response.Listener<String>, Response.ErrorListener {

    private static final String PARAM_NETWORK_INFO = "org.mike.autolog.NETWORK_INFO";
    private static final String URL = "http://clients3.google.com/generate_204";

    private WebView web;
    private TextView textView;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager cm;
    private Network network;
    private android.support.v4.app.NotificationCompat.Builder notification;
    private NotificationManager mNotificationManager;

    public static Intent newIntent(Context ctx, NetworkInfo networkInfo) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(PARAM_NETWORK_INFO, networkInfo);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = (WebView) findViewById(R.id.webView);
        textView = (TextView) findViewById(R.id.info_view);
        textView.setText(R.string.info_starting);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_wifi_lock_black_24dp).setContentTitle(getString(R.string.app_name));
        notification.setContentText(getString(R.string.info_starting));
        mNotificationManager.notify(1, notification.build());
    }

    @Override
    protected void onStart() {
        super.onStart();

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                MainActivity.this.network = network;
                cm.unregisterNetworkCallback(networkCallback);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.bindProcessToNetwork(network);
                } else {
                    ConnectivityManager.setProcessDefaultNetwork(network);
                }
                final RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                StringRequest checkConnectionRequest = new StringRequest(Request.Method.GET, URL, MainActivity.this, MainActivity.this);
                queue.add(checkConnectionRequest);
            }
        };
        cm.requestNetwork(new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), networkCallback);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 302) {

            final WebViewClient webClient = new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, final String url) {
                    super.onPageFinished(view, url);
                    textView.setText(R.string.info_portal_injecting);
                    notification.setContentText(getString(R.string.info_portal_injecting));
                    mNotificationManager.notify(1, notification.build());
                    view.evaluateJavascript("document.getElementById('user.username').value='" + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_user_id", "") + "';" +
                            "document.getElementById('user.password').value='" + PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("pref_password", "") + "';" +
                            "document.getElementById('aupAccepted').click();" +
                            "document.getElementById('ui_login_signon_button').click();", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("TAG", "Connected " + s);
                            notification.setContentText("Connected");
                            mNotificationManager.notify(1, notification.build());
                            new Thread(new Runnable() {
                                public void run() {
                                    // Give time for captive portal to open.
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        cm.reportNetworkConnectivity(network, true);
                                    } else {
                                        cm.reportBadNetwork(network);
                                    }
                                }
                            }).start();
                        }
                    });
                    finish();
                }
            };

            textView.setText(R.string.info_starting);
            web.getSettings().setJavaScriptEnabled(true);
            web.setWebViewClient(webClient);
            web.loadUrl(error.networkResponse.headers.get("Location"));
        } else {
            textView.setText(R.string.guest_long_unknown_error);
            finish();
        }
    }

    @Override
    public void onResponse(String response) {
        Log.d("MainActivity", "Already connected");
        notification.setContentText("Connected");
        mNotificationManager.notify(1, notification.build());
        textView.setText(R.string.info_no_portal);
        finish();
    }
}

