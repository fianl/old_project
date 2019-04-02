package vdream.vd.com.vdream.view.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CommentData
import vdream.vd.com.vdream.data.CommentWriteData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils

class CommentActivity: BaseActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flCommentBack -> {
                sendAppEvent("댓글화면_헤더_백버튼")
                finish()
            }
            R.id.flWriteComment -> {
                sendAppEvent("댓글화면_댓글등록")
                postComment()
            }
        }
    }

    var flBack: FrameLayout? = null
    var rcvComments: RecyclerView? = null
    var ivWriterImage: ImageView? = null
    var etContent: EditText? = null
    var flPost: FrameLayout? = null

    var userData: UserInfoData? = null
    var classIdx = -1
    var referenceIdx = -1
    var articleIdx = -1
    var commentIdx = -1
    var commentArray: Array<CommentData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), -1)
        articleIdx = intent.getIntExtra(getString(R.string.intent_key_name_article_index), -1)
        referenceIdx = intent.getIntExtra(getString(R.string.intent_key_name_reference_index), -1)

        setContentView(R.layout.activity_comment)

        flBack = findViewById(R.id.flCommentBack)
        rcvComments = findViewById(R.id.rcvComments)
        ivWriterImage = findViewById(R.id.ivCommentWriterImage)
        etContent = findViewById(R.id.etCommentContent)
        flPost = findViewById(R.id.flWriteComment)

        flBack?.setOnClickListener(this)
        flPost?.setOnClickListener(this)

        init()
    }

    private fun init(){
        userData = MyInfoStore.myInfo

        var layoutManager = LinearLayoutManager(this)
        layoutManager?.orientation = LinearLayoutManager.VERTICAL
        rcvComments?.layoutManager = layoutManager

        getComments()
    }

    private fun getComments(){
        var apiService = ApiManager.getInstance().apiService

        if(classIdx == -1){
            apiService.getRecordCommentList(articleIdx)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        commentArray = result.result
                        setCommentData()
                    }, { error ->
                        Log.e("GET_COMMENTS", error.toString())
                    })
        }else {
            apiService.getCommentList(classIdx, referenceIdx)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        commentArray = result.result
                        setCommentData()
                    }, { error ->
                        Log.e("GET_COMMENTS", error.toString())
                    })
        }

    }

    private fun setCommentData(){
        var adapter = CommentListAdapter(this, commentArray!!.toCollection(ArrayList<CommentData>()), R.layout.view_comment)
        rcvComments?.adapter = adapter
    }

    private fun postComment(){
        flPost?.isClickable = false
        if(etContent!!.text.toString().equals("")){
            Toast.makeText(this, getString(R.string.input_comment), Toast.LENGTH_SHORT).show()
            return
        }

        var commentData = CommentWriteData()
        commentData.content = etContent!!.text.toString()

        if(commentIdx != -1)
            commentData.parent = commentIdx.toString()

        var apiServicde = ApiManager.getInstance().apiService

        if(classIdx == -1){
            apiServicde.registerRecordComment(articleIdx, commentData)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        Toast.makeText(this, getString(R.string.comment_register), Toast.LENGTH_SHORT).show()
                        getComments()
                        etContent?.setText("")
                        flPost?.isClickable = true
                    }, { error ->
                        Log.e("WRITE_COMMENT", error.toString())
                        flPost?.isClickable = true
                    })
        }else {
            apiServicde.writeComment(classIdx, referenceIdx, commentData)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe({ result ->
                        if(result.status == "Y") {
                            Toast.makeText(this, getString(R.string.comment_register), Toast.LENGTH_SHORT).show()
                            getComments()
                            etContent?.setText("")
                        }else{
                            Log.e("WRITE_COMMENT", result.error)
                            Toast.makeText(this, getString(R.string.fail_to_write_comment), Toast.LENGTH_SHORT).show()
                        }
                        flPost?.isClickable = true
                    }, { error ->
                        Log.e("WRITE_COMMENT", error.toString())
                        Toast.makeText(this, getString(R.string.fail_to_write_comment), Toast.LENGTH_SHORT).show()
                        flPost?.isClickable = true
                    })
        }
    }

    inner class CommentListAdapter: RecyclerView.Adapter<CommentListViewHolder> {
        var context: Context? = null
        var dataList = ArrayList<CommentData>()
        var layoutRes = 0

        constructor(context: Context, list: ArrayList<CommentData>, layoutRes: Int){
            this.context = context
            dataList = list
            this.layoutRes = layoutRes
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommentListViewHolder {
            var rootView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            var viewHolder = CommentListViewHolder(rootView)

            return viewHolder
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: CommentListViewHolder?, position: Int) {
            var comment = dataList.get(position)

            if(comment.depth == 1){
                holder?.flEmpty?.visibility = View.VISIBLE
            }

            if(comment.profile_img.equals(context!!.getString(R.string.default_text))){
                holder?.ivWriter?.setImageResource(R.drawable.default_profile)
            }else{
                var bitmap = ImageCacheUtils.getBitmap(comment.profile_img)

                if(bitmap == null) {
                    Glide.with(context!!)
                            .asBitmap()
                            .load(CommonUtils.getThumbnailLinkPath(context!!, comment.profile_img))
                            .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                            .into(object : ViewTarget<ImageView, Bitmap>(holder!!.ivWriter!!){
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(comment.profile_img, resource)
                                    holder!!.ivWriter?.setImageBitmap(resource)
                                }
                            })
                }else{
                    holder!!.ivWriter?.setImageBitmap(bitmap)
                }
            }

            holder?.tvWriterAndContent?.text = setSpannableString(comment.nickname, comment.content)
            holder?.tvDate?.text = CommonUtils.calculateTimeFromCreated(comment.created_at)

            holder?.tvReply?.setOnClickListener({view ->
                commentIdx = comment.idx
                etContent?.requestFocus()
            })
        }

        private fun setSpannableString(name: String?, content: String): SpannableStringBuilder {
            var nickname = "default_user"
            if(name != null)
                nickname = name

            var spannable = SpannableStringBuilder("$nickname $content")
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, nickname.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            return spannable
        }

    }

    inner class CommentListViewHolder: RecyclerView.ViewHolder {
        var flEmpty: FrameLayout? = null
        var ivWriter: ImageView? = null
        var tvWriterAndContent: TextView? = null
        var tvDate: TextView? = null
        var tvLikeCount: TextView? = null
        var tvReply: TextView? = null
        var ivLike: ImageView? = null

        constructor(view: View): super(view) {
            flEmpty = view.findViewById(R.id.flEmptySpace)
            ivWriter = view.findViewById(R.id.ivCommentWriterImage)
            tvWriterAndContent = view.findViewById(R.id.tvCommentWriterAndComment)
            tvDate = view.findViewById(R.id.tvCommentDate)
            tvLikeCount = view.findViewById(R.id.tvCommentLikeCount)
            tvReply = view.findViewById(R.id.tvCommentReply)
            ivLike = view.findViewById(R.id.ivCommentLike)
        }
    }

}