package vdream.vd.com.vdream.view.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.NewsFeedData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.view.activity.NewsDetailActivity
import vdream.vd.com.vdream.view.component.NewsFeedView
import vdream.vd.com.vdream.view.dialog.CommonProgressDialog

class NewsFragment: Fragment() {
    private var llContainer: LinearLayout? = null
    private var progressDialog: CommonProgressDialog?  = null
    private var newsList = ArrayList<NewsFeedData>()
    var page = 0
    var offest = 15

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.fragment_news, container, false)
        llContainer = rootView.findViewById<LinearLayout>(R.id.llNewsContainer)

        if(newsList.isEmpty())
            getNewsFeed()
        else
            setNewsUI(newsList)

        return rootView
    }

    open fun getNewsFeed(){
        showProgressDialog()
        var apiService = ApiManager.getInstance().apiService
        apiService.getNewsFeed(offest, (page+1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    progressDialog?.dismiss()
                    if(result.status == "Y"){
                        page = result.result!!.page
                        newsList.addAll(result.result!!.data!!.toCollection(ArrayList()))

                        if(page == 1)
                            setNewsUI(newsList)
                        else
                            setNewsUI(result.result!!.data!!.toCollection(ArrayList()))
                    }else{
                        Log.e("GET_NEWS", result.error)
                    }
                }, { err ->
                    progressDialog?.dismiss()
                    Log.e("GET_NEWS", err.toString())
                })
    }

    private fun setNewsUI(list: ArrayList<NewsFeedData>){
        for(news in list){
            var newsView = NewsFeedView(context!!)
            newsView.setData(news)

            newsView.setOnClickListener({
                Log.d("NEWS_FEED", "CLICK")
                var intent = Intent(context!!, NewsDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_news_data), getNewsSubList(list.indexOf(news), list.indexOf(news)+7, list))
                startActivity(intent)
            })

            llContainer?.addView(newsView)
        }
    }

    private fun showProgressDialog() {
        if(progressDialog == null)
            progressDialog = CommonProgressDialog(context!!)

        progressDialog?.show()
    }

    private fun getNewsSubList(start: Int, end: Int, list: ArrayList<NewsFeedData>): ArrayList<NewsFeedData>{
        var result = ArrayList<NewsFeedData>()

        for(idx in start until end){
            result.add(list[idx])
        }

        return result
    }
}