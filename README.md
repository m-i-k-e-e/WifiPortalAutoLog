# WifiPortalAutoLog
Sample project to show how you can use intent-filter to auto connect on captive portal. You need to **adapt the javascript injection for your portal**.
## Usage ##
### Setup your username and password ###
![pic 1](https://lh3.googleusercontent.com/mXNFMTfc95D7yZawjTrAPgijxrLTVkQD0WO8rlPSUzBuFPVLbtZjtXqnGK-TmMQ3qS4aMz4HY6Pd_TH_6VkNusWXRLcj_dA3BG6tC9UnYzGJaW8snXGQaTAQ6PNYN5jn9qasCTaPieXBu0Bp5z5rMwpTptlouqSRLq0HNfby-J4w8cz9YG9DxxPYjLmJXJfuzFJiKH_uUQ4yeexIPOPTdgF1L4ge0AoObIUhEw_2XE5QflCXbFZND06C7yAvK4x0QB7TPTEpafqtB2LC8HrsUC3kUOdjd_6s47FQa8Xj8HK1KK4VRbXjrKNnDFjzvp7ZJs1MJrh-pUMpoF__YI-xq1xpX57IlbOhdXth0l1qbon4AQPt5PShzine1zZwofaDIeKV7iznD1UevzOwksn-pxTwVLsY1rYL-f1IHVScINch5B5SvxCLzSF8fHXVjcztiHtoVmiyOBJB4JtYYDySNRIqYsbMqYp88Q0T6yJp3gelUPOIKQtN6GiAw4CJ9cnGkvNfH_N93MzJEXCjwGw9Q1vlCrflZFmMp-s3-TlQfgJxaKkKoTGLGwbY9JYe5DWnasa5lcOesYqObeqJ-lu1Yq6-c2X2AMfQGy3A34FYI5OxqzbmkKTGVbvfZK4pl-Ppp0yawgFvl3r0Pq-O4d7uJXekkcLTGB8wvA=w509-h904-no)
### Connect to a portal ###
When you connect to a captive portal a selection box will appear. Select the WifiPortalAutoLog application to connect to the configured portal or the other one for another portal. _**Always select the "Only Once" option regardless the connection application you are using**_

![pic 2](https://lh3.googleusercontent.com/-SC6qA3HVpRyIjnOFyKg9VN-1MTqnrpWUTkDAn69GoqEbHN63fxyBX8I0KfMAWXMPE8waFtj5RszGW4fFleiai1X_K1EUNPwdQgN38aEQbtbskWFjyFMfvQKJLQCENrlt06v8FdeQphRKzHcaz_ABjwtE0uWvHV0f02XciEbWLCLwSiFpwM6lZiJrpfAseZrdCT8yfxIuW0-YYQzxaoRajFPvb32u4iYD-_6rqZPyPNFjy1KK4Z49F1jaErwou9O9zoK9zs-9eup30jgrDQk0tLbZDmzcaiA6Kzo3ZgGe8x2wdUL3WSuKrD2Oq2cH3tVPxJH5g-Z8zKewhtFohtDUJH9_Y9Hrw4AJF4-9IIiByOtKQU8UI4LZel2zzl4hgtY7pux5HErzqI4bbeGpiczPYdIUyTm7BgwsvbeAxDNdP7Pq66d613PpH0kijji8qKBjq8v2lc5PGLz2VGVx0nVg6s_HwjABBz1iepGcos7j6MChAknpkl8-P9SwAEe1Wp5q6OpY1fb0X6k5VXtZRKOBFX2z5ShgmvZ0FoGvuzVjVLMMFbORNMRKwFMzLGZR2L69DUbVoVtYTsb_IPp3gijw5IMxFC9HWHyDc9RD-UY8vVyUGuCVRMK3XeN_zCOdc7xBjzl8RKkqIn75e4Kwgv3egSSu2hhxc_p3w=w509-h904-no)

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
