package com.tapbi.spark.emojimashup.ui.custom;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CenterRcv {
    private final static int DIMENSION = 2;
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;

    public void scrollToCenter(LinearLayoutManager layoutManager, RecyclerView recyclerList, int clickPosition) {
        if (clickPosition == -1){
            return;
        }
        try {
            RecyclerView.SmoothScroller smoothScroller = createSnapScroller(recyclerList, layoutManager);
            if (smoothScroller != null) {
                smoothScroller.setTargetPosition(clickPosition);
                layoutManager.startSmoothScroll(smoothScroller);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private LinearSmoothScroller createSnapScroller(RecyclerView mRecyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
                action.update(snapDistances[HORIZONTAL], snapDistances[VERTICAL], 200, mDecelerateInterpolator);
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 0.015f;
            }
        };
    }

    private int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[DIMENSION];
        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }
        return out;
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter;
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            containerCenter = helper.getEnd() / 2;
        }
        return childCenter - containerCenter;
    }

}
