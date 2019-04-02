package vdream.vd.com.vdream.view.component

import android.graphics.*
import android.graphics.drawable.Drawable

class DrawableWithText: Drawable() {
    var mAlpha = 0
    var mOpacity = 255
    var mColorFilter: ColorFilter? = null
    var mText = ""
    var mRect: RectF? = null
    var mRectPaint: Paint? = null
    var mTextPaint: Paint? = null
    var cornerRadius = 48f
    var mTextMarginLeft = 0f
    var mTextMarginTop = 0f
    var mIsTextPosRight = false

    override fun draw(canvas: Canvas?) {
        if(mRect == null)
            mRect = RectF(0f, 0f, 128f, 48f)

        if(mRectPaint == null)
            mRectPaint = Paint(Color.MAGENTA)

        if(mTextPaint == null)
            mTextPaint = Paint(Color.WHITE)

        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, mRectPaint)

        if(mIsTextPosRight) {
            var textRect = Rect()
            mTextPaint?.getTextBounds(mText, 0, mText.length, textRect)
            canvas?.drawText(mText, mRect!!.width() - textRect.width() - mTextMarginLeft, mTextMarginTop, mTextPaint)
        }
        else
            canvas?.drawText(mText, mTextMarginLeft, mTextMarginTop, mTextPaint)
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha

        if(mAlpha < 0){
            mAlpha = 0
        }
    }

    override fun getOpacity(): Int {
        return mOpacity
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mColorFilter = colorFilter
    }

    internal fun setRectPaint(color: Int){
        mRectPaint = Paint()
        mRectPaint?.color = color
    }

    internal fun setRectPaint(startColor: Int, endColor: Int){
        mRectPaint = Paint()
        mRectPaint?.setShader(LinearGradient(0f, 0f, mRect!!.width().toFloat(), mRect!!.height(), startColor, endColor, Shader.TileMode.MIRROR))
    }

    internal fun setTextPaint(color: Int, textSize: Float){
        mTextPaint = Paint()
        mTextPaint?.color = color
        mTextPaint?.textSize = textSize
        mTextPaint?.typeface = Typeface.DEFAULT_BOLD
    }
}