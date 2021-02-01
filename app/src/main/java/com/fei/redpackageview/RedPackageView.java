package com.fei.redpackageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

/**
 * @ClassName: RedPackageView
 * @Description: 红包动画
 * @Author: Fei
 * @CreateDate: 2021/2/1 11:38
 * @UpdateUser: Fei
 * @UpdateDate: 2021/2/1 11:38
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class RedPackageView extends View {

    private Paint mPaint;
    // 颜色渐变
    private int mProgressStarColor = Color.parseColor("#FDA501");
    private int mProgressEndColor = Color.parseColor("#FFEF74");
    //图片
    private Bitmap mRedPackageBitmap;
    private Bitmap mProgressBitmap;
    //图片宽高度
    private int mRedPackageBitmapWidth;
    private int mRedPackageBitmapHeight;
    private int mProgressBitmapWidth;
    private int mProgressBitmapHeight;
    //进度高宽度
    private float mProgressWidth;
    private float mProgressHeight;
    private int mWidth;
    private int mHeight;
    //进度条进度
    private float mCurrentProgress = 0;
    private float mTotalProgress = 4;
    private RectF mProgressRectF;
    //爆炸动画半径
    private float mBombRadius;
    private float mCurrentBombRadius;
    //爆炸图片数量
    private int mBombCount = 8;
    //爆炸图片
    private Bitmap[] mBombBitmap = new Bitmap[2];
    //爆炸图片宽高度
    private int mBombWidth;
    private int mBombHeight;

    private AnimatorSet mAnimatorSet;
    private float mCurrentAlpha = 1;

    public RedPackageView(Context context) {
        this(context, null);
    }

    public RedPackageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedPackageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);

        //获取红包图片，进度背景图片
        mRedPackageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_game_red_package_normal);
        mProgressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_game_red_package_pb_bg);

        mBombBitmap[0] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_package_bomb_1);
        mBombBitmap[1] = BitmapFactory.decodeResource(getResources(), R.drawable.icon_red_package_bomb_2);

        //获取图片高宽度
        mRedPackageBitmapWidth = mRedPackageBitmap.getWidth();
        mRedPackageBitmapHeight = mRedPackageBitmap.getHeight();
        mProgressBitmapWidth = mProgressBitmap.getWidth();
        mProgressBitmapHeight = mProgressBitmap.getHeight();
        mProgressWidth = mProgressBitmapWidth * 3.94f / 5f;
        mProgressHeight = mProgressBitmapHeight / 2.8f;
        //进度条
        mProgressRectF = new RectF();
        //爆炸总半径为进度图片高度三分之一
        mBombRadius = mProgressBitmapHeight / 2f;
        //爆炸图片高宽度
        mBombWidth = mBombBitmap[0].getWidth();
        mBombHeight = mBombBitmap[0].getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //红包宽度最大值的1.2倍
        int size = Math.max(mRedPackageBitmapWidth, mRedPackageBitmapHeight);
        mWidth = (int) (size * 1.2f);
        mHeight = (int) (size * 1.2f);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画红包
        float redPackageLeft = (mWidth - mRedPackageBitmapWidth) / 2f;
        float redPackageTop = (mHeight - mRedPackageBitmapHeight) / 2f;
        canvas.drawBitmap(mRedPackageBitmap, redPackageLeft, redPackageTop, null);
        //画进度背景
        float progressLeft = redPackageLeft + mRedPackageBitmapWidth - mProgressBitmapWidth;
        float progressTop = redPackageTop + mRedPackageBitmapHeight * 7 / 10f;
        canvas.drawBitmap(mProgressBitmap, progressLeft, progressTop, null);
        //渐变颜色
        float progressRight = 0;
        if (mCurrentProgress > 0) {
            mPaint.setAlpha(255);
            float currentProgressWidth = mProgressWidth * mCurrentProgress / mTotalProgress;
            LinearGradient shader = new LinearGradient(0, 0, currentProgressWidth, 0,
                    new int[]{mProgressStarColor, mProgressEndColor},
                    new float[]{0, 1.0f}, Shader.TileMode.CLAMP);
            mPaint.setShader(shader);
            progressTop = progressTop + mProgressBitmapHeight / 3f;
            progressLeft = progressLeft + mProgressBitmapWidth / 8.8f;
            progressRight = progressLeft + currentProgressWidth;
            float progressBottom = progressTop + mProgressHeight;
            mProgressRectF.set(progressLeft, progressTop, progressRight, progressBottom);
            float circle = mProgressBitmapHeight / 4.8f;
            canvas.drawRoundRect(mProgressRectF, circle, circle, mPaint);
        }

        if (mCurrentBombRadius > 0) {
            mPaint.setAlpha((int) (122f + mCurrentBombRadius / mBombRadius * 133f * mCurrentAlpha));
            double angle = Math.PI * 2 / mBombCount;
            for (int i = 0; i < mBombCount; i++) {
                //当前进度的位置+半径的三角函数-图片的高宽度5分之3
                float cx = (float) (progressRight + Math.cos(angle * i) * mCurrentBombRadius - mBombWidth * 3 / 5f);
                float cy = (float) (progressTop + mProgressHeight / 2f + Math.sin(angle * i) * mCurrentBombRadius - mBombHeight * 3 / 5f);
                canvas.drawBitmap(mBombBitmap[i % 2], cx, cy, mPaint);
            }
        }
    }

    /**
     * 设置总进度
     *
     * @param totalProgress
     */
    public void setTotalProgress(float totalProgress) {
        this.mTotalProgress = totalProgress;
    }

    /**
     * 从begin到end进度滚动
     *
     * @param begin
     * @param end
     */
    public void setCurrentProgress(int begin, int end) {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        //进度动画
        ValueAnimator mProgressAnimator = ObjectAnimator.ofFloat(begin, end);
        mProgressAnimator.setDuration(600);
        mProgressAnimator.setInterpolator(new LinearInterpolator());
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        //爆炸动画
        ValueAnimator mBombAnimator = ObjectAnimator.ofFloat(0, mBombRadius);
        mBombAnimator.setDuration(800);
        mBombAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mBombAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentBombRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //透明度动画
        ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(1f, 0f);
        alphaAnimator.setDuration(600);
        alphaAnimator.setInterpolator(new DecelerateInterpolator());
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAlpha = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentBombRadius = 0;
                mCurrentAlpha = 1;
                invalidate();
            }
        });
        //顺序执行
        mAnimatorSet.playSequentially(mProgressAnimator, mBombAnimator,alphaAnimator);
        mAnimatorSet.start();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }
}
