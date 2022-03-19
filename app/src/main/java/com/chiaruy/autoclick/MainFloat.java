package com.chiaruy.autoclick;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLDisplay;

public class MainFloat extends FrameLayout {
    private String TAG = "MainFloat";
    private Context mContext;
    private View mView_add;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams_add;
    private int SizeX, SizeY;
    private ImageView mImage_play;
    private boolean Play = false;

    public MainFloat(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);

        mView_add = mLayoutInflater.inflate(R.layout.floating_add, null);
        mImage_play = (ImageView) mView_add.findViewById(R.id.play);

        mView_add.setOnTouchListener(mViewAdd_OnTouchListener);
        mImage_play.setOnClickListener(mImagePlay_OnClickListener);

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        SizeX = point.x;
        SizeY = point.y;

        mWindowParams_add = new WindowManager.LayoutParams();
        mWindowParams_add.gravity = Gravity.START;
        mWindowParams_add.x = 0;
        mWindowParams_add.y = 0;
        mWindowParams_add.format = PixelFormat.RGBA_8888;
        mWindowParams_add.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams_add.width = LayoutParams.WRAP_CONTENT;
        mWindowParams_add.height = LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT >= 26) {
            mWindowParams_add.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams_add.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        try {
            mWindowManager.addView(mView_add, mWindowParams_add);
        } catch (Exception e) {
            Log.d(TAG+" Exception", "addView");
            e.printStackTrace();
        }

    }

    private OnTouchListener mViewAdd_OnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (mWindowParams_add == null) return false;

            mWindowParams_add.x = (int) event.getRawX();
            mWindowParams_add.y = (int) event.getRawY() - SizeY / 2;

            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                try {
                    mWindowManager.updateViewLayout(view, mWindowParams_add);
                } catch (Exception e) {
                    Log.d("Exception", "Add_MotionEvent");
                    e.printStackTrace();
                }
            }
            return true;
        }
    };

    private OnClickListener mImagePlay_OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent autoServiceIntent = new Intent(getContext(), AutoService.class);
            if (!Play) {
                Play = true;
                mImage_play.setImageDrawable(getResources().getDrawable(R.drawable.stop, null));

                autoServiceIntent.putExtra(AutoService.ACTION, AutoService.PLAY);
                getContext().startService(autoServiceIntent);

            } else {
                Play = false;
                mImage_play.setImageDrawable(getResources().getDrawable(R.drawable.play, null));

                autoServiceIntent.putExtra(AutoService.ACTION, AutoService.STOP);
                getContext().startService(autoServiceIntent);

            }
        }
    };
}
