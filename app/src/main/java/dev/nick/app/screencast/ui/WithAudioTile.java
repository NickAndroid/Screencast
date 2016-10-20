package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.app.screencast.R;
import dev.nick.tiles.tile.SwitchTileView;
import dev.nick.tiles.tile.TileListener;

public class WithAudioTile extends HeadlessTile {
    public WithAudioTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_mic_black_24dp;
        this.tileView = new SwitchTileView(context) {
            @Override
            protected void onCheckChanged(boolean checked) {
                super.onCheckChanged(checked);
            }
        };
        this.title = "With audio";
    }
}
