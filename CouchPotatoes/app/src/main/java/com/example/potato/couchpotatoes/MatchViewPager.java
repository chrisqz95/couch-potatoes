package com.example.potato.couchpotatoes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager to disable swiping between tabs due to swiping being used for cards within the fragment.
 * https://stackoverflow.com/questions/42196030/android-how-to-disable-swiping-between-tablayout-control-by-button-click
 */
public class MatchViewPager extends android.support.v4.view.ViewPager {

    private boolean enableSwipe;

    public MatchViewPager(Context context) {
        super(context);
        init();
    }

    public MatchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        enableSwipe = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return enableSwipe && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return enableSwipe && super.onTouchEvent(event);
    }

    /**
     * Set the ability to swipe to change tabs
     * @param enableSwipe
     */
    public void setEnableSwipe(boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
    }
}
