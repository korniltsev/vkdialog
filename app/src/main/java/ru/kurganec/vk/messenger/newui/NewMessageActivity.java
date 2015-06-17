package ru.kurganec.vk.messenger.newui;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
// import com.flurry.android.FlurryAgent;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.SearchUserEvent;
import ru.kurganec.vk.messenger.newui.fragments.FriendPickerFragment;
import ru.kurganec.vk.messenger.newui.fragments.OnlineFriendPickerFragment;
import ru.kurganec.vk.messenger.utils.SupportTabListener;
import ru.kurganec.vk.messenger.utils.VKActivity;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 21:12
 */
public class NewMessageActivity extends VKActivity implements SearchView.OnQueryTextListener {


    private static final String TAG = "VK-CHAT-COMPANION_PICKER";
    private final static String STATE_SELECTED_TAB = "selected tab";


    private int mSelectedTab = 0;

    private String mLastQuery;
    private Bundle mFragmentArgument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_companion_picker);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(Intent.EXTRA_STREAM)) {
            try {
                Uri imgToUpload = extras.getParcelable(Intent.EXTRA_STREAM);

                String filePath = null;
                String scheme = imgToUpload.getScheme();
                if (scheme.equals("content")){
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().
                            query(imgToUpload, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                     filePath= cursor.getString(columnIndex);
                    cursor.close();

                } else if (scheme.equals("file")){
                    filePath = imgToUpload.getPath()    ;
                } else {
                    // FlurryAgent.logEvent("Unknown gallery return type - " + imgToUpload.toString());
                }
                mFragmentArgument = new Bundle();
                mFragmentArgument.putString(ChatActivity.EXTRA_IMAGE_ATTACH, filePath);
                Log.d(TAG, filePath);

            } catch (Exception e) {
                // FlurryAgent.onError("gallery-uri-error", e.getMessage(), "todo-class");
            }
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        addTabs();


        if (savedInstanceState != null) {
            mSelectedTab = savedInstanceState.getInt(STATE_SELECTED_TAB);
        }
        getSupportActionBar().setSelectedNavigationItem(mSelectedTab);


    }

    private void addTabs() {
        ActionBar ab = getSupportActionBar();





        ab.addTab(ab.newTab().
                setText(getString(R.string.friends)).
                setTabListener(new SupportTabListener<FriendPickerFragment>(this,
                        FriendPickerFragment.class.getName(),
                        FriendPickerFragment.class,
                        mFragmentArgument)
                ));
        ab.addTab(ab.newTab().
                setText(getString(R.string.friends_online)).
                setTabListener(new SupportTabListener<OnlineFriendPickerFragment>(this,
                        OnlineFriendPickerFragment.class.getName(),
                        OnlineFriendPickerFragment.class,
                        mFragmentArgument)
                ));

    }


    @Override
    protected String getCustomTitle() {
        return getString(R.string.pick_user);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());


        getSupportActionBar().removeAllTabs();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.pick, menu);
        MenuItem search = menu.findItem(R.id.menu_search);
        SearchView sv = (SearchView) search.getActionView();
        sv.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;


        }

        return true;
    }

    public String getLastQuery() {
        return mLastQuery;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mLastQuery = query;
        VK.bus().post(new SearchUserEvent(query));
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            VK.bus().post(new SearchUserEvent(null));
            mLastQuery = null;
        }
        return true;
    }


}
