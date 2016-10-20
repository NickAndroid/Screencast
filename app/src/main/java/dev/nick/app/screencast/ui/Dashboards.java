package dev.nick.app.screencast.ui;

import android.support.annotation.NonNull;

import java.util.List;

import dev.nick.tiles.tile.Category;
import dev.nick.tiles.tile.DashboardFragment;
import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.TileListener;

public class Dashboards extends DashboardFragment implements TileListener {

    @Override
    protected void onCreateDashCategories(List<Category> categories) {
        super.onCreateDashCategories(categories);
        Category category = new Category();
        category.addTile(new WithAudioTile(getContext(), this));
        category.addTile(new ResolutionsDropdownTile(getContext(), this));
        categories.add(category);
    }

    @Override
    public void onTileClick(@NonNull QuickTile tile) {
        // Nothing.
    }
}

