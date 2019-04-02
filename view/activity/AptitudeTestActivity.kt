package vdream.vd.com.vdream.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import vdream.vd.com.vdream.R

class AptitudeTestActivity: BaseActivity(), View.OnClickListener {
    var flback: FrameLayout? = null
    var flNext: FrameLayout? = null

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.flAptitudeBack -> finish()
            R.id.flAptitudeNex -> {
                var intent = Intent(this, InterestsSetActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aptitude_test)

        flback = findViewById(R.id.flAptitudeBack)
        flNext = findViewById(R.id.flAptitudeNex)

        if(!intent.getBooleanExtra("IS_BACK", true)){
            flback?.visibility = View.GONE
        }else{
            flNext?.visibility = View.GONE
        }

        flback?.setOnClickListener(this)
        flNext?.setOnClickListener(this)
    }
}