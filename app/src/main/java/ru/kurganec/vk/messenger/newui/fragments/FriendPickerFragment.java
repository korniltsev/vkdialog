package ru.kurganec.vk.messenger.newui.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.Actions;
import ru.kurganec.vk.messenger.newui.ChatActivity;
import ru.kurganec.vk.messenger.newui.adapters.ProfilesAdapter;
import ru.kurganec.vk.messenger.newui.adapters.ProfilesAdapter.ProfileHolder;
import ru.kurganec.vk.messenger.utils.BaseActionsObserver;
import ru.kurganec.vk.messenger.widget.TwoElementLargerListView;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 23:06
 */
public class FriendPickerFragment extends SherlockListFragment implements FriendsPagerFragment.SearchProfileListener {
    private static final String STATE_FIRST_VISIBLE = "ru.kurganec.vk.messenger.ConversationsFragment.FirstVisible";
    private static final String STATE_FIRST_VISIBLE_PIXELS_OFFSET = "ru.kurganec.vk.messenger.ConversationsFragment.FirstVisibleOffset";
    public static final String TAG = "FriendPickerFragment";

    private int mFirstVisible = 0;
    private int mFirstVisibleOffset = 0;


    private ProfilesAdapter mAdapter;
    private Actions.Observer mObserver = new BaseActionsObserver() {
        @Override
        public void mainActionPerformed() {
            super.mainActionPerformed();
            reQuery();
        }
    };
    private String mLastQuery;

    private void reQuery() {
        Log.d(TAG, "Requery");
        int oldCount = mAdapter.getCount();
        int firstVisiblePosition = getListView().getFirstVisiblePosition();

        View v = getListView().getChildAt(1);
        int top = 0;
        if (v != null) {
            Rect r = new Rect();
            Point offset = new Point();
            getListView().getChildVisibleRect(v, r, offset);
            top = offset.y;
        }

        Cursor c;
        if (mLastQuery == null){
            c = getData();
        } else {
            c = getData(mLastQuery);
        }

        mAdapter.changeCursor(c);

        int newCount = mAdapter.getCount();
        int showPosition = newCount - oldCount + firstVisiblePosition + 1;//header


        getListView().setSelectionFromTop(showPosition, top);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ProfilesAdapter(getActivity(), null);
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_FIRST_VISIBLE)
                && savedInstanceState.containsKey(STATE_FIRST_VISIBLE_PIXELS_OFFSET)) {
            mFirstVisible = savedInstanceState.getInt(STATE_FIRST_VISIBLE);
            mFirstVisibleOffset = savedInstanceState.getInt(STATE_FIRST_VISIBLE_PIXELS_OFFSET);
        }

        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FriendsPagerFragment) {
            ((FriendsPagerFragment) parentFragment).registerSearchListener(this);
        }

        VK.actions().registerObserver(mObserver);
    }


    @Override
    public View onCreateView(LayoutInflater factory, ViewGroup container, Bundle savedInstanceState) {
        return factory.inflate(R.layout.fragment_companion_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        showAll();
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FriendsPagerFragment) {
            ((FriendsPagerFragment) parentFragment).dispatchSearchQuery(this);
        }
        ((TwoElementLargerListView) getListView()).extend(getActivity());

        setListAdapter(mAdapter);
        getListView().setOnScrollListener(mAdapter);

        if (savedInstanceState!= null){

            int position = savedInstanceState.getInt(STATE_FIRST_VISIBLE);
            int top = savedInstanceState.getInt(STATE_FIRST_VISIBLE_PIXELS_OFFSET);
            Log.d(TAG, String.format("scroll to %d,%d", position, top));
            getListView().setSelectionFromTop(position, top);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VK.actions().unRegisterObserver(mObserver);
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        ProfileHolder holder = (ProfileHolder) view.getTag();
        if (holder != null) {
            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
            chatIntent.putExtra(ChatActivity.EXTRA_UID, holder.uid);

            if (getArguments() != null) {
                chatIntent.putExtra(ChatActivity.EXTRA_IMAGE_ATTACH,
                        getArguments().getString(ChatActivity.EXTRA_IMAGE_ATTACH));
            }
            startActivity(chatIntent);
        }
    }


    @Override
    public void showAll() {
        mLastQuery = null;
        mAdapter.changeCursor(getData());
    }

    @Override
    public void showQueryResults(String q) {
        mLastQuery = q;
        Cursor data = getData(q);
        mAdapter.changeCursor(data);
    }


    protected Cursor getData() {
        return VK.db().profiles().queryFriends();
    }

    protected Cursor getData(String query) {
        return VK.db().profiles().queryFriends(query,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        VK.bus().register(this);
        getListView().setSelectionFromTop(mFirstVisible, mFirstVisibleOffset);
    }

    @Override
    public void onPause() {
        super.onPause();
        VK.bus().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getFragmentManager().beginTransaction().detach(this).commit(); //todo ftw?
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FIRST_VISIBLE, getListView().getFirstVisiblePosition());
        outState.putInt(STATE_FIRST_VISIBLE_PIXELS_OFFSET, getFirstVisibleChildOffset());
    }



    public int getFirstVisibleChildOffset() {
        int top = 0;
        View v = getListView().getChildAt(0);
        if (v != null) {
            Rect r = new Rect();
            Point offset = new Point();
            getListView().getChildVisibleRect(v, r, offset);
            top = offset.y;
        }
        return top;
    }

}
