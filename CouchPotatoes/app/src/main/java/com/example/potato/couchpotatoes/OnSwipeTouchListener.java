package com.example.potato.couchpotatoes;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Handles the swipe right and left gestures.
 * Source: https://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures/19506010#19506010
 */

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    /**
     * Handles the swipe left gesture
     */
    public void onSwipeLeft() {

    }

    /**
     * Handles the swipe right gesture
     */
    public void onSwipeRight() {

    }

    /**
     * Handles the click gesture
     */
    public void onClick() {

    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * When the view or layout is clicked
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onClick();
            return super.onSingleTapUp(e);
        }

        /**
         * Determines if a swipe passes the threshold
         *
         * @param e1
         * @param e2
         * @param velocityX
         * @param velocityY
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                // Swipe right
                if (distanceX > 0) {
                    onSwipeRight();
                }
                // Swipe left
                else {
                    onSwipeLeft();
                }
                return true;
            }
            return false;
        }
    }
}
