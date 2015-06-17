package ru.kurganec.vk.messenger.newui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.viewpagerindicator.TitlePageIndicator;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyOnPageChangeListener;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyTextWatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 06.11.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class FriendsPagerFragment extends Fragment {
    public static final String STATE_SELECTED_TAB = "ru.kurganec.vk.messenger.newui.fragments.selected_tab";
    private static final String STATE_SEARCH_QUERY = "ru.kurganec.vk.messenger.newui.fragments.search_query";
    ;

    TitlePageIndicator mIndicator;
    ViewPager mPager;
    EditText mInputSearch;
    View mClearInputBtn;
    private Set<SearchProfileListener> mSearchListeners = new HashSet<SearchProfileListener>();
    private String mQuery = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_pager, container, false);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        mIndicator = (TitlePageIndicator) root.findViewById(R.id.indicator);
        mInputSearch = (EditText) root.findViewById(R.id.input_search);
        mClearInputBtn = root.findViewById(R.id.btn_clear);
        mClearInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputSearch.setText("");
            }
        });
        mInputSearch.addTextChangedListener(new EmptyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mQuery = s.toString();
                for (SearchProfileListener searchListener : mSearchListeners) {
                    dispatchSearchQuery(searchListener);
                }
            }
        });
        mPager = (ViewPager) root.findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapter());
        mPager.setOnPageChangeListener(new EmptyOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
            }
        });
        mIndicator.setViewPager(mPager);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_TAB)) {
                mPager.setCurrentItem(savedInstanceState.getInt(STATE_SELECTED_TAB), false);
            }
            if (savedInstanceState.containsKey(STATE_SEARCH_QUERY)){
                mQuery = savedInstanceState.getString(STATE_SEARCH_QUERY);
            }
        }

    }

    public void dispatchSearchQuery(SearchProfileListener searchListener) {
        if (TextUtils.isEmpty(mQuery)) {
            searchListener.showAll();
        } else {
            searchListener.showQueryResults(mQuery);
        }
    }


    public void registerSearchListener(SearchProfileListener friendPickerFragment) {
        mSearchListeners.add(friendPickerFragment);
    }


    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter() {
            super(getChildFragmentManager());
        }

        @Override
        public Fragment getItem(int i) {
            Fragment ret = Fragment.instantiate(getActivity(), i == 0 ? FriendPickerFragment.class.getName()
                    : OnlineFriendPickerFragment.class.getName());
            ret.getId();
            return ret;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.friends);
                case 1:
                    return getString(R.string.friends_online);
                default:
                    throw new IllegalArgumentException("wrong position, we only have 2 fragments");
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, mPager.getCurrentItem());
        outState.putString(STATE_SEARCH_QUERY, mQuery);
    }


    public static interface SearchProfileListener {

        public void showAll();

        public void showQueryResults(String q);
    }
}
