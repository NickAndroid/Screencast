package dev.nick.app.screencast;

import android.media.projection.MediaProjection;

public interface IScreencaster {
    boolean start(boolean withAudio);
    void stop();
    boolean isCasting();
    void setProjection(MediaProjection projection);
}
