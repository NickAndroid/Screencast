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
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import dev.nick.app.screencast.IScreencaster;
import dev.nick.app.screencast.R;
import dev.nick.app.screencast.ScreencastServiceProxy;
import dev.nick.app.screencast.camera.CameraPreviewServiceProxy;
import dev.nick.app.screencast.provider.SettingsProvider;
import dev.nick.logger.LoggerManager;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ScreenCastActivity extends TransactionSafeActivity {

    private static final int PERMISSION_CODE = 1;

    private MediaProjection mMediaProjection;
    private MediaProjectionManager mProjectionManager;

    private FloatingActionButton mFab;

    private boolean mIsCasting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        placeFragment(R.id.container, new Dashboards(), null, false);

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsCasting) {
                    stopRecording();
                } else {
                    ScreenCastActivityPermissionsDispatcher.startRecordingWithCheck(ScreenCastActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ScreencastServiceProxy.watch(getApplicationContext(), new IScreencaster.ICastWatcher() {
            @Override
            public void onStartCasting() {
                refreshState(true);
                if (SettingsProvider.get().withCamera()) {
                    CameraPreviewServiceProxy.show(getApplicationContext());
                }
                finish();
            }

            @Override
            public void onStopCasting() {
                refreshState(false);
            }
        });
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void startRecording() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(),
                    PERMISSION_CODE);
            return;
        }
        ScreencastServiceProxy.start(getApplicationContext(), mMediaProjection, SettingsProvider.get().withAudio());
    }

    private void stopRecording() {
        ScreencastServiceProxy.stop(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ScreenCastActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PERMISSION_CODE) {
            LoggerManager.getLogger(getClass()).error("Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "User denied screen sharing permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                onProjectionStop();
            }
        }, null);
        startRecording();
    }

    private void onProjectionStop() {
        mMediaProjection = null;
        ScreencastServiceProxy.stop(getApplicationContext());
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onNoStoragePermission() {
        finish();
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onNoAudioPermission() {
        SettingsProvider.get().setWithAudio(false);
    }

    private void refreshState(final boolean isCasting) {
        mIsCasting = isCasting;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isCasting) {
                    mFab.setImageResource(R.drawable.stop);
                } else {
                    mFab.setImageResource(R.drawable.record);
                }
            }
        });
    }
}
