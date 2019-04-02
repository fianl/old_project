package vdream.vd.com.vdream.view.activity

import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.FrameLayout
import vdream.vd.com.vdream.R

class WebViewActivity: BaseActivity() {
    var flBack: FrameLayout? = null
    var wvMain: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        flBack = findViewById(R.id.flWebBack)
        wvMain = findViewById(R.id.wvMain)

        var url = intent.getStringExtra(getString(R.string.intent_key_name_url))

        wvMain?.webChromeClient = ChromClient()
        wvMain?.webViewClient = CustomWebViewClient()
        wvMain?.settings?.javaScriptEnabled = true

        var cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wvMain?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cookieManager.setAcceptThirdPartyCookies(wvMain!!, true)
        }

        flBack?.setOnClickListener({
            finish()
        })

        wvMain?.loadUrl(url)
    }

    inner class CustomWebViewClient: WebViewClient() {}
    inner class ChromClient: WebChromeClient() {}
}