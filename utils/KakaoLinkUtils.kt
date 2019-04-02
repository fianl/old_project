package vdream.vd.com.vdream.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.*
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.kakao.message.template.TextTemplate



class KakaoLinkUtils {
    companion object {
        fun sendKakaoFeedTemplete(context: Context, title: String, imageUrl: String, content: String, url: String?, excuteData: String){
            var feedTemplete = FeedTemplate
                    .newBuilder(ContentObject.newBuilder(title, imageUrl,
                            LinkObject.newBuilder().setWebUrl(url).build())
                            .setDescrption(content)
                            .build())
                    .addButton(ButtonObject("앱에서보기", LinkObject.newBuilder().setAndroidExecutionParams("idx=$excuteData").build()))
                    .build()

            KakaoLinkService.getInstance().sendDefault(context, feedTemplete, object : ResponseCallback<KakaoLinkResponse>(){
                override fun onSuccess(result: KakaoLinkResponse?) {

                }

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.e("KAKAO_LINK_FEED", errorResult.toString())
                }
            })
        }

        fun sendKakaoTextTemplete(context: Context, text: String, url: String?) {
            val params = TextTemplate.newBuilder(text, LinkObject.newBuilder()
                    .setWebUrl(url)
                            .build())
                    .build()

            KakaoLinkService.getInstance().sendDefault(context, params, object : ResponseCallback<KakaoLinkResponse>() {
                override fun onFailure(errorResult: ErrorResult) {
                    Log.e("KAKAO_LINK_TEXT", errorResult.toString())
                }

                override fun onSuccess(result: KakaoLinkResponse) {}
            })
        }

        fun sendNormalText(context: Context, title: String, content: String){
            var intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TITLE, title)
            intent.putExtra(Intent.EXTRA_SUBJECT, content)

            context.startActivity(intent)
        }
    }
}