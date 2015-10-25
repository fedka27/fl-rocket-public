package wash.rocket.xor.rocketwash.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import wash.rocket.xor.rocketwash.R;


public class CalendarScrollWidget extends ViewGroup implements View.OnClickListener, NestedScrollingParent, NestedScrollingChild {
    private final String TAG = "CalendarScrollWidget";

    private static final int INVALID_POINTER = -1;
    private static final int DEFAULT_PAGES_COUNT = 3;
    private static final int FIRST_PAGE = 0;

    private static final int MAX_SETTLE_DURATION = 600; // ms
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int PADDING_ARRAYS = 3; // px
    private static int mActivePointerId = INVALID_POINTER;
    public static final int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the pager is currently being dragged by the user.
     */
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_DRAGGING_VERTICAL = 3;

    /**
     * Indicates that the pager is in the process of settling to a final position.
     */
    public static final int SCROLL_STATE_SETTLING = 2;
    public static final int SCROLL_STATE_SETTLING_VERTICAL = 4;

    public static final int OVERSCROLL_SIZE = 30;
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;

    private boolean mIsBeingDragged = false;
    private boolean mIsBeingDraggedVertical = false;

    private float mLastMotionX;
    private float mLastMotionY;

    private int mTouchSlop;
    private float mInitialMotionX;
    private float mInitialMotionY;

    private boolean mScrolling;
    private boolean mFling;
    private int mScrollState;
    private Scroller mScroller;
    private int mCurItem;
    private IOnPageChanged mOnPageChanged;

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    // Дни недели..
    private List<View> mWeekDaysContainerItems = new ArrayList<>();
    // Кнопки
    private List<View> mButtonsContainerItems = new ArrayList<>();
    // Время

    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private EdgeEffectCompat mTopEdge;
    private EdgeEffectCompat mBottomEdge;

    private VelocityTracker mVelocityTracker;
    private int mPageMargin;
    boolean mScrollingCacheEnabled;

    private int mRowCount;
    //private int mSelectedRow = -1;
    private int mColumnCount;
    private float mColumnWidth;
    private float mRowDayHeight;
    private float mRowHeight;
    private float mRowDivider;
    private int mPageCount = DEFAULT_PAGES_COUNT;

    private Time mStart_time;
    private int mInterval;

    private Context mcontext;

    private int mVisiblePageCount = 1;
    private int mFirstVisiblePage = 1;
    private int mFirstVisibleColumn = 0;
    private int mFlingDistance;

    private boolean mSnapToRow = true;

    // для месяца,
    private Date mCurrentDate;

    private OnClickStateButtonListenner mOnClickStateButtonListenner;
    private OnChangeListenner mOnChangeListenner;
    private OnColumnChangeListenner mOnColumnChangeListenner;

    private Handler mHandler = new Handler();
    // через сколько секунд рефрешим, если часто будут листать то не будем вызывать
    // метод обновления.
    private static final int REFRESH_DELAY = 300;
    private Runnable mRunnable;

    private IOnRefreshButtons mIOnRefreshButtons;
    private List<View> mPages;

    private boolean changed = false;

    public CalendarScrollWidget(Context context) {
        super(context);
        init(context);
    }

