package com.freeman.youtubechallenge;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.freeman.youtubechallenge.data.AbstractExpandableDataProvider;
import com.freeman.youtubechallenge.data.ExpandableDataProvider;
import com.freeman.youtubechallenge.widget.ExpandableItemIndicator;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.loopj.android.image.SmartImageView;





public class ExpandableAdapter extends AbstractExpandableItemAdapter<ExpandableAdapter.MyGroupViewHolder, ExpandableAdapter.MyChildViewHolder> {
    private static final String TAG = "ExpandableItemAdapter";
    private static final String YouTubeApiKey = "AIzaSyD_OcZ5Ar2XkreH-Y-leEAtoFMqlfNuZ0Y";
    private Activity activity;

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    private AbstractExpandableDataProvider mProvider;
    private View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = String.valueOf(((TextView)v.findViewById(android.R.id.text1)).getHint());
            if(YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(activity).equals(YouTubeInitializationResult.SUCCESS)){
                //This means that your device has the Youtube API Service (the app) and you are safe to launch it.
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(activity, YouTubeApiKey, url.split("/?v=")[1], 0, true, true);
                activity.startActivity(intent);
            }else{
                // If not - playing the video in webview
                MainActivity.content.setVisibility(View.GONE);
                MainActivity.webView.setVisibility(View.VISIBLE);
                MainActivity.webView.setWebViewClient(new WebViewClient());
                MainActivity.webView.getSettings().setJavaScriptEnabled(true);
                MainActivity.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                MainActivity.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                MainActivity.webView.setWebChromeClient(new WebChromeClient());
                MainActivity.webView.loadUrl(url);
            }

        }
    };

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public FrameLayout mContainer;
        public TextView mTextView;
        public SmartImageView mImageView;

        public MyBaseViewHolder(View v, View.OnClickListener clickListener) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mImageView = (SmartImageView) v.findViewById(R.id.list_item_image);
            mContainer.setOnClickListener(clickListener);
        }

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
            mImageView = (SmartImageView) v.findViewById(R.id.list_item_image);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator mIndicator;

        public MyGroupViewHolder(View v) {
            super(v);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            v.setOnClickListener(clickListener);
        }
    }

    public ExpandableAdapter(AbstractExpandableDataProvider dataProvider, Activity activity) {
        mProvider = dataProvider;
        this.activity = activity;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item, parent, false);
        return new MyChildViewHolder(v, mItemOnClickListener);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
                isExpanded = true;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
                isExpanded = false;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            holder.mIndicator.setExpandedState(isExpanded, animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final ExpandableDataProvider.ConcreteChildData item = (ExpandableDataProvider.ConcreteChildData) mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());
        holder.mTextView.setHint(item.getVideoUrl());

        holder.mImageView.setImageUrl(item.getImageUrl(), R.drawable.youtube);

        // set background resource (target view ID: container)
        int bgResId;
        bgResId = R.drawable.bg_item_normal_state;
        holder.mContainer.setBackgroundResource(bgResId);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getGroupItem(groupPosition).isPinned()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        return true;
    }
}
