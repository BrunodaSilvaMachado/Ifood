package com.cursoandroid.ifood.activity.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecyclerView {
    public interface OnSwipedListener{
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder);
    }
    public static void onSwipe(@NonNull RecyclerView recyclerView, OnSwipedListener swipedListener){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (swipedListener != null) {
                    swipedListener.onSwiped(viewHolder);
                }
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }
}
