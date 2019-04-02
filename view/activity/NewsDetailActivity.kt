package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.NewsFeedData
import vdream.vd.com.vdream.view.component.NewsFeedView

class NewsDetailActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flNewsDetailBack -> finish()
            R.id.tvNewsOrigin -> {
                var intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(newsList[0].url)
                startActivity(intent)
            }
        }
    }

    var newsList = ArrayList<NewsFeedData>()
    var flBack: FrameLayout? = null
    var tvTitle: TextView? = null
    var tvSource: TextView? = null
    var tvDate: TextView? = null
    var ivContent: ImageView? = null
    var tvContent: TextView? = null
    var tvOrigin: TextView? = null
    var llOthers: LinearLayout? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsList = intent.getSerializableExtra(getString(R.string.intent_key_name_news_data)) as ArrayList<NewsFeedData>

        setContentView(R.layout.activity_news_detail)

        flBack = findViewById(R.id.flNewsDetailBack)
        tvTitle = findViewById(R.id.tvNewsTitle)
        tvSource = findViewById(R.id.tvNewsSource)
        tvDate = findViewById(R.id.tvNewsDate)
        ivContent = findViewById(R.id.ivNewsImage)
        tvContent = findViewById(R.id.tvNewsContent)
        tvOrigin = findViewById(R.id.tvNewsOrigin)
        llOthers = findViewById(R.id.llOtherNews)

        tvTitle?.text = newsList[0].title
        tvSource?.text = newsList[0].category1
        tvDate?.text = newsList[0].created_at.replace("-", ".").split(" ")[0]
        tvContent?.text = newsList[0].content

        if(newsList[0].thumbnail != null){
            Glide.with(this).load(newsList[0].thumbnail).into(ivContent!!)
        }

        flBack?.setOnClickListener(this)
        tvOrigin?.setOnClickListener(this)

        for(idx in 1 until newsList.lastIndex){
            var news = newsList[idx]
            var newsView = NewsFeedView(this)
            newsView.setData(news)

           /* newsView.setOnClickListener({
                Log.d("NEWS_FEED", "CLICK")
                var intent = Intent(context!!, NewsDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_news_data), list.subList(list.indexOf(news), list.indexOf(news)+5) as ArrayList<NewsFeedData>)
                startActivity(intent)
            })*/

            llOthers?.addView(newsView)
        }
    }
}