package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ThumbnailUtils
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
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
import vdream.vd.com.vdream.interfaces.AddressRequestCallback
import vdream.vd.com.vdream.interfaces.AnnounceImageChangeCallback
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.network.S3Uploader
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.component.*
import java.util.regex.Pattern

class WriteExperienceActivity: BaseActivity(), View.OnClickListener, UploadFinishCallback, AddressRequestCallback, AnnounceImageChangeCallback {
    override fun imageDeleted(idx: Int) {
        if(oldData != null) {
            var isOldImage = false
            for (data in oldData!!.files!!) {
                if (data.kind == "IMAGE" && data.idx == idx) {
                    deletedImage.add(idx)
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

    override fun requestAddress() {
        moveToAddressMap()
    }

    override fun uploadFinished(uploadPath: ArrayList<String>, filename: ArrayList<String>) {
        registerData.images = Array<UploadImageFormData>(uploadPath.size, {i -> UploadImageFormData(filename.get(i), uploadPath.get(i))})

        if(oldData == null)
            registerExperience()
        else
            updateExperience()
    }

    val GALLERY_IMAGE = 0
    val ADDRESS_ON_MAP = 10
    val openKind = arrayOf("PUBLIC", "MEMBER")

    var fileList = HashMap<Int, String>()
    var writerInfo: UserInfoData? = null
    var status = "PUBLIC"
    var latitude = 0.0
    var longitude = 0.0

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flExperienceBack -> {
                sendAppEvent("클래스체험학습작성_헤더_뒤로가기")
                finish()
            }
            R.id.tvExperienceSave -> {
                sendAppEvent("클래스체험학습작성_체험학습등록")
                checkExperienceData()
            }
            R.id.clExperienceOpen -> {
                sendAppEvent("클래스체험학습작성_공개설정")
                getOpenTypeDialog()
            }
        }
    }

    var flBack: FrameLayout? = null
    var ivWriterImg: ImageView? = null
    var tvWriterName: TextView? = null
    var clRecordOpen: ConstraintLayout? = null
    var tvRecordOpen: TextView? = null
    var tswSecure: TitledSwitch? = null
    var tetfCharge: TitledEditTextFlat? = null
    var tetfPhone: TitledEditTextFlat? = null

    var tetfTitle: TitledEditTextFlat? = null
    var teaSummary: TitledEditArea? = null
    var teaContent: TitledEditArea? = null
    var tetfMinPeople: TitledEditTextFlat? = null
    var tetfMaxPeople: TitledEditTextFlat? = null
    var tetfPrice: TitledEditTextFlat? = null

    var tliPlace: TitledLocationInputForm? = null
    var tdtDate: TitledDateTime? = null
    var tdtDeadline: TitledDateTime? = null
    var rovTag: RecordOptionView? = null
    var rovPicture: RecordOptionView? = null
    var tvSave: TextView? = null

    var classIdx = 0
    var registerData = ExperienceRegisterData()
    var oldData: FeedDetailData? = null
    var deletedImage = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writerInfo = MyInfoStore.myInfo
        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), 1)
        if(intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) != null)
            oldData = intent.getSerializableExtra(getString(R.string.intent_key_name_feeddata)) as FeedDetailData
        setContentView(R.layout.activity_write_experience)

        flBack = findViewById(R.id.flExperienceBack)
        ivWriterImg = findViewById(R.id.ivExperienceWriterImg)
        tvWriterName = findViewById(R.id.tvExperienceWriterName)
        clRecordOpen = findViewById(R.id.clExperienceOpen)
        tvRecordOpen = findViewById(R.id.tvExperienceOpen)
        tswSecure = findViewById(R.id.tswExperienceUseSecure)
        tetfCharge = findViewById(R.id.tetfExperienceCharge)
        tetfPhone = findViewById(R.id.tetfExperiencePhone)

        tetfTitle = findViewById(R.id.tetfExperienceTitle)
        teaSummary = findViewById(R.id.teaExperienceSummary)
        teaContent = findViewById(R.id.teaExperienceContent)
        tetfMinPeople = findViewById(R.id.tetfExperienceMinPeople)
        tetfMaxPeople = findViewById(R.id.tetfExperienceMaxPeople)
        tetfPrice = findViewById(R.id.tetfExperiencePrice)

        tliPlace = findViewById(R.id.tliExperiencePlace)
        tdtDate = findViewById(R.id.tdtExperienceDate)
        tdtDeadline = findViewById(R.id.tdtExperienceDeadline)

        rovTag = findViewById(R.id.rovTag)
        rovPicture = findViewById(R.id.rovPicture)
        tvSave = findViewById(R.id.tvExperienceSave)

        flBack?.setOnClickListener(this)
        clRecordOpen?.setOnClickListener(this)
        tvSave?.setOnClickListener(this)

        userInfoSetToUI()
        setCustomViewInit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GALLERY_IMAGE -> {
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
                        selectedImage.scaleType = ImageView.ScaleType.FIT_XY
                        Glide.with(this).load(bitmap).apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density*2).toInt(), 0)))
                                .into(selectedImage)
                        selectedImage.setTag(R.id.record_added_iv, idx)
                        rovPicture?.addContent(selectedImage)
                    }
                }

                ADDRESS_ON_MAP -> {
                    tliPlace!!.setBigAddress(data!!.getStringExtra(getString(R.string.intent_key_name_address)))
                    latitude = data!!.getDoubleExtra(getString(R.string.intent_key_name_lat), 0.0)
                    longitude = data!!.getDoubleExtra(getString(R.string.intent_key_name_lng), 0.0)
                }
            }
        }
    }

    private fun setCustomViewInit() {
        tswSecure?.setTitle(getString(R.string.use_secure_code))
        tetfCharge?.setImeOptionNext()
        tetfCharge?.setTitle(getString(R.string.experience_charge))
        tetfPhone?.setTitle(getString(R.string.experience_phone))
        tetfPhone?.setNumericInput()

        tetfTitle?.setTitle(getString(R.string.title))
        tetfTitle?.setContentHint(getString(R.string.experience_title_hint))
        teaSummary?.setTitle(getString(R.string.experience_summary))
        teaSummary?.setMaxContentLine(3)
        teaContent?.setTitle(getString(R.string.experience_content))
        teaContent?.setContentHint(getString(R.string.experience_content_hint))
        teaContent?.setMaxContentLine(8)
        tetfMinPeople?.setTitle(getString(R.string.experience_min_people))
        tetfMaxPeople?.setTitle(getString(R.string.experience_max_people))
        tetfMinPeople?.setNumericInput()
        tetfMaxPeople?.setNumericInput()
        tetfPrice?.setTitle(getString(R.string.experience_price))
        tetfPrice?.setNumericInput()

        tliPlace?.setTitle(getString(R.string.experience_location))
        tliPlace?.setAddressRequestCallback(this)
        tdtDate?.setTitle(getString(R.string.time))
        tdtDeadline?.setTitle(getString(R.string.experience_deadline))

        rovTag?.setType(getString(R.string.record_option_tag))
        rovTag?.setTextInputEnable()
        rovTag?.setValue("#${ClassDetailAcitivity.detailData!!.classroom!!.title}")

        rovPicture?.setType(getString(R.string.record_option_picture))
        rovPicture?.setCallback(this)

        rovPicture?.setOnClickListener(View.OnClickListener {
            sendAppEvent("클래스체험학습작성_이미지영역클릭")
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent(this, ImagePickActivity::class.java)
                intent.putExtra(getString(R.string.intent_key_name_limit_count), 10)
                startActivityForResult(intent, GALLERY_IMAGE)
            }else{
                Toast.makeText(this, getString(R.string.permission_external_storage_denied), Toast.LENGTH_SHORT).show()
            }
        })

        if(oldData != null)
            setOldDataToUi()
    }

    private fun setOldDataToUi(){
        if(oldData!!.status == getString(R.string.open_type_member))
            tvRecordOpen?.text = getString(R.string.record_open_type_member)
        tswSecure?.setSwitchChecked(oldData!!.is_secure == "Y")
        tetfCharge?.setValue(oldData!!.name)
        tetfPhone?.setValue(oldData!!.phone)
        tetfTitle?.setValue(oldData!!.title)
        teaSummary?.setValue(oldData!!.summary)
        teaContent?.setValue(oldData!!.content)
        tetfMinPeople?.setValue(oldData!!.min_people.toString())
        tetfMaxPeople?.setValue(oldData!!.max_people.toString())
        tetfPrice?.setValue(oldData!!.price.toString())
        tliPlace?.setBigAddress(oldData!!.address_1)
        tliPlace?.setDetailAddress(oldData!!.address_2)
        var dateTime = oldData!!.opened_at!!.split(" ")
        tdtDate?.setDate(dateTime[0])
        tdtDate?.setTime(dateTime[1])
        var deadline = oldData!!.expired_at!!.split(" ")
        tdtDeadline?.setDate(deadline[0])
        tdtDeadline?.setTime(deadline[1])

        rovTag?.setValue(CommonUtils.convertTagsToString(oldData!!.tags))

        for(data in oldData!!.files!!){
            if(data.kind == "IMAGE") {
                var oldImage = ImageView(this)
                var params = LinearLayout.LayoutParams(resources.getDimension(R.dimen.attache_image_size).toInt(),
                        resources.getDimension(R.dimen.attache_image_size).toInt())
                params.leftMargin = resources.getDimension(R.dimen.margin_between_attched_image).toInt()
                oldImage.layoutParams = params
                oldImage.scaleType = ImageView.ScaleType.FIT_XY
                Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, data.uploaded_path))
                        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation((resources.displayMetrics.density * 2).toInt(), 0)))
                        .into(oldImage)
                oldImage.setTag(R.id.record_added_iv, data.idx)
                rovPicture?.addContent(oldImage)
            }
        }

        latitude = oldData!!.lat.toDouble()
        longitude = oldData!!.lng.toDouble()
    }

    private fun userInfoSetToUI(){
        if(MyInfoStore.myInfo!!.profile_img.equals(getString(R.string.default_text))){
            Glide.with(this).load(R.drawable.default_profile)
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivWriterImg!!)
        }else{
            Glide.with(this).load(CommonUtils.getThumbnailLinkPath(this, MyInfoStore.myInfo!!.profile_img))
                    .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                    .into(ivWriterImg!!)
        }

        tvWriterName?.text = MyInfoStore.myInfo!!.nickname
    }

    private fun checkExperienceData(){
        if(tetfCharge!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.input_charge), Toast.LENGTH_SHORT).show()
            return
        }
        if(tetfPhone!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.input_phone), Toast.LENGTH_SHORT).show()
            return
        }
        if(tetfMinPeople!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.input_min_people), Toast.LENGTH_SHORT).show()
            return
        }
        if(tetfMaxPeople!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.input_max_people), Toast.LENGTH_SHORT).show()
            return
        }
        if(tliPlace!!.bigAddress == ""|| tliPlace!!.getDetailAddress() == ""){
            Toast.makeText(this, getString(R.string.confirm_location_info), Toast.LENGTH_SHORT).show()
            return
        }
        if(tetfTitle!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.confirm_title), Toast.LENGTH_SHORT).show()
            return
        }
        if(teaSummary?.getValue() == ""){
            Toast.makeText(this, getString(R.string.confirm_summary), Toast.LENGTH_SHORT).show()
            return
        }
        if(teaContent!!.getValue() == ""){
            Toast.makeText(this, getString(R.string.confirm_content), Toast.LENGTH_SHORT).show()
            return
        }
        if(fileList.size == 0 && oldData == null){
            Toast.makeText(this, getString(R.string.confirm_images), Toast.LENGTH_SHORT).show()
            return
        }

        if(tswSecure!!.isSwitchOn){
            registerData.secure_code = createSecureCode()
            registerData.is_secure = "Y"
        }else{
            registerData.is_secure = "N"
        }

        registerData.status = status
        registerData.name = tetfCharge!!.getValue()
        registerData.phone = tetfPhone!!.getValue()
        registerData.address_1 = tliPlace!!.bigAddress
        registerData.address_2 = tliPlace!!.getDetailAddress()
        registerData.lat = latitude
        registerData.lng = longitude
        registerData.min_people = tetfMinPeople!!.getValue().toInt()
        registerData.max_people = tetfMaxPeople!!.getValue().toInt()
        registerData.opened_at = "${tdtDate!!.getDate()} ${tdtDate!!.getTime()}"
        registerData.expired_at = "${tdtDeadline!!.getDate()} ${tdtDeadline!!.getTime()}"
        if(tetfPrice!!.getValue().equals(""))
            registerData.price = null
        else
            registerData.price = tetfPrice!!.getValue().toInt()
        registerData.title = tetfTitle!!.getValue()
        registerData.summary = teaSummary!!.getValue()
        registerData.content = teaContent!!.getValue()
        registerData.tags = makeTagArray(rovTag!!.getContextText())

        if(fileList.size > 0){
            var keys = fileList.keys.toCollection(ArrayList())
            var uploadList = ArrayList<String>()
            for(key in keys){
                uploadList.add(fileList.getValue(key))
            }
            var uploader = S3Uploader(this, uploadList, this)
            uploader.upload()
            return
        }else{
            if(oldData == null)
                registerExperience()
            else
                updateExperience()
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

    private fun registerExperience(){
        var apiService = ApiManager.getInstance().apiService
        apiService.registerExperience(registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, { error ->
                    Log.e("REGISTER_BOARD", error.toString())
                })
    }

    private fun updateExperience(){
        if(deletedImage.size > 0)
            registerData.deleted = Array<Int>(deletedImage.size, {i -> deletedImage[i]})
        var apiService = ApiManager.getInstance().apiService
        apiService.updateExperience(oldData!!.idx, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    var intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }, { error ->
                    Log.e("REGISTER_BOARD", error.toString())
                })
    }

    private fun moveToAddressMap(){
        var intent = Intent(this, AddAddressActivity::class.java)
        startActivityForResult(intent, ADDRESS_ON_MAP)
    }

    private fun createSecureCode(): String {
        var charArray = arrayOf('a' , 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
        var num = (Math.random() * 999998).toInt()

        if(num < 100000)
            num += 100000

        var code = num.toString()

        for(c in code){
            var isAlpahbet = (Math.random() * 51).toInt()

            if(isAlpahbet < 26)
                code.replace(c, charArray[isAlpahbet])
        }

        return code
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
            sendAppEvent("클래스체험학습작성_공개설정_전체공개")
            status = openKind[0]
            tvRecordOpen?.text = tvOpen.text
            open.dismiss()
        })

        tvMember.setOnClickListener({
            sendAppEvent("클래스체험학습작성_공개설정_멤버공개")
            status = openKind[1]
            tvRecordOpen?.text = tvMember.text
            open.dismiss()
        })

        open.show()
    }
}