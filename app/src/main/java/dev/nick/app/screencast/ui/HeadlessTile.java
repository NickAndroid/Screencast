package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.app.screencast.R;
import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import dev.nick.tiles.tile.TileListener;

public class HeadlessTile extends QuickTile {
    public HeadlessTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.title = getClass().getSimpleName();
        this.iconRes = R.drawable.ic_share;
        this.tileView = new QuickTileView(context, this);
    }
}
