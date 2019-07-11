package com.bytedance.clockapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();
    private static final int FULL_ANGLE = 360;
    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;
    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;
    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;
    public final static int AM = 0;
    private static final int RIGHT_ANGLE = 90;
    private int mWidth, mCenterX, mCenterY, mRadius;

    /*** properties     */
    private int centerInnerColor;
    private int centerOuterColor;
    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;
    private int degreesColor;
    private int hoursValuesColor;
    private int numbersColor;
    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        size = Math.min(widthWithoutPadding, heightWithoutPadding);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {
        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;
        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.degreesColor = DEFAULT_PRIMARY_COLOR;
        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;
        numbersColor = Color.WHITE;

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mWidth = Math.min(getWidth(), getHeight());
        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;
        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {
            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }
            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));
            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }


    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /** * Draw Hour Text Values, such as 1 2 3 ...*/
    private void drawHoursValues(Canvas canvas) {
        Paint textPaint =new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(68);
        textPaint.setColor(hoursValuesColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
        int radius = mCenterX - (int) (mWidth * 0.12f);
        int num=1;
        for (int i = 60+FULL_ANGLE; i > 60; i -= 30) {
            int centerX = (int) (mCenterX + radius* Math.cos(Math.toRadians(i)));
            int centerY = (int) (mCenterX - radius * Math.sin(Math.toRadians(i)));
            Rect rect=new Rect();
            String text=String.valueOf(num);
            textPaint.getTextBounds(text,0,text.length(),rect);
            canvas.drawText(text,centerX,(centerY+rect.height()/2),textPaint);
            num+=1;
        }
    }

    /*** Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.   */
    private void drawNeedles(final Canvas canvas) {
        Paint nHour=new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint nMinute=new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint nSecond=new Paint(Paint.ANTI_ALIAS_FLAG);
        nHour.setColor(hoursNeedleColor);
        nMinute.setColor(minutesNeedleColor);
        nSecond.setColor(secondsNeedleColor);
        nHour.setStrokeWidth(18f);
        nMinute.setStrokeWidth(12f);
        nSecond.setStrokeWidth(8f);
        nHour.setStrokeCap(Paint.Cap.ROUND);
        nMinute.setStrokeCap(Paint.Cap.ROUND);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);
        int degSecond=second*6;
        int degMinute=minute*6+(int)(second*0.1);
        int degHour=hour*30+degMinute/120;
        int endX = (int) (mCenterX + mWidth*0.28 * Math.cos(Math.toRadians(450-degHour)));
        int endY = (int) (mCenterX - mWidth*0.28* Math.sin(Math.toRadians(450-degHour)));
        canvas.drawLine(mCenterX, mCenterY,endX,endY,nHour);
        endX = (int) (mCenterX + mWidth*0.32 * Math.cos(Math.toRadians(450-degMinute)));
        endY = (int) (mCenterX - mWidth*0.32* Math.sin(Math.toRadians(450-degMinute)));
        canvas.drawLine(mCenterX, mCenterY,endX,endY,nMinute);
        endX = (int) (mCenterX + mWidth*0.4 * Math.cos(Math.toRadians(450-degSecond)));
        endY = (int) (mCenterX - mWidth*0.4* Math.sin(Math.toRadians(450-degSecond)));
        canvas.drawLine(mCenterX, mCenterY,endX,endY,nSecond);

    }

    /**  * Draw Center Dot     */
    private void drawCenter(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(centerInnerColor);
        paint.setStyle(Paint.Style.FILL);
        Paint outpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outpaint.setColor(centerOuterColor);
        outpaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX,mCenterY,40f,outpaint);
        canvas.drawCircle(mCenterX,mCenterY,25f,paint);
    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }
}