package vdream.vd.com.vdream.view.component

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.NewsFeedData

class NewsFeedView: FrameLayout {
    var ivThumbnail: ImageView? = null
    var tvTitle: TextView? = null
    var tvSource: TextView? = null
    var tvDate: TextView? = null
    
    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_news_item, this, false)
        ivThumbnail = rootView.findViewById(R.id.ivNewsThumbnail)
        tvTitle = rootView.findViewById(R.id.tvNewsTitle)
        tvSource = rootView.findViewById(R.id.tvNewsSource)
        tvDate = rootView.findViewById(R.id.tvNewsDate)

        addView(rootView)
    }

    internal fun setData(news: NewsFeedData) {
        if(news.thumbnail != null && news.thumbnail != ""){
            ivThumbnail?.visibility = View.VISIBLE
            Glide.with(context).load(news.thumbnail).into(ivThumbnail!!)
        }
        tvTitle!!.text = "[${news.category2}] ${news.title}"
        tvSource!!.text = "${news.category1}・"
        tvDate!!.text = news.created_at.replace("-", ".").split(" ")[0]


    }
}