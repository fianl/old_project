package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.AnnounceData
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.interfaces.CommentClickCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils

open class BaseAnnounceView: FrameLayout {
    var ivWriterImg: ImageView? = null
    var tvTitle: TextView? = null
    var tvWriter: TextView? = null
    var tvWriteTime: TextView? = null
    var flEdit: FrameLayout? = null
    var tvContent: TextView? = null
    var vpImages: ViewPager? = null
    var llAttchContainer: LinearLayout? = null
    var clLike: ConstraintLayout? = null
    var ivLike: ImageView? = null
    var clComment: ConstraintLayout? = null
    var ivComment: ImageView? = null
    var clShare: ConstraintLayout? = null
    var ivShare: ImageView? = null
    var commentClickCallBack: CommentClickCallback? = null

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    open fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_base_announce, this, false)

        ivWriterImg = rootView.findViewById(R.id.ivWriterImage)
        tvTitle = rootView.findViewById(R.id.tvAnnounceTitle)
        tvWriter = rootView.findViewById(R.id.tvWriterName)
        tvWriteTime = rootView.findViewById(R.id.tvAnnounceTime)
        flEdit = rootView.findViewById(R.id.flAnnounceEdit)
        tvContent = rootView.findViewById(R.id.tvAnnounceTextContent)
        vpImages = rootView.findViewById(R.id.vpAnnounceImages)
        llAttchContainer = rootView.findViewById(R.id.llAnnounceAttachContainer)
        clLike = rootView.findViewById(R.id.clAnnounceLike)
        ivLike = rootView.findViewById(R.id.ivAnnounceLike)
        clComment = rootView.findViewById(R.id.clAnnounceComment)
        ivComment = rootView.findViewById(R.id.ivAnnounceComment)
        clShare = rootView.findViewById(R.id.clAnnounceShare)
        ivShare = rootView.findViewById(R.id.ivAnnounceShare)

        addView(rootView)
    }

    internal fun setData(data: AnnounceData){
        Glide.with(context).load(data.writerImgUrl).apply(RequestOptions.bitmapTransform(CropCircleTransformation())).into(ivWriterImg!!)
        tvWriter?.setText(data.writerName)
        tvWriteTime?.setText(data.time)
        tvContent?.setText(data.textContent)

        if(data.attchUrlList != null){
            for(attchUrl in data.attchUrlList!!){
                var attchedView = AttachedContentView(context)
                attchedView.setData(attchUrl.filePath, attchUrl.fileName)

                llAttchContainer?.addView(attchedView)
            }
        }
    }

    open fun setImageResource(imgList: ArrayList<String>) {
        vpImages?.visibility = View.VISIBLE
        vpImages?.adapter = ImagePagerAdapter(context, imgList)
    }
}