package com.eziot.demo.widget.pulltorefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eziot.iotsdkdemo.R;


public class PullToRefreshHeader extends LoadingLayout {

    public enum Style {
        NORMAL, NO_TIME, MORE, SHOW_REFRESH
    }

    private PullToLoadView mArrowImageView;
    private LoadingView mProgressBar;
    private TextView mHintTextView, mHeaderTimeView, mHintMoreView;
    private ViewGroup mHeaderTimelayout;

    private Style mStyle = Style.NORMAL;

    public PullToRefreshHeader(Context context) {
        this(context, Style.NORMAL);
    }

    public PullToRefreshHeader(Context context, Style style) {
        super(context, true, PullToRefreshBase.Orientation.VERTICAL);
        setContentView(R.layout.pull_to_refresh_header);

        mArrowImageView = (PullToLoadView) findViewById(R.id.header_arrow);
        mHintTextView = (TextView) findViewById(R.id.header_hint);
        mHeaderTimeView = (TextView) findViewById(R.id.header_time);
        mProgressBar = (LoadingView) findViewById(R.id.header_progress);
        mHeaderTimelayout = (ViewGroup) findViewById(R.id.header_time_layout);
        mHintMoreView = (TextView) findViewById(R.id.header_hint_more);

        if (style == Style.NO_TIME) {
            mHeaderTimelayout.setVisibility(View.GONE);
        } else if (style == Style.MORE) {
            mHeaderTimelayout.setVisibility(View.GONE);
        } else if (style == Style.SHOW_REFRESH) {
            mHeaderTimelayout.setVisibility(View.GONE);
            mHintTextView.setVisibility(View.VISIBLE);
        } else {
            mHeaderTimelayout.setVisibility(View.INVISIBLE);
        }
        mStyle = style;
    }

    @Override
    public void pullToRefresh() {
        if (mStyle == Style.MORE) {
            mHintTextView.setText(R.string.xlistview_header_hint_more);
        } else {
            mHintTextView.setText(R.string.xlistview_header_hint_normal);
        }
        mArrowImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshing() {
        mHintTextView.setText(R.string.xlistview_header_hint_loading);
        mArrowImageView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void releaseToRefresh() {
        if (mStyle == Style.MORE) {
            mHintTextView.setText(R.string.xlistview_footer_hint_ready);
        } else {
            mHintTextView.setText(R.string.xlistview_header_hint_ready);
        }
    }

    @Override
    public void onPull(float scaleOfLayout) {
        mArrowImageView.drawProgress(scaleOfLayout);
    }

    @Override
    public void reset() {
        if (mStyle == Style.MORE) {
            mHintTextView.setText(R.string.xlistview_header_hint_more);
        } else {
            mHintTextView.setText(R.string.xlistview_header_hint_normal);
        }
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mHintMoreView.setVisibility(View.GONE);
    }

    @Override
    public void disableRefresh() {
        if (mStyle == Style.MORE) {
            mHintMoreView.setVisibility(View.VISIBLE);
            mHintTextView.setText(R.string.xlistview_footer_no_more);
        }
    }

    public void setLastRefreshTime(CharSequence text) {
        mHeaderTimeView.setText(text);
        mHeaderTimelayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }
}