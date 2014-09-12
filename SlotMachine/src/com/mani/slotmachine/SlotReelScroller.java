package com.mani.slotmachine;

import android.content.Context;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * Responsible for generating scroll values based on a distance & time given.
 * Uses 'Scroller' class to generate the scroll values.
 * 
 * @author maniselvaraj
 *
 */
public class SlotReelScroller implements Runnable {

    /**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * @param the distance to scroll
         */
        void onScroll(int distance);

        /**
         * Finishing callback.
         */
        void onFinished();        
    }

    private Handler mHandler;
    private Scroller mScroller;
    private ScrollingListener mScrollListener;
    int lastY = 0;
    private int distance;
    private int previousDistance;
    
    public SlotReelScroller(Context context, ScrollingListener listener) {
    	mHandler = new Handler();
    	mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
    	//mScroller = new Scroller(context, new AccelerateInterpolator());
    	//mScroller = new Scroller(context);
    	mScrollListener = listener;
    }
    
    public void scroll(int distance, int duration) {
    	this.distance = distance;
        mScroller.forceFinished(true);
    	mScroller.startScroll(0, 0, 0, distance, duration);
    	mHandler.post(this);
    }
    
    public void run() {		
		int delta = 0;
		mScroller.computeScrollOffset();
		int currY = mScroller.getCurrY();		
		
		delta = currY - lastY;
		lastY = currY;	
		
		if (Math.abs(delta) != previousDistance && delta != 0) {					  
			mScrollListener.onScroll(delta);
		}			
		
		if (mScroller.isFinished() == false) {
			//Post this runnable again on UI thread until all scroll values are read.
			mHandler.post(this);
		} else {
			previousDistance = distance;
			mScrollListener.onFinished();
		}
    }
}
