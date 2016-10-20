package dev.nick.app.screencast.provider;

import dev.nick.app.screencast.camera.PreviewSize;

public abstract class SettingsProvider {

    static Impl sImpl;

    public static synchronized SettingsProvider get() {
        if (sImpl == null) sImpl = new Impl();
        return sImpl;
    }

    public abstract boolean withAudio();

    public abstract boolean withCamera();

    public abstract PreviewSize previewSize();

    static class Impl extends SettingsProvider {

        @Override
        public boolean withAudio() {
            return false;
        }

        @Override
        public boolean withCamera() {
            return false;
        }

        @Override
        public PreviewSize previewSize() {
            return PreviewSize.Small;
        }
    }

}
