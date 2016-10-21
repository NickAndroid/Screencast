package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import dev.nick.app.screencast.R;
import dev.nick.app.screencast.provider.SettingsProvider;
import dev.nick.tiles.tile.DropDownTileView;
import dev.nick.tiles.tile.TileListener;

public class PreviewSizeDropdownTile extends HeadlessTile {

    public PreviewSizeDropdownTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_photo_size_select_small_black_24dp;
        this.title = "Camera size";
        this.summary = SettingsProvider.get().previewSize().name();
        this.tileView = new DropDownTileView(context) {
            @Override
            protected List<String> onCreateDropDownList() {
                return Arrays.asList("Large", "Small", "Middle");
            }
        };
    }
}