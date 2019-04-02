package vdream.vd.com.vdream.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.Window
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import vdream.vd.com.vdream.R
import java.text.SimpleDateFormat
import java.util.*

class CommonUtils {
    companion object {
        fun getImageWidePath(context: Context, fileName: String): String {
            var fileNames = fileName.split(".")
            Log.d("WIDE_IMAGE", context.getString(R.string.aws_s3_image_link_base) + fileNames[0] + "_1440x810." + fileNames[1])
            return context.getString(R.string.aws_s3_image_link_base) + fileNames[0] + "_1440x810." + fileNames[1]
        }

        fun getBigImageLinkPath(context: Context, fileName: String): String {
            var fileNames = fileName.split(".")
            return context.getString(R.string.aws_s3_image_link_base) + fileNames[0] + "_720x720." + fileNames[1]
        }

        fun getThumbnailLinkPath(context: Context, fileName: String): String {
            var fileNames = fileName.split(".")
            return context.getString(R.string.aws_s3_image_link_base) + fileNames[0] + "_200x200." + fileNames[1]
        }

        fun getOriginImagePath(context: Context, fileName: String): String {
            Log.d("ORIGIN_IMAGE", context.getString(R.string.aws_s3_image_link_base_origin) + fileName)
            return context.getString(R.string.aws_s3_image_link_base_origin) + fileName
        }

        fun convertTagsToString(tags: Array<String>?): String {
            if(tags == null)
                return ""

            var builder = StringBuilder()

            for(tag in tags){
                builder.append("#")
                builder.append(tag)
            }

            return builder.toString()
        }

        fun calculateTimeFromCreated(time: String): String{
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

            var curDate = Date()
            var createdDate = format.parse(time)

            var term = ((curDate.time - createdDate.time) / 1000.0 / 60.0).toInt()

            if(term < 60)
                if(term < 5)
                    return "방금 전"
                else
                    return term.toString() + "분 전"
            else{
                var hour = term / 60
                if(hour < 24)
                    return hour.toString() + "시간 전"
                else{
                    var day = hour / 24
                    when(day){
                        1 -> return "하루 전"
                        2 -> return "이틀 전"

                        else -> return day.toString() + "일 전"
                    }
                }
            }
        }

        fun createDatePicker(context: Context, target: TextView){
            var picker = Dialog(context)
            picker.window.requestFeature(Window.FEATURE_NO_TITLE)
            picker.setContentView(R.layout.dialog_date_pick)
            picker.window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            picker.window.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            var dp = picker.findViewById<DatePicker>(R.id.dpTargetDate)
            var tvConfirm = picker.findViewById<TextView>(R.id.tvDatePickConfirm)

            picker.setOnDismissListener(object : DialogInterface.OnDismissListener{
                override fun onDismiss(dialog: DialogInterface?) {
                    target.text = dp.year.toString() + "-" + String.format("%02d", dp.month + 1) + "-" + String.format("%02d", dp.dayOfMonth)
                }
            })

            tvConfirm.setOnClickListener({
                picker.dismiss()
            })

            picker.show()
        }

        fun createTimePicker(context: Context, target: TextView){
            var picker = Dialog(context)
            picker.window.requestFeature(Window.FEATURE_NO_TITLE)
            picker.setContentView(R.layout.dialog_time_pick)
            picker.window.setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
            picker.window.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            var tp = picker.findViewById<TimePicker>(R.id.tpTargetTime)
            tp.setIs24HourView(true)
            var tvConfirm = picker.findViewById<TextView>(R.id.tvTimePickConfirm)

            picker.setOnDismissListener(object : DialogInterface.OnDismissListener{
                override fun onDismiss(dialog: DialogInterface?) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        target.text = tp.hour.toString() + ":" + String.format("%02d", tp.minute)+ ":00"
                    else
                        target.text = tp.currentHour.toString() + ":" + String.format("%02d", tp.currentMinute) + ":00"
                }
            })

            tvConfirm.setOnClickListener({
                picker.dismiss()
            })

            picker.show()
        }

        fun volunteerDateRetouch(date: String): String {
            if(date == "")
                return ""

            var format = SimpleDateFormat("yyyyMMdd")
            var reFormat = SimpleDateFormat("yyyy.MM.dd")
            var originDate = format.parse(date)

            return reFormat.format(originDate)
        }
    }
}