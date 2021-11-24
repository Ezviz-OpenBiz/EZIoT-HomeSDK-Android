package com.eziot.demo.widget.pulltorefresh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import com.eziot.iotsdkdemo.R;


/**
 * Created by huchenxi on 2016/9/18.
 */
public class LoadingViewBase extends View {

    //几个球的资源共用（资源比较小，而且用的地方很多所以static吧）
    protected static Bitmap[] mBalls;

    public LoadingViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBalls(context);
    }

    protected void initBalls(Context context) {
        if (mBalls == null || mBalls[0] == null) {
            mBalls = new Bitmap[4];
            mBalls[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_loading_1);
            mBalls[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_loading_2);
            mBalls[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_loading_3);
            mBalls[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_loading_4);
        }
    }

}
