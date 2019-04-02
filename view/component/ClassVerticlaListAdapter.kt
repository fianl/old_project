package vdream.vd.com.vdream.view.component

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.SearchActivity

class ClassVerticalListAdapter: RecyclerView.Adapter<ClassVerticalListViewHolder> {
    var context: Context? = null
    var dataList = ArrayList<ClassData>()
    var layoutRes = 0

    constructor(context: Context, list: ArrayList<ClassData>, layoutRes: Int){
        this.context = context
        dataList = list
        this.layoutRes = layoutRes
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClassVerticalListViewHolder {
        var rootView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
        var viewHolder = ClassVerticalListViewHolder(rootView)

        return viewHolder
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ClassVerticalListViewHolder?, position: Int) {
        var data = dataList[position]

        if(data.classroom!!.background_img == context!!.getString(R.string.default_text))
            holder!!.ivMain!!.setImageResource(R.drawable.default_bg)
        else {
            var bitmap = ImageCacheUtils.getBitmap(data.classroom!!.title)

            if(bitmap == null) {
                Glide.with(context!!)
                        .asBitmap()
                        .load(CommonUtils.getThumbnailLinkPath(context!!, data.classroom!!.background_img))
                        .into(object : ViewTarget<ImageView, Bitmap>(holder!!.ivMain!!){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                ImageCacheUtils.putBitmap(data.classroom!!.title, resource)
                                holder!!.ivMain?.setImageBitmap(resource)
                            }
                        })
            }else{
                holder!!.ivMain?.setImageBitmap(bitmap)
            }
        }

        holder.tvTitle!!.text = data.classroom!!.title
        holder.tvCategory!!.text = data.classroom!!.category!!.depth_1!!.title + " / " + data.classroom!!.category!!.depth_2!!.title

        if(data.classroom!!.is_locale == "Y")
            holder.tvLocation!!.text = data.classroom!!.address_1 + " " + data.classroom!!.address_2

        if(data.is_mine == "Y")
            holder.ivIsMine!!.visibility = View.VISIBLE

        if(data.is_joined == "Y")
            holder.ivIsJoined!!.visibility = View.VISIBLE

        holder!!.clContainer!!.setOnClickListener({
            var intent = Intent(context, ClassDetailAcitivity::class.java)
            intent.putExtra(context!!.getString(R.string.intent_key_name_index), data.classroom!!.idx)
            context!!.startActivity(intent)
        })
    }

}