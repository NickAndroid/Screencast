package dev.nick.app.screencast.camera;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
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
import dev.nick.logger.LoggerManager;

public class CameraPreviewService extends Service {

    private View mFloatView;
    private WindowManager windowManager;
    private LayoutParams mFloatContainerParams;
    private ViewGroup mFloatViewContainer;

    private WindowSize mSize;

    private ServiceBinder mBinder;

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
        if (mBinder == null) mBinder = new ServiceBinder();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSize = new WindowSize(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void showPreview() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (mFloatView != null) {
            windowManager.removeView(mFloatView);
        }
        mFloatView = new SoftwareCameraPreview(this);
        mFloatContainerParams = new LayoutParams(
                mSize.w,
                mSize.h,
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

    private void hidePreview() {

    }

    public void setSize(WindowSize size) {
        this.mSize = size;
        mFloatContainerParams.width = size.w;
        mFloatContainerParams.height = size.h;
        windowManager.updateViewLayout(mFloatViewContainer,
                mFloatContainerParams);
    }

    @Override
    public void onDestroy() {
        if (mFloatView != null) {
            windowManager.removeView(mFloatView);
        }
        super.onDestroy();
    }

    public static class WindowSize {
        int w, h;

        public WindowSize(int w, int h) {
            this.w = w;
            this.h = h;
        }
    }

    class ServiceBinder extends Binder implements ICameraPreviewService {

        @Override
        public void show() {
            CameraPreviewService.this.showPreview();
        }

        @Override
        public void hide() {
            CameraPreviewService.this.hidePreview();
        }
    }
}
