package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import dev.nick.app.screencast.R;
import dev.nick.app.screencast.camera.CameraManager;
import dev.nick.app.screencast.camera.ThreadUtil;
import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.nick.tiles.tile.TileListener;

public class SwitchCameraTile extends QuickTile {
    public SwitchCameraTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.title = "Switch camera";
        this.iconRes = R.drawable.ic_camera_front_black_24dp;
        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                ThreadUtil.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        CameraManager.get().swapCamera();
                    }
                });
            }
        };
    }
}
