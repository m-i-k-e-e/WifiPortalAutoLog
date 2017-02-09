package org.mike.autolog.broadcast;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.mike.autolog.R;

public class GuestLongReceiver extends BroadcastReceiver implements Response.ErrorListener, Response.Listener<String>, ValueCallback<String> {

    private static final int NOTIFICATION_ID = 1;
    private static final String AP_NAME = "\"ING_GUEST_LONG\"";
    private static final String URL = "http://clients3.google.com/generate_204";
    private static final String SCRIPT = "document.getElementById('user.username').value='%s'; document.getElementById('user.password').value='%s'; document.getElementById('aupAccepted').click(); document.getElementById('ui_login_signon_button').click();";
    private static final String LOCATION_HEADER = "Location";

    private Context currentContext;
    private Network wifiNetwork;
    private ConnectivityManager cm;
    private NotificationManager mNotificationManager;
    private Notification.Builder notification;

    @Override
    public void onReceive(final Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (networkInfo != null) {
            if (AP_NAME.equals(networkInfo.getExtraInfo()) && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                this.currentContext = context;
                connect(context);
            }
        }
    }

    private synchronized void connect(final Context context) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                wifiNetwork = network;
                cm.unregisterNetworkCallback(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.bindProcessToNetwork(network);
                } else {
                    ConnectivityManager.setProcessDefaultNetwork(network);
                }
                final RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest checkConnectionRequest = new StringRequest(URL, GuestLongReceiver.this, GuestLongReceiver.this);
                queue.add(checkConnectionRequest);
            }
        };
        cm.requestNetwork(new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(), networkCallback);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onErrorResponse(VolleyError error) {
        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 302) {
            mNotificationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new Notification.Builder(currentContext).setSmallIcon(R.drawable.ic_wifi_lock_black_24dp).setContentTitle(currentContext.getString(R.string.app_name));
            notification.setContentText(currentContext.getString(R.string.info_connecting));
            mNotificationManager.notify(NOTIFICATION_ID, notification.build());
            final WebView webView = new WebView(currentContext);
            final WebViewClient webClient = new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, final String url) {
                    super.onPageFinished(view, url);
                    notification.setContentText("page loaded");
                    mNotificationManager.notify(NOTIFICATION_ID, notification.build());
                    view.evaluateJavascript(
                            String.format(SCRIPT,
                                    PreferenceManager.getDefaultSharedPreferences(currentContext).getString("pref_user_id", ""),
                                    PreferenceManager.getDefaultSharedPreferences(currentContext).getString("pref_password", ""))
                            , GuestLongReceiver.this);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    notification.setContentText(currentContext.getString(R.string.unable_connect));
                    mNotificationManager.notify(NOTIFICATION_ID, notification.build());
                }
            };

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(webClient);
            webView.loadUrl(error.networkResponse.headers.get(LOCATION_HEADER));
        }
    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void onReceiveValue(String value) {
        new Thread(new Runnable() {
            public void run() {
                // Give time for captive portal to open.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //nothing to do
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cm.reportNetworkConnectivity(wifiNetwork, true);
                } else {
                    cm.reportBadNetwork(wifiNetwork);
                }
                mNotificationManager.cancel(NOTIFICATION_ID);
            }
        }).start();
    }
}
