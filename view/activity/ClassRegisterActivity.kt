package vdream.vd.com.vdream.view.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import retrofit2.HttpException
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.CategoryData
import vdream.vd.com.vdream.data.ClassroomBaseData
import vdream.vd.com.vdream.data.RegisterClassData
import vdream.vd.com.vdream.interfaces.SecondCategorySelectedCallback
import vdream.vd.com.vdream.interfaces.UploadFinishCallback
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.network.S3Uploader
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.utils.ImageCacheUtils
import vdream.vd.com.vdream.view.component.CategoryGroupView
import vdream.vd.com.vdream.view.component.ComplexSelectView
import vdream.vd.com.vdream.view.component.TitledEditText
import vdream.vd.com.vdream.view.component.TitledSwitch
import java.io.File
import java.net.URLEncoder
import java.util.regex.Pattern

class ClassRegisterActivity: BaseActivity(), View.OnClickListener, UploadFinishCallback, SecondCategorySelectedCallback {
    override fun onSelected(first: String, second: CategoryData) {
        sendAppEvent("클래스등록_2차카테고리_선택")
        selectedCateogry = second.idx
        csvCateogry!!.setValue("$first / ${second.title}")
        category?.dismiss()
    }

    override fun uploadFinished(uploadPath: ArrayList<String>, filename: ArrayList<String>) {
        if(uploadPath.size == 2) {
            classInfo.profile_img = uploadPath.get(0)
            classInfo.background_img = uploadPath.get(1)
        }else{
            if(classInfo.profile_img.equals(getString(R.string.default_text)))
                classInfo.background_img = uploadPath.get(0)
            else
                classInfo.profile_img = uploadPath.get(0)
        }

        if(classIdx == -1)
            registerClass()
        else
            updateClass()
    }

