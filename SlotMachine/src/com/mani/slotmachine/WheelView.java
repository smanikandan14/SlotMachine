package com.mani.slotmachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.View;

/**
 * Responsible for drawing a slot machine wheel.
 * @author maniselvaraj
 *
 */
public class WheelView extends View implements SlotReelScroller.ScrollingListener {
	
	Paint mWhiteBackgroundPaint;
	
	/** Top and bottom shadows colors */
	private int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };

	/** To bring frame effect around wheel view give left and right padding value */
	private static final int FRAME_PADDING = 10;
	
	/** Slot item height **/
	int itemHeight;
	
	/** Offset X to start draw the slot item.Used to avoid drawing on frame */
	int itemX;

	int mViewFullWidth;
	int mViewFullHeight;
	int mViewWidth;
		
	//List of slot items this wheel holds.
	List<DrawSlotItem> mSlotItems;
		
	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	//Background frame drawable
	private Drawable wheelFrame;
	
	private Rect mFullViewRect;
	
	//Scroller
	private SlotReelScroller mReelScroller;
	
	//Default visible slot items is set to 1.
	private int visibleSlotItems = 1;
	
	//Determines the scroll direction 
	private boolean scrollDown = false;
	
	//Allows to check whether to middle the slot items after a scroll.
	private boolean checkForMiddling = true;
	
	/**
	 * Listener which informs the parent the position of slot item which
	 * is middled in center after a scroll. 
	 */
	interface OnScrollFinishedListener {
		void onWheelFinishedScrolling(int position);
	}
	
	private OnScrollFinishedListener mScrollFinishedListener;
	
	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		init(context);
	}
	
	void init(Context context) {
		
		mFullViewRect = new Rect();
		
		mReelScroller = new SlotReelScroller(context, this);
		
		mWhiteBackgroundPaint = new Paint();
		mWhiteBackgroundPaint.setColor(Color.WHITE);

		topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		
		mSlotItems = new ArrayList<DrawSlotItem>();		
	}
	
	public void setSlotItems(List<ISlotMachineItem> items) {
		fillSlotDrawItems(items);
	}
	
	void fillSlotDrawItems(List<ISlotMachineItem> slotItems)  {
		mSlotItems.clear();
		int position = 1;
		for (ISlotMachineItem item: slotItems) {			
			mSlotItems.add(new DrawSlotItem(item, position));	
			position++;
		}
	}

	public void setNumberOfVisibleItems(int visible) {
		visibleSlotItems = visible;
	}
	
	public void scroll(int distance, int duration) {		
		if (distance != 0 ) {
			checkForMiddling = true;
			mReelScroller.scroll(distance, duration);
		}
	}
	
	public void setScrollFinishedListener(OnScrollFinishedListener listener) {
		mScrollFinishedListener = listener;
	}
	
	/**
	 * Sets the drawable for the wheel background
	 * @param resource
	 */
	public void setWheelBackground(Drawable resource) {
		wheelFrame = resource;
	}

	public void setWheelScrollingDirection(boolean scrollDown) {
		this.scrollDown = scrollDown;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//Full View width
		mViewFullWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		mViewFullHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		
		//Width excluding frames
		mViewWidth = mViewFullWidth - (2 * FRAME_PADDING);
		int viewHeight = mViewFullHeight- (2 * FRAME_PADDING);
		itemHeight = viewHeight / visibleSlotItems;
		
		setCorrectVisibleItems();
		resetSlotItemsPositions(scrollDown);
		
		itemX = FRAME_PADDING;
		
    	mFullViewRect.top = FRAME_PADDING;
    	mFullViewRect.left = FRAME_PADDING;
    	mFullViewRect.right = mViewWidth + FRAME_PADDING;
    	mFullViewRect.bottom = mViewFullHeight - FRAME_PADDING;    	

    	setMeasuredDimension(mViewFullWidth, mViewFullHeight);
	}

    /**
     *  Sets correct number of visible mSlotItems.
     */
    void setCorrectVisibleItems() {
    	if (visibleSlotItems == 0  || visibleSlotItems == mSlotItems.size()) {
    		visibleSlotItems = mSlotItems.size()-1;
    	}
    }
    
	public void onDraw(Canvas canvas) {

		//Draw the frame background
		drawFrame(canvas);

		//Draw the white background
		canvas.drawRect(mFullViewRect, mWhiteBackgroundPaint);
		
		//Draw all the slot items.
		drawSlotItems(canvas);	
		
		//Draw the top and bottom shadows
		drawShadows(canvas);
	}
	
	private void drawSlotItems(Canvas canvas) {
		
		for (DrawSlotItem item: mSlotItems) {
			View view = item.getView();
			
			//Measure the view at the exact dimensions.
	        int widthSpec = View.MeasureSpec.makeMeasureSpec(mViewWidth, View.MeasureSpec.EXACTLY);
	        int heightSpec = View.MeasureSpec.makeMeasureSpec(itemHeight, View.MeasureSpec.EXACTLY);
	        view.measure(widthSpec, heightSpec);

	        //Lay the view out at the rect width and height
	        view.layout(itemX, item.getY() + FRAME_PADDING, mViewWidth, item.getY()+itemHeight);

	        //Translate the Canvas into position and draw it
	        canvas.save();
	        canvas.translate(itemX, item.getY()+FRAME_PADDING);
	        view.draw(canvas);
	        canvas.restore();
		}		
	}
	
	/**
	 * Draw the frame around the slot items.
	 * @param canvas
	 */
	private void drawFrame(Canvas canvas) {
		if (wheelFrame != null) {
			wheelFrame.setBounds(0, 0, mViewFullWidth, mViewFullHeight);
			wheelFrame.draw(canvas);
		}
	}
	
	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
		int height = (int)(1.0 * itemHeight);
		topShadow.setBounds(0, 0, getWidth(), height);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Helper class to hold the position Y of each slot item.
	 * The scrolling is achieved by incrementing/decrementing 
	 * the position Y of each slot items and drawn on every scroll ( delta)
	 * obtained from scroller. 
	 *
	 */
	class DrawSlotItem {
		int positionY;
		View view;
		int slotPosition;
		
		public DrawSlotItem(ISlotMachineItem item, int slotPosition) {
			view = item.getView();
			this.slotPosition = slotPosition;
		}
		
		public void setPosY(int y) {
			this.positionY = y;
		}
		
		public int getY() {
			return positionY;
		}

		public View getView() {
			return view;
		}
		
		public int getSlotPosition() {
			return slotPosition;
		}
	}
	
	public void scroll(int delta) {
		
		boolean scrollDown = delta < 0;
		//update y positions of all mSlotItems.
		for(DrawSlotItem item: mSlotItems) {
			item.setPosY(item.getY()-delta);
		}

		//Check if item1 position is out of screen.
		//Swap all the mSlotItems.Set Y = itemheight;
		int hiddingItemY;
		if (scrollDown) {
			hiddingItemY = mSlotItems.get(visibleSlotItems-1).getY();
			if (hiddingItemY >= mViewFullHeight) {
				int j = 0;
				for(int i = 1; i < mSlotItems.size(); i++) {
					Collections.swap(mSlotItems, j, i);
					j++;
				}
				resetSlotItemsPositions(scrollDown);
			}			
		} else {
			hiddingItemY = mSlotItems.get(0).getY();
			if (hiddingItemY <= -itemHeight) {
				int j = 0;
				for(int i = 1; i < mSlotItems.size(); i++) {
					Collections.swap(mSlotItems, j, i);
					j++;
				}
				resetSlotItemsPositions(scrollDown);
			}
			
		}
		
		invalidate();
	}
	
	void resetSlotItemsPositions(boolean scrollDown) {
		int multipler = scrollDown ? -1 : 0;
		for(DrawSlotItem item: mSlotItems) {			
			item.setPosY(FRAME_PADDING + itemHeight * multipler);
			multipler++;
		}
	}
	
	@Override
	public void onScroll(int distance) {
		scroll(distance);
	}

	@Override
	public void onFinished() {
		if (checkForMiddling == true) {
			positionNearestMiddleItem();
			checkForMiddling = false;
		}		
	}
	
	/**
	 * Middle the last visible item after a scroll.
	 */
	private void positionNearestMiddleItem() {		
		// Middle of view height  
		int viewCenter = (mViewFullHeight - ( 2 * FRAME_PADDING))/2;
		int distance = mSlotItems.get(visibleSlotItems).getY() - ( viewCenter - (itemHeight/2));
		scroll(distance, 1000);
		if (mScrollFinishedListener != null) {
			mScrollFinishedListener.onWheelFinishedScrolling(mSlotItems.get(visibleSlotItems).getSlotPosition());
		}
 	}
}
