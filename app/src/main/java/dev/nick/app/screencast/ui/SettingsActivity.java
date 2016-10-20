package dev.nick.app.screencast.ui;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;

import dev.nick.app.screencast.R;

public class SettingsActivity extends TransactionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        placeFragment(R.id.container, new Dashboards(), null, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB(view);
            }
        });
    }

    private void animateFAB(View view) {
        int centerX = (view.getLeft() + view.getRight()) / 2;
        int centerY = (view.getTop() + view.getBottom()) / 2;
        int startRadius = 0;
        int endRadius = (int) Math.hypot(view.getWidth(), view.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(
                view, centerX, centerY, startRadius, endRadius);

        anim.setDuration(800);
        anim.start();
    }
}
