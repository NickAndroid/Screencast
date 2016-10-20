/*
 * Copyright (C) 2013 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nick.app.screencast.ui;

import android.Manifest;
import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import dev.nick.app.screencast.IScreencaster;
import dev.nick.app.screencast.R;
import dev.nick.app.screencast.ScreencastService;
import dev.nick.app.screencast.ui.window.FloatWindowService;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1;

    private static final String TAG = "MediaProjectionDemo";

    private IScreencaster mCaster;
    private ImageButton mScreencastButton;
    private TextView mText;
    private TextView mAudioText;
    private CheckBox mChkWithAudio;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mCaster = (IScreencaster) iBinder;
            mScreencastButton.setEnabled(true);
            refreshState();
            MainActivityPermissionsDispatcher.startPreviewWithCheck(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mCaster = null;
        }
    };
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        mScreencastButton = (ImageButton) findViewById(R.id.screencast);
        mText = (TextView) findViewById(R.id.hint);
        mAudioText = (TextView) findViewById(R.id.audio_warning);
        mChkWithAudio = (CheckBox) findViewById(R.id.with_audio);
        mScreencastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB(v);
                if (mCaster.isCasting()) {
                    stopRecording();
                } else {
                    MainActivityPermissionsDispatcher.startRecordingWithCheck(MainActivity.this);
                }
            }
        });
        mScreencastButton.setEnabled(false);
        bindCaster();
    }

    void bindCaster() {
        Intent intent = new Intent(this, ScreencastService.class);
        startService(intent);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void startPreview() {
        startService(new Intent(this, FloatWindowService.class));
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void startRecording() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(),
                    PERMISSION_CODE);
            return;
        }
        mCaster.setProjection(mMediaProjection);
        mCaster.start(mChkWithAudio.isChecked());
        finish();
    }

    void stopRecording() {
        mCaster.stop();
        refreshState();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "User denied screen sharing permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(new MediaProjectionCallback(), null);
        startRecording();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        refreshState();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onNoStoragePermission() {
        finish();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onNoAudioPermission() {
        mChkWithAudio.setChecked(false);
        mChkWithAudio.setEnabled(false);
    }

    private void refreshState() {
        if (mCaster == null) return;
        final boolean recording = mCaster.isCasting();
        Log.d(TAG, "refreshState, recording=" + recording);
        if (recording) {
            mScreencastButton.setImageResource(R.drawable.stop);
            mText.setText(R.string.stop_description);
            mAudioText.setVisibility(View.GONE);
            mChkWithAudio.setVisibility(View.GONE);
        } else {
            mScreencastButton.setImageResource(R.drawable.record);
            mText.setText(R.string.start_description);

            mChkWithAudio.setChecked(true);
            mChkWithAudio.setVisibility(View.VISIBLE);
            mChkWithAudio.setOnCheckedChangeListener(null);
            mAudioText.setVisibility(View.GONE);
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    private void animateFAB(View view) {
        int centerX = (view.getLeft() + view.getRight()) / 2;
        int centerY = (view.getTop() + view.getBottom()) / 2;
        int startRadius = 0;
        int endRadius = (int) Math.hypot(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(
                view, centerX, centerY, startRadius, endRadius);

        anim.setDuration(800);
        anim.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaProjection = null;
            mCaster.stop();
            refreshState();
        }
    }
}
