package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.wasabeef.glide.transformations.CropCircleTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.BoardRegisterData
import vdream.vd.com.vdream.data.FeedDetailData
import vdream.vd.com.vdream.data.UploadImageFormData
import vdream.vd.com.vdream.data.UserInfoData
import vdream.vd.com.vdream.interfaces.AnnounceImageChangeCallback
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.network.S3Uploader
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.component.RecordOptionView
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class WriteFeedActivity: BaseActivity(), View.OnClickListener, UploadFinishCallback, AnnounceImageChangeCallback{
    override fun imageDeleted(idx: Int) {
        if(feedData != null) {
            var isOldImage = false
            for (data in feedData!!.files!!) {
                if (data.kind == "IMAGE" && data.idx == idx) {
                    deleteList.add(idx)
                    isOldImage = true
                    break
                }
            }

            if(!isOldImage)
                fileList.remove(idx)
        }else{
            fileList.remove(idx)
        }
    }

    override fun uploadFinished(uploadPath: ArrayList<String>, filename: ArrayList<String>) {
        registerData.images = Array<UploadImageFormData>(uploadPath.size, {i -> UploadImageFormData(filename.get(i), uploadPath.get(i))})

        if(feedData == null)
            registerBoard()
        else
            updateBoard()
    }

    val GALLERY_IMAGE = 0
    val ADDRESS_ON_MAP = 10

    var fileList = HashMap<Int, String>()
    var writerInfo: UserInfoData? = null

    var openKind = arrayOf("PUBLIC", "MEMBER")
    var curOpen = openKind[0]
    var isNotice = "Y"
    var feedData: FeedDetailData? = null

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flFeedBack -> {
                sendAppEvent("클래스게시물작성_헤더_뒤로가기")
                finish()
            }
            R.id.tvFeedSave -> {
                sendAppEvent("클래스게시물작성_게시물등록")
                checkBoardData()
            }
            R.id.clFeedOpen -> {
                sendAppEvent("클래스게시물작성_공개설정")
                getOpenTypeDialog()
            }
        }
    }

    var flBack: FrameLayout? = null
    var ivWriterImg: ImageView? = null
    var tvWriterName: TextView? = null
    var clRecordOpen: ConstraintLayout? = null
    var tvRecordOpen: TextView? = null
    var etContent: EditText? = null
    var rovTag: RecordOptionView? = null
    var rovPicture: RecordOptionView? = null
    var rovVideo: RecordOptionView? = null
    var rovLocation: RecordOptionView? = null
    var tvSave: TextView? = null

    var classIdx = 0
    var registerData = BoardRegisterData()
    var deleteList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writerInfo = MyInfoStore.myInfo
        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), 1)
        isNotice = intent.getStringExtra(getString(R.string.intent_key_name_is_notice))

        if(intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) != null)
            feedData = intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) as FeedDetailData

        setContentView(R.layout.activity_write_feed)

        flBack = findViewById(R.id.flFeedBack)
        ivWriterImg = findViewById(R.id.ivFeedWriterImg)
        tvWriterName = findViewById(R.id.tvFeedWriterName)
        clRecordOpen = findViewById(R.id.clFeedOpen)
        tvRecordOpen = findViewById(R.id.tvFeedOpen)
        etContent = findViewById(R.id.etFeedContent)
        rovTag = findViewById(R.id.rovTag)
        rovPicture = findViewById(R.id.rovPicture)
        rovVideo = findViewById(R.id.rovVideoLink)
        tvSave = findViewById(R.id.tvFeedSave)

        flBack?.setOnClickListener(this)
        clRecordOpen?.setOnClickListener(this)
        tvSave?.setOnClickListener(this)

        setWriterInfoToUI()
        recordOptionViewInit()

        if(feedData != null)
            setOldData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GALLERY_IMAGE -> {
                    sendAppEvent("클래스게시물작성_이미지추가")
                    var imageList = data?.getStringArrayListExtra(getString(R.string.intent_key_name_images))

                    for(idx in 0..imageList!!.lastIndex){
                        fileList.put(idx, imageList[idx])
                        var option = BitmapFactory.Options()
                        option.inSampleSize = 12
                        var bitmap = BitmapFactory.decodeFile(imageList!![idx], option)

                        var selectedImage = ImageView(this)
                        var params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.attache_image_size).toInt(),
                                resources.getDimension(R.dimen.attache_image_size).toInt())
                        params.leftMargin = resources.getDimension(R.dimen.margin_between_attched_image).toInt()
                        selectedImage.layoutParams = params
                        selectedImage.scaleType = ImageView.ScaleType.CENTER_CROP
                        Glide.with(this).load(bitmap).apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density*2).toInt(), 0)))
                                .into(selectedImage)
                        selectedImage.setTag(R.id.record_added_iv, idx)
                        rovPicture?.addContent(selectedImage)
                    }
                }

                ADDRESS_ON_MAP -> {
                    sendAppEvent("클래스게시물작성_주소추가")
                    if(rovLocation?.llContainer!!.childCount > 0)
                        rovLocation?.llContainer!!.removeAllViews()

                    var tvAddress = TextView(this)
                    tvAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
                    tvAddress.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
                    tvAddress.text = data!!.getStringExtra(getString(R.string.intent_key_name_address))
                    rovLocation?.addContent(tvAddress)
                }
            }
        }
    }

    private fun setWriterInfoToUI(){
        if(writerInfo!!.profile_img.equals(getString(R.string.default_text))){
            Glide.with(this).load(R.drawable.default_profile)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivWriterImg!!)
        }else{
            Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, writerInfo!!.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivWriterImg!!)
        }

        tvWriterName?.text = writerInfo!!.nickname
    }

    private fun recordOptionViewInit(){
        rovTag?.setType(getString(R.string.record_option_tag))
        rovTag?.setTextInputEnable()
        rovTag?.setOnClickListener(View.OnClickListener {
            sendAppEvent("클래스게시물작성_태그영역클릭")
            rovTag?.setFocus()
        })
        rovTag?.setValue("#${ClassDetailAcitivity.detailData!!.classroom!!.title}")

        rovPicture?.setType(getString(R.string.record_option_picture))
        rovPicture?.setCallback(this)

        rovPicture?.setOnClickListener(View.OnClickListener {
            sendAppEvent("클래스게시물작성_이미지영역클릭")
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent(this, ImagePickActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_limit_count), 10)
                startActivityForResult(intent, GALLERY_IMAGE)
            }else{
                Toast.makeText(this, getString(R.string.permission_external_storage_denied), Toast.LENGTH_SHORT).show()
            }
        })

        if(isNotice == "N") {
            rovVideo?.setType(getString(R.string.record_option_video))
            rovVideo?.setTextInputEnable()
            rovVideo?.setOnClickListener(View.OnClickListener {
                rovVideo?.setFocus()
            })
        }else{
            rovVideo?.visibility = View.GONE
        }
    }

    private fun checkBoardData(){
        if(etContent?.text.toString().equals("")){
            Toast.makeText(this, getString(R.string.confirm_content), Toast.LENGTH_SHORT).show()
            etContent?.requestFocus()
            return
        }

        if(rovVideo?.getContextText() != ""){
            if(rovVideo?.getContextText()!!.startsWith("https://www.youtube.com/watch?") || rovVideo?.getContextText()!!.startsWith("https://youtu.be")) {
                registerData.video = rovVideo!!.getContextText()
            }else{
                Toast.makeText(this, getString(R.string.confirm_video_link), Toast.LENGTH_SHORT).show()
                return
            }
        }

        registerData.is_notice = isNotice
        registerData.content = etContent!!.text.toString()
        registerData.status = curOpen
        registerData.tags = makeTagArray(rovTag!!.getContextText())

        if(fileList.size > 0){
            var keys = fileList.keys
            var uploadList = ArrayList<String>()
            for (key in keys){
                uploadList.add(fileList.getValue(key))
            }
            var uploader = S3Uploader(this, uploadList, this)
            uploader.upload()
            return
        }else{
            if(feedData == null)
                registerBoard()
            else
                updateBoard()
        }
    }

    private fun makeTagArray(origin: String): Array<String>?{
        if(origin.equals(""))
            return null

        var tempList = origin.split("#")
        var tempConfirmedList = ArrayList<String>()

        for(content in tempList){
            if(content.equals(""))
                continue

            var match = Pattern.matches("(^[\\s\\'\\\"\\\\]{1,15}$)", content)

            if(!match){
                tempConfirmedList.add(content)
            }else{
                Toast.makeText(this, content + getString(R.string.unable_tag), Toast.LENGTH_SHORT).show()
            }
        }

        if(tempConfirmedList.size == 0)
            return null

        return Array<String>(tempList.size, {i -> tempList.get(i)})
    }

    private fun registerBoard(){
        var apiService = ApiManager.getInstance().apiService
        apiService.registerAnnounce(classIdx, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        var intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }else{
                        Log.e("REGISTER_BOARD", result.status)
                        Toast.makeText(this, getString(R.string.fail_to_register_board), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("REGISTER_BOARD", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_register_board), Toast.LENGTH_SHORT).show()
                })
    }

    private fun updateBoard(){
        if(deleteList.size > 0)
            registerData.deleted = Array<Int>(deleteList.size, {i -> deleteList[i]})
        var apiService = ApiManager.getInstance().apiService
        apiService.updateAnnounce(classIdx, feedData!!.idx, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        var intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }else{
                        Log.e("REGISTER_BOARD", result.status)
                        Toast.makeText(this, getString(R.string.fail_to_register_board), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("REGISTER_BOARD", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_register_board), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getOpenTypeDialog() {
        var open = Dialog(this)
        open.window.requestFeature(Window.FEATURE_NO_TITLE)
        open.setContentView(R.layout.dialog_record_open_type)
        open.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        open.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvOpen = open.findViewById<TextView>(R.id.tvRecordOpenTypeOpen)
        var tvMember = open.findViewById<TextView>(R.id.tvRecordOpenTypePrivate)
        tvMember.text = getString(R.string.record_open_type_member)

        tvOpen.setOnClickListener({
            sendAppEvent("클래스게시물작성_공개설정_전체공개")
            curOpen = openKind[0]
            tvRecordOpen?.text = tvOpen.text
            open.dismiss()
        })

        tvMember.setOnClickListener({
            sendAppEvent("클래스게시물작성_공개설정_멤버공개")
            curOpen = openKind[1]
            tvRecordOpen?.text = tvMember.text
            open.dismiss()
        })

        open.show()
    }

    private fun setOldData(){
        etContent?.text = SpannableStringBuilder(feedData!!.content)
        curOpen = feedData!!.status

        if(curOpen.equals(openKind[0]))
            tvRecordOpen?.text = getString(R.string.record_open_type_open)
        else
            tvRecordOpen?.text = getString(R.string.record_open_type_member)

        if(feedData!!.files!!.isNotEmpty()){
            for(data in feedData!!.files!!) {
                var selectedImage = ImageView(this)
                var params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.attache_image_size).toInt(),
                        resources.getDimension(R.dimen.attache_image_size).toInt())
                params.leftMargin = resources.getDimension(R.dimen.margin_between_attched_image).toInt()
                selectedImage.layoutParams = params
                selectedImage.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, data.uploaded_path))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density * 2).toInt(), 0)))
                        .into(selectedImage)
                selectedImage.setTag(R.id.record_added_iv, data.idx)
                rovPicture?.addContent(selectedImage)
            }
        }

        rovTag?.setValue(CommonUtils.convertTagsToString(feedData!!.tags!!))

        if(feedData!!.video != null){
            rovVideo?.setValue(feedData!!.video!!)
        }
    }
}