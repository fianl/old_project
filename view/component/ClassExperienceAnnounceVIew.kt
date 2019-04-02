package vdream.vd.com.vdream.view.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.interfaces.AnnounceChangeCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.WriteExperienceActivity
import java.text.NumberFormat
import java.util.*

class ClassExperienceAnnounceVIew: FrameLayout, View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.flClassExpLike -> {
                if (expData!!.is_like == "Y")
                    setAnnounceLikeCancel()
                else
                    setAnnounceLike()
            }
            R.id.flClassExpMenu -> {
                createOptionDialog()
            }
        }
    }

    var ivProfile: ImageView? = null
    var tvNickname: TextView? = null
    var tvTime: TextView? = null
    var flMenu: FrameLayout? = null
    var vpMain: ViewPager? = null
    var llIndicator: LinearLayout? = null
    var flLike: FrameLayout? = null
    var ivLike: ImageView? = null
    var flPublic: FrameLayout? = null
    var ivPublic: ImageView? = null
    var tvTitle: TextView? = null
    var tvTag: TextView? = null
    var tvPrice: TextView? = null
    var tvPlace: TextView? = null
    var tvDate: TextView? = null

    var expData: FeedDetailData? = null
    var changeCallback: AnnounceChangeCallback? = null

    constructor(context: Context, changeCallback: AnnounceChangeCallback?) : super(context) {
        this.changeCallback = changeCallback
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_class_experience_item, this, false)
        ivProfile = rootView.findViewById(R.id.ivClassExpProfile)
        tvNickname = rootView.findViewById(R.id.tvClassExpNickname)
        tvTime = rootView.findViewById(R.id.tvClassExpTime)
        flMenu = rootView.findViewById(R.id.flClassExpMenu)
        vpMain = rootView.findViewById(R.id.vpClassExpMain)
        llIndicator = rootView.findViewById(R.id.llClassExpIndicator)
        flLike = rootView.findViewById(R.id.flClassExpLike)
        ivLike = rootView.findViewById(R.id.ivClassExpLike)
        flPublic = rootView.findViewById(R.id.flClassExpPublic)
        ivPublic = rootView.findViewById(R.id.ivClassExpPublic)
        tvTitle = rootView.findViewById(R.id.tvClassExpTitle)
        tvTag = rootView.findViewById(R.id.tvClassExpTag)
        tvPrice = rootView.findViewById(R.id.tvClassExpPrice)
        tvPlace = rootView.findViewById(R.id.tvClassExpPlace)
        tvDate = rootView.findViewById(R.id.tvClassExpDate)

        addView(rootView)

        flLike?.setOnClickListener(this)
        flMenu?.setOnClickListener(this)
    }

    internal fun setData(data: FeedDetailData) {
        this.expData = data

        if (data.profile_img.equals(context.getString(R.string.default_text))) {
            ivProfile?.setImageResource(R.drawable.default_profile)
        } else {
            var bitmap = ImageCacheUtils.getBitmap(data.profile_img)

            if(bitmap == null) {
                Glide.with(context)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(context, data.profile_img))
                        .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                        .into(object : ViewTarget<ImageView, Bitmap>(ivProfile!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.profile_img, resource)
                                ivProfile?.setImageBitmap(resource)
                            }
                        })
            }else{
                ivProfile?.setImageBitmap(bitmap)
            }
        }

        tvNickname?.text = data.nickname
        tvTime?.text = CommonUtils.calculateTimeFromCreated(data.created_at)

        var imageList = ArrayList<FileData>()
        var fileList = ArrayList<FileData>()

        if (data.files != null) {
            for (file in data.files!!) {
                if (file.kind.equals("FILE"))
                    fileList.add(file)
                else
                    imageList.add(file)
            }
        }

        if (imageList.size > 0) {
            indicatorInit(imageList)
            vpMain?.adapter = ExpImageAdapter(imageList)
            vpMain?.offscreenPageLimit = imageList.size
            vpMain?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    indicatorUiChange(position)
                }
            })
        } else {
            vpMain?.visibility = View.GONE
            llIndicator?.visibility = View.GONE
        }

        if (data.is_like == "Y")
            ivLike?.setImageResource(R.drawable.icon_like)

        if (data.status == context.getString(R.string.open_type_member))
            ivPublic?.setImageResource(R.drawable.icon_lock)

        if (data.is_mine == "Y") {
            flMenu?.visibility = View.VISIBLE
        }

        tvTitle?.text = data.title
        tvTag?.text = CommonUtils.convertTagsToString(data.tags!!)
        tvPrice?.text = NumberFormat.getCurrencyInstance(Locale.KOREA).format(data.price) + "원"
        tvPlace?.text = data.address_1
        tvDate?.text = data.opened_at.split(" ")[0].replace("-", ".")
    }

    private fun createDeleteConfirmDialog() {
        var deleteBuilder = AlertDialog.Builder(context)
        deleteBuilder.setTitle(context.getString(R.string.update_alert_title))
        deleteBuilder.setMessage(context.getString(R.string.dialog_delete_confirm_content))
        deleteBuilder.setPositiveButton(context.getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteFeed()
            }
        })
        deleteBuilder.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        deleteBuilder.create().show()
    }

    private fun deleteFeed() {
        var apiService = ApiManager.getInstance().apiService
        apiService.deleteExperience(expData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        changeCallback?.announceDeleted()
                    } else {
                        Log.e("DELETE_ANNOUNCE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("DELETE_ANNOUNCE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_delete_announce), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setAnnounceLike() {
        var apiService = ApiManager.getInstance().apiService
        apiService.experienceLike(expData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        expData!!.is_like = "Y"
                        ivLike?.setImageResource(R.drawable.icon_like)
                    } else {
                        Log.e("NOTICE_LIKE", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("NOTICE_LIKE", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_like), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setAnnounceLikeCancel() {
        var apiService = ApiManager.getInstance().apiService
        apiService.experienceLikeCancel(expData!!.idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        expData!!.is_like = "N"
                        ivLike?.setImageResource(R.drawable.icon_unlike)
                    } else {
                        Log.e("LIKE_CANCEL", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("LIKE_CANCEL", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_unlike), Toast.LENGTH_SHORT).show()
                })
    }

    inner class ExpImageAdapter : PagerAdapter {
        var images = ArrayList<FileData>()

        constructor(images: ArrayList<FileData>) {
            this.images = images
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return obj == view
        }

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var iv = ImageView(context)
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            var bitmap = ImageCacheUtils.getBitmap(images[position].file_name)

            if(bitmap == null) {
                Glide.with(context!!)
                        .asBitmap()
                        .load(CommonUtils.getImageWidePath(context!!, images[position].uploaded_path))
                        .into(object : ViewTarget<ImageView, Bitmap>(iv){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(images[position].file_name, resource)
                                iv.setImageBitmap(resource)
                            }
                        })
            }else {
                iv.setImageBitmap(bitmap)
            }

            container.addView(iv)
            return iv
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    private fun indicatorInit(images: ArrayList<FileData>) {
        for (idx in 0..images.lastIndex) {
            var indicatorItem = ImageView(context)
            var params = LinearLayout.LayoutParams((resources.displayMetrics.density * 6).toInt(), (resources.displayMetrics.density * 6).toInt())

            if (idx != 0)
                params.leftMargin = (resources.displayMetrics.density * 4).toInt()

            indicatorItem.layoutParams = params

            llIndicator?.addView(indicatorItem)

            indicatorItem.setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)

            if(idx == 0){
                indicatorItem.setBackgroundResource(R.drawable.rectangle_200dp_rounded_gray_all)
            }
        }
    }

    private fun indicatorUiChange(order: Int) {
        for (idx in 0 until llIndicator!!.childCount) {
            if (idx == order)
                llIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_gray_all)
            else
                llIndicator!!.getChildAt(idx).setBackgroundResource(R.drawable.rectangle_200dp_rounded_lightgray_all)
        }
    }

    private fun createOptionDialog() {
        var edit = Dialog(context)
        edit.window.requestFeature(Window.FEATURE_NO_TITLE)
        edit.setContentView(R.layout.dialog_annouce_edit_option)
        edit.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        edit.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvModify = edit.findViewById<TextView>(R.id.tvAnnounceEditOptModify)
        var tvDelete = edit.findViewById<TextView>(R.id.tvAnnounceEditOptDelete)

        tvModify.setOnClickListener {
            changeCallback?.requestUpdate("EXPERIENCE", expData!!.idx)
            edit.dismiss()
        }

        tvDelete.setOnClickListener {
            createDeleteConfirmDialog()
            edit.dismiss()
        }

        edit.show()
    }
}