    public CalendarScrollWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarScrollWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mcontext = context;
        mScroller = new Scroller(context, sInterpolator);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration) * 2;
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);

        mTopEdge = new EdgeEffectCompat(context);
        mBottomEdge = new EdgeEffectCompat(context);

        mRowCount = 0;
        mColumnCount = 1;
        //mRowDayHeight = 0;
        mRowDivider = 0;
        mRowHeight = 0;

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);

        changed = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPages != null && mPages.size() > 0) {
            for (int i = 0; i < mPages.size(); i++) {
                View page = mPages.get(i);
                page.layout(i * getWidth(), 0, (i + 1) * getWidth(), b - t);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!changed)
            return;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = 0;
        int height = 0;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
            mColumnWidth = (float) getMeasuredWidth() / mColumnCount;
            //height = getMeasuredHeight();
        } else {
            //mColumnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_COLUMN_WIDTH, getResources().getDisplayMetrics());
            //width = (int) (mColumnCount * mColumnWidth);
            //height = 200;
        }

        int mheight = 300;
        //int childHeightSpec1 = MeasureSpec.makeMeasureSpec(mheight, MeasureSpec.EXACTLY);

        if (mPages != null && mPages.size() > 0) {
            int childWidthSpec = 0;
            int childHeightSpec = 0;

            for (int i = 0; i < mPages.size(); i++) {
                View page = mPages.get(i);
                childWidthSpec = MeasureSpec.makeMeasureSpec(widthMode, MeasureSpec.EXACTLY);
                childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                page.measure(widthMeasureSpec, heightMeasureSpec);
                mheight = page.getMeasuredHeight();

                changed = false;
            }
        }

        setMeasuredDimension(width, heightSize);
    }

    private void setScrollState(int newState) {
        mScrollState = newState;
    }

    /**
     * тут, с третьего раза я решил не изобретать вилосипед. а взял большую часть с обработкой касания и "тасканием" страницу у ViewPager
     * onInterceptTouchEvent , onTouchEvent  - только несколько подкорректированы.
     * в принципе гугловцы так и говорят, смотрите лучше как мы это делали
     * <p/>
     * но, лучше почитать порядок выполнения, данных методов, может пригодится.
     * onInterceptTouchEvent(), onTouchEvent(), dispatchTouchEvent()
     * ну, я бы сказал , что то схожее с веб, с верху вниз, с низу вверх.
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onMotionEvent will be called and we do the actual
		 * scrolling there.
		 */

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the drag.
            Log.d(TAG, "Intercept done!");
            mIsBeingDragged = false;
            mIsBeingDraggedVertical = false;

            mActivePointerId = INVALID_POINTER;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return false;
        }

        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                Log.d(TAG, "Intercept returning true!");
                return true;
            }
            if (mIsBeingDraggedVertical) {
                Log.d(TAG, "Intercept returning false!");
                return true;
            }

        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {

                requestDisallowParentInterceptTouchEvent(this, true);
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
				 * whether the user has moved far enough from his original down touch.
				 */

				/*
                * Locally do absolute value. mLastMotionY is set to the y value
				* of the down event.
				*/

                Log.d("calendar", "ACTION_MOVE");

                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float dy = y - mLastMotionY;
                final float yDiff = Math.abs(dy);

                Log.d(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);

                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    Log.d(TAG, "Starting drag!");
                    mIsBeingDragged = true;
                    mIsBeingDraggedVertical = false;
                    setScrollState(SCROLL_STATE_DRAGGING);
                    mLastMotionX = x;

                } else {
                    if (yDiff > mTouchSlop) {
                        setScrollState(SCROLL_STATE_DRAGGING_VERTICAL);
                        mIsBeingDragged = false;
                        mLastMotionY = y;

                        if (mRowCount * mRowHeight + mRowDivider > getHeight())
                            mIsBeingDraggedVertical = true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {

                requestDisallowParentInterceptTouchEvent(this, true);

                /*
                 * Remember location of down touch.
				 * ACTION_DOWN always refers to pointer index 0.
				 */
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                if (mScrollState == SCROLL_STATE_SETTLING) {
                    // Let the user 'catch' the pager as it animates.
                    mIsBeingDragged = true;
                    mIsBeingDraggedVertical = false;
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else if (mScrollState == SCROLL_STATE_SETTLING_VERTICAL) {
                    mIsBeingDraggedVertical = true;
                    mIsBeingDragged = false;
                    setScrollState(SCROLL_STATE_DRAGGING_VERTICAL);
                } else {
                    completeScroll();
                    mIsBeingDragged = false;
                    mIsBeingDraggedVertical = false;
                }

                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        if (!mIsBeingDragged && !mIsBeingDraggedVertical) {
            // Track the velocity as long as we aren't dragging.
            // Once we start a real drag we will track in onTouchEvent.
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(ev);
        }

		/*
         * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
        return mIsBeingDragged || mIsBeingDraggedVertical;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // Don't handle edge touches immediately -- they may actually belong to one of our
            // descendants.
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        //requestDisallowParentInterceptTouchEvent(this, true);

        final int action = ev.getAction();
        boolean needsInvalidate = false;

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {

                Log.d("calendar", "ACTION_DOWN");
                requestDisallowParentInterceptTouchEvent(this, true);

				/*
                 * If being flinged and user touches, stop the fling. isFinished
				 * will be false if being flinged.
				 */
                completeScroll();

                // Remember where the motion event started
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();

                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);

                break;
            }
            case MotionEvent.ACTION_MOVE:
                if (!mIsBeingDragged && !mIsBeingDraggedVertical) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float xDiff = Math.abs(x - mLastMotionX);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    final float yDiff = Math.abs(y - mLastMotionY);
                    Log.d(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
                    if (xDiff > mTouchSlop && xDiff > yDiff) {
                        Log.d(TAG, "Starting drag!");
                        mIsBeingDragged = true;
                        mIsBeingDraggedVertical = false;
                        mLastMotionX = x;
                        setScrollState(SCROLL_STATE_DRAGGING);
                    }

                    if (yDiff > mTouchSlop) {
                        mLastMotionY = y;
                        mIsBeingDraggedVertical = true;
                        mIsBeingDragged = false;
                        setScrollState(SCROLL_STATE_DRAGGING_VERTICAL);
                    }
                }

                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
                    final float deltaX = mLastMotionX - x;

                    mLastMotionX = x;
                    float oldScrollX = getScrollX();
                    float scrollX = oldScrollX + deltaX;

                    // Don't lose the rounded component
                    mLastMotionX += scrollX - (int) scrollX;

                    scrollTo((int) scrollX, getScrollY());

                    // Use "cached" pages...
                    //XXX
                    //columnScrolledSwap((int) scrollX);
                }

                if (mIsBeingDraggedVertical) {

                    Log.d("calendar", "mIsBeingDraggedVertical");

                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float y = MotionEventCompat.getY(ev, activePointerIndex);
                    final float deltaY = mLastMotionY - y;

                    mLastMotionY = y;
                    float oldScrollY = getScrollY();
                    float scrollY = oldScrollY + deltaY;

                    final int height = getHeight();
                    final int all_h = (int) (mRowDayHeight + (mRowHeight + mRowDivider) * mRowCount);
                    //final int all_h = (int) ((mRowHeight + mRowDivider) * mRowCount);

                    if (scrollY < 0) {
                        scrollY = 0;
                        needsInvalidate = mTopEdge.onPull(OVERSCROLL_SIZE);
                    }

                    if (scrollY > getBottomBound()) {
                        scrollY = getBottomBound();
                        needsInvalidate = mBottomEdge.onPull(-OVERSCROLL_SIZE);
                    }

                    if (all_h < height) {
                        scrollY = 0;
                    }

                    // Don't lose the rounded component
                    mLastMotionY += scrollY - (int) scrollY;
                    scrollTo(getScrollX(), (int) scrollY);
                }

                break;
            case MotionEvent.ACTION_UP:

                Log.d("calendar", "ACTION_UP");

                if (mIsBeingDragged) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);

                    final int scrollX = getScrollX();

                    final int startColumn = Math.round(scrollX / mColumnWidth);
                    final float columnOffset = ((scrollX % mColumnWidth) / mColumnWidth);

                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                    final float x = MotionEventCompat.getX(ev, activePointerIndex);

                    final int totalDelta = (int) (x - mInitialMotionX);

                    int nextColumn = determineColumnTarget(startColumn, columnOffset, initialVelocity, totalDelta);

                    setCurrentColumnInternal(nextColumn, true, true, initialVelocity);

                    mActivePointerId = INVALID_POINTER;

                    endDrag();

                    requestDisallowParentInterceptTouchEvent(this, false);
                }

                if (mIsBeingDraggedVertical) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                    int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker, mActivePointerId);

                    final int scrollY = getScrollY();
                    final int activePointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                    final int startRow = (int) Math.ceil(scrollY / (mRowHeight + mRowDivider));
                    final float rowOffset = (float) ((scrollY % (mRowHeight + mRowDivider)) / (mRowHeight + mRowDivider));

                    final float y = MotionEventCompat.getY(ev, activePointerIndex);

                    final int totalDelta = (int) (y - mInitialMotionY);

                    int nextRow = determineRowTarget(startRow, rowOffset, initialVelocity, totalDelta);

                    mActivePointerId = INVALID_POINTER;

                    final int height = getHeight();
                    //final int all_h = Math.round(mRowDayHeight + (mRowHeight + mRowDivider) * mRowCount);
                    final int all_h = Math.round((mRowHeight + mRowDivider) * mRowCount);

                    if (mSnapToRow)
                        setCurrentRowInternal(nextRow, true, true, initialVelocity);

                    if (all_h > height && !mSnapToRow)
                        flingScrollByY(scrollY, initialVelocity);

                    endDrag();
                    needsInvalidate = mTopEdge.onRelease() | mBottomEdge.onRelease();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                }
                if (mIsBeingDraggedVertical) {
                    mActivePointerId = INVALID_POINTER;
                    endDrag();
                    needsInvalidate = mTopEdge.onRelease() | mBottomEdge.onRelease();
                }

                endDrag();

                break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, index);
                final float y = MotionEventCompat.getY(ev, index);
                mLastMotionX = x;
                mLastMotionY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                mLastMotionY = MotionEventCompat.getY(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
                endDrag();
                break;
        }
        if (needsInvalidate) {
            invalidate();
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent");

        return super.dispatchTouchEvent(ev);
    }

    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {

        return;
        /*
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof ScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            else
            if (__v.getParent() instanceof NestedScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }*/
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);

            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsBeingDraggedVertical = false;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * тут тольок выводим синее свечение при оверскролле , сверху и снизу.
     */

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        boolean needsInvalidate = false;

        final int overScrollMode = ViewCompat.getOverScrollMode(this);

        if (overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS || (overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS)) {
            if (!mTopEdge.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();

                canvas.translate(getScrollX(), 50);
                mTopEdge.setSize(getWidth(), height);
                needsInvalidate |= mTopEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
            if (!mBottomEdge.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();
                canvas.rotate(180, getWidth() / 2, (mRowCount * (50)) / 2);
                canvas.translate(-getScrollX(), -50);
                mBottomEdge.setSize(getWidth(), height);
                needsInvalidate |= mBottomEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
        } else {
            mTopEdge.finish();
            mBottomEdge.finish();
        }

        if (needsInvalidate) {
            // Keep animating
            invalidate();
        }
    }


    /**
     * данный метод, будет выпонятся, когда мы работаем с хелпером - Scroller
     * например в етоде SmoothScroll
     * mScroller.startScroll(sx, sy, dx, dy, duration);
     */

    @Override
    public void computeScroll() {

        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();

                if (oldX != x || oldY != y) {
                    if (y > getBottomBound())
                        y = getBottomBound();

                    if (mRowCount * mRowHeight + mRowDivider > getHeight())
                        scrollTo(x, y);
                    else
                        scrollTo(x, oldY);

                    if (mFling && oldX != x) {
                        //columnScrolledSwap(x);
                    }
                }

                // Keep on drawing until the animation has finished.
                invalidate();
                return;
            }

        }

        // Done with scroll, clean up state.
        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {

                if (mRowCount * mRowHeight + mRowDivider > getHeight())
                    scrollTo(x, oldY);
                else
                    scrollTo(x, y);

                scrollTo(x, y);

                //if (oldX != x)
                //    checkVisiblePages();
            }
            setScrollState(SCROLL_STATE_IDLE);
        }

        mScrolling = false;
        mFling = false;

        if (mOnColumnChangeListenner != null) {
            mOnColumnChangeListenner.OnColumnChange(mFirstVisibleColumn, mFirstVisiblePage, mVisiblePageCount);
        }

        //setBounceWeekDayColor();
    }

    void setCurrentColumnInternal(int column, boolean smoothScroll, boolean always, int velocity) {

        if (column < 0)
            column = 0;
        if (column >= mPages.size())
            column = mPages.size() - 1;

        final int destX = Math.round(mColumnWidth * column);

        if (smoothScroll) {
            smoothScrollTo(destX, getScrollY(), velocity);
        } else {
            completeScroll();
            scrollTo(destX, getScrollY());
        }

        if (mOnPageChanged != null)
            mOnPageChanged.onPagetChange(column);

        mCurItem = column;
    }

    void setCurrentRowInternal(int row, boolean smoothScroll, boolean always, int velocity) {
        int destY = Math.round((mRowHeight + mRowDivider) * row);

        if (row == getFirstRowLP()) {
            final int v = Math.round(getHeight() / (mRowHeight + mRowDivider));
            destY -= getHeight() - (mRowDayHeight + v * (mRowHeight + mRowDivider));
        }

        if (smoothScroll) {
            smoothScrollTo(getScrollX(), destY, velocity);
        } else {
            completeScroll();
            scrollTo(getScrollX(), destY);
        }
    }

    void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 0);
    }

    /**
     * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
     *
     * @param x        the number of pixels to scroll by on the X axis
     * @param y        the number of pixels to scroll by on the Y axis
     * @param velocity the velocity associated with a fling, if applicable. (0 otherwise)
     */
    void smoothScrollTo(int x, int y, int velocity) {
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll();
            setScrollState(SCROLL_STATE_IDLE);
            return;
        }

        mScrolling = true;

        if (Math.abs(dx) > 0)
            setScrollState(SCROLL_STATE_SETTLING);
        else if (Math.abs(dy) > 0)
            setScrollState(SCROLL_STATE_SETTLING_VERTICAL);

        final int width = getWidth();
        final float halfWidth = width / 2;

        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
        final float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);

        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dx) / (width + mPageMargin);
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION);

        if (mRowCount < getHeight()) {
            sy = 0;
            dy = 0;
        }
        mScroller.startScroll(sx, sy, dx, dy, duration);

        invalidate();
    }

    private int getBottomBound() {
        final int height = getHeight();
        final int all_h = (int) (mRowDayHeight + (mRowHeight + (int) mRowDivider) * mRowCount);
        return all_h - height;
    }

    void flingScrollByY(int y, int velocity) {
        mScrolling = true;
        setScrollState(SCROLL_STATE_SETTLING_VERTICAL);
        mScroller.fling(getScrollX(), getScrollY(), 0, -velocity, getScrollX(), getScrollX(), 0, getBottomBound());
        invalidate();
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;

        return (float) Math.sin(f);
    }


    private int determineColumnTarget(int startColumn, float columnOffset, int velocity, int deltaX) {
        int targetColumn;

        /*
        if (Math.abs(velocity) >= mMaximumVelocity / 2) {
            targetColumn = velocity > 0 ? startColumn : startColumn + 1;
            mFling = true;

        } else if (Math.abs(deltaX) > mColumnWidth && Math.abs(velocity) > mMinimumVelocity * 2) {
            targetColumn = startColumn;
        } else {
            targetColumn = (int) (startColumn + columnOffset + 1.0f / mColumnCount);
        }*/

        if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            targetColumn = velocity > 0 ? startColumn : startColumn + 1;
        } else {
            final float truncator = startColumn >= mCurItem ? 0.4f : 0.6f;
            targetColumn = (int) (startColumn + columnOffset + truncator);
        }

        if (mPages.size() > 0) {
            //final ItemInfo firstItem = mItems.get(0);
            //final ItemInfo lastItem = mItems.get(mItems.size() - 1);

            // Only let the user target pages we have items for
            //targetPage = Math.max(firstItem.position, Math.min(targetPage, lastItem.position));
        }

        return targetColumn;
    }

    // последняя колонка, на которую можем спозиционироватся.
    public int getFirstRowLP() {
        return mRowCount - (int) Math.ceil((getHeight() - mRowDayHeight) / (mRowHeight + mRowDivider));
    }

    private int determineRowTarget(int startRow, float rowOffset, int velocity, int deltaY) {
        int targetRow;

        if (Math.abs(velocity) >= mMaximumVelocity / 3) {
            targetRow = velocity > 0 ? startRow - 5 : startRow + 5;
            mFling = true;
        } else if (Math.abs(deltaY) > mRowHeight && Math.abs(velocity) > mMinimumVelocity * 2) {
            targetRow = velocity > 0 ? startRow - 1 : startRow;
        } else {
            targetRow = velocity > 0 ? startRow - 1 : startRow;
        }

        if (targetRow < 0)
            targetRow = 0;

        if (targetRow > getFirstRowLP())
            targetRow = getFirstRowLP();

        return targetRow;
    }

    /**
     * @param list_view
     */
    private void sort(List<View> list_view) {
        Collections.sort(list_view, new Comparator<View>() {
            @Override
            public int compare(View lhs, View rhs) {
                int x1 = ((View) lhs).getLeft();
                int x2 = ((View) rhs).getLeft();
                return ((Integer) x1).compareTo(x2);
            }
        });
    }


    private void populatePages() {
        if (mPages != null) {
            for (int i = 0; i < mPages.size(); i++) {
                this.addView(mPages.get(i));
            }
        }
    }

    @Override
    public void onClick(View v) {
        /*
        if (v instanceof StateButton)
		{
			if (mOnClickStateButtonListenner != null)
			{
				//mOnClickStateButtonListenner.OnClick((StateButton) v);
			}
		}
		else if (v instanceof TextView)
		{
			// nothing todo
		}*/

    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CalendarScrollWidget.LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof CalendarScrollWidget.LayoutParams;
    }

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new CalendarScrollWidget.LayoutParams(p);
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /**
     * LayoutParams используется, для компоновки дочерних элементов.
     */

    public static class LayoutParams extends ViewGroup.LayoutParams {

        public static final int INTERNAL_LAYOUT_WEEK_DATES = 1;
        public static final int INTERNAL_LAYOUT_TIME_LINE = 2;
        public static final int INTERNAL_LAYOUT_BUTTONS = 3;
        public static final int INTERNAL_LAYOUT_LEFT_ARROW = 4;
        public static final int INTERNAL_LAYOUT_RIGHT_ARROW = 5;

        private int left;
        private int top;
        private int width;
        private int height;
        private int page_index;
        private int layoutType;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarLayout);
            top = a.getInt(R.styleable.CalendarLayout_layout_top, 0);
            height = a.getInt(R.styleable.CalendarLayout_layout_height, 0);
            width = a.getInt(R.styleable.CalendarLayout_layout_width, 0);
            left = a.getInt(R.styleable.CalendarLayout_layout_left, 0);

            a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);

            if (params instanceof LayoutParams) {
                LayoutParams p = (LayoutParams) params;
                height = p.height;
                width = p.width;
                left = p.left;
                top = p.top;
                layoutType = p.layoutType;
            }
        }

        public LayoutParams() {
            this(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public int getLayoutType() {
            return layoutType;
        }

        public void setLayoutType(int value) {
            layoutType = value;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getPageIndex() {
            return page_index;
        }

        public void setPageIndex(int page_index) {
            this.page_index = page_index;
        }
    }

    public static class LinearLayoutEx extends LinearLayout {
        private Date mDate;

        public LinearLayoutEx(Context context) {
            super(context);
        }

        public Date getDate() {
            return mDate;
        }

        public void setDate(Date mDate) {
            this.mDate = mDate;
        }

        public Date getIndexDate(int index) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mDate);
            cal.add(Calendar.DATE, index);
            return cal.getTime();
        }
    }

    public interface OnClickStateButtonListenner {
        void OnClick(View view);
    }

    public OnClickStateButtonListenner getOnClickStateButtonListenner() {
        return mOnClickStateButtonListenner;
    }

    public void setOnClickStateButtonListenner(OnClickStateButtonListenner mOnClickStateButtonListenner) {
        this.mOnClickStateButtonListenner = mOnClickStateButtonListenner;
    }

    public interface OnChangeListenner {
        void OnMonthChange(Date date);
    }

    public void setOnChangeListenner(OnChangeListenner value) {
        mOnChangeListenner = value;
    }

    public interface OnColumnChangeListenner {
        void OnColumnChange(int firstcolumn, int firstpage, int pagevisiblecount);
    }

    public void setOnColumnChangeListenner(OnColumnChangeListenner value) {
        mOnColumnChangeListenner = value;
    }

    public List<View> getButtonContainers() {
        return mButtonsContainerItems;
    }

    public List<View> getWeekDayCntainers() {
        return mWeekDaysContainerItems;
    }

    private OnTouchListener TOUCH_BTN = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //final int i = ((StateButton) v).getRowIndex();
                    //	internalSelectRow(i, true);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    //final int j = ((StateButton) v).getRowIndex();
                    //	internalSelectRow(j, false);
                    break;
            }

            return false;
        }
    };

    public interface IOnRefreshButtons {
        void onRefresh(final Date start_date, final int page_index);
    }

    public void setOnRefreshButtons(IOnRefreshButtons mIOnRefreshButtons) {
        this.mIOnRefreshButtons = mIOnRefreshButtons;
    }

    private Date getZeroTimeDate(Date fecha) {
        Date res = fecha;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public View findPageByDate(Date date) {

        for (int p = 0; p < DEFAULT_PAGES_COUNT; p++) {
            LinearLayoutEx page_dates = (LinearLayoutEx) mWeekDaysContainerItems.get(p);
            if (getZeroTimeDate(page_dates.getDate()).compareTo(getZeroTimeDate(date)) == 0) {
                return mButtonsContainerItems.get(p);
            }
        }
        return null;
    }

    public int getColumnCount() {
        return mColumnCount;
    }

    public int getRowCount() {
        return mRowCount;
    }

    public void selected(int position) {
        setCurrentColumnInternal(position, true, true, 30);
    }

    public interface IOnPageChanged {
        void onPagetChange(int page);
    }

    public void setOnPagetChange(IOnPageChanged value) {
        mOnPageChanged = value;
    }


    public void setPages(ArrayList<View> pages) {
        mPages = pages;
        if (mPages != null)
            populatePages();
        changed = true;
    }

    public List<View> getPages() {
        return mPages;
    }

    public void endDragging() {
        endDrag();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {

        Log.d(TAG, "startNestedScroll");

        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {

        Log.d(TAG, "stopNestedScroll");

        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {

        Log.d(TAG, "hasNestedScrollingParent");

        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {

        Log.d(TAG, "dispatchNestedScroll");

        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {

        Log.d(TAG, "dispatchNestedPreScroll");

        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {

        Log.d(TAG, "dispatchNestedFling");

        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {

        Log.d(TAG, "dispatchNestedPreFling");

        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {

        Log.d(TAG, "onStartNestedScroll");

        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

        Log.d(TAG, "onNestedScrollAccepted");

        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View target) {
        Log.d(TAG, "onStopNestedScroll");
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll");

        //final int myConsumed = moveBy(dyUnconsumed);
        //final int myUnconsumed = dyUnconsumed - myConsumed;
        //dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        Log.d(TAG, "onNestedPreScroll");

        /*
        if (dy > 0 && mHeaderController.canScrollUp()) {
            final int delta = moveBy(dy);
            consumed[0] = 0;
            consumed[1] = delta;
            //dispatchNestedScroll(0, myConsumed, 0, consumed[1], null);
        }*/
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {

        Log.d(TAG, "onNestedFling");

        if (!consumed) {
            flingWithNestedDispatch((int) velocityY);
            return true;
        }
        return false;
    }

    private boolean flingWithNestedDispatch(int velocityY) {
        /*
        final boolean canFling = (mHeaderController.canScrollUp() && velocityY > 0) ||
                (mHeaderController.canScrollDown() && velocityY < 0);
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, canFling);
            if (canFling) {
                fling(velocityY);
            }
        }*/
        return true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {

        Log.d(TAG, "onNestedPreFling");

        return flingWithNestedDispatch((int) velocityY);
    }

    @Override
    public int getNestedScrollAxes() {

        return mParentHelper.getNestedScrollAxes();
    }

}
