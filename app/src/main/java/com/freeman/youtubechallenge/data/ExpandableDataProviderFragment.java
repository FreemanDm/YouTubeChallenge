package com.freeman.youtubechallenge.data;

import android.os.Bundle;
import android.support.v4.app.Fragment;



public class ExpandableDataProviderFragment extends Fragment {
    private ExpandableDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mDataProvider = new ExpandableDataProvider();
    }

    public AbstractExpandableDataProvider getDataProvider() {
        return mDataProvider;
    }
}
