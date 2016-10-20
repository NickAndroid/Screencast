package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.app.screencast.R;
import dev.nick.tiles.tile.DateTileView;
import dev.nick.tiles.tile.TileListener;

public class DataTimeTile extends HeadlessTile {
    public DataTimeTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_share;
        this.tileView = new DateTileView(context);
    }
}
