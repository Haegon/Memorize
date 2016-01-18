package com.gohn.memorize.activity.base.observable;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.gohn.memorize.activity.base.ActionBarActivity;

/**
 * Created by Vayne on 16. 1. 18..
 */
public class ObservableListActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    protected ObservableListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setListView(ObservableListView listView) {
        this.listView = listView;
        listView.setScrollViewCallbacks(this);
    }

    protected ObservableListView getListView(){
        return this.listView;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }
    }
}
