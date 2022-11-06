package com.example.solcalbeach;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class profileActivity extends Activity {

    private ListView listView;
    private RelativeLayout rl_head;
    private ImageView iv_head;
    private TextView tv_name,tv_other;


    //*******************************
    private int mLastY = 0;  //最后的点
    private static int mNeedDistance;   // 需要滑动的距离
    private static int mMinHight; //最小高度
    private static int mOrignHight; //原始的高度

    private int mCurrentDistance = 0;  //当前的距离
    private float mRate = 0;  //距离与目标距离的变化率 mRate=mCurrentDistance/mNeedDistance
    private int mPhotoOriginHeight; //图片的原始高度
    private int mPhotoOriginWidth; //图片的原始宽度
    private int mPhotoLeft;  //图片距左边的距离
    private int mPhotoTop;  //图片距离上边的距离
    private int mPhotoNeedMoveDistanceX;  // 图片需要移动的X距离
    private int mPhotoNeedMoveDistanceY;  // 图片需要移动的Y距离
    //需要移动的文字
    private int mTextLeft;  //文字距左边的距离
    private int mTextTop;  //文字距离上边的距离
    private int mTextNeedMoveDistanceX;  // 文字需要移动的X距离
    private int mTextNeedMoveDistanceY;  //文字需要移动的Y距离

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_activity);
        initView();
        initDistance();
    }

    private void initView() {
        listView= (ListView) findViewById(R.id.lv);
        rl_head= (RelativeLayout) findViewById(R.id.rl_head);
        iv_head= (ImageView) findViewById(R.id.iv_head);
        tv_name= (TextView) findViewById(R.id.tv_name);
        tv_other= (TextView) findViewById(R.id.tv_other);
    }

    private void initDistance() {
        mOrignHight = rl_head.getLayoutParams().height;
        mMinHight =240;
        mNeedDistance = mOrignHight - mMinHight;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
        mPhotoOriginHeight = params.height;
        mPhotoOriginWidth = params.width;
        mPhotoLeft = params.leftMargin;
        mPhotoTop = params.topMargin;
        mPhotoNeedMoveDistanceX = getWindowManager().getDefaultDisplay().getWidth() / 2 - mPhotoLeft - mMinHight;
        mPhotoNeedMoveDistanceY = mPhotoTop - 20;

        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) tv_name.getLayoutParams();
        mTextLeft = textParams.leftMargin;
        mTextTop = textParams.topMargin;
        mTextNeedMoveDistanceX = getWindowManager().getDefaultDisplay().getWidth() / 2 - mTextLeft + 10;
        mTextNeedMoveDistanceY = mTextTop - (mNeedDistance) / 2;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                //  LogUtils.d(TAG, "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                return super.dispatchTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                int y = (int) ev.getY();
                int dy = mLastY - y;
                //    LogUtils.d(TAG, "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                if (mCurrentDistance >= mNeedDistance && dy > 0) {
                    return super.dispatchTouchEvent(ev);
                }
                if (mCurrentDistance <= 0 && dy < 0) {
                    return super.dispatchTouchEvent(ev);
                }
                //改变布局
                changeTheLayout(dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                checkTheHeight();
                //       LogUtils.d(TAG, "ACTION_MOVE==mCurrentDistance" + mCurrentDistance);
                return super.dispatchTouchEvent(ev);
        }

        return false;
    }


    /**
     * 通过滑动的偏移量
     *
     * @param dy
     */
    private void changeTheLayout(int dy) {
        final ViewGroup.LayoutParams layoutParams = rl_head.getLayoutParams();
        layoutParams.height = layoutParams.height - dy;
        rl_head.setLayoutParams(layoutParams);
        checkTheHeight();
        rl_head.requestLayout();
        mCurrentDistance = mOrignHight - rl_head.getLayoutParams().height;
        mRate = (float) (mCurrentDistance * 1.0 / mNeedDistance);
        changeTheAlphaAndPostion(mRate);
    }


    /**
     * 根据变化率来改变这些这些控件的变化率位置
     *
     * @param rate
     */
    private void changeTheAlphaAndPostion(float rate) {
        if (rate >= 1) {
            tv_other.setVisibility(View.GONE);
        } else {
            tv_other.setVisibility(View.VISIBLE);
            tv_other.setAlpha(1 - rate);
            tv_other.setScaleY(1 - rate);
            tv_other.setScaleX(1 - rate);
        }
        RelativeLayout.LayoutParams photoParams = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
        //  photoParams.width = (int) (mPhotoOriginWidth - (rate * (mPhotoOriginWidth - mMinHight - 10)));
        //   photoParams.height = (int) (mPhotoOriginWidth - (rate * (mPhotoOriginWidth - mMinHight - 10)));
        photoParams.leftMargin = (int) (mPhotoLeft + mPhotoNeedMoveDistanceX * rate);
        photoParams.topMargin = (int) (mPhotoTop - mPhotoNeedMoveDistanceY * rate);
        iv_head.setLayoutParams(photoParams);
        //针对文字
        RelativeLayout.LayoutParams textParams = (RelativeLayout.LayoutParams) tv_name.getLayoutParams();
        textParams.leftMargin = (int) (mTextLeft + mTextNeedMoveDistanceX * rate);
        textParams.topMargin = (int) (mTextTop - mTextNeedMoveDistanceY * rate);
        tv_name.setLayoutParams(textParams);
    }


    /**
     * 检查上边界和下边界
     */
    private void checkTheHeight() {
        final ViewGroup.LayoutParams layoutParams = rl_head.getLayoutParams();
        if (layoutParams.height < mMinHight) {
            layoutParams.height = mMinHight;
            rl_head.setLayoutParams(layoutParams);
            rl_head.requestLayout();
        }
        if (layoutParams.height > mOrignHight) {
            layoutParams.height = mOrignHight;
            rl_head.setLayoutParams(layoutParams);
            rl_head.requestLayout();
        }

    }

}