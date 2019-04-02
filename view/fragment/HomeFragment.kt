package vdream.vd.com.vdream.view.fragment

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.crosswall.lib.coverflow.CoverFlow
import me.crosswall.lib.coverflow.core.PagerContainer
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.BannerData
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.data.ClassListRequestData
import vdream.vd.com.vdream.data.NewsFeedData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.ClassMoreActivity
import vdream.vd.com.vdream.view.component.ClassSummaryLargeImageView
import vdream.vd.com.vdream.view.component.NewsAdapter

class HomeFragment: Fragment(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flTodayMore -> {
                var intent = Intent(context!!, ClassMoreActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_class_kind), getString(R.string.type_today))
                startActivity(intent)
            }
        }
    }

    var vpBanner: ViewPager? = null
    var flMore: FrameLayout? = null
    var cfToday: PagerContainer? = null
    var vpToday: ViewPager? = null
    var llHomeContents: LinearLayout? = null
    var bannerList = ArrayList<BannerData>()
    var todayClasses: Array<ClassData>? = null

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, instance: Bundle?): View {
        var rootView = inflater.inflate(R.layout.fragment_home, parent, false)
        vpBanner = rootView.findViewById(R.id.vpHomeBanner)
        flMore = rootView.findViewById(R.id.flTodayMore)
        cfToday = rootView.findViewById(R.id.cfToday)
        vpToday = rootView.findViewById(R.id.vpToday)
        llHomeContents = rootView.findViewById(R.id.llHomeContentsContainer)

        flMore?.setOnClickListener(this)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getTodayClasses()
        getBannerData()
    }

    override fun onResume() {
        super.onResume()
        Log.d("HOME_FRAGMENT", "RESUME")
    }

    private fun getTodayClasses(){
        var requestToday = ClassListRequestData()

        var apiService = ApiManager.getInstance().apiService
        apiService.getClassList(1, requestToday)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        todayClasses = result.result!!.data
                        vpToday?.adapter = TodayClassesAdapter()
                        vpToday?.clipChildren = false
                        vpToday?.offscreenPageLimit = todayClasses!!.size

                        CoverFlow.Builder().with(vpToday)
                                .scale(0.0f)
                                .pagerMargin(-16 * resources.displayMetrics.density)
                                .spaceSize(0f)
                                .build()
                    }else{
                        Log.e("TODAY_LIST", result.error)
                    }
                }, { error ->
                    Log.e("TODAY_LIST", error.toString())
                })
    }

    private fun getBannerData(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getBannerInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({result ->
                    if(result.status == "Y"){
                        bannerList = result.result!!.toCollection(ArrayList())
                        var bannerAdapter = BannerAdapter()
                        vpBanner?.adapter = bannerAdapter
                    }else{
                        Log.e("GET_BANNER", result.error)
                    }
                }, { err ->
                    Log.e("GET_BANNER", err.toString())
                })
    }

    inner class BannerAdapter: PagerAdapter {
        constructor()

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
             return bannerList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var banner = bannerList[position]
            var iv = ImageView(context)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(context!!).load(CommonUtils.getImageWidePath(context!!, banner.image)).into(iv)
            iv.setOnClickListener {
                if(banner.url != null){
                    var intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse(banner.url))
                    startActivity(intent)
                }
            }

            container.addView(iv)

            return iv
        }
    }

    private fun bigBannerDialog(){
        var big = Dialog(context)
        big.window.requestFeature(Window.FEATURE_NO_TITLE)
        big.setContentView(R.layout.dialog_big_image)
        big.window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        big.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var ivImage = big.findViewById<ImageView>(R.id.ivBigDialog)

        ivImage.setImageResource(R.drawable.banner_big)

        big.show()
    }

    inner class TodayClassesAdapter: PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
            return todayClasses!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var data = todayClasses!![position]
            var classLargeView = ClassSummaryLargeImageView(context!!)
            classLargeView.setThumbnailImage(data.classroom!!.background_img)
            classLargeView.setTitle(data.classroom!!.category!!.depth_2!!.title, data.classroom!!.title)
            classLargeView.setTag(CommonUtils.convertTagsToString(data.classroom!!.tags!!))
            classLargeView.setOnClickListener({
                var intent = Intent(context!!, ClassDetailAcitivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), data.classroom!!.idx)
                startActivity(intent)
            })

            container?.addView(classLargeView)
            return classLargeView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }

    }
}