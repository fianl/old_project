package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.InputType
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
import vdream.vd.com.vdream.data.*
import vdream.vd.com.vdream.interfaces.AnnounceImageChangeCallback
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.network.S3Uploader
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.component.RecordOptionView
import vdream.vd.com.vdream.view.fragment.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RecordActivity: BaseActivity(), View.OnClickListener, UploadFinishCallback, AnnounceImageChangeCallback{
    override fun imageDeleted(idx: Int) {
        if(oldData != null){
            var isOldImage = false
            for(data in oldData!!.files!!){
                if(data.idx == idx) {
                    deleteList.add(idx)
                    isOldImage = true
                    break
                }
            }

            if(!isOldImage)
                fileList.remove(idx)

        }else if(oldDiary!= null){
            var isOldImage = false
            for(data in oldDiary!!.files!!){
                if(data.idx == idx) {
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
        registerData!!.images = Array<UploadImageFormData>(uploadPath.size, {i -> UploadImageFormData(filename.get(i), uploadPath.get(i))})

        if(oldData != null || oldDiary != null)
            updateRecord()
        else
            registerRecord()
    }

    val GALLERY_IMAGE = 0
    val ADDRESS_ON_MAP = 10
    val REQ_CODE_SPEECH_INPUT = 11

    val openType = arrayOf("PUBLIC", "PRIVATE")
    var curOpen: String? = openType[0]
    var recordType: String? = null
    var registerData: RecordRegisterData? = null
    var fileList = HashMap<Int, String>()
    var writerInfo: UserInfoData? = null
    var fragment: BaseRecordFragment? = null
    var oldData: UserRecordData? = null
    var oldDiary: UserDiaryData? = null
    var deleteList = ArrayList<Int>()

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flRecordBack -> {
                sendAppEvent("생기부작성_헤더_뒤로가기")
                onBackPressed()
            }
            R.id.clRecordOpen -> {
                sendAppEvent("생기부작성_공개설정")
                getOpenTypeDialog()
            }
            R.id.tvRecordSave -> {
                sendAppEvent("생기부작성_저장하기")
                getRegisterData()
            }
            R.id.ivRecordVoiceToText -> {
                sendAppEvent("생기부작성_음성입력")
                askSpeechInput()
            }
        }
    }

    var flBack: FrameLayout? = null
    var tvTitle: TextView? = null
    var ivWriterImg: ImageView? = null
    var tvWriterName: TextView? = null
    var clRecordOpen: ConstraintLayout? = null
    var tvRecordOpen: TextView? = null
    var ivVoice: ImageView? = null
    var rovTag: RecordOptionView? = null
    var rovPicture: RecordOptionView? = null
    var rovLocation: RecordOptionView? = null
    var tvSave: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writerInfo = MyInfoStore.myInfo
        recordType = intent.getStringExtra(getString(R.string.intent_key_name_record_kind))
        if(intent.getSerializableExtra(getString(R.string.intent_key_name_record_data)) != null){
            if(recordType == "")
                oldDiary = intent.getSerializableExtra(getString(R.string.intent_key_name_record_data)) as UserDiaryData
            else
                oldData = intent.getSerializableExtra(getString(R.string.intent_key_name_record_data)) as UserRecordData
        }
        setContentView(R.layout.activity_record)

        flBack = findViewById(R.id.flRecordBack)
        tvTitle = findViewById(R.id.tvRecordTitle)
        ivWriterImg = findViewById(R.id.ivRecordWriterImg)
        tvWriterName = findViewById(R.id.tvRecordWriterName)
        clRecordOpen = findViewById(R.id.clRecordOpen)
        tvRecordOpen = findViewById(R.id.tvRecordOpen)
        ivVoice = findViewById(R.id.ivRecordVoiceToText)
        rovTag = findViewById(R.id.rovTag)
        rovPicture = findViewById(R.id.rovPicture)
        rovLocation = findViewById(R.id.rovLocation)
        tvSave = findViewById(R.id.tvRecordSave)

        flBack?.setOnClickListener(this)
        clRecordOpen?.setOnClickListener(this)
        ivVoice?.setOnClickListener(this)
        tvSave?.setOnClickListener(this)

        setWriterData()
        recordOptionViewInit()
        setRecordFragment()

        if(oldData != null || oldDiary != null)
            setOldData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GALLERY_IMAGE -> {
                    sendAppEvent("생기부작성_이미지추가")
                    var tempList = data?.getStringArrayListExtra(getString(R.string.intent_key_name_images))!!

                    for(idx in 0..tempList.lastIndex){
                        fileList.put(idx, tempList[idx])
                        var option = BitmapFactory.Options()
                        option.inSampleSize = 12
                        var bitmap =BitmapFactory.decodeFile(tempList!![idx], option)

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
                    sendAppEvent("생기부작성_주소추가")
                    if(rovLocation?.llContainer!!.childCount > 0)
                        rovLocation?.llContainer!!.removeAllViews()

                    var tvAddress = TextView(this)
                    tvAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
                    tvAddress.setTextColor(ContextCompat.getColor(this, R.color.sky_blue))
                    tvAddress.text = data!!.getStringExtra(getString(R.string.intent_key_name_address))
                    rovLocation?.addContent(tvAddress)
                }

                REQ_CODE_SPEECH_INPUT -> {
                    sendAppEvent("생기부작성_음성입력_텍스트")
                    var result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    var voiceText = result!![0]
                    var focusedView = currentFocus
                    if(focusedView is EditText) {
                        if(focusedView.inputType == InputType.TYPE_CLASS_NUMBER) {
                            var numText = voiceText.toIntOrNull()

                            if (numText == null)
                                Toast.makeText(this, "숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
                            else
                                focusedView.text = SpannableStringBuilder(numText.toString())
                        } else
                            focusedView.text = SpannableStringBuilder(voiceText)
                    }
                }
            }
        }
    }

    private fun setWriterData(){
        tvWriterName?.text = writerInfo!!.nickname

        if(writerInfo!!.profile_img.equals(getString(R.string.default_text)))
            ivWriterImg?.setImageResource(R.drawable.default_profile)
        else{
            Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, writerInfo!!.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivWriterImg!!)
        }
    }

    private fun setRecordFragment(){
        var title = ""
        when(recordType) {
            getString(R.string.user_record_type_class) -> {
                fragment = RecordClassFragment()
                title = getString(R.string.user_record_kor_type_class)
            }
            getString(R.string.user_record_type_club) -> {
                fragment = RecordClubFragment()
                title = getString(R.string.user_record_kor_type_club)
            }
            getString(R.string.user_record_type_career) -> {
                fragment = RecordCareerFragment()
                title = getString(R.string.user_record_kor_type_career)
            }
            getString(R.string.user_record_type_contest) -> {
                fragment = RecordContestFragment()
                title = getString(R.string.user_record_kor_type_contest)
            }
            getString(R.string.user_record_type_volunteer) -> {
                fragment = RecordVolunteerFragment()
                title = getString(R.string.user_record_kor_type_volunteer)
            }
            getString(R.string.user_record_type_behavior) -> {
                fragment = RecordBehaviorFragment()
                title = getString(R.string.user_record_kor_type_behavior)
            }
            getString(R.string.user_record_type_reading) -> {
                fragment = RecordReadingFragment()
                title = getString(R.string.user_record_kor_type_reading)
            }
            "" -> {
                fragment = RecordDiaryFragment()
                title = getString(R.string.user_record_kor_type_diary)
            }
        }

        tvTitle?.text = title
        var tr = supportFragmentManager.beginTransaction()
        tr?.replace(R.id.flRecordContents, fragment)
        tr?.commit()

        if(oldData != null){
            fragment!!.setOldData(oldData!!)
        }else if(oldDiary != null){
            fragment!!.setDiaryData(oldDiary!!)
        }
    }

    private fun recordOptionViewInit(){
        rovTag?.setType(getString(R.string.record_option_tag))
        rovTag?.setTextInputEnable()
        rovTag?.setOnClickListener(View.OnClickListener {
            sendAppEvent("생기부작성_태그영역클릭")
            rovTag?.setFocus()
        })
        rovPicture?.setType(getString(R.string.record_option_picture))
        rovLocation?.setType(getString(R.string.record_option_location))

        rovPicture?.setCallback(this)
        rovPicture?.setOnClickListener(View.OnClickListener {
            sendAppEvent("생기부작성_이미지영역클릭")
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent(this, ImagePickActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_limit_count), 10)
                startActivityForResult(intent, GALLERY_IMAGE)
            }else{
                Toast.makeText(this, getString(R.string.permission_external_storage_denied), Toast.LENGTH_SHORT).show()
            }
        })

        rovLocation?.setOnClickListener(View.OnClickListener {
            sendAppEvent("생기부작성_주소영역클릭")
            var intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, ADDRESS_ON_MAP)
        })
    }

    private fun registerRecord(){
        var apiService = ApiManager.getInstance().apiService
        apiService.registerRecord(registerData!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    Toast.makeText(this, getString(R.string.register_diary_success), Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }, { error ->
                    Log.e("REGISTER_RECORD", error.toString())
                })
    }

    private fun updateRecord(){
        var idx = -1
        if(oldData != null)
            idx = oldData!!.idx
        else
            idx = oldDiary!!.idx

        if(deleteList.size > 0)
            registerData!!.deleted = Array<Int>(deleteList.size, {i -> deleteList[i]})

        var apiService = ApiManager.getInstance().apiService
        apiService.updateRecord(idx, registerData!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    Toast.makeText(this, getString(R.string.update_diary_success), Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }, { error ->
                    Log.e("UPDATE_RECORD", error.toString())
                })
    }

    private fun getOpenTypeDialog() {
        var open = Dialog(this)
        open.window.requestFeature(Window.FEATURE_NO_TITLE)
        open.setContentView(R.layout.dialog_record_open_type)
        open.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), ConstraintLayout.LayoutParams.WRAP_CONTENT)
        open.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var tvOpen = open.findViewById<TextView>(R.id.tvRecordOpenTypeOpen)
        var tvPrivate = open.findViewById<TextView>(R.id.tvRecordOpenTypePrivate)

        tvOpen.setOnClickListener({
            curOpen = openType[0]
            tvRecordOpen?.text = tvOpen.text
            open.dismiss()
        })

        tvPrivate.setOnClickListener({
            curOpen = openType[1]
            tvRecordOpen?.text = tvPrivate.text
            open.dismiss()
        })

        open.show()
    }

    private fun getRegisterData(){
        var msg = fragment!!.checkRegisterData()

        if(msg != "") {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            return
        } else {
            registerData = fragment!!.getRecordData()
            registerData!!.kind = recordType
            if(recordType == "")
                registerData!!.kind = null
            registerData!!.status = curOpen!!
        }

        if(fileList.size > 0){
            var keys = fileList.keys.toCollection(ArrayList())

            var uploadList = ArrayList<String>()
            for(key in keys){
                uploadList.add(fileList.getValue(key))
            }

            var uploader = S3Uploader(this, uploadList, this)
            uploader.upload()
        }else{
            if(oldData != null || oldDiary != null)
                updateRecord()
            else
                registerRecord()
        }
    }

    private fun setOldData(){
        if(oldData != null && oldData!!.files != null) {
            for(data in oldData!!.files!!){
                var selectedImage = ImageView(this)
                var params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.attache_image_size).toInt(),
                        resources.getDimension(R.dimen.attache_image_size).toInt())
                params.leftMargin = resources.getDimension(R.dimen.margin_between_attched_image).toInt()
                selectedImage.layoutParams = params
                selectedImage.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, data.uploaded_path))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density*2).toInt(), 0)))
                        .into(selectedImage)
                selectedImage.tag = data.idx
                rovPicture?.addContent(selectedImage)
            }
        }else if(oldDiary != null && oldDiary!!.files != null){
            for(data in oldDiary!!.files!!){
                var selectedImage = ImageView(this)
                var params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.attache_image_size).toInt(),
                        resources.getDimension(R.dimen.attache_image_size).toInt())
                params.leftMargin = resources.getDimension(R.dimen.margin_between_attched_image).toInt()
                selectedImage.layoutParams = params
                selectedImage.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, data.uploaded_path))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density*2).toInt(), 0)))
                        .into(selectedImage)
                selectedImage.tag = data.idx
                rovPicture?.addContent(selectedImage)
            }
        }

        var status = ""

        if(oldData != null){
            status = oldData!!.status
        }else{
            status = oldDiary!!.status
        }

        if(status == openType[0]){
            tvRecordOpen?.text = getString(R.string.record_open_type_open)
        }else{
            tvRecordOpen?.text = getString(R.string.record_open_type_private)
        }
    }

    private fun askSpeechInput(){
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.please_tell_content))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (e: ActivityNotFoundException) {
            Log.e("SPEECH", e.toString())
        }
    }
}