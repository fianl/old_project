package vdream.vd.com.vdream.view.component

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.BaseAdapter
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.NewsFeedData

class NewsAdapter: BaseAdapter {
    var context: Context? = null
    var list = ArrayList<NewsFeedData>()

    constructor(context: Context, list: ArrayList<NewsFeedData>){
        this.context = context
        this.list = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: NewsHolder? = null
        var view = convertView
        var news = list[position]

        if(convertView == null){
            view = LayoutInflater.from(context!!).inflate(R.layout.view_news_item, parent, false)
            holder = NewsHolder()
            holder.tvTitle = view.findViewById(R.id.tvNewsTitle)
            holder.tvSource = view.findViewById(R.id.tvNewsSource)
            holder.tvDate = view.findViewById(R.id.tvNewsDate)
            holder.tvContent = view.findViewById(R.id.tvNewsContent)
            view?.tag = holder
        }else{
            holder = view!!.tag as NewsHolder
        }

        holder.tvTitle!!.text = "[${news.category2}] ${news.title}"
        holder.tvSource!!.text = news.category1
        holder.tvDate!!.text = news.created_at.replace("-", ".")
        holder.tvContent!!.text = news.content


        view?.setOnClickListener({
            /*if(news.isExpandable){
                var hideAni = ScaleAnimation(1f, 1f, 1f, 0f)
                hideAni.duration = 300
                hideAni.setAnimationListener(object : Animation.AnimationListener{
                    override fun onAnimationRepeat(animation: Animation?) {}
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        holder.tvContent?.visibility = View.GONE
                    }
                })
                holder.tvContent?.startAnimation(hideAni)
            }else{
                holder.tvContent?.visibility = View.VISIBLE
                var showAni = ScaleAnimation(1f, 1f, 0f, 1f)
                showAni.duration = 300
                holder.tvContent?.startAnimation(showAni)
            }
            news.isExpandable = !news.isExpandable*/
        })

        return view!!
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    inner class NewsHolder {
        var tvTitle: TextView? = null
        var tvSource: TextView? = null
        var tvDate: TextView? = null
        var tvContent: TextView? = null
    }
}