package dev.nick.app.screencast.provider;

import android.preference.PreferenceManager;

import dev.nick.app.screencast.Factory;
import dev.nick.app.screencast.camera.PreviewSize;

public abstract class SettingsProvider {

    static Impl sImpl;

    public static synchronized SettingsProvider get() {
        if (sImpl == null) sImpl = new Impl();
        return sImpl;
    }

    public abstract boolean withAudio();

    public abstract void setWithAudio(boolean value);

    public abstract boolean withCamera();

    public abstract void setWithCamera(boolean value);

    public abstract PreviewSize previewSize();

    public abstract void setPreviewSize(PreviewSize size);

    static class Impl extends SettingsProvider {

        private static final String KEY_WITH_AUDIO = "settings.with.audio";
        private static final String KEY_WITH_CAMERA = "settings.with.camera";
        private static final String KEY_PREVIEW_SIZE = "settings.preview.size";

        @Override
        public boolean withAudio() {
            return PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .getBoolean(KEY_WITH_AUDIO, false);
        }

        @Override
        public void setWithAudio(boolean value) {
            PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .edit().putBoolean(KEY_WITH_AUDIO, value).apply();
        }

        @Override
        public boolean withCamera() {
            return PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .getBoolean(KEY_WITH_CAMERA, false);
        }

        @Override
        public void setWithCamera(boolean value) {
            PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .edit().putBoolean(KEY_WITH_CAMERA, value).apply();
        }

        @Override
        public PreviewSize previewSize() {
            return PreviewSize.valueOf(PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .getString(KEY_PREVIEW_SIZE, PreviewSize.Small.name()));
        }

        @Override
        public void setPreviewSize(PreviewSize size) {
            PreferenceManager.getDefaultSharedPreferences(Factory.get().getApplicationContext())
                    .edit().putString(KEY_PREVIEW_SIZE, size.name()).apply();
        }
    }

}
