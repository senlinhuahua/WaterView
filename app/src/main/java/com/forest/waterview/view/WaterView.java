package com.forest.waterview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.forest.waterview.R;

/**
 * Created by forest on 2018/9/14 0014.
 */

public class WaterView extends View {

    private Paint paint;
    private Path path;
    private int waveLength = 400;
    private int dx;
    private int dv;
    private Bitmap mBitmap;
    private int width;
    private int height;

    private int bitmapLength;


    private Region region;
    private int waveHeight;
    private int wave_boatBitmap;
    private boolean wave_rise;
    private int duration;
    private int originY;



    public WaterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.WaterView);
        wave_boatBitmap = a.getResourceId(R.styleable.WaterView_boatBitmap,0);
        wave_rise = a.getBoolean(R.styleable.WaterView_rise,false);
        duration = (int) a.getDimension(R.styleable.WaterView_duration,2000);
        originY = (int) a.getDimension(R.styleable.WaterView_originY,1000);
        waveHeight = (int) a.getDimension(R.styleable.WaterView_waveHeight,200);
        waveLength = (int) a.getDimension(R.styleable.WaterView_waveLength,400);
        bitmapLength = (int) a.getDimension(R.styleable.WaterView_bitmapLength,80);
        a.recycle();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        if (wave_boatBitmap > 0){
            mBitmap = BitmapFactory.decodeResource(getResources(),wave_boatBitmap,options);
            mBitmap = getCircleBitmap(mBitmap);
        }else {
            mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.yeyeye,options);
        }

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.water));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if (originY==0){
            originY = height;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setPathData();
        canvas.drawPath(path,paint);
        Rect bounds = region.getBounds();
        if (bounds.top >0 ||bounds.bottom>0){
            if (bounds.top < originY){
                canvas.drawBitmap(mBitmap,bounds.left-mBitmap.getWidth()/2,
                        bounds.top-mBitmap.getHeight(), paint);
            }else {
                canvas.drawBitmap(mBitmap,bounds.left-mBitmap.getWidth()/2,
                        bounds.bottom-mBitmap.getHeight(), paint);
            }
        }else {
            canvas.drawBitmap(mBitmap,width/2 - mBitmap.getWidth()/2,
                    originY-mBitmap.getHeight(),paint);
        }


    }

    private void setPathData() {
        //二阶贝塞尔曲线
        path.reset();
        int halfWaveLength = waveLength /2;
        path.moveTo(-waveLength +dx,originY);
        for (int i = -waveLength;i < width + waveLength; i += waveLength){
            //相对坐标的二阶贝塞尔曲线
            path.rQuadTo(halfWaveLength/2,-waveHeight,halfWaveLength,0);
            path.rQuadTo(halfWaveLength/2,waveHeight,halfWaveLength,0);
        }
        region = new Region();
        Region cilp = new Region(width /2 -1,0,width /2,height);
        region.setPath(path,cilp);//交点区域

        //曲线封闭
        path.lineTo(width,height);
        path.lineTo(0,height);
        path.close();
    }

    public void startAniation(){
        ValueAnimator animator = ValueAnimator.ofFloat(0,1);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                dx = (int) (waveLength * fraction);//波长=0^1百分比的波长
                postInvalidate();
            }
        });
        animator.start();
    }

    //取圆
    public Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap == null){
            return null;
        }
        try {
            Bitmap circleBitmap = Bitmap.createBitmap(bitmapLength,bitmapLength,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            final Paint paint = new Paint();
            Rect rect = new Rect(0,0,bitmapLength,bitmapLength);
            final RectF rectF = new RectF(new Rect(0,0,bitmapLength,
                    bitmapLength));
            float roundPx = 0.0f;
            //已较短的边为标准
            if (bitmap.getWidth() > bitmap.getHeight()){
                roundPx = bitmap.getHeight() / 2.0f;
            }else {
                roundPx = bitmap.getWidth() / 2.0f;
            }
            paint.setAntiAlias(true);
            canvas.drawARGB(0,0,0,0);
            paint.setColor(Color.WHITE);
            //canvas.drawRoundRect(rectF,roundPx,roundPx,paint);
            canvas.drawRoundRect(rectF,bitmapLength,bitmapLength,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0,0,bitmapLength,bitmapLength);
            canvas.drawBitmap(bitmap,src,rect,paint);
            return circleBitmap;
        }catch (Exception e){
            return bitmap;
        }

    }


}
