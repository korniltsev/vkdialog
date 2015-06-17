package ru.kurganec.vk.messenger.newui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.newui.ChatActivity;
import ru.kurganec.vk.messenger.newui.adapters.ConversationsAdapter;
import ru.kurganec.vk.messenger.utils.BaseActionsObserver;
import ru.kurganec.vk.messenger.utils.VKActivity;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyAnimatorListener;
import ru.kurganec.vk.messenger.widget.TwoElementLargerListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 18:30
 */
public class ConversationsFragment extends SherlockListFragment implements AbsListView.OnScrollListener {
    private static final String STATE_FIRST_VISIBLE = "ru.kurganec.vk.messenger.ConversationsFragment.FirstVisible";
    private static final String STATE_FIRST_VISIBLE_PIXELS_OFFSET = "ru.kurganec.vk.messenger.ConversationsFragment.FirstVisibleOffset";

    private Cursor mDataCursor;
    private ConversationsAdapter mAdapter;
    private ConversationsObserver mObserver = new ConversationsObserver();

    private int mFirstVisible = 0;
    private int mFirstVisibleOffset = 0;
    private TwoElementLargerListView mList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mFirstVisible = savedInstanceState.getInt(STATE_FIRST_VISIBLE);
            mFirstVisibleOffset = savedInstanceState.getInt(STATE_FIRST_VISIBLE_PIXELS_OFFSET);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        VK.actions().registerObserver(mObserver);
        VK.bus().register(mObserver);
        VK.actions().performMainAction(VK.model().isInvisible());
        ((VKActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
        if (mAdapter == null) {
            mDataCursor = VK.db().queryConversations();
            mAdapter = new ConversationsAdapter(getActivity(), mDataCursor, mList);
            setListAdapter(mAdapter);
            mList.setSelectionFromTop(mFirstVisible, mFirstVisibleOffset);
        } else {
            performDBQuery();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        VK.actions().unRegisterObserver(mObserver);
        VK.bus().unregister(mObserver);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FIRST_VISIBLE, getListView().getFirstVisiblePosition());
        outState.putInt(STATE_FIRST_VISIBLE_PIXELS_OFFSET, getFirstVisibleChildOffset());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDataCursor != null) { // i really don't know how i got here npe
            mDataCursor.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mList = (TwoElementLargerListView) inflater.inflate(R.layout.fragment_conversations, container, false);
        mList.setDivider(null);
        mList.setDividerHeight(0);
        mList.setOnScrollListener(this);
        mList.extend(getActivity());
        return mList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ConversationsAdapter.ConversationHolder h = (ConversationsAdapter.ConversationHolder) view.getTag();
                if (h == null)
                    return false;
                showDeleteHistoryDialog(h, view, position);

                return true;
            }
        });
    }

    private AlertDialog showDeleteHistoryDialog(final ConversationsAdapter.ConversationHolder h, final View view, final int position) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.delete_history))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VK.db().deleteConversation(h.uid, h.chat_id);
                        VK.actions().deleteWholeConversation(h.uid, h.chat_id);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
                            animateDeletedHistory(view, position);
                        } else {
                            mObserver.reQuery();
                        }
                    }
                }).setNegativeButton(R.string.no, null)
                .show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateDeletedHistory(final View view, int position) {

        AnimatorSet s = new AnimatorSet();
        ObjectAnimator slide = ObjectAnimator.ofFloat(view, "translationX", view.getWidth())
                .setDuration(150);

        Collection<Animator> conversationsBelow = new ArrayList<Animator>();
        final List<View> animatedViews = new ArrayList<View>();
        for (int i = 0; i < mList.getChildCount(); i++) {
            View childAt = mList.getChildAt(i);
            if (mList.getPositionForView(childAt) > position){
                animatedViews.add(childAt);
                conversationsBelow.add(ObjectAnimator.ofFloat(childAt, "translationY", -view.getHeight())
                        .setDuration(300));
            }
        }

        AnimatorSet slideUp = new AnimatorSet();
        slideUp.playTogether(conversationsBelow);
        s.playSequentially(slide, slideUp);
        s.addListener(new EmptyAnimatorListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setTranslationX(0);
                for (View animatedView : animatedViews) {
                    animatedView.setTranslationY(0);
                }
                mObserver.reQuery();
            }
        });
        s.start();


    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        ConversationsAdapter.ConversationHolder holder = (ConversationsAdapter.ConversationHolder) view.getTag();
        if (null != holder) {
            Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
            if (holder.chat_id != null) {
                //start group chat
                chatIntent.putExtra(ChatActivity.EXTRA_CHAT_ID, holder.chat_id);
            } else {
                //start user chat
                chatIntent.putExtra(ChatActivity.EXTRA_UID, holder.uid);
            }
            startActivity(chatIntent);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        mAdapter.onScrollStateChanged(absListView, i);
    }

//    private boolean mMonitorListIncreasing = true;

    @Override
    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int countAll) {
//        if (firstVisible + visibleCount >= countAll && visibleCount > 0 && mMonitorListIncreasing) {
//            Integer restConversationsCount = VK.model().getConversationCount();
//            if (restConversationsCount == null || mDataCursor.getCount() < VK.model().getConversationCount()) {
//                querry new
//                VK.actions().getDialogs(mDataCursor.getCount());
//            } else {
//                mList.removeFooterView(mFooter);
//            }
//            mMonitorListIncreasing = false;
//        }
    }


    private class ConversationsObserver extends BaseActionsObserver {

        @Override
        public void messageChanged(Bundle uid) {
            performDBQuery();
        }

        @Override
        public void gotDialogs() {
            performDBQuery();
        }

        @Override
        public void mainActionPerformed() {
            Log.d("VK-CHAT Conversation fragment", "main action performed");
            performDBQuery();
            ((VKActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        }

        @SuppressWarnings("unchecked")
        private void reQuery() {
            Cursor cursor = VK.db().queryConversations();

            mDataCursor.close();
            mDataCursor = cursor;

            int firstVisible = mList.getFirstVisiblePosition();

            int top = getFirstVisibleChildOffset();

            mAdapter.changeCursor(mDataCursor);
            mList.setSelectionFromTop(firstVisible, top);

//            if (mDataCursor != null &&
//                    VK.model().getConversationCount() != null &&
//                    mDataCursor.getCount() >= VK.model().getConversationCount()) {
//                mMonitorListIncreasing = false;
//                mList.removeFooterView(mFooter);
//            } else {
//                mMonitorListIncreasing = true;
//            }
        }


    }

    private void performDBQuery() {
        mObserver.reQuery();
    }


    public int getFirstVisibleChildOffset() {
        int top = 0;
        View v = mList.getChildAt(0);
        if (v != null) {
            Rect r = new Rect();
            Point offset = new Point();
            mList.getChildVisibleRect(v, r, offset);
            top = offset.y;
        }
        return top;
    }
}
