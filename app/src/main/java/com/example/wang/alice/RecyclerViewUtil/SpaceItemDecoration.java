package com.example.wang.alice.RecyclerViewUtil;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Set space between items in RecyclerView
 * Created by Wang on 3/5/18.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;

        if (parent.getChildLayoutPosition(view) == 0){
            outRect.top = space;
        }else{
            outRect.top = 0;
        }
    }
}
