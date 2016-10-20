package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import dev.nick.app.screencast.R;
import dev.nick.app.screencast.ValidResolutions;
import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.TileListener;

public class ResolutionsDropdownTile extends HeadlessTile {

    public ResolutionsDropdownTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_collections_bookmark_black_24dp;
        this.title = "Resolution";
        this.summary = "1080x720";
        this.tileView = new DropDownTileView(context) {
            @Override
            protected List<String> onCreateDropDownList() {
                return ValidResolutions.string();
            }
        };
    }
}