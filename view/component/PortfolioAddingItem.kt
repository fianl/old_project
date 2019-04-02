package vdream.vd.com.vdream.view.component

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.UserRecordData

class PortfolioAddingItem: FrameLayout {
    var cbAdding: CheckBox? = null
    var tvTitle: TextView? = null
    var tvDate: TextView? = null
    var recordData: UserRecordData? = null

    constructor(context: Context): super(context) {
        init()
    }

    private fun init(){
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_portfolio_adding_item, this, false)
        cbAdding = rootView.findViewById(R.id.cbPortfolioAdding)
        tvTitle = rootView.findViewById(R.id.tvPortfolioAddingTitle)
        tvDate = rootView.findViewById(R.id.tvPortfolioAddingDate)

        rootView.setOnClickListener {
            if(cbAdding!!.isChecked){
                cbAdding!!.isChecked = false
                rootView.setBackgroundColor(Color.WHITE)
            }else{
                cbAdding!!.isChecked = true
                rootView.setBackgroundColor(ContextCompat.getColor(context, R.color.portfolio_selected))
            }
        }

        addView(rootView)
    }

    internal fun setData(data: UserRecordData){
        recordData = data
        if(data.kind == context.getString(R.string.user_record_type_diary))
            tvTitle?.text = data.content
        else
            tvTitle?.text = getTextContentFromData(data.kind)
        tvDate?.text = data.created_at.split(" ")[0].replace("-", ".")
    }

    internal fun getIsChecked(): Boolean{
        return cbAdding!!.isChecked
    }

    internal fun getRecordIdx(): Int {
        return recordData!!.idx
    }

    internal fun getRecordData(): UserRecordData {
        return recordData!!
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