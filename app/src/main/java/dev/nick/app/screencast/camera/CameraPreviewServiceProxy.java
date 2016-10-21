package dev.nick.app.screencast.camera;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import dev.nick.app.screencast.ServiceProxy;

public class CameraPreviewServiceProxy extends ServiceProxy implements ICameraPreviewService {

    ICameraPreviewService mService;

    private CameraPreviewServiceProxy(Context context) {
        super(context, new Intent(context, CameraPreviewService.class));
        context.startService(new Intent(context, CameraPreviewService.class));
    }

    public static void show(final Context context) {
        ThreadUtil.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                new CameraPreviewServiceProxy(context).show();
            }
        });
    }

    public static void hide(final Context context) {
        ThreadUtil.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                new CameraPreviewServiceProxy(context).hide();
            }
        });
    }

    @Override
    public void show() {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                mService.show();
            }
        });
    }

    @Override
    public void hide() {
        setTask(new ProxyTask() {
            @Override
            public void run() throws RemoteException {
                mService.hide();
            }
        });
    }

    @Override
    public void onConnected(IBinder binder) {
        mService = (ICameraPreviewService) binder;
    }
}
