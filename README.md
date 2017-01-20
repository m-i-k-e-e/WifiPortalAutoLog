# WifiPortalAutoLog
Sample project to show how you can use intent-filter to auto connect on captive portal.

## How it works ##
By adding the following lines to your activity, it will start when you click on the connection notification

    <intent-filter>
        <action android:name="android.net.conn.CAPTIVE_PORTAL" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>

The intent wil contain two extra parcelable items :
  + ConnectivityManager.EXTRA_NETWORK (*android.net.Network*)
  + ConnectivityManager.EXTRA_CAPTIVE_PORTAL (*android.net.CaptivePortal*)
And the action will be ConnectivityManager.ACTION_CAPTIVE_PORTAL_SIGN_IN.

Now, Bind the *ConnectivityManager* to the *Network* object.

And send a request to a known working location (like : http://clients3.google.com/generate_204).

You should get a 302. Follow the *Location* header and using javascript injection and a *WebView* fill the different fields and submit your form.

At the end of the logon, **don't forget** to call *reportCaptivePortalDismissed* from the *CaptivePortal* object to indicate to the system that the captive portal has been dismissed.
