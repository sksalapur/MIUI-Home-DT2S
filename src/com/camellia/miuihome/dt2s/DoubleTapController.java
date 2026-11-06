package com.camellia.miuihome.dt2s;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class DoubleTapController {
    private static final long MAX_DURATION_MS = 380;
    private float mActionDownRawX = 0f;
    private float mActionDownRawY = 0f;
    private int mClickCount = 0;
    private float mFirstClickRawX = 0f;
    private float mFirstClickRawY = 0f;
    private long mLastClickTime = 0;
    private final int mTouchSlop;

    public DoubleTapController(Context context) {
        int slop = 16;
        if (context != null) {
            try {
                slop = ViewConfiguration.get(context).getScaledTouchSlop();
            } catch (Throwable ignored) {}
        }
        this.mTouchSlop = slop * 2;
    }

    public boolean isDoubleTapEvent(MotionEvent event) {
        if (event == null) return false;
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mActionDownRawX = event.getRawX();
            mActionDownRawY = event.getRawY();
            return false;
        } else if (action != MotionEvent.ACTION_UP) {
            return false;
        }

        float rawX = event.getRawX();
        float rawY = event.getRawY();

        if (Math.abs(rawX - mActionDownRawX) <= mTouchSlop && Math.abs(rawY - mActionDownRawY) <= mTouchSlop) {
            long now = SystemClock.elapsedRealtime();
            if (now - mLastClickTime > MAX_DURATION_MS ||
                Math.abs(rawX - mFirstClickRawX) > mTouchSlop ||
                Math.abs(rawY - mFirstClickRawY) > mTouchSlop) {
                mClickCount = 0;
            }

            mClickCount++;
            if (mClickCount == 1) {
                mFirstClickRawX = rawX;
                mFirstClickRawY = rawY;
                mLastClickTime = now;
                return false;
            } else if (mClickCount >= 2) {
                if (Math.abs(rawX - mFirstClickRawX) <= mTouchSlop &&
                    Math.abs(rawY - mFirstClickRawY) <= mTouchSlop &&
                    (now - mLastClickTime) <= MAX_DURATION_MS) {
                    mClickCount = 0;
                    return true;
                }
            }
        }

        mClickCount = 0;
        return false;
    }
}
