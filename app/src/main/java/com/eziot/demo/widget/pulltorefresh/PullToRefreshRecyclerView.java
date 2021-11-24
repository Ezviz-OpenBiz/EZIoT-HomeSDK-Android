package com.eziot.demo.widget.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {

    public PullToRefreshRecyclerView(Context context) {
        super(context);
        setScrollingWhileRefreshingEnabled(true);
    }

    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollingWhileRefreshingEnabled(true);
    }

    public PullToRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
        setScrollingWhileRefreshingEnabled(true);
    }



    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        if (mRefreshableView == null) {
            return Orientation.VERTICAL;
        } else {
            RecyclerView.LayoutManager layoutManager = mRefreshableView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).getOrientation() == GridLayoutManager.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            } else if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) layoutManager).getOrientation() == StaggeredGridLayoutManager.HORIZONTAL ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            } else {
                return Orientation.VERTICAL;
            }
        }
    }

    @Override
    protected final RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        final RecyclerView rv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            rv = new InternalRecyclerViewSDK9(context, attrs);
        } else {
            rv = new RecyclerView(context, attrs);
        }

        return rv;
    }

    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;

        } else {
            final View firstVisibleChild = mRefreshableView.getChildAt(0);
            if (firstVisibleChild != null && getFirstVisiblePosition() == 0) {
                return firstVisibleChild.getTop() >= mRefreshableView.getTop();
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            final int lastItemPosition = adapter.getItemCount() - 1;
            final int firstVisiblePosition = getFirstVisiblePosition();
            final int lastVisiblePosition = firstVisiblePosition + getRefreshableView().getLayoutManager().getChildCount() - 1;

            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            if (lastVisiblePosition == lastItemPosition) {
                final int childIndex = lastVisiblePosition - firstVisiblePosition;
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    public int getFirstVisiblePosition() {
        View firstVisibleChild = mRefreshableView.getChildAt(0);
        return firstVisibleChild != null ? mRefreshableView.getChildPosition(firstVisibleChild) : -1;
    }

    @TargetApi(9)
    final class InternalRecyclerViewSDK9 extends RecyclerView {

        public InternalRecyclerViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshRecyclerView.this, deltaX, scrollX, deltaY, scrollY,
                    isTouchEvent);

            return returnValue;
        }
    }
}
