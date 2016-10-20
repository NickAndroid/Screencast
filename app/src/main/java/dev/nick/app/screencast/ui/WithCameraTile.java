package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.app.screencast.R;
import dev.nick.tiles.tile.SwitchTileView;
import dev.nick.tiles.tile.TileListener;

public class WithCameraTile extends HeadlessTile {
    public WithCameraTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_camera_alt_black_24dp;
        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
            }
        };
        this.title = "Show camera";
    }
}
