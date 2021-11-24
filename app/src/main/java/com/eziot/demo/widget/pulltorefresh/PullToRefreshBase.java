/***
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * huchenxi
 *
 * 版本修改记录
 * 2016/02/03
 * 加入新的功能
 * 两段式下拉
 * 即轻微下拉显示一段
 * 用力下拉显示第二段
 * 当然这两段都是定义在HeaderView 中的
 * 试用方法
 * xml中定义ptrDoublePullEnabled  ptrDoublePullFirstHeader 两个属性值
 * 如
 * ptr:ptrDoublePullEnabled="true"
 * ptr:ptrDoublePullFirstHeader="@id/header_time_layout"
 *
 *
 * huchenxi
 *
 * 版本修改记录
 * 2017/07/03
 * 加入新的监听
 * 对全局的滚动方向监听
 * 支持垂直滑动事件二次分发
 * OnTouchDirectionListener
 */

package com.eziot.demo.widget.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.eziot.iotsdkdemo.R;


public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T> {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int SMOOTH_SCROLL_DURATION_MS = 200;
    public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;

    public static final int UPDATE_VIEW_NORMAL = 1;//根据系统来调用ondraw，onlayout
    public static final int UPDATE_VIEW_SLEEP = 2;//一定时间间隔内忽略
    public static final int UPDATE_VIEW_NONE = 3;//不更新，忽略系统信号

    static final boolean DEBUG = false;
    static final boolean USE_HW_LAYERS = false;
    static final String LOG_TAG = "PullToRefresh";
    static final int DEMO_SCROLL_INTERVAL = 225;
    //触发滑动方向变化的最小距离
    static final int MIN_SCROLL_DISTANCE = 50;
    static final String STATE_STATE = "ptr_state";
    static final String STATE_MODE = "ptr_mode";
    static final String STATE_CURRENT_MODE = "ptr_current_mode";
    static final String STATE_SCROLLING_REFRESHING_ENABLED = "ptr_disable_scrolling";
    static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
    static final String STATE_SUPER = "ptr_super";
    float frection = 2.0f;//不用final了(可以更改)

    // ===========================================================
    // Fields
    // ===========================================================
    T mRefreshableView;
    /***
     * authored by huchenxi
     * 2016/2/3
     * 扩展功能
     */
    private boolean mDoublePullEnabled = false;//支持两段式下拉
    private int mDoublePullFirstViewId = 0;//轻微下拉显示的viewid（headerview 中）
    private int firstHeaderViewHeight = 0;//第一段view的高度
    private boolean pullWithHeader = false;//是否在显示header的时候下拉

    /***
     * @authored by huchenxi
     * @date 2017/07/05
     * 支持垂直滑动事件二次分发
     */
    private float mLastRecordY;//上一次记录的y
    private boolean interceptByScroll = false;
    private boolean interceptByUp = false;
    private boolean interceptByDown = false;
    private boolean intercepted = false;
    private float lastMoveY = 0;
    private float interceptY = 0;
    private boolean canUpdateView = true;//是否可以onlayout
    private long lastUpdateTime = 0;

    private int mTouchSlop;
    private float mLastMotionX, mLastMotionY;
    private float mInitialMotionX, mInitialMotionY;
    private boolean mIsBeingDragged = false;
    private State mState = State.RESET;
    private Mode mMode = Mode.getDefault();
    private Mode mCurrentMode;
    private FrameLayout mRefreshableViewWrapper;

    private boolean mShowViewWhileRefreshing = true;

    private boolean mScrollingWhileRefreshingEnabled = false;

    private boolean mFilterTouchEvents = true;

    private boolean mOverScrollEnabled = true;

    private boolean mLayoutVisibilityChangesEnabled = true;

    private boolean mHeaderRefreshEnabled = true;

    private boolean mFooterRefreshEnabled = true;

    private Interpolator mScrollAnimationInterpolator;

    private LoadingLayout mHeaderLayout;

    private LoadingLayout mFooterLayout;

    private LoadingLayoutCreator mLoadingLayoutCreator;

    private OnRefreshListener<T> mOnRefreshListener;

    private OnPullEventListener<T> mOnPullEventListener;

    private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

    private OnPullPercentListener mOnPullPercentListener;

    private OnTouchDirectionListener mOnTouchDirectionListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshBase(Context context, Mode mode) {
        super(context);
        mMode = mode;
        init(context, null);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (DEBUG) {
            Log.d(LOG_TAG, "addView: " + child.getClass().getSimpleName());
        }

        final T refreshableView = getRefreshableView();

        if (refreshableView instanceof ViewGroup) {
            ((ViewGroup) refreshableView).addView(child, index, params);
        } else {
            throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
        }
    }

    @Override
    public final boolean demo() {
        if (mMode.showHeaderLoadingLayout() && isReadyForPullStart()) {
            smoothScrollToAndBack(-getHeaderSize() * 2);
            return true;
        } else if (mMode.showFooterLoadingLayout() && isReadyForPullEnd()) {
            smoothScrollToAndBack(getFooterSize() * 2);
            return true;
        }

        return false;
    }

    @Override
    public final Mode getCurrentMode() {
        return mCurrentMode;
    }

    @Override
    public final boolean getFilterTouchEvents() {
        return mFilterTouchEvents;
    }

    @Override
    public final void setFilterTouchEvents(boolean filterEvents) {
        mFilterTouchEvents = filterEvents;
    }

    @Override
    public final LoadingLayoutProxy getLoadingLayoutProxy() {
        return getLoadingLayoutProxy(true, true);
    }

    @Override
    public final LoadingLayoutProxy getLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
        return createLoadingLayoutProxy(includeStart, includeEnd);
    }

    @Override
    public final Mode getMode() {
        return mMode;
    }

    @Override
    public final void setMode(Mode mode) {
        if (mode != mMode) {
            if (mode.showHeaderLoadingLayout() && mHeaderLayout == null) {
                throw new RuntimeException("can't set this mode before set headerlayout");
            }

            if (mode.showFooterLoadingLayout() && mFooterLayout == null) {
                throw new RuntimeException("can't set this mode before set footerlayout");
            }

            if (DEBUG) {
                Log.d(LOG_TAG, "Setting mode to: " + mode);
            }
            mMode = mode;
            updateUIForMode();
        }
    }

    @Override
    public final T getRefreshableView() {
        return mRefreshableView;
    }

    @Override
    public final boolean getShowViewWhileRefreshing() {
        return mShowViewWhileRefreshing;
    }

    @Override
    public final void setShowViewWhileRefreshing(boolean showView) {
        mShowViewWhileRefreshing = showView;
    }

    @Override
    public final State getState() {
        return mState;
    }

    @Override
    public final boolean isPullToRefreshEnabled() {
        return mMode.permitsPullToRefresh();
    }

    @Override
    public final boolean isPullToRefreshOverScrollEnabled() {
        return VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD
                && mOverScrollEnabled
                && OverscrollHelper.isAndroidOverScrollEnabled(mRefreshableView);
    }

    @Override
    public final void setPullToRefreshOverScrollEnabled(boolean enabled) {
        mOverScrollEnabled = enabled;
    }

    @Override
    public final boolean isRefreshing() {
        return mState == State.REFRESHING || mState == State.MANUAL_REFRESHING;
    }

    @Override
    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            mCurrentMode = (mMode != Mode.BOTH) ? mMode : Mode.PULL_FROM_START;
            setState(State.MANUAL_REFRESHING, doScroll);
        }
    }

    @Override
    public final boolean isScrollingWhileRefreshingEnabled() {
        return mScrollingWhileRefreshingEnabled;
    }

    public final void setScrollingWhileRefreshingEnabled(boolean allowScrollingWhileRefreshing) {
        mScrollingWhileRefreshingEnabled = allowScrollingWhileRefreshing;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {

        /**
         * @author huchenxi
         * 解析头部需要显示的view高度
         */

        if (mDoublePullEnabled && mDoublePullFirstViewId != 0 && firstHeaderViewHeight == 0) {
            if (mHeaderLayout != null) {
                View view = mHeaderLayout.findViewById(mDoublePullFirstViewId);
                firstHeaderViewHeight = view.getHeight();
            }
        }

        if (!isPullToRefreshEnabled()) {
            return false;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // If we're refreshing, and the flag is set. Eat all MOVE events
                if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
                    return true;
                }

                if (isReadyForPull()) {
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;

                    // We need to use the correct values, based on scroll
                    // direction
                    switch (getPullToRefreshScrollDirection()) {
                        case HORIZONTAL:
                            diff = x - mLastMotionX;
                            oppositeDiff = y - mLastMotionY;
                            break;
                        case VERTICAL:
                        default:
                            diff = y - mLastMotionY;
                            oppositeDiff = x - mLastMotionX;
                            break;
                    }
                    absDiff = Math.abs(diff);

                    if (absDiff > mTouchSlop && (!mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                        if (mMode.showHeaderLoadingLayout() && diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                            if (mMode == Mode.BOTH) {
                                mCurrentMode = Mode.PULL_FROM_START;
                            }
                        } else if (mMode.showFooterLoadingLayout() && diff <= -1f && isReadyForPullEnd()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                            if (mMode == Mode.BOTH) {
                                mCurrentMode = Mode.PULL_FROM_END;
                            }
                        } else if (mMode.showHeaderLoadingLayout()
                                && diff <= -1f
                                && isReadyForPullStart()
                                && mState == State.SHOW_HEADER) {
                            //showheader的情况下捕捉事件
                            //@huchenxi
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (mState == State.SHOW_HEADER) {
                    pullWithHeader = true;
                } else {
                    pullWithHeader = false;
                }

                if (isReadyForPull()) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    mIsBeingDragged = false;
                }

/*
                //在有header的情况下捕捉事件
                if(mState==State.SHOW_HEADER){
                    mIsBeingDragged = true;
                    return false;
                }
*/

                break;
            }
            default:
                break;
        }

        return mIsBeingDragged;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //如果有这个监听的话对dispatch事件做下处理
        //@author huchenxi
        if (mOnTouchDirectionListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    intercepted = false;
                    interceptByScroll = false;
                    mLastRecordY = ev.getRawY();
                    lastMoveY = ev.getRawY();
                    interceptByDown = false;
                    interceptByUp = false;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    interceptByScroll = false;
                    if (intercepted) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    if (intercepted) {
                        mOnTouchDirectionListener.onTouchScrollFinish(interceptByUp);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = ev.getRawY();
                    if (mLastRecordY == 0) {
                        mLastRecordY = y;
                    }
                    if (lastMoveY == 0) {
                        lastMoveY = y;
                    }
                    if (interceptByScroll && (interceptByDown || interceptByUp)) {
                        ev.setAction(MotionEvent.ACTION_DOWN);
                    }
                    if (y - mLastRecordY > 0 && !interceptByDown && isReadyForPullStart()) {
                        mLastRecordY = y;
                        lastMoveY = y;
                        interceptY = y;
                        interceptByUp = false;
                        interceptByDown = mOnTouchDirectionListener.onInterceptTouchScrollVertical(false);
                        if (interceptByDown) {
                            return true;
                        } else {
                            interceptByScroll = false;
                        }
                    } else if (mLastRecordY - y > 0 && !interceptByUp) {
                        mLastRecordY = y;
                        lastMoveY = y;
                        interceptY = y;
                        interceptByDown = false;
                        interceptByUp = mOnTouchDirectionListener.onInterceptTouchScrollVertical(true);
                        if (interceptByUp) {
                            return true;
                        } else {
                            interceptByScroll = false;
                        }
                    }
                    if ((interceptByUp && y - lastMoveY > 0) || (interceptByDown && y - lastMoveY < 0)) {
                        interceptByUp = false;
                        interceptByDown = false;
                        mLastRecordY = y;
                    }
                    if (interceptByDown || interceptByUp) {
                        if (Math.abs(y - interceptY) > MIN_SCROLL_DISTANCE) {
                            interceptByScroll = mOnTouchDirectionListener.onTouchScrollVertical(y - lastMoveY);
                        }else{
                            return true;
                        }
                    } else {
                        interceptByScroll = false;
                    }

                    lastMoveY = y;
                    if (interceptByScroll) {
                        intercepted = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return super.dispatchTouchEvent(ev);
                    }
                    if(!interceptByScroll&&(interceptByUp||interceptByDown)){
                        mOnTouchDirectionListener.onTouchScrollFinish(interceptByUp);
                        interceptByDown=false;
                        interceptByUp=false;
                    }

                    break;
            }
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public final void onRefreshComplete() {
        if (isRefreshing()) {
            setState(State.RESET);
            setFooterRefreshEnabled(true);
        }
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {

        if (!isPullToRefreshEnabled()) {
            return false;
        }

        // If we're refreshing, and the flag is set. Eat the event
        if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (mIsBeingDragged) {
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();
                    pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {

                    //松开的监听
                    mHeaderLayout.onUpAction();

                    mIsBeingDragged = false;

                    if (mState == State.RELEASE_TO_REFRESH && (null != mOnRefreshListener)) {
                        setState(State.REFRESHING, true);
                        return true;
                    }

                    // If we're already refreshing, just scroll back to the top
                    if (isRefreshing()) {
                        smoothScrollTo(0);
                        return true;
                    }

                    if (mDoublePullEnabled) {
                        //在需要两段式下拉的情况下，超过view的一半并且小于整个header时展示header
                        if (mState != State.SHOW_HEADER
                                && getScrollY() < -firstHeaderViewHeight / 2
                                && getScrollY() > -getHeaderSize()) {
                            setState(State.SHOW_HEADER);
                            return true;
                        }
                    }

                    // If we haven't returned by here, then we're not in a state
                    // to pull, so just reset
                    setState(State.RESET);

                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    public void setOnPullEventListener(OnPullEventListener<T> listener) {
        mOnPullEventListener = listener;
    }

    @Override
    public final void setOnRefreshListener(OnRefreshListener<T> listener) {
        mOnRefreshListener = listener;
    }

    @Override
    public final void setRefreshing() {
        try {
            setRefreshing(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScrollAnimationInterpolator(Interpolator interpolator) {
        mScrollAnimationInterpolator = interpolator;
    }

    /**
     * @return Either {@link Orientation#VERTICAL} or {@link Orientation#HORIZONTAL} depending on
     * the scroll direction.
     */
    public abstract Orientation getPullToRefreshScrollDirection();

    final void setState(State state, final Object... params) {
        if ((mCurrentMode == Mode.PULL_FROM_START && !mHeaderRefreshEnabled) || (mCurrentMode == Mode.PULL_FROM_END
                && !mFooterRefreshEnabled)) {
            if (state == State.RESET) smoothScrollTo(0);
            return;
        }

        mState = state;
        if (DEBUG) {
            Log.d(LOG_TAG, "State: " + mState.name());
        }

        switch (mState) {
            case RESET:
                onReset();
                break;
            case PULL_TO_REFRESH:
                onPullToRefresh();
                break;
            case RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;
            case REFRESHING:
            case MANUAL_REFRESHING:
                onRefreshing((Boolean) params[0]);
                break;
            case OVERSCROLLING:
                // NO-OP
                break;
            //如果在需要显示第一段头部的模式下，滚动至第一段view的高度
            case SHOW_HEADER:
                smoothScrollTo(-firstHeaderViewHeight);
                break;
        }

        // Call OnPullEventListener
        if (null != mOnPullEventListener) {
            mOnPullEventListener.onPullEvent(this, mState, mCurrentMode);
        }
    }

    /**
     * Used internally for adding view. Need because we override addView to pass-through to the
     * Refreshable View
     */
    protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    /**
     * Used internally for adding view. Need because we override addView to pass-through to the
     * Refreshable View
     */
    protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
        super.addView(child, -1, params);
    }

    protected LoadingLayoutCreator getLoadingLayoutCreator() {
        return mLoadingLayoutCreator;
    }

    public void setLoadingLayoutCreator(LoadingLayoutCreator creator) {
        mLoadingLayoutCreator = creator;
        mHeaderLayout = creator.create(getContext(), true, getPullToRefreshScrollDirection());
        mFooterLayout = creator.create(getContext(), false, getPullToRefreshScrollDirection());
    }

    /**
     * Used internally for {@link #getLoadingLayoutProxy(boolean, boolean)}. Allows derivative
     * classes to include any extra LoadingLayouts.
     */
    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
        LoadingLayoutProxy proxy = new LoadingLayoutProxy();

        if (includeStart && mMode.showHeaderLoadingLayout()) {
            proxy.addLayout(mHeaderLayout);
        }
        if (includeEnd && mMode.showFooterLoadingLayout()) {
            proxy.addLayout(mFooterLayout);
        }

        return proxy;
    }

    /**
     * This is implemented by derived classes to return the created View. If you need to use a
     * custom View (such as a custom ListView), override this method and return an instance of your
     * custom class.
     * <p/>
     * Be sure to set the ID of the view in this method, especially if you're using a ListActivity
     * or ListFragment.
     *
     * @param context Context to create view with
     * @param attrs   AttributeSet from wrapped class. Means that anything you include in the XML layout
     *                declaration will be routed to the created View
     * @return New instance of the Refreshable View
     */
    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    protected final void disableLoadingLayoutVisibilityChanges() {
        mLayoutVisibilityChangesEnabled = false;
    }

    protected final LoadingLayout getFooterLayout() {
        return mFooterLayout;
    }

    protected final int getFooterSize() {
        if (mFooterLayout != null) {
            return mFooterLayout.getContentSize(getPullToRefreshScrollDirection());
        } else {
            return 0;
        }
    }

    protected final LoadingLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    protected final int getHeaderSize() {
        if (mHeaderLayout != null) {
            return mHeaderLayout.getContentSize(getPullToRefreshScrollDirection());
        } else {
            return 0;
        }
    }

    protected int getPullToRefreshScrollDuration() {
        return SMOOTH_SCROLL_DURATION_MS;
    }

    protected int getPullToRefreshScrollDurationLonger() {
        return SMOOTH_SCROLL_LONG_DURATION_MS;
    }

    protected FrameLayout getRefreshableViewWrapper() {
        return mRefreshableViewWrapper;
    }

    /**
     * Allows Derivative classes to handle the XML Attrs without creating a TypedArray themsevles
     *
     * @param a - TypedArray of PullToRefresh Attributes
     */
    protected void handleStyledAttributes(TypedArray a) {
    }

    /**
     * Implemented by derived class to return whether the View is in a state where the user can Pull
     * to Refresh by scrolling from the end.
     *
     * @return true if the View is currently in the correct state (for example, bottom of a
     * ListView)
     */
    protected abstract boolean isReadyForPullEnd();

    /**
     * Implemented by derived class to return whether the View is in a state where the user can Pull
     * to Refresh by scrolling from the start.
     *
     * @return true if the View is currently the correct state (for example, top of a ListView)
     */
    protected abstract boolean isReadyForPullStart();

    /**
     * Called by {@link #onRestoreInstanceState(Parcelable)} so that derivative classes can handle
     * their saved instance state.
     *
     * @param savedInstanceState - Bundle which contains saved instance state.
     */
    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
    }

    /**
     * Called by {@link #onSaveInstanceState()} so that derivative classes can save their instance
     * state.
     *
     * @param saveState - Bundle to be updated with saved state.
     */
    protected void onPtrSaveInstanceState(Bundle saveState) {
    }

    /**
     * Called when the UI has been to be updated to be in the {@link State#PULL_TO_REFRESH} state.
     */
    protected void onPullToRefresh() {
        switch (mCurrentMode) {
            case PULL_FROM_END:
                mFooterLayout.pullToRefresh();
                break;
            case PULL_FROM_START:
                mHeaderLayout.pullToRefresh();
                break;
            default:
                // NO-OP
                break;
        }
    }

    /**
     * Called when the UI has been to be updated to be in the {@link State#REFRESHING} or
     * {@link State#MANUAL_REFRESHING} state.
     *
     * @param doScroll - Whether the UI should scroll for this event.
     */
    protected void onRefreshing(final Boolean doScroll) {
        if (mMode.showHeaderLoadingLayout()) {
            mHeaderLayout.refreshing();
        }
        if (mMode.showFooterLoadingLayout()) {
            mFooterLayout.refreshing();
        }

        if (doScroll) {
            if (mShowViewWhileRefreshing) {

                // Call Refresh Listener when the Scroll has finished
                final OnSmoothScrollFinishedListener listener = new OnSmoothScrollFinishedListener() {

                    @Override
                    public void onSmoothScrollFinished() {
                        callRefreshListener();
                    }
                };

                switch (mCurrentMode) {
                    case MANUAL_REFRESH_ONLY:
                    case PULL_FROM_END:
                        if (getFooterSize() == 0) {
                            mFooterLayout.postRenderRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    smoothScrollTo(getFooterSize(), listener);
                                }
                            });
                        } else {
                            smoothScrollTo(getFooterSize(), listener);
                        }
                        break;

                    default:
                    case PULL_FROM_START:
                        if (getHeaderSize() == 0) {
                            mHeaderLayout.postRenderRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    smoothScrollTo(-getHeaderSize(), listener);
                                }
                            });
                        } else {
                            smoothScrollTo(-getHeaderSize(), listener);
                        }
                        break;
                }
            } else {
                smoothScrollTo(0);
            }
        } else {
            // We're not scrolling, so just call Refresh Listener now
            callRefreshListener();
        }
    }

    /**
     * Called when the UI has been to be updated to be in the {@link State#RELEASE_TO_REFRESH}
     * state.
     */
    protected void onReleaseToRefresh() {
        switch (mCurrentMode) {
            case PULL_FROM_END:
                mFooterLayout.releaseToRefresh();
                break;
            case PULL_FROM_START:
                mHeaderLayout.releaseToRefresh();
                break;
            default:
                // NO-OP
                break;
        }
    }

    /**
     * Called when the UI has been to be updated to be in the {@link State#RESET} state.
     */
    protected void onReset() {
        mIsBeingDragged = false;
        mLayoutVisibilityChangesEnabled = true;

        // Always reset both layouts, just in case...
        if (mHeaderLayout != null && mHeaderRefreshEnabled) mHeaderLayout.reset();
        if (mFooterLayout != null && mFooterRefreshEnabled) mFooterLayout.reset();

        smoothScrollTo(0);
    }

    /**
     * Called when the UI has been to be updated to be in the {@link State#RESET} state.
     */
    protected void onReset(int duration) {
        mIsBeingDragged = false;
        mLayoutVisibilityChangesEnabled = true;

        // Always reset both layouts, just in case...
        if (mHeaderLayout != null && mHeaderRefreshEnabled) mHeaderLayout.reset();
        if (mFooterLayout != null && mFooterRefreshEnabled) mFooterLayout.reset();

        smoothScrollTo(0, duration);
    }

    @Override
    protected final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            setMode(Mode.mapIntToValue(bundle.getInt(STATE_MODE, 0)));
            mCurrentMode = Mode.mapIntToValue(bundle.getInt(STATE_CURRENT_MODE, 0));

            mScrollingWhileRefreshingEnabled = bundle.getBoolean(STATE_SCROLLING_REFRESHING_ENABLED, false);
            mShowViewWhileRefreshing = bundle.getBoolean(STATE_SHOW_REFRESHING_VIEW, true);

            // Let super Restore Itself
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));

            State viewState = State.mapIntToValue(bundle.getInt(STATE_STATE, 0));
            if (viewState == State.REFRESHING || viewState == State.MANUAL_REFRESHING) {
                setState(viewState, true);
            }

            // Now let derivative classes restore their state
            onPtrRestoreInstanceState(bundle);
            return;
        }

        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    protected final Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        // Let derivative classes get a chance to save state first, that way we
        // can make sure they don't overrite any of our values
        onPtrSaveInstanceState(bundle);

        bundle.putInt(STATE_STATE, mState.getIntValue());
        bundle.putInt(STATE_MODE, mMode.getIntValue());
        bundle.putInt(STATE_CURRENT_MODE, mCurrentMode.getIntValue());
        bundle.putBoolean(STATE_SCROLLING_REFRESHING_ENABLED, mScrollingWhileRefreshingEnabled);
        bundle.putBoolean(STATE_SHOW_REFRESHING_VIEW, mShowViewWhileRefreshing);
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());

        return bundle;
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (DEBUG) {
            Log.d(LOG_TAG, String.format("onSizeChanged. W: %d, H: %d", w, h));
        }

        super.onSizeChanged(w, h, oldw, oldh);

        // We need to update the header/footer when our size changes
        refreshLoadingViewsSize();

        // Update the Refreshable View layout
        refreshRefreshableViewSize(w, h);

        /**
         * As we're currently in a Layout Pass, we need to schedule another one to layout any
         * changes we've made here
         */
        post(new Runnable() {

            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * Re-measure the Loading Views height, and adjust internal padding as necessary
     */
    protected final void refreshLoadingViewsSize() {
        final int maximumPullScroll = (int) (getMaximumPullScroll() * 1.2f);

        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();

        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                if (mMode.showHeaderLoadingLayout()) {
                    mHeaderLayout.setWidth(maximumPullScroll);
                    pLeft = -maximumPullScroll;
                } else {
                    pLeft = 0;
                }

                if (mMode.showFooterLoadingLayout()) {
                    mFooterLayout.setWidth(maximumPullScroll);
                    pRight = -maximumPullScroll;
                } else {
                    pRight = 0;
                }
                break;

            case VERTICAL:
                if (mMode.showHeaderLoadingLayout()) {
                    mHeaderLayout.setHeight(maximumPullScroll);
                    pTop = -maximumPullScroll;
                } else {
                    pTop = 0;
                }

                if (mMode.showFooterLoadingLayout()) {
                    mFooterLayout.setHeight(maximumPullScroll);
                    pBottom = -maximumPullScroll;
                } else {
                    pBottom = 0;
                }
                break;
        }

        if (DEBUG) {
            Log.d(LOG_TAG,
                    String.format("Setting Padding. L: %d, T: %d, R: %d, B: %d", pLeft, pTop, pRight, pBottom));
        }
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    protected final void refreshRefreshableViewSize(int width, int height) {
        // We need to set the Height of the Refreshable View to the same as
        // this layout
        LayoutParams lp = (LayoutParams) mRefreshableViewWrapper.getLayoutParams();

        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                if (lp.width != width) {
                    lp.width = width;
                    mRefreshableViewWrapper.requestLayout();
                }
                break;
            case VERTICAL:
                if (lp.height != height) {
                    lp.height = height;
                    mRefreshableViewWrapper.requestLayout();
                }
                break;
        }
    }

    /**
     * Helper method which just calls scrollTo() in the correct scrolling direction.
     *
     * @param value - New Scroll value
     */
    protected final void setHeaderScroll(int value) {
        if (DEBUG) {
            Log.d(LOG_TAG, "setHeaderScroll: " + value);
        }

        // Clamp value to with pull scroll range
        final int maximumPullScroll = getMaximumPullScroll();
        value = Math.min(maximumPullScroll, Math.max(-maximumPullScroll, value));

        if (mLayoutVisibilityChangesEnabled) {
            if (value < 0) {
                mHeaderLayout.setVisibility(View.VISIBLE);
            } else if (value > 0) {
                mFooterLayout.setVisibility(View.VISIBLE);
            } else {
                if (mHeaderLayout != null) mHeaderLayout.setVisibility(View.INVISIBLE);
                if (mFooterLayout != null) mFooterLayout.setVisibility(View.INVISIBLE);
            }
        }

        if (USE_HW_LAYERS) {
            /**
             * Use a Hardware Layer on the Refreshable View if we've scrolled at all. We don't use
             * them on the Header/Footer Views as they change often, which would negate any HW layer
             * performance boost.
             */
            ViewCompat.setLayerType(mRefreshableViewWrapper,
                    value != 0 ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE);
        }

        switch (getPullToRefreshScrollDirection()) {
            case VERTICAL:
                scrollTo(0, value);
                break;
            case HORIZONTAL:
                scrollTo(value, 0);
                break;
        }
    }

    /**
     * Smooth Scroll to position using the default duration of {@value #SMOOTH_SCROLL_DURATION_MS}
     * ms.
     *
     * @param scrollValue - Position to scroll to
     */
    protected final void smoothScrollTo(int scrollValue) {
        smoothScrollTo(scrollValue, getPullToRefreshScrollDuration());
    }

    /**
     * Smooth Scroll to position using the default duration of {@value #SMOOTH_SCROLL_DURATION_MS}
     * ms.
     *
     * @param scrollValue - Position to scroll to
     * @param listener    - Listener for scroll
     */
    protected final void smoothScrollTo(int scrollValue, OnSmoothScrollFinishedListener listener) {
        smoothScrollTo(scrollValue, getPullToRefreshScrollDuration(), 0, listener);
    }

    /**
     * Smooth Scroll to position using the longer default duration of
     * {@value #SMOOTH_SCROLL_LONG_DURATION_MS} ms.
     *
     * @param scrollValue - Position to scroll to
     */
    protected final void smoothScrollToLonger(int scrollValue) {
        smoothScrollTo(scrollValue, getPullToRefreshScrollDurationLonger());
    }

    /**
     * Updates the View State when the mode has been set. This does not do any checking that the
     * mode is different to current state so always updates.
     */
    protected void updateUIForMode() {
        // We need to use the correct LayoutParam values, based on scroll
        // direction
        final LayoutParams lp = getLoadingLayoutLayoutParams();

        // Remove Header, and then add Header Loading View again if needed
        if (mHeaderLayout != null && this == mHeaderLayout.getParent()) {
            removeView(mHeaderLayout);
        }
        if (mMode.showHeaderLoadingLayout()) {
            addViewInternal(mHeaderLayout, 0, lp);
        }

        // Remove Footer, and then add Footer Loading View again if needed
        if (mFooterLayout != null && this == mFooterLayout.getParent()) {
            removeView(mFooterLayout);
        }
        if (mMode.showFooterLoadingLayout()) {
            addViewInternal(mFooterLayout, lp);
        }

        // Hide Loading Views
        refreshLoadingViewsSize();

        // If we're not using Mode.BOTH, set mCurrentMode to mMode, otherwise
        // set it to pull down
        mCurrentMode = (mMode != Mode.BOTH) ? mMode : Mode.PULL_FROM_START;
    }

    private void addRefreshableView(Context context, T refreshableView) {
        mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        addViewInternal(mRefreshableViewWrapper,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private void callRefreshListener() {
        if (null != mOnRefreshListener) {
            if (mCurrentMode == Mode.PULL_FROM_START) {
                mOnRefreshListener.onRefresh(this, true);
            } else if (mCurrentMode == Mode.PULL_FROM_END) {
                mOnRefreshListener.onRefresh(this, false);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs) {
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                setOrientation(LinearLayout.HORIZONTAL);
                break;
            case VERTICAL:
            default:
                setOrientation(LinearLayout.VERTICAL);
                break;
        }

        setGravity(Gravity.CENTER);

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();

        // Styleables from XML
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);

        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        mRefreshableView = createRefreshableView(context, attrs);
        addRefreshableView(context, mRefreshableView);

        // We need to create now layouts now
        // mHeaderLayout = createLoadingLayout(context, Mode.PULL_FROM_START, a);
        // mFooterLayout = createLoadingLayout(context, Mode.PULL_FROM_END, a);

        /**
         * Styleables from XML
         */
        if (a.hasValue(R.styleable.PullToRefresh_ptrRefreshableViewBackground)) {
            Drawable background = a.getDrawable(R.styleable.PullToRefresh_ptrRefreshableViewBackground);
            if (null != background) {
                mRefreshableView.setBackgroundDrawable(background);
            }
        }

        if (a.hasValue(R.styleable.PullToRefresh_ptrOverScroll)) {
            mOverScrollEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrOverScroll, true);
        }

        if (a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
            mScrollingWhileRefreshingEnabled =
                    a.getBoolean(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled, false);
        }

        /***
         * 获取两段式下拉的参数
         */
        if (a.hasValue(R.styleable.PullToRefresh_ptrDoublePullEnabled)) {
            mDoublePullEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrDoublePullEnabled, false);
        }

        if (a.hasValue(R.styleable.PullToRefresh_ptrDoublePullFirstHeader)) {
            mDoublePullFirstViewId = a.getResourceId(R.styleable.PullToRefresh_ptrDoublePullFirstHeader, 0);
        }

        if (mDoublePullEnabled) {
            frection = 1.2f;
        }

        // Let the derivative classes have a go at handling attributes, then
        // recycle them...
        handleStyledAttributes(a);
        a.recycle();

        // Finally update the UI for the modes
        updateUIForMode();
    }

    private boolean isReadyForPull() {
        switch (mMode) {
            case PULL_FROM_START:
                return isReadyForPullStart();
            case PULL_FROM_END:
                return isReadyForPullEnd();
            case BOTH:
                return isReadyForPullEnd() || isReadyForPullStart();
            default:
                return false;
        }
    }

    /**
     * Actions a Pull Event
     *
     * @return true if the Event has been handled, false if there has been no change
     */
    private void pullEvent() {
        final int newScrollValue;
        final int itemDimension;
        final float initialMotionValue, lastMotionValue;

        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                initialMotionValue = mInitialMotionX;
                lastMotionValue = mLastMotionX;
                break;
            case VERTICAL:
            default:
                initialMotionValue = mInitialMotionY;
                lastMotionValue = mLastMotionY;
                break;
        }

        switch (mCurrentMode) {
            case PULL_FROM_END:
                newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0) / frection);
                itemDimension = getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                if (mState == State.SHOW_HEADER || pullWithHeader) {
                    if (lastMotionValue - initialMotionValue < 0) {
                        float dis = Math.abs(lastMotionValue - initialMotionValue) / frection;
                        if (dis < firstHeaderViewHeight) {
                            newScrollValue = (int) (dis - firstHeaderViewHeight);
                        } else {
                            newScrollValue = 0;
                        }
                    } else {
                        float dis = Math.abs(lastMotionValue - initialMotionValue) / frection;
                        newScrollValue = (int) (-dis - firstHeaderViewHeight);
                    }
                } else {
                    newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / frection);
                }
                itemDimension = getHeaderSize();
                break;
        }

        setHeaderScroll(newScrollValue);

        if (newScrollValue != 0 && !isRefreshing()) {
            float scale = Math.abs(newScrollValue) / (float) itemDimension;
            switch (mCurrentMode) {
                case PULL_FROM_END:
                    mFooterLayout.onPull(scale);
                    break;
                case PULL_FROM_START:
                default:
                    mHeaderLayout.onPull(scale);
                    mHeaderLayout.onMove(mLastMotionX, mLastMotionY, scale, getMode(), getState());
                    //监听拉动百分比
                    //huchenxi
                    if (mOnPullPercentListener != null) {
                        mOnPullPercentListener.onPullPercent(scale);
                    }
                    break;
            }

            if (mState != State.PULL_TO_REFRESH && itemDimension >= Math.abs(newScrollValue)) {
                setState(State.PULL_TO_REFRESH);
            } else if ((mState == State.PULL_TO_REFRESH || mState == State.SHOW_HEADER) && itemDimension < Math.abs(
                    newScrollValue)) {
                setState(State.RELEASE_TO_REFRESH);
            }
        }
    }

    private LayoutParams getLoadingLayoutLayoutParams() {
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                return new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT);
            case VERTICAL:
            default:
                return new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
        }
    }

    private int getMaximumPullScroll() {
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                return Math.round(getWidth() / frection);
            case VERTICAL:
            default:
                return Math.round(getHeight() / frection);
        }
    }

    /**
     * Smooth Scroll to position using the specific duration
     *
     * @param scrollValue - Position to scroll to
     * @param duration    - Duration of animation in milliseconds
     */
    private final void smoothScrollTo(int scrollValue, long duration) {
        smoothScrollTo(scrollValue, duration, 0, null);
    }

    private final void smoothScrollTo(int newScrollValue, long duration, long delayMillis,
                                      OnSmoothScrollFinishedListener listener) {
        if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }

        final int oldScrollValue;
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                oldScrollValue = getScrollX();
                break;
            case VERTICAL:
            default:
                oldScrollValue = getScrollY();
                break;
        }

        if (oldScrollValue != newScrollValue) {
            if (null == mScrollAnimationInterpolator) {
                // Default interpolator is a Decelerate Interpolator
                mScrollAnimationInterpolator = new DecelerateInterpolator();
            }
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, listener);

            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                if (duration == 0) {
                    setHeaderScroll(0);
                } else {
                    post(mCurrentSmoothScrollRunnable);
                }
            }
        }
    }

    private final void smoothScrollToAndBack(int y) {
        smoothScrollTo(y, SMOOTH_SCROLL_DURATION_MS, 0, new OnSmoothScrollFinishedListener() {

            @Override
            public void onSmoothScrollFinished() {
                smoothScrollTo(0, SMOOTH_SCROLL_DURATION_MS, DEMO_SCROLL_INTERVAL, null);
            }
        });
    }

    /***
     * 重新设置状态
     * huchenxi
     */
    public void resetRefreshState() {
        setState(State.RESET);
    }

    public void setHeaderRefreshEnabled(boolean enable) {
        if (enable != mHeaderRefreshEnabled) {
            mHeaderRefreshEnabled = enable;
            if (mHeaderRefreshEnabled) {
                setState(State.RESET);
            } else {
                if (mState != State.RESET)
                    throw new RuntimeException("set refresh disable on reset state only");

                if (mHeaderLayout != null) mHeaderLayout.disableRefresh();
            }
        }
    }

    public void setFooterRefreshEnabled(boolean enable) {
        if (enable != mFooterRefreshEnabled) {
            mFooterRefreshEnabled = enable;
            if (mFooterRefreshEnabled) {
                setState(State.RESET);
            } else {
                if (mState != State.RESET) return;
                if (mFooterLayout != null) mFooterLayout.disableRefresh();
            }
        }
    }

    // ===========================================================
    // Inner, Anonymous Classes, and Enumerations
    // ===========================================================

    public void setOnPullPercentListener(OnPullPercentListener onPullPercentListener) {

        this.mOnPullPercentListener = onPullPercentListener;
    }

    public void setOnTouchDirectionListener(OnTouchDirectionListener onTouchDirectionListener) {
        this.mOnTouchDirectionListener = onTouchDirectionListener;
    }


    public static enum Orientation {
        VERTICAL, HORIZONTAL
    }

    /**
     * Simple Listener that allows you to be notified when the user has scrolled to the end of the
     * AdapterView. See ( {@link PullToRefreshAdapterViewBase#setOnLastItemVisibleListener}.
     *
     * @author Chris Banes
     */
    public static interface OnLastItemVisibleListener {

        /**
         * Called when the user has scrolled to the end of the list
         */
        public void onLastItemVisible();
    }


    /***
     * 手势的方向监听
     */
    public interface OnTouchDirectionListener {

        //是否需要拦截该滚动
        boolean onInterceptTouchScrollVertical(boolean isUp);

        //竖直方向(返回true拦截手势，返回false继续传递)
        boolean onTouchScrollVertical(float distance);

        //结束滑动
        void onTouchScrollFinish(boolean isUp);

    }


    static interface OnSmoothScrollFinishedListener {

        void onSmoothScrollFinished();
    }

    public interface OnPullPercentListener {
        //拉动百分比监听
        public void onPullPercent(float percent);
    }

    public abstract static class LoadingLayoutCreator {

        public abstract LoadingLayout create(Context context, boolean headerOrFooter, Orientation orientation);
    }

    final class SmoothScrollRunnable implements Runnable {

        private final Interpolator mInterpolator;

        private final int mScrollToY;

        private final int mScrollFromY;

        private final long mDuration;

        private OnSmoothScrollFinishedListener mListener;

        private boolean mContinueRunning = true;

        private long mStartTime = -1;

        private int mCurrentY = -1;

        public SmoothScrollRunnable(int fromY, int toY, long duration, OnSmoothScrollFinishedListener listener) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mInterpolator = mScrollAnimationInterpolator;
            mDuration = duration;
            mListener = listener;
        }

        @Override
        public void run() {

            /**
             * Only set mStartTime if this is the first time we're starting, else actually calculate
             * the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float calculations. We use
                 * 1000 as it gives us good accuracy and small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY =
                        Math.round((mScrollFromY - mScrollToY) * mInterpolator.getInterpolation(normalizedTime / 1000f));
                mCurrentY = mScrollFromY - deltaY;
                Log.d("currentY", mCurrentY + "     " + getScrollY());

                if (Math.abs(getScrollY()) >= Math.abs(mCurrentY) || getState() != State.RESET) {
                    Log.d("toY", mCurrentY + " ");
                    setHeaderScroll(mCurrentY);
                }
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                ViewCompat.postOnAnimation(PullToRefreshBase.this, this);
            } else {
                if (null != mListener) {
                    mListener.onSmoothScrollFinished();
                }
            }
        }

        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }
}
