package vdream.vd.com.vdream.view.component

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.ReviewData
import vdream.vd.com.vdream.data.ReviewRegisterData
import vdream.vd.com.vdream.interfaces.ReviewChangeCallback
import vdream.vd.com.vdream.network.ApiManager

class ExperienceDetailReViewView: FrameLayout, ReviewChangeCallback {
    override fun onRequestUpdate(data: ReviewData) {
        createReviewWriteDialog(data)
    }

    override fun onRequestDelete(idx: Int) {
        deleteIdx = idx
        createDeleteConfirmDialog()
    }

    var tvScore: TextView? = null
    var pbScore: ProgressBar? = null
    var tvApplyCnt: TextView? = null
    var tvWrite: TextView? = null
    var llContainer: LinearLayout? = null
    var expIdx = 0
    var reviewList = ArrayList<ReviewData>()
    var average = 0f
    var deleteIdx = 0

    constructor(context: Context): super(context) {
        init()
    }

    private fun init() {
        var rootView = LayoutInflater.from(context).inflate(R.layout.view_experience_review, this, false)
        tvScore = rootView.findViewById(R.id.tvExpDetailReviewScore)
        pbScore = rootView.findViewById(R.id.pbExpDetailReviewScore)
        tvApplyCnt = rootView.findViewById(R.id.tvExpDetailReviewApplyCnt)
        tvWrite = rootView.findViewById(R.id.tvExpDetailReviewWrite)
        llContainer = rootView.findViewById(R.id.llExpDetailReviewContainer)

        addView(rootView)

        tvWrite?.setOnClickListener({
            createReviewWriteDialog(null)
        })
    }

    internal fun setData(idx: Int) {
        expIdx = idx

        getReview()
    }

    private fun getReview() {
        var apiService = ApiManager.getInstance().apiService
        apiService.getExperienceReview(expIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        reviewList = result.result!!.toCollection(ArrayList())
                        setReviewData()
                    }else{
                        Log.e("GET_REVIEW", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_get_exp_review), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("GET_REVIEW", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_get_exp_review), Toast.LENGTH_SHORT).show()
                })
    }

    private fun setReviewData() {
        if(llContainer!!.childCount > 0)
            llContainer!!.removeAllViews()

        for(data in reviewList) {
            var review = ReviewItmeView(context, this)
            llContainer?.addView(review)
            review.setData(data)
            average += data.rank
        }

        average /= reviewList.size

        if(reviewList.size == 0)
            average = 0.0f

        tvScore?.text = String.format("%.1f", average)
        pbScore?.progress = (average * 10).toInt()

        tvApplyCnt?.text = "${reviewList.size}명 참여"
    }

    private fun createReviewWriteDialog(reviewData: ReviewData?) {
        var review = Dialog(context)
        review.window.requestFeature(Window.FEATURE_NO_TITLE)
        review.setContentView(R.layout.dialog_write_review)
        review.window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        review.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var flClose = review.findViewById<FrameLayout>(R.id.flWriteReviewClose)
        var sbAverage = review.findViewById<SeekBar>(R.id.sbWriteReviewAverage)
        var etContent = review.findViewById<EditText>(R.id.etWriteReviewContent)
        var tvWrite = review.findViewById<TextView>(R.id.tvWriteReviewApply)

        if(reviewData != null) {
            sbAverage.progress = (reviewData!!.rank / 10f).toInt()
            etContent.text = SpannableStringBuilder(reviewData!!.content)
        }

        sbAverage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var standard = progress / 5
                sbAverage.progress = standard * 5
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        flClose.setOnClickListener({
            review.dismiss()
        })

        tvWrite.setOnClickListener({
            var score = (0.05 * sbAverage.progress).toFloat()
            score = Math.round(score * 10f) / 10f
            var content = etContent.text.toString()
            if(reviewData == null) {
                registerReview(score, content, review)
            }else{
                updateReview(score, content, reviewData.idx, review)
            }
        })

        review.show()
    }

    private fun registerReview(score: Float, content: String, dialog: Dialog){
        var registerData = ReviewRegisterData()
        registerData.rank = score
        registerData.content = content
        var apiService = ApiManager.getInstance().apiService
        apiService.registerExpReview(expIdx, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(context, context.getString(R.string.success_to_register_exp_review), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        getReview()
                    }else{
                        Log.e("REGISTER_REVIEW", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_register_exp_review), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("REGISTER_REVIEW", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_register_exp_review), Toast.LENGTH_SHORT).show()
                })
    }

    private fun updateReview(score: Float, content: String, reviewIdx: Int, dialog: Dialog) {
        var registerData = ReviewRegisterData()
        registerData.rank = score
        registerData.content = content
        var apiService = ApiManager.getInstance().apiService
        apiService.updateExpReview(expIdx, reviewIdx, registerData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(context, context.getString(R.string.success_to_update_exp_review), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        getReview()
                    }else{
                        Log.e("REGISTER_REVIEW", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_update_review), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("REGISTER_REVIEW", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_update_review), Toast.LENGTH_SHORT).show()
                })
    }

    private fun createDeleteConfirmDialog(){
        var deleteBuilder = AlertDialog.Builder(context)
        deleteBuilder.setTitle(context.getString(R.string.update_alert_title))
        deleteBuilder.setMessage(context.getString(R.string.dialog_delete_confirm_content))
        deleteBuilder.setPositiveButton(context.getString(R.string.update_alert_positiv_button), object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                deleteReview(deleteIdx)
            }
        })
        deleteBuilder.setNegativeButton(context.getString(R.string.update_alert_negative_button_close), null)
        deleteBuilder.create().show()
    }

    private fun deleteReview(reviewIdx: Int) {
        var apiService = ApiManager.getInstance().apiService
        apiService.deleteExpReview(expIdx, reviewIdx)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                    if(result.status == "Y"){
                        Toast.makeText(context, context.getString(R.string.success_to_delete_exp_review), Toast.LENGTH_SHORT).show()
                        getReview()
                    }else{
                        Log.e("DEL_REVIEW", result.error)
                        Toast.makeText(context, context.getString(R.string.fail_to_delete_review), Toast.LENGTH_SHORT).show()
                    }
                }, { err ->
                    Log.e("DEL_REVIEW", err.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_delete_review), Toast.LENGTH_SHORT).show()
                })
    }
}