    val GALLERY_IMAGE_PROFILE = 0
    val GALLERY_IMAGE_BG = 1
    val ADDRESS_ON_MAP = 10

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flBackContainer -> {
                sendAppEvent("클래스등록_헤더_백버튼")
                finish()
            }
            R.id.tvRegisterChnageBg -> {
                sendAppEvent("클래스등록_배경이미지_변경버튼")
                moveToGallery(GALLERY_IMAGE_BG)
            }
            R.id.tvRegisterChnageProfile -> {
                sendAppEvent("클래스등록_프로필이미지_변경버튼")
                moveToGallery(GALLERY_IMAGE_PROFILE)
            }
            R.id.tvRegisterClass -> {
                sendAppEvent("클래스등록_등록버튼")
                checkClassData()
            }
        }
    }

    var clProfileBg: ConstraintLayout? = null
    var flBack: FrameLayout? = null
    var ivProfile: ImageView? = null
    var tvHashTag: TextView? = null
    var tetClassTitle: TitledEditText? = null
    var csvCateogry: ComplexSelectView? = null
    var tetHashTag: TitledEditText? = null
    var tvChagneProfile: TextView? = null
    var tvChangeBg: TextView? = null
    var csvLocation: ComplexSelectView? = null
    var tswClassType: TitledSwitch? = null
    var tswNotification: TitledSwitch? = null
    var tvRegister: TextView? = null
    var category: Dialog? = null

    var firstCategories = ArrayList<CategoryData>()
    var secondCategories = ArrayList<ArrayList<CategoryData>>()
    var titleDuplicateCheck = false
    var classInfo = RegisterClassData()
    var profileImgPath = ""
    var bgImgPath = ""
    var selectedCateogry = -1
    var latitude = ""
    var longitude = ""
    var classroomData: ClassroomBaseData? = null
    var classIdx = -1

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        classIdx = intent.getIntExtra(getString(R.string.intent_key_name_index), -1)

        if(classIdx != -1)
            classroomData = intent.getSerializableExtra(getString(R.string.intent_key_name_classroom)) as ClassroomBaseData

        setContentView(R.layout.activity_class_register)

        clProfileBg = findViewById(R.id.clProfileContainer)
        flBack = findViewById(R.id.flBackContainer)
        ivProfile = findViewById(R.id.ivRegisterProfile)
        tvHashTag = findViewById(R.id.tvRegisterTag)
        tetClassTitle = findViewById(R.id.tetClassTitle)
        csvCateogry = findViewById(R.id.csvCategory)
        tetHashTag = findViewById(R.id.tetClassHashTag)
        tvChagneProfile = findViewById(R.id.tvRegisterChnageProfile)
        tvChangeBg = findViewById(R.id.tvRegisterChnageBg)
        csvLocation = findViewById(R.id.csvLocation)
        tswClassType = findViewById(R.id.tswClassType)
        tswNotification = findViewById(R.id.tswNotification)
        tvRegister = findViewById(R.id.tvRegisterClass)

        getFirstCategories()
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                GALLERY_IMAGE_PROFILE -> {
                    sendAppEvent("클래스등록_프로필이미지_변경")
                    profileImgPath = data?.getStringArrayListExtra(getString(R.string.intent_key_name_images))!!.get(0)
                    Glide.with(this).load(File(profileImgPath)).apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                            .into(ivProfile!!)
                }

                GALLERY_IMAGE_BG -> {
                    sendAppEvent("클래스등록_배경이미지_변경")
                    bgImgPath = data?.getStringArrayListExtra(getString(R.string.intent_key_name_images))!!.get(0)
                    var bitmapOption = BitmapFactory.Options()
                    bitmapOption.inSampleSize = 2
                    var bitmap = BitmapFactory.decodeFile(bgImgPath, bitmapOption)
                    clProfileBg?.background = BitmapDrawable(resources, bitmap)
                }

                ADDRESS_ON_MAP -> {
                    sendAppEvent("클래스등록_주소_추")
                    csvLocation!!.setValue(data!!.getStringExtra(getString(R.string.intent_key_name_address)))
                    csvLocation!!.showSecondeInputView()
                    latitude = data!!.getDoubleExtra(getString(R.string.intent_key_name_lat), 0.0).toString()
                    longitude = data!!.getDoubleExtra(getString(R.string.intent_key_name_lng), 0.0).toString()
                }
            }
        }
    }

    private fun init(){
        tetClassTitle?.setTitle(getString(R.string.class_title))
        tetClassTitle?.setTitleColor(ContextCompat.getColor(this, R.color.text_gray))
        tetClassTitle?.setExplain(getString(R.string.is_enable))
        tetClassTitle?.setExplainColor(ContextCompat.getColor(applicationContext, R.color.mainColor))
        tetClassTitle?.setOptionText(getString(R.string.duplicate_confirm))
        tetClassTitle?.showOptionalButton()
        tetClassTitle?.setOptionButtonClickListener(View.OnClickListener {
            sendAppEvent("클래스등록_클래스명중복확인버튼")
            classTitleDuplicatedConfirm()
        })

        csvCateogry?.setTitle(getString(R.string.category))
        csvCateogry?.setTitleColor(ContextCompat.getColor(this, R.color.text_gray))
        csvCateogry?.setSelectorTitle(getString(R.string.select_category))
        csvCateogry?.setSelectorClickListener(View.OnClickListener { view ->
            sendAppEvent("클래스등록_클래스카테고리_팝업생성버튼")
            createCategoryDialog()
        })

        tetHashTag?.setTitle(getString(R.string.hash_tag))
        tetHashTag?.setTitleColor(ContextCompat.getColor(this, R.color.text_gray))
        tetHashTag?.setExplain(getString(R.string.hash_tag_example))
        tetHashTag?.setTextChangeListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvHashTag?.text = s.toString()
            }
        })

        csvLocation?.setTitle(getString(R.string.location))
        csvLocation?.setTitleColor(ContextCompat.getColor(this, R.color.text_gray))
        csvLocation?.setSelectorTitle(getString(R.string.set_location))
        csvLocation?.setSelectorMark(R.drawable.attach_location)
        csvLocation?.showSelectorOptionSwitch()
        csvLocation?.setSelectorClickListener(View.OnClickListener { view ->
            sendAppEvent("클래스등록_주소추가영역클릭")
            if(csvLocation!!.isOptionSwitchOn)
                moveToAddressMap()
        })

        tswClassType?.setSwitchSize(40)
        tswClassType?.setTitleColor(ContextCompat.getColor(this, R.color.text_gray))
        tswClassType?.setTitle(getString(R.string.class_type))
        tswClassType?.setSwitchTextAndBackground(getString(R.string.class_type_open), getString(R.string.class_type_close))

        tswNotification?.setTitle(getString(R.string.board_type_notification))

        flBack?.setOnClickListener(this)
        tvChagneProfile?.setOnClickListener(this)
        tvChangeBg?.setOnClickListener(this)
        tvRegister?.setOnClickListener(this)

        Glide.with(this).load(R.drawable.default_bg)
                .into(object : ViewTarget<ConstraintLayout, Drawable>(clProfileBg!!) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        clProfileBg?.background = resource
                    }
                })

        if(classroomData != null){
            tvRegister?.text = getString(R.string.do_class_update)
            if(!classroomData!!.background_img.equals(getString(R.string.default_text))) {
                var bitmap = ImageCacheUtils.getBitmap(classroomData!!.background_img)

                if(bitmap == null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(CommonUtils.getBigImageLinkPath(this, classroomData!!.background_img))
                            .into(object : ViewTarget<ConstraintLayout, Bitmap>(clProfileBg!!) {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(classroomData!!.background_img, resource)
                                    clProfileBg?.background = BitmapDrawable(resources, resource)
                                }
                            })
                }else{
                    clProfileBg?.background = BitmapDrawable(resources, bitmap)
                }
            }

            if(!classroomData!!.profile_img.equals(getString(R.string.default_text))) {
                var bitmap = ImageCacheUtils.getBitmap(classroomData!!.profile_img)

                if(bitmap == null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(CommonUtils.getBigImageLinkPath(this, classroomData!!.profile_img))
                            .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                            .into(object : ViewTarget<ImageView, Bitmap>(ivProfile!!){
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    ImageCacheUtils.putBitmap(classroomData!!.profile_img, resource)
                                    ivProfile?.setImageBitmap(resource)
                                }
                            })
                }else{
                    ivProfile?.setImageBitmap(bitmap)
                }
            }

            tetClassTitle?.setText(classroomData!!.title)

            csvCateogry?.setValue(classroomData!!.category!!.depth_1!!.title + " / " + classroomData!!.category!!.depth_2!!.title)
            selectedCateogry = classroomData!!.category!!.depth_2!!.idx

            if(classroomData!!.tags != null && classroomData!!.tags!!.isNotEmpty())
                tetHashTag?.setText(CommonUtils.convertTagsToString(classroomData!!.tags!!))

            csvLocation?.switchOption?.isChecked = classroomData!!.is_locale == "Y"
            if(csvLocation!!.switchOption!!.isChecked) {
                csvLocation?.setValue(classroomData!!.address_1 + " " + classroomData!!.address_2)
                latitude = classroomData!!.lat
                longitude = classroomData!!.lng
            }
            tswClassType?.switch?.isChecked = classroomData!!.is_public == "Y"
        }
    }

    private fun createCategoryDialog(){
        if(category == null) {
            category = Dialog(this)
            category!!.window.requestFeature(Window.FEATURE_NO_TITLE)
            category!!.setContentView(R.layout.dialog_category)
            category!!.window.setLayout((resources.displayMetrics.widthPixels * 0.8f).toInt(), (resources.displayMetrics.heightPixels * 0.6f).toInt())
            category!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var flClose = category!!.findViewById<FrameLayout>(R.id.flCategoryDialogClose)
            var llCategory = category!!.findViewById<LinearLayout>(R.id.llCategoryList)
            categoryListInit(llCategory)

            flClose.setOnClickListener({
                sendAppEvent("클래스등록_카테고리팝업_닫기")
                category!!.dismiss()
            })
        }

        category!!.show()
    }

    private fun classTitleDuplicatedConfirm(){
        var checkId = tetClassTitle!!.getText()
        checkId = URLEncoder.encode(checkId, "UTF-8")
        var apiService = ApiManager.getInstance().apiService
        apiService.validateClassroomTitle(checkId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    result ->
                    if(result.status == "Y") {
                        titleDuplicateCheck = true
                        tetClassTitle?.setExplain(getString(R.string.is_enable))
                        tetClassTitle?.setExplainColor(ContextCompat.getColor(this, R.color.mainColor))
                        tetClassTitle?.showExplain()
                    }else{
                        Log.e("CLASS_TITLE", result.error)
                        titleDuplicateCheck = false
                        tetClassTitle?.setExplain(getString(R.string.class_title_duplicated))
                        tetClassTitle?.setExplainColor(ContextCompat.getColor(this, R.color.red))
                        tetClassTitle?.showExplain()
                        tetClassTitle?.requestTetFocus()
                    }
                }, {error ->
                    titleDuplicateCheck = false
                    if(error is HttpException){
                        if(error.code() == 400){
                            tetClassTitle?.setExplain(getString(R.string.class_title_duplicated))
                            tetClassTitle?.setExplainColor(ContextCompat.getColor(this, R.color.red))
                            tetClassTitle?.showExplain()
                            tetClassTitle?.requestTetFocus()
                        }
                    }
                })
    }

    private fun checkClassData(){
        if(tswClassType!!.isSwitchOn)
            classInfo.is_public = "Y"
        else
            classInfo.is_public = "N"

        if(csvLocation!!.isOptionSwitchOn){
            classInfo.is_locale = "Y"

            if(csvLocation!!.selectedValue.equals("")) {
                Toast.makeText(this, getString(R.string.confirm_location_info), Toast.LENGTH_SHORT).show()
                return
            }
            classInfo.address_1 = csvLocation!!.selectedValue
            classInfo.address_2 = csvLocation!!.getSecondContentValue()
            classInfo.lat = latitude
            classInfo.lng = longitude
        }else{
            classInfo.is_locale = "N"
        }

        if(!titleDuplicateCheck && classIdx == -1){
            Toast.makeText(this, getString(R.string.confirm_class_title), Toast.LENGTH_SHORT).show()
            return
        }else{
            classInfo.title = tetClassTitle!!.getText()
        }

        if(selectedCateogry == -1){
            Toast.makeText(this, getString(R.string.confirm_category_set), Toast.LENGTH_SHORT).show()
            return
        }else{
            classInfo.category = selectedCateogry
        }

        classInfo.tags = makeTagArray(tetHashTag!!.getText())

        var imageList = ArrayList<String>()

        if(profileImgPath.equals(""))
            classInfo.profile_img = getString(R.string.default_text)
        else
            imageList.add(profileImgPath)


        if(bgImgPath.equals(""))
            classInfo.background_img = getString(R.string.default_text)
        else
            imageList.add(bgImgPath)

        if(imageList.size == 0){
            if(classIdx == -1)
                registerClass()
            else
                updateClass()
        }else{
            var uploader = S3Uploader(this, imageList, this)
            uploader.upload()
        }
    }

    private fun registerClass(){
        var apiService = ApiManager.getInstance().apiService
        apiService.registerClass(classInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        Toast.makeText(this, getString(R.string.register_class_success), Toast.LENGTH_SHORT).show()
                        var intent = Intent(this, ClassDetailAcitivity::class.java)
                        intent.putExtra(getString(R.string.intent_key_name_index), result.result!!.idx)
                        startActivity(intent)
                        finish()
                    }else{
                        Log.e("REGISTER_CLASS", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_register_class), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("REGISTER_CLASS", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_register_class), Toast.LENGTH_SHORT).show()
                })
    }

    private fun updateClass(){
        var apiService = ApiManager.getInstance().apiService
        apiService.updateClassroomInfo(classIdx, classInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        Toast.makeText(this, getString(R.string.update_class_success), Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }else{
                        Log.e("UPDATE_CLASS", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_update_class_info), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("UPDATE_CLASS", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_update_class_info), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getFirstCategories(){
        var apiService = ApiManager.getInstance().apiService
        apiService.getFirstCategory()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y") {
                        firstCategories = result.result!!.toCollection(ArrayList())

                        for(idx in 0..firstCategories.lastIndex) {
                            secondCategories.add(ArrayList<CategoryData>())
                            getSecondCategoris(firstCategories[idx].idx, idx)
                        }
                    }else{
                        Log.e("FIRST_CATEGORY", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                    }
                },{ error ->
                    Log.e("FIRST_CATEGORY", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                })
    }

    private fun getSecondCategoris(idx: Int, firstPos: Int) {
        var apiService = ApiManager.getInstance().apiService
        apiService.getSecondCategory(idx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if (result.status == "Y") {
                        secondCategories.set(firstPos, result.result!!.toCollection(ArrayList()))
                    } else {
                        Log.e("SECOND_CATEGORY", result.error)
                        Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                    }
                }, { error ->
                    Log.e("SECOND_CATEGORY", error.toString())
                    Toast.makeText(this, getString(R.string.fail_to_category_load), Toast.LENGTH_SHORT).show()
                })
    }

    private fun categoryListInit(container: LinearLayout){
        for(idx in 0..firstCategories.lastIndex){
            var categoryGroup = CategoryGroupView(applicationContext)
            container.addView(categoryGroup)
            categoryGroup.setCategoryData(firstCategories[idx], secondCategories[idx], this)

            categoryGroup.setOnClickListener {
                sendAppEvent("클래스등록_1차카테고리선택")
                for(gIdx in 0 until container.childCount){
                    var child = container.getChildAt(gIdx) as CategoryGroupView
                    if(gIdx == idx)
                        if(child.isExpand){
                            child.groupUnselected()
                        }else {
                            child.groupSelected()
                        }
                    else
                        child.groupUnselected()
                }
            }
        }
    }

    private fun moveToAddressMap(){
        var intent = Intent(this, AddAddressActivity::class.java)
        startActivityForResult(intent, ADDRESS_ON_MAP)
    }

    private fun moveToGallery(requestCode: Int){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            var intent = Intent(this, ImagePickActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_name_limit_count), 1)
            startActivityForResult(intent, requestCode)
        }else{
            Toast.makeText(this, getString(R.string.permission_external_storage_denied), Toast.LENGTH_SHORT).show()
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
}