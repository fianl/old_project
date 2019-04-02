package vdream.vd.com.vdream.view.activity

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import vdream.vd.com.vdream.R
import android.os.Build
import android.util.Log
import android.webkit.*
import com.kakao.util.helper.Utility
import java.net.URISyntaxException
import java.net.URLDecoder


class PaymentWebViewActivity: BaseActivity() {
    val ISP_LINK = "market://details?id=kvp.jjy.MispAndroid320"
    val KFTC_LINK = "market://detais?id=com.kftc.bankpay.android"
    val MERCHANT_URL = "http://payment.vdream.co.kr/?doc=request"
    var NICE_BANK_URL: String? = ""
    var WAP_URL = "littleplanet://"
    var BANK_TID: String? = ""

    var wvMain: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_webview)

        wvMain = findViewById(R.id.wbPayment)
        wvMain?.webChromeClient = ChromClient()
        wvMain?.webViewClient = CustomWebViewClient()
        wvMain?.settings?.javaScriptEnabled = true

        var cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wvMain?.settings?.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cookieManager.setAcceptThirdPartyCookies(wvMain!!, true)
        }

        var uri = intent.data

        if(uri != null){
            var url = uri.toString()

            if(url.startsWith(WAP_URL)){
                wvMain?.loadUrl(url.substring(WAP_URL.length))
            }
        }else{
            wvMain?.postUrl(MERCHANT_URL, null)
        }
    }

    inner class ChromClient: WebChromeClient() {

    }

    inner class CustomWebViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
            var intent: Intent? = null

            if(url.startsWith("ispmobile")) {
                if(Utility.isPackageInstalled(applicationContext, "kvp.jjy.MispAndroid320")){
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }else{
                    installISP()
                    return true
                }
            }else if(url.startsWith("kftc-bankpay")){
                if(Utility.isPackageInstalled(applicationContext, "com.kftc.bankpay.android")){
                    var sub_str = "kftc-bankpay://eftpay?"
                    var reqParam = url.substring(sub_str.length)
                    try{
                        reqParam = URLDecoder.decode(reqParam, "utf-8")
                    }catch (e: Exception){
                        Log.e("URL_DECODE", e.toString())
                    }

                    reqParam = makeBankPayData(reqParam)
                    intent = Intent(Intent.ACTION_MAIN)
                    intent.component = ComponentName("com.kftc.bankpay.android", "com.kftc.bankpay.android.activyt.MainActivity")
                    intent.putExtra("requestInfo", reqParam)
                    startActivityForResult(intent, 1)

                    return true
                }else{
                    installKFTC()
                    return true
                }
            }else if(url != null && (url.contains("vguard")
                            || url.contains("droidxantivirus")
                            || url.contains("lottesmartpay")
                            || url.contains("smshinhancardusim://")
                            || url.contains("shinhan-sr-ansimclick")
                            || url.contains("v3mobile")
                            || url.endsWith(".apk")
                            || url.contains("smartwall://")
                            || url.contains("appfree://")
                            || url.contains("market://")
                            || url.contains("ansimclick://")
                            || url.contains("ansimclickscard")
                            || url.contains("ansim://")
                            || url.contains("mpocket")
                            || url.contains("mvaccine")
                            || url.contains("market.android.com")
                            || url.startsWith("intent://")
                            || url.contains("samsungpay")
                            || url.contains("droidx3web://")
                            || url.contains("kakaopay")
                            || url.contains("callonlinepay")
                            || url.contains("http://m.ahnlab.com/kr/site/download"))){
                try{
                    try{
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    }catch (e: URISyntaxException){
                        Log.e("BAD_URI", url + e.message)
                        return false
                    }

                    if(url.startsWith("intent")){
                        if(packageManager.resolveActivity(intent, 0) == null){
                            var packageName = intent!!.`package`

                            if(packageName != null){
                                var uri = Uri.parse("market://search?q=panme:" + packageName)
                                intent = Intent(Intent.ACTION_VIEW, uri)
                                startActivity(intent)
                                return true
                            }
                        }

                        var uri = Uri.parse(intent!!.dataString)
                        intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                        return true
                    }else{
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    }
                }catch (e: Exception){
                    Log.e("NICE_PAY", e.message)
                    return false
                }
            }else if(url.startsWith(WAP_URL)){
                var thisUrl = url.substring(WAP_URL.length)
                view!!.loadUrl(thisUrl)
                return true
            }else{
                view?.loadUrl(url)
                return false
            }

            return true
        }
    }

    private fun makeBankPayData(str: String): String {
        val arr = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var parse_temp: Array<String>
        val tempMap = HashMap<String, String>()
        for (i in arr.indices) {
            try {
                parse_temp = arr[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                tempMap[parse_temp[0]] = parse_temp[1]
            } catch (e: Exception) {
            }

        }
        BANK_TID = tempMap["user_key"]
        NICE_BANK_URL = tempMap["callbackparam1"]

        return str
    }

    private fun installISP() {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(ISP_LINK)
        startActivity(intent)
    }

    private fun installKFTC() {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(KFTC_LINK)
        startActivity(intent)
    }
}