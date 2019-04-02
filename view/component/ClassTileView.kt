package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class ClassTileView: FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.flClassTileLike -> {
                if(classData!!.is_subscribed == "Y")
                    classSubscribeCancel()
                else
                    classSubscribe()
            }
        }
    }

    var ivMain: ImageView? = null
    var flLike: FrameLayout? = null
    var ivLike: ImageView? = null
    var flPublic: FrameLayout? = null
    var ivPublic: ImageView? = null
    var tvCategory: TextView? = null
    var tvTitle: TextView? = null
    var tvTag: TextView? = null
    var tvLikeCount: TextView? = null
    var tvSubscribeCnt: TextView? = null
    var tvExperienceCnt: TextView? = null
    var classData: ClassData? = null

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_class_tile, this, false)
        ivMain = rootView.findViewById(R.id.ivClassTileMain)
        flLike = rootView.findViewById(R.id.flClassTileLike)
        ivLike = rootView.findViewById(R.id.ivClassTileLike)
        flPublic = rootView.findViewById(R.id.flClassTilePublic)
        ivPublic = rootView.findViewById(R.id.ivClassTilePublic)
        tvCategory = rootView.findViewById(R.id.tvClassTileCategory)
        tvTitle = rootView.findViewById(R.id.tvClassTileTitle)
        tvTag = rootView.findViewById(R.id.tvClassTileTag)
        tvLikeCount = rootView.findViewById(R.id.tvClassTileLikeCount)
        tvSubscribeCnt = rootView.findViewById(R.id.tvClassTileSubscribeCount)
        tvExperienceCnt = rootView.findViewById(R.id.tvClassTileExperienceCount)

        addView(rootView)

        flLike?.setOnClickListener(this)
    }

    internal fun setData(data: ClassData) {
        classData = data

        if (data.classroom!!.background_img == context.getString(R.string.default_text))
            ivMain?.setImageResource(R.drawable.default_bg)
        else {
            var bitmap = ImageCacheUtils.getBitmap(data.classroom!!.background_img)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getBigImageLinkPath(context, data.classroom!!.background_img))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivMain!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.classroom!!.background_img, resource)
                                ivMain?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivMain?.setImageBitmap(bitmap)
            }
        }

        if (data.is_subscribed == "Y")
            ivLike?.setImageResource(R.drawable.icon_follow)

        if (data.classroom!!.is_public == "N")
            ivPublic?.setImageResource(R.drawable.icon_lock)

        tvCategory?.text = "[" + data.classroom!!.category!!.depth_2!!.title + "]"
        tvTitle?.text = data.classroom!!.title
        tvTag?.text = CommonUtils.convertTagsToString(data.classroom!!.tags!!)
        tvLikeCount?.text = "좋아요 " + data.classroom!!.like_count + "명"
        tvSubscribeCnt?.text = "구독 " + data.classroom!!.subscribe_count + "명"
        tvExperienceCnt?.text = "체험활동 " + data.classroom!!.experience_count + "개"
    }

    private fun classSubscribe() {
        flLike?.isClickable = false
        var apiService = ApiManager.getInstance().apiService
        var subscribe = apiService.classSubscribe(classData!!.classroom!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        classData!!.is_subscribed = "Y"
                        classData!!.classroom!!.subscribe_count++
                        tvSubscribeCnt?.text = "구독 " + classData!!.classroom!!.subscribe_count + "명"
                        ivLike?.setImageResource(R.drawable.icon_follow)
                        Toast.makeText(context, context.getString(R.string.class_joined), Toast.LENGTH_SHORT).show()
                        flLike?.isClickable = true
                    } else {
                        Log.e("CLASS_SUBSCRIBE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                        flLike?.isClickable = true
                    }
                }, { error ->
                    Log.e("CLASS_SUBSCRIBE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                    flLike?.isClickable = true
                })
    }

    private fun classSubscribeCancel() {
        flLike?.isClickable = false
        var apiService = ApiManager.getInstance().apiService
        var subscribeCancel = apiService.classSubscribeCancel(classData!!.classroom!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        classData!!.is_subscribed = "N"
                        classData!!.classroom!!.subscribe_count--
                        tvSubscribeCnt?.text = "구독 " + classData!!.classroom!!.subscribe_count + "명"
                        ivLike?.setImageResource(R.drawable.icon_follow)
                        Toast.makeText(context, context.getString(R.string.class_joined), Toast.LENGTH_SHORT).show()
                        flLike?.isClickable = true
                    } else {
                        Log.e("CLASS_SUBSCRIBE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                        flLike?.isClickable = true
                    }
                }, { error ->
                    Log.e("CLASS_SUBSCRIBE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_class_join), Toast.LENGTH_SHORT).show()
                    flLike?.isClickable = true
                })
    }
}
