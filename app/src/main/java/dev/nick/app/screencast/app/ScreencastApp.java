package dev.nick.app.screencast.app;

import android.app.Application;
import android.util.Log;

import dev.nick.app.screencast.Factory;
import dev.nick.logger.LoggerManager;

public class ScreencastApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(Factory.get());
        Factory.get().onApplicationCreate(this);
        LoggerManager.setDebugLevel(Log.VERBOSE);
        LoggerManager.setTagPrefix(ScreencastApp.class.getSimpleName());
    }
}
