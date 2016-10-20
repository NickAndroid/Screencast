package dev.nick.app.screencast.ui.window;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import dev.nick.app.screencast.R;
import dev.nick.app.screencast.camera.SoftwareCameraPreview;
import dev.nick.logger.LoggerManager;

public class FloatWindowService extends Service {

    private View mFloatView;
    private WindowManager windowManager;
    private LayoutParams mFloatContainerParams;
    private ViewGroup mFloatViewContainer;
    private OnTouchListener mFloatViewTouchListener = new OnTouchListener() {

        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LoggerManager.getLogger(getClass()).debug("ACTION_DOWN");
                    initialX = mFloatContainerParams.x;
                    initialY = mFloatContainerParams.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    return true;
                case MotionEvent.ACTION_UP:
                    LoggerManager.getLogger(getClass()).debug("ACTION_UP");
                    windowManager.updateViewLayout(mFloatViewContainer,
                            mFloatContainerParams);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    LoggerManager.getLogger(getClass()).debug("ACTION_MOVE");
                    int diffX = (int) (event.getRawX() - initialTouchX);
                    int diffY = (int) (event.getRawY() - initialTouchY);
                    mFloatContainerParams.x = initialX + diffX;
                    mFloatContainerParams.y = initialY + diffY;
                    windowManager.updateViewLayout(mFloatViewContainer,
                            mFloatContainerParams);
                    return true;
            }
            return false;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mFloatView != null) {
            windowManager.removeView(mFloatView);
        }
        mFloatView = new SoftwareCameraPreview(this);
        mFloatContainerParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_TOAST,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mFloatContainerParams.y = 0;
        mFloatContainerParams.x = 0;
        mFloatViewContainer = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.float_containor, null);
        mFloatViewContainer.setOnTouchListener(mFloatViewTouchListener);
        mFloatViewContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        windowManager.addView(mFloatViewContainer, mFloatContainerParams);
        mFloatViewContainer.addView(mFloatView);
    }

    @Override
    public void onDestroy() {
        if (mFloatView != null) {
            windowManager.removeView(mFloatView);
        }
        super.onDestroy();
    }
}
