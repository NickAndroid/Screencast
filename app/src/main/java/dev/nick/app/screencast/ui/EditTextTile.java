package dev.nick.app.screencast.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import dev.nick.app.screencast.R;
import dev.nick.tiles.tile.EditTextTileView;
import dev.nick.tiles.tile.TileListener;

public class EditTextTile extends HeadlessTile {
    public EditTextTile(@NonNull Context context, TileListener listener) {
        super(context, listener);
        this.iconRes = R.drawable.ic_share;
        this.tileView = new EditTextTileView(context) {
            @Override
            protected void onPositiveButtonClick() {
                super.onPositiveButtonClick();
                title = getEditText().getText().toString();
                getTitleTextView().setText(title);
            }
        };
    }
}
