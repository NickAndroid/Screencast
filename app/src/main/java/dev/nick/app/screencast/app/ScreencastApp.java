package dev.nick.app.screencast.app;

import android.app.Application;

import dev.nick.app.screencast.Factory;

public class ScreencastApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(Factory.get());
        Factory.get().onApplicationCreate(this);
    }
}
