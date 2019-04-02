package vdream.vd.com.vdream.view.component

import android.content.Context
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.FileData
import vdream.vd.com.vdream.data.UserDiaryData
import vdream.vd.com.vdream.data.UserRecordData
import vdream.vd.com.vdream.interfaces.PortfolioChangeCallback

class PortfolioItemView: FrameLayout {
    var tvDate: TextView? = null
    var ivDelete: ImageView? = null
    var tvContent: TextView? = null
    var tvTag: TextView? = null
    var vpImages: ViewPager? = null
    var llIndicator: LinearLayout? = null

    var recordData: UserRecordData? = null
    var callback: PortfolioChangeCallback? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_portfolio_item, this, false)
        tvDate = rootView.findViewById(R.id.tvPortfolioItemDate)
        ivDelete = rootView.findViewById(R.id.ivPortfolioItemDelete)
        tvContent = rootView.findViewById(R.id.tvPortfolioItemContent)
        tvTag = rootView.findViewById(R.id.tvPortfolioItemTag)
        vpImages = rootView.findViewById(R.id.vpPortfolioItemImages)
        llIndicator = rootView.findViewById(R.id.llPortfolioItemIndicator)

        addView(rootView)
    }

    internal fun setData(idx: Int, data: UserRecordData, callback: PortfolioChangeCallback){
        this.callback = callback
        recordData = data
        tvDate?.text = data.created_at.split(" ")[0].replace("-", ".")

        if(data.kind == context.getString(R.string.user_record_type_diary))
            tvContent?.text = data.content
        else
            tvContent?.text = getTextContentFromData(data.kind)

        tvTag?.text = vdream.vd.com.vdream.utils.CommonUtils.convertTagsToString(data.tags)

        if(data.files!!.isNotEmpty()){
            var imageList = ArrayList<String>()
            for(file in data.files!!){
                imageList.add(file.uploaded_path)
            }
            setImageResource(imageList)

            indicatorInit(data.files!!.toCollection(ArrayList()))
        }

        ivDelete?.setOnClickListener {
            callback?.portfolioDeleteRequest(idx)
        }
    }

    private fun indicatorInit(images: java.util.ArrayList<FileData>) {
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

    private fun setImageResource(imgList: ArrayList<String>) {
        vpImages?.visibility = View.VISIBLE
        vpImages?.adapter = ImagePagerAdapter(context, imgList)
        vpImages?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                indicatorUiChange(position)
            }
        })
    }

    private fun getTextContentFromData(kind: String): String {
        var content = ""

        when(kind){
            context.getString(R.string.user_record_type_class) -> content = makeClassTextContent()
            context.getString(R.string.user_record_type_club) -> content = makeClubTextContent()
            context.getString(R.string.user_record_type_career) -> content = makeCareerTextContent()
            context.getString(R.string.user_record_type_contest) -> content = makeContestTextContent()
            context.getString(R.string.user_record_type_volunteer) -> content = makeVolunteerTextContent()
            context.getString(R.string.user_record_type_behavior) -> content = makeBehaviorTextContent()
            context.getString(R.string.user_record_type_reading) -> content = makeReadingTextContent()
        }

        return content
    }

    private fun makeClassTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_class_unit))
        builder.append(" : ")
        builder.append(recordData!!.class_unit)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_class_learning))
        builder.append(" : ")
        builder.append(recordData!!.class_learning_content)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_class_leading_hor))
        builder.append(" :\n")
        builder.append(recordData!!.class_leading_learning)

        return builder.toString()
    }

    private fun makeClubTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_club_topic))
        builder.append(" : ")
        builder.append(recordData!!.club_topic)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.club_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_participants))
        builder.append(" : ")
        builder.append(recordData!!.club_participants)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_motivation))
        builder.append(" : ")
        builder.append(recordData!!.club_motivation)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_principle))
        builder.append(" : ")
        builder.append(recordData!!.club_principle)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_club_learning))
        builder.append(" : ")
        builder.append(recordData!!.club_learning)

        return builder.toString()
    }

    private fun makeCareerTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_career_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.career_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_effort))
        builder.append(" : ")
        builder.append(recordData!!.career_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_learning))
        builder.append(" : ")
        builder.append(recordData!!.career_learning)

        return builder.toString()
    }

    private fun makeContestTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_career_activated_at))
        builder.append(" : ")
        builder.append(recordData!!.contest_activated_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_effort))
        builder.append(" : ")
        builder.append(recordData!!.contest_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_career_learning))
        builder.append(" : ")
        builder.append(recordData!!.contest_learning)

        return builder.toString()
    }

    private fun makeVolunteerTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_volunteer_kind))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_kind)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_place))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_place)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_started_at))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_started_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_ended_at))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_ended_at)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_period))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_period)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_activity_content))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_activity_content)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_effort))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_effort)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_volunteer_learning))
        builder.append(" : ")
        builder.append(recordData!!.volunteer_learning)

        return builder.toString()
    }

    private fun makeBehaviorTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_behavior_kind))
        builder.append(" : ")
        builder.append(recordData!!.behavior_kind)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_behavior_case))
        builder.append(" : ")
        builder.append(recordData!!.behavior_case)

        return builder.toString()
    }

    private fun makeReadingTextContent(): String {
        var builder = StringBuilder()
        builder.append(context.getString(R.string.user_record_reading_book_name))
        builder.append(" : ")
        builder.append(recordData!!.reading_book_name)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_author))
        builder.append(" : ")
        builder.append(recordData!!.reading_author)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_motivation))
        builder.append(" : ")
        builder.append(recordData!!.reading_motivation)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_summary))
        builder.append(" : ")
        builder.append(recordData!!.reading_summary)
        builder.append("\n\n")
        builder.append(context.getString(R.string.user_record_reading_learning))
        builder.append(" : ")
        builder.append(recordData!!.reading_learning)

        return builder.toString()
    }
}