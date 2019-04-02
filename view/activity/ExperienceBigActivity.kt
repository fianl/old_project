package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import kotlin.math.exp

class ExperienceBigActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flExpBigBack -> {
                sendAppEvent("체험크게보기화면_헤더_백버튼")
                finish()
            }
            R.id.flExpBigLike -> {
                if(expData!!.is_like == "Y") {
                    sendAppEvent("체험크게보기화면_좋아요_취소")
                    setAnnounceLikeCancel()
                }else {
                    sendAppEvent("체험크게보기화면_좋아요")
                    setAnnounceLike()
                }
            }
            R.id.tvExpBigDetail -> {
                sendAppEvent("체험크게보기화면_자세히보기")
                var intent = Intent(this, ExperienceDetailActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_index), classIdx)
                intent.putExtra(getString(R.string.intent_key_name_feeddata), expData)
                startActivity(intent)
                finish()
            }
        }
    }

    var vpImages: ViewPager? = null
    var flBack: FrameLayout? = null
    var flLike: FrameLayout? = null
    var ivLike: ImageView? = null
    var llPageIndicator: LinearLayout? = null
    var ivPublic: ImageView? = null
    var tvTitle: TextView? = null
    var tvSummary: TextView? = null
    var tvSeeDetail: TextView? = null
    var flSubscribe: FrameLayout? = null
    var ivSubscribe: ImageView? = null
    var tvLikeCount: TextView? = null
    var tvSubscribeCount: TextView? = null
    var tvTag: TextView? = null

    var classIdx = 0
    var expData: FeedDetailData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), 0)
        expData = intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) as FeedDetailData
        setContentView(R.layout.activity_experience_big)
        init()
    }

    private fun init() {
        vpImages = findViewById(R.id.vpExpBigBackground)
        flBack = findViewById(R.id.flExpBigBack)
        flLike = findViewById(R.id.flExpBigLike)
        ivLike = findViewById(R.id.ivExpBigLike)
        llPageIndicator = findViewById(R.id.llExpBigPageIndicator)
        ivPublic = findViewById(R.id.ivExpBigPublic)
        tvTitle = findViewById(R.id.tvExpBigTitle)
        tvSummary = findViewById(R.id.tvExpBigSummary)
        tvSeeDetail = findViewById(R.id.tvExpBigDetail)
        flSubscribe = findViewById(R.id.flExpBigSubscribe)
        ivSubscribe = findViewById(R.id.ivExpBigSubscribe)
        tvLikeCount = findViewById(R.id.tvExpBigLikeCount)
        tvSubscribeCount = findViewById(R.id.tvExpBigSubscribeCount)
        tvTag = findViewById(R.id.tvExpBigTag)

        if(expData!!.is_like == "Y")
            ivLike?.setImageResource(R.drawable.icon_like)

        if(expData!!.files!!.size > 0){
            indicatorInit()
            vpImages?.offscreenPageLimit = expData!!.files!!.size
            vpImages?.adapter = BackgroundAdpater()

            vpImages?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    indicatorUiChange(position)
                }
            })
        }

        if(expData!!.status != getString(R.string.open_type_public))
            ivPublic?.setImageResource(R.drawable.lock_on_white)

        tvLikeCount?.text = convertIntToString(expData!!.like_count)
        tvTitle?.text = expData!!.title
        tvSummary?.text = expData!!.summary
        tvSubscribeCount?.text = convertIntToString(78)
        tvTag?.text = CommonUtils.convertTagsToString(expData!!.tags!!)

        flBack?.setOnClickListener(this)
        flLike?.setOnClickListener(this)
        tvSeeDetail?.setOnClickListener(this)
    }

    inner class BackgroundAdpater: PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
            return expData!!.files!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var iv = ImageView(applicationContext)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(applicationContext).load(CommonUtils.getOriginImagePath(applicationContext, expData!!.files!![position].uploaded_path))
                    .into(iv)

            container.addView(iv)
            return iv
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    private fun indicatorInit() {
        for(idx in 0..expData!!.files!!.lastIndex){
            var indicatorItem = ImageView(this)
            var params = LinearLayout.LayoutParams((resources.displayMetrics.density*6).toInt(), (resources.displayMetrics.density*6).toInt())

            if(idx != 0)
                params.leftMargin = (resources.displayMetrics.density*4).toInt()

            indicatorItem.layoutParams = params

            llPageIndicator?.addView(indicatorItem)

            indicatorItem.setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)
        }
    }

    private fun indicatorUiChange(order: Int) {
        for(idx in 0 until llPageIndicator!!.childCount){
            if(idx == order)
                llPageIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_gray_all)
            else
                llPageIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)
        }
    }

    private fun convertIntToString(num: Int): String {
        var convert = ""

        if(num > 999)
            convert = String.format("%.1f", (num.toFloat()/1000f)) + "K"
        else
            convert = num.toString()

        return convert
    }

    private fun setAnnounceLike(){
        var apiService = ApiManager.getInstance().apiService
        apiService.experienceLike(expData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        expData!!.is_like = "Y"
                        ivLike?.setImageResource(R.drawable.icon_like)
                        expData!!.like_count++
                        tvLikeCount?.text = convertIntToString(expData!!.like_count)
                    }else{
                        Log.e("NOTICE_LIKE", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("NOTICE_LIKE", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setAnnounceLikeCancel() {
        var apiService = ApiManager.getInstance().apiService
        apiService.experienceLikeCancel(expData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        expData!!.is_like = "N"
                        ivLike?.setImageResource(R.drawable.icon_unlike)
                        expData!!.like_count--
                        tvLikeCount?.text = convertIntToString(expData!!.like_count)
                    }else{
                        Log.e("LIKE_CANCEL", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("LIKE_CANCEL", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                })
    }
}