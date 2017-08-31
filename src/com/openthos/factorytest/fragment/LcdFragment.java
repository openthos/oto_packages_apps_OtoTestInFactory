package com.openthos.factorytest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openthos.factorytest.MainActivity;
import com.openthos.factorytest.R;

/**
 * Created by root on 5/2/17.
 */

public class LcdFragment extends Fragment {
    private int mNum = 0;
    private TextView mTextView;
    private RelativeLayout mRelativeLayout;
    Bitmap mBitmap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_lcd, container, false);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.skip);
        mTextView = (TextView) view.findViewById(R.id.text);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.parent);
        DragView dragView = new DragView(getActivity());

        mRelativeLayout.addView(dragView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNum++;
                changeColor(v);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private void changeColor(View v) {
        mTextView.setVisibility(View.GONE);
        switch (mNum % 3) {
            case 0:
                v.setBackground(new ColorDrawable(Color.RED));
                break;
            case 1:
                v.setBackground(new ColorDrawable(Color.BLUE));
                break;
            case 2:
                v.setBackground(new ColorDrawable(Color.GREEN));
                break;
        }
    }

    private class DragView extends View {
        private int mMotionX = 100;
        private int mMotionY = 100;
        private int mTempX = 0;
        private int mTempY = 0;
        private Paint mPaint;

        public DragView(Context context) {
            super(context);
            mPaint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, mMotionX, mMotionY, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean isMove = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getX() <= mMotionX + mBitmap.getWidth()
                            && event.getX() >= mMotionX
                            && event.getY() <= mMotionY + mBitmap.getHeight()
                            && event.getY() >= mMotionY) {
                        mTempX = (int) event.getX();
                        mTempY = (int) event.getY();
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    mMotionX = (int) event.getX() - mBitmap.getWidth() / 2;
                    mMotionY = (int) event.getY() - mBitmap.getHeight() / 2;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs((int) event.getX() - mTempX) < mBitmap.getWidth() / 2
                            && Math.abs((int) event.getY() - mTempY) < mBitmap.getHeight() / 2) {
                        ((MainActivity) getActivity()).checkNextPage();
                    }
                    break;
            }
            return isMove;
        }
    }
}
