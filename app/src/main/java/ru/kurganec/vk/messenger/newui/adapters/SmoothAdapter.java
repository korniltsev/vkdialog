package ru.kurganec.vk.messenger.newui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;

/**
 * User: anatoly
 * Date: 23.07.12
 * Time: 1:23
 */
public abstract class SmoothAdapter extends CursorAdapter implements AbsListView.OnScrollListener {
    public static final String TAG = "VK-CHAT-SMOOTH_ADAPTER"               ;
    private ListView list;
    public SmoothAdapter(Context context, Cursor c, ListView v) {
        super(context, c, false);
        list = v;
    }

    protected int state = SCROLL_STATE_IDLE;

    @Override
    public void onScrollStateChanged(AbsListView absListView, int newState) {
        if (newState != SCROLL_STATE_FLING && state == SCROLL_STATE_FLING) {
            notifyDataSetChanged();
            Log.d(TAG, "INVALIDATE LIST");
//            absListView.requestLayout();
        }
        state = newState;

        if (state == SCROLL_STATE_FLING){
            Log.d(TAG, "FLING");
        }


    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
