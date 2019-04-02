package vdream.vd.com.vdream.view.component

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ClassData
import vdream.vd.com.vdream.data.ClassSummaryData
import vdream.vd.com.vdream.utils.CommonUtils
import vdream.vd.com.vdream.view.activity.ClassDetailAcitivity
import vdream.vd.com.vdream.view.activity.ClassMoreActivity

class ClassesViewGroup: FrameLayout {
    var ivType: ImageView? = null
    var tvType: TextView? = null
    var tvViewMore: TextView? = null
    var llSummaryContainer: LinearLayout? = null

    var kind = ""
    var dataList: ArrayList<ClassSummaryData> = ArrayList()
    var realDataArray: Array<ClassData>? = null

    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attr: AttributeSet): super(context, attr){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_classes_view_group, this, false)

        ivType = rootView.findViewById(R.id.ivClassType)
        tvType = rootView.findViewById(R.id.tvClassType)
        tvViewMore = rootView.findViewById(R.id.tvClassMore)
        llSummaryContainer = rootView.findViewById(R.id.llClassesContainer)

        tvViewMore?.setOnClickListener({
            var intent = Intent(context, ClassMoreActivity::class.java)
            intent.putExtra(context.getString(R.string.intent_key_name_class_kind), kind)
            context.startActivity(intent)
        })

        addView(rootView)
    }

    internal fun setType(type: String){
        kind = type
        tvType?.text = convertTypeEngToKor(type)
        ivType?.setImageResource(getClassTypeImageResource(type))
    }

    internal fun setData(dataList: ArrayList<ClassSummaryData>){
        this.dataList = dataList

        for(data in dataList){
            var summaryView = ClassSummaryView(context)
            summaryView.setThumbnailImage(data.imageUrl)
            summaryView.setTitle(data.title)
            summaryView.setTag(data.tag)
            llSummaryContainer?.addView(summaryView)

            summaryView.setOnClickListener({view ->
                var intent = Intent(context, ClassDetailAcitivity::class.java)
                intent.putExtra(context.getString(R.string.intent_key_name_index), data.id)
                context.startActivity(intent)
            })
        }
    }

    internal fun setData(data: Array<ClassData>){
        this.realDataArray = data

        for(data in data){
            var summaryView = ClassSummaryView(context)
            summaryView.setThumbnailImage(data.classroom!!.background_img)
            summaryView.setTitle(data.classroom!!.title)
            summaryView.setTag(CommonUtils.convertTagsToString(data.classroom!!.tags!!))
            llSummaryContainer?.addView(summaryView)

            summaryView.setOnClickListener({view ->
                var intent = Intent(context, ClassDetailAcitivity::class.java)
                intent.putExtra(context.getString(R.string.intent_key_name_index), data.classroom!!.idx)
                context.startActivity(intent)
            })
        }
    }

    internal fun setDataLargeView(dataList: ArrayList<ClassSummaryData>){
        this.dataList = dataList

        for(data in dataList){
            var summaryView = ClassSummaryLargeImageView(context)
            summaryView.setThumbnailImage(data.imageUrl)
            summaryView.setTitle("", data.title)
            summaryView.setTag(data.tag)
            llSummaryContainer?.addView(summaryView)

            summaryView.setOnClickListener({view ->
                var intent = Intent(context, ClassDetailAcitivity::class.java)
                intent.putExtra("CLASS_ID", data.id)
                context.startActivity(intent)
            })
        }
    }

    internal fun setDataLargeView(data: Array<ClassData>){
        this.realDataArray = data

        for(data in data){
            var summaryView = ClassSummaryLargeImageView(context)
            summaryView.setThumbnailImage(data.classroom!!.background_img)
            summaryView.setTitle(data.classroom!!.category!!.depth_2!!.title, data.classroom!!.title)
            summaryView.setTag(CommonUtils.convertTagsToString(data.classroom!!.tags!!))
            llSummaryContainer?.addView(summaryView)

            summaryView.setOnClickListener({view ->
                var intent = Intent(context, ClassDetailAcitivity::class.java)
                intent.putExtra(context.getString(R.string.intent_key_name_index), data.classroom!!.idx)
                context.startActivity(intent)
            })
        }
    }

    internal fun setMyRegisterClassData(dataList: ArrayList<ClassSummaryData>){
        this.dataList = dataList

        for(data in dataList){
            var summaryView = RegistedClassSummaryView(context)
            summaryView.setThumbnailImage(data.imageUrl)
            summaryView.setClassTitle(data.title)
            llSummaryContainer?.addView(summaryView)

            summaryView.setOnClickListener({view ->
                var intent = Intent(context, ClassDetailAcitivity::class.java)
                intent.putExtra("CLASS_ID", data.id)
                context.startActivity(intent)
            })
        }
    }

    private fun convertTypeEngToKor(type: String): String{
        var kor = ""
        when(type){
            context.getString(R.string.type_recommend) -> kor = context.getString(R.string.recommend_class)
            context.getString(R.string.type_institute) -> kor = context.getString(R.string.institute_class)
            context.getString(R.string.type_vip) -> kor = context.getString(R.string.vip_class)
            context.getString(R.string.type_my) -> kor = context.getString(R.string.my_class)
            context.getString(R.string.type_today) -> kor = context.getString(R.string.today_class)
        }

        return kor
    }

    private fun getClassTypeImageResource(type: String): Int{
        var res = 0
        when(type) {
            context.getString(R.string.type_recommend) -> res = R.drawable.icon_recommendation
            context.getString(R.string.type_institute) -> res = R.drawable.icon_education
            context.getString(R.string.type_vip) -> res = R.drawable.icon_education
            context.getString(R.string.type_my) -> res = 0
            context.getString(R.string.type_today) -> 0
        }

        return res
    }

    internal fun setClassMarkNone(){
        ivType?.visibility = View.GONE
    }

    internal fun hideClassTypeImage(){
        ivType?.visibility = View.GONE
    }
}