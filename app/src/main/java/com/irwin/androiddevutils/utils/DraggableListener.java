package com.irwin.androiddevutils.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * A TouchListener implementation to make view dragging on screen easier.</p>
 * Just create a new instance of {@link DraggableListener} and call <code>setOnTouchListener()</code> on view to make your view draggable.
 */

public class DraggableListener implements View.OnTouchListener {
    private View mView;
    private int mTouchX;
    private int mTouchY;
    private int mMoveSlot;
    private int mTransitionX;
    private int mTransitionY;
    private Rect mLayoutRect;
    private Rect mRangeRect = new Rect();

    /**
     * @param view View to drag.
     */
    public DraggableListener(View view) {
        mView = view;
        mMoveSlot = 20;
        checkRect(view);
    }

    private void checkRect(View view) {
        if (mLayoutRect != null || view.getWidth() <= 0) {
            return;
        }
        //Save initial rect of view.
        mLayoutRect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        View parent = (View) view.getParent();

        //Record region range in which view can be drag around.
        mRangeRect.left = parent.getLeft() + parent.getPaddingLeft();
        mRangeRect.top = parent.getTop() + parent.getPaddingTop();
        mRangeRect.right = parent.getRight() - parent.getPaddingRight();
        mRangeRect.bottom = parent.getBottom() - parent.getPaddingBottom();
    }

    /**
     * Set move slot which will affect click behavior of view.
     *
     * @param slot Distance in px.
     * @return
     */
    public DraggableListener setMoveSlot(int slot) {
        mMoveSlot = slot;
        return this;
    }


    private final void move(int eventX, int eventY, int offsetX, int offsetY) {
        Rect layoutRect = mLayoutRect;
        Rect rangeRect = mRangeRect;
        if (layoutRect.left + offsetX < rangeRect.left || layoutRect.right + offsetX > rangeRect.right) {
            offsetX = 0;
        }
        if (layoutRect.top + offsetY < rangeRect.top || layoutRect.bottom + offsetY > rangeRect.bottom) {
            offsetY = 0;
        }
        int l = layoutRect.left + offsetX;
        int t = layoutRect.top + offsetY;
        int r = layoutRect.right + offsetX;
        int b = layoutRect.bottom + offsetY;
        mView.layout(l, t, r, b);
        Log.i("Drag", "Drag(" + offsetX + "," + offsetY + ")");
        if (offsetX != 0 || offsetY != 0) {
            mTransitionX += Math.abs(offsetX);
            mTransitionY += Math.abs(offsetY);
            mTouchX = eventX;
            mTouchY = eventY;
            layoutRect.set(l, t, r, b);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (event.getRawX() - mTouchX);
                int offsetY = (int) (event.getRawY() - mTouchY);
                move((int) event.getRawX(), (int) event.getRawY(), offsetX, offsetY);
                return true;
            case MotionEvent.ACTION_DOWN:
                mTouchX = (int) event.getRawX();
                mTouchY = (int) event.getRawY();
                //Reset transition distance.
                checkRect(v);
                mTransitionX = 0;
                mTransitionY = 0;
                return true;
            case MotionEvent.ACTION_UP:
                //If transit more than move slot, take it as drag event, click event otherwise.
                if (mTransitionX >= mMoveSlot || mTransitionY >= mMoveSlot) {
                    //Update last rect.
                    mLayoutRect.set(mView.getLeft(), mView.getTop(), mView.getRight(), mView.getBottom());
                    return true;
                }
        }
        return false;
    }
}
