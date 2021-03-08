package com.example.callapp

import android.webkit.JavascriptInterface

//take to CallActivity!
class JavascriptInterface(val callActivity: CallActivity) {

    @JavascriptInterface
    //this func call from the javascript
    public fun onPeerConnected() {
        callActivity.onPeerConnected()
    }

}