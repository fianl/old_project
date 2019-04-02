package vdream.vd.com.vdream.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.data.QuestionAddData
import vdream.vd.com.vdream.network.ApiManager
import vdream.vd.com.vdream.store.MyInfoStore
import vdream.vd.com.vdream.view.component.TitledEditTextFlat

class QuestionAddDialog: Dialog {
    var tetfName: TitledEditTextFlat? = null
    var tetfPhone: TitledEditTextFlat? = null
    var tetfEmail: TitledEditTextFlat? = null
    var etContent: EditText? = null
    var tvAdd: TextView? = null

    constructor(context: Context): super(context){
        init()
    }

    private fun init(){
        window.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_question)
        window.setLayout((context.resources.displayMetrics.widthPixels*0.9f).toInt(), (context.resources.displayMetrics.heightPixels*0.9f).toInt())
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        tetfName = findViewById(R.id.tetfQuetionName)
        tetfPhone = findViewById(R.id.tetfQuetionPhone)
        tetfEmail = findViewById(R.id.tetfQuetionEmail)
        etContent = findViewById(R.id.etQuetionContent)
        tvAdd = findViewById(R.id.tvQuestionAdd)

        tetfName?.setTitle(context.getString(R.string.name))
        tetfPhone?.setTitle(context.getString(R.string.phone))
        tetfEmail?.setTitle(context.getString(R.string.email))

        tvAdd?.setOnClickListener({
            addQuestion()
        })

        if(MyInfoStore.myInfo!!.email != null)
            tetfEmail?.setValue(MyInfoStore.myInfo!!.email)
    }

    private fun addQuestion(){
        var body = QuestionAddData()

        if(!tetfName!!.getValue().equals(""))
            body.name = tetfName!!.getValue()

        if(!tetfPhone!!.getValue().equals(""))
            body.phone = tetfPhone!!.getValue()

        if(!tetfEmail!!.getValue().equals(""))
            body.email = tetfEmail!!.getValue()

        if(!etContent!!.text.toString().equals(""))
            body.content = etContent!!.text.toString()

        var apiService = ApiManager.getInstance().apiService
        apiService.addQuestion(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ result ->
                   if(result.status.equals("Y")){
                        Toast.makeText(context, context.getString(R.string.question_added), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }else{
                       Log.e("ADD_QUESTION", result.error)
                       Toast.makeText(context, context.getString(R.string.fail_to_add_question), Toast.LENGTH_SHORT).show()
                   }
                }, { error ->
                    Log.e("ADD_QUESTION", error.toString())
                    Toast.makeText(context, context.getString(R.string.fail_to_add_question), Toast.LENGTH_SHORT).show()
                })
    }
}