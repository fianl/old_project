package vdream.vd.com.vdream.view.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import vdream.vd.com.vdream.R

class TutorialActivity: BaseActivity() {
    var pbCurpage: ProgressBar? = null
    var vpTutorial: ViewPager? = null
    var tvStart: TextView? = null
    val pbRange = 20

    override fun onCreate(instance: Bundle?){
        super.onCreate(instance)
        setContentView(R.layout.activity_tutorial)
        pbCurpage = findViewById(R.id.pbCurpage)
        vpTutorial = findViewById(R.id.vpTutorial)
        tvStart = findViewById(R.id.tvAppStart)

        var adpater = TutorialAdpater()
        vpTutorial!!.adapter = adpater

        pbCurpage!!.max = 100
        pbCurpage!!.progress = pbRange

        vpTutorial!!.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                var valueAni = ValueAnimator.ofInt(pbCurpage!!.progress, (position+1)*pbRange)
                valueAni.duration = 500
                valueAni.addUpdateListener {
                    pbCurpage!!.progress = it.animatedValue as Int
                }
                valueAni.start()

                if(position == 4)
                    tvStart?.visibility = View.VISIBLE
                else
                    tvStart?.visibility = View.GONE
            }
        })

        tvStart?.setOnClickListener({
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    inner class TutorialAdpater: PagerAdapter(){
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var ivSample = ImageView(baseContext)
            ivSample.scaleType = ImageView.ScaleType.CENTER_CROP
            var imgRes = 0

            when(position){
                0 -> imgRes = R.drawable.tutorial_1
                1 -> imgRes = R.drawable.tutorial_2
                2 -> imgRes = R.drawable.tutorial_3
                3 -> imgRes = R.drawable.tutorial_4
                4 -> imgRes = R.drawable.tutorial_5
            }

            ivSample.setImageResource(imgRes)
            container.addView(ivSample)

            return ivSample
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as ImageView)
        }


        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return 5
        }

    }
}