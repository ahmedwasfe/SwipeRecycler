package ahmed.com.swiperecyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public abstract class SwipeRecyclerViewHelper extends ItemTouchHelper.SimpleCallback {


    private int btnWidth;
    private RecyclerView mRecyclerView;
    private List<MButton> mListMButton;
    private GestureDetector mGestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<MButton>> mMapButtonBuffer;
    private Queue<Integer> mRemoveQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (MButton mButton : mListMButton)
                if (mButton.onClick(e.getX(), e.getY()))
                    break;
            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (swipePosition < 0) return false;
            Point point = new Point((int) event.getRawX(), (int) event.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = mRecyclerView.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y) {
                    mGestureDetector.onTouchEvent(event);
                } else {
                    mRemoveQueue.add(swipePosition);
                    swipePosition = -1;
                }

            }
            return false;
        }
    };

    public SwipeRecyclerViewHelper(Context mContext, RecyclerView mRecyclerView, int btnWidth) {

        super(0, ItemTouchHelper.START);
        this.mRecyclerView = mRecyclerView;
        this.mListMButton = new ArrayList<>();
        this.mGestureDetector = new GestureDetector(mContext, gestureListener);
        this.mRecyclerView.setOnTouchListener(onTouchListener);
        this.mMapButtonBuffer = new HashMap<>();
        this.btnWidth = btnWidth;

        mRemoveQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer integer) {
                if (contains(integer))
                    return false;
                else
                    return super.add(integer);
            }
        };

        attachSwipe();
    }

    private void attachSwipe() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private synchronized void recoverSwipedItem(){
        while (!mRemoveQueue.isEmpty()){
            int pos = mRemoveQueue.poll();
            if (pos > -1)
                mRecyclerView.getAdapter().notifyItemChanged(pos);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        int position = viewHolder.getAdapterPosition();
        if (swipePosition != position)
            mRemoveQueue.add(swipePosition);
        swipePosition = position;
        if (mMapButtonBuffer.containsKey(swipePosition))
            mListMButton = mMapButtonBuffer.get(swipePosition);
        else
            mListMButton.clear();
        mMapButtonBuffer.clear();
        swipeThreshold = 0.5f * mListMButton.size()*btnWidth;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f*defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f*defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        int position = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (position < 0){
            swipePosition = position;
            return;
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if (dX < 0){
                List<MButton> mListBuffer = new ArrayList<>();
                if (!mMapButtonBuffer.containsKey(position)){
                    instantiateButton(viewHolder, mListBuffer);
                    mMapButtonBuffer.put(position, mListBuffer);
                }else{
                    mListBuffer = mMapButtonBuffer.get(position);
                }

                translationX = dX * mListBuffer.size() * btnWidth / itemView.getWidth();
                drawButton(c, itemView, mListBuffer, position, translationX);
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MButton> mListBuffer, int position, float translationX) {

        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / mListBuffer.size();
        for (MButton mButton : mListBuffer){
            float left = right - dButtonWidth;
            mButton.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), position);
            right = left;
        }
    }

    public abstract void instantiateButton(RecyclerView.ViewHolder viewHolder, List<MButton> mListMButton);


}
