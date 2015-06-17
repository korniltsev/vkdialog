package ru.kurganec.vk.messenger.newui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.UserTypingEvent;
import ru.kurganec.vk.messenger.model.db.Profile;
import ru.kurganec.vk.messenger.newui.adapters.HistoryAdapter;
import ru.kurganec.vk.messenger.newui.dialogs.AttachPopUp;
import ru.kurganec.vk.messenger.utils.*;
import ru.kurganec.vk.messenger.utils.emoji.Emoji;
import ru.kurganec.vk.messenger.utils.emoji.Popup;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyTarget;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyTextWatcher;

//import com.google.common.eventbus.Subscribe;
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.ImageSize;

// import com.flurry.android.FlurryAgent;

/**
 * User: anatoly
 * Date: 18.07.12
 * Time: 18:04
 */
public class ChatActivity extends VKActivity implements AttachPopUp.AttachActionListener {
    private static final String TAG = "VK-CHAT-CONVERSATION";
    private static final int REQUEST_PICK_FROM_GALLERY = 0;
    private static final int REQUEST_CAMERA_CAPTURE = 1;
    private static final int REQUEST_PICK_LOCATION = 2;
    public static final String EXTRA_UID = "uid";
    public static final String EXTRA_CHAT_ID = "chat_id";
    public static final String EXTRA_MID = "mid";
    private static final String STATE_FIRST = "first";
    public static final String EXTRA_IMAGE_ATTACH = "extra-image-attach";

    private ChatObserver mObserver = new ChatObserver();
    private HistoryAdapter mAdapter;

    private EditText mMessageInput;

    private Button mAddSmileButton;

    private Button mHeaderButton;
    private View mHeaderProgressBar;
    private View mHeader;

    private Long mChatId;
    private Long mUid;

    private String mTitle;
    private int mAllCount;

    private AttachHandler mAttachHandler;

    private AttachPopUp mAttachPopUp;
    private Popup mEmojiPopup;
    private Uri mImageUri;
    private ProgressDialog mImageProgress;


    private Long mMidToDisplay;
    private long lastTypeTime;
    private TextView mFooter;
    private View mSendBar;
    private SlidingMenu sideMenu;
    private ColorDrawable windowBg = new ColorDrawable(Color.BLACK);


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_FIRST, getListView().getFirstVisiblePosition());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeUtil.refresh();
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        if (extras.containsKey(EXTRA_UID)) {
            mUid = extras.getLong(EXTRA_UID);
            mTitle = VK.db().getUserDisplayInfo(mUid);
        } else {
            mChatId = extras.getLong(EXTRA_CHAT_ID);
            mTitle = VK.db().getChatDisplayInfo(mChatId);
        }
        setupActionBar();
        mHeader = getLayoutInflater().inflate(R.layout.view_history_header, null, false);
        mHeaderButton = (Button) mHeader.findViewById(R.id.btn_get_history);
        mHeaderProgressBar = mHeader.findViewById(R.id.pb);
        mHeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHistory();
            }
        });
        getListView().addHeaderView(mHeader);

        mFooter = (TextView) getLayoutInflater().inflate(R.layout.view_user_typing_status, null, false);
        getListView().addFooterView(mFooter);

        Cursor c = getCursor();
        mAdapter = new HistoryAdapter(this, c);
        setListAdapter(mAdapter);

        if (extras.containsKey(EXTRA_MID) && savedInstanceState == null) {
            mMidToDisplay = extras.getLong(EXTRA_MID);
            int pos = VK.db().msg().getOffset(mMidToDisplay, mUid, mChatId);
            getListView().setSelection(pos);
        } else if (savedInstanceState != null) {
            int pos = savedInstanceState.getInt(STATE_FIRST);
            getListView().setSelection(pos);
        }

        mMidToDisplay = null;
        showHeaderProgress();
        downloadHistory(0);
        getListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        getListView().setDivider(null);
        getListView().setDividerHeight(0);


        mMessageInput = (EditText) findViewById(R.id.input_msg);
        mMessageInput.addTextChangedListener(new EmptyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //do this to mark messages as read
                if (VK.model().isInvisible()){
                    return;
                }
                mAdapter.onScrollStateChanged(getListView(), AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                long time = System.currentTimeMillis();
                if (time - lastTypeTime > 6 * 1000 ) {
                    lastTypeTime = time;
                    VK.actions().iAmTyping(mUid, mChatId);
                }
            }
        });

        mAttachPopUp = AttachPopUp.build(this);
        mAttachPopUp.setAttachListener(this);
        mEmojiPopup = Emoji.buildPopup(this);
        mEmojiPopup.setEditor(mMessageInput);

        mAttachHandler = new AttachHandler((FrameLayout) findViewById(R.id.wrapper_attach));
        if (extras.containsKey(EXTRA_IMAGE_ATTACH)) {
            mAttachHandler.handlePhoto(extras.getString(EXTRA_IMAGE_ATTACH));
            mAttachHandler.show();
            // FlurryAgent.logEvent("attached photo from gallery");
        }

        getListView().setOnScrollListener(mAdapter);

        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (view.getTag() != null) {
                    showCopyMessageDialog((HistoryAdapter.MessageHolder) view.getTag());
                    return true;
                }
                return false;
            }
        });

        mSendBar = findViewById(R.id.send_bar);


        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        createSlideMenu();
    }




    private void showCopyMessageDialog(final HistoryAdapter.MessageHolder tag) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.copy_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //copy message
                        if (TextUtils.isEmpty(tag.messageBody)) {
                            return;
                        }
                        VKUtils.copyMessage(ChatActivity.this, tag);

                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void setupActionBar() {

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(mTitle);

        if (mChatId != null) {
            return;//show images only for single user chats
        }
        Cursor client = VK.db().profiles().get(mUid);
        String photo;
        if (client.moveToFirst()) {
            photo = client.getString(client.getColumnIndex(Profile.PHOTO_BIG));
        } else {
            photo = "http://vk.com/images/camera_a.gif";//не должно никогда случаться =( тока блин репорты приходят((
            VK.actions().retrieveProfile(VK.model().getUserID());
            // FlurryAgent.logEvent("no profile");
        }
        Picasso.with(this)
                .load(photo)
                .into(new EmptyTarget(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ab.setIcon(new BitmapDrawable(VKUtils.cropChatBitmap(bitmap)));//todo
                    }
                });
    }


    private void setListAdapter(HistoryAdapter mAdapter) {
        getListView().setAdapter(mAdapter);

    }

    private ListView getListView() {
        return (ListView) findViewById(android.R.id.list);
    }

    private void downloadHistory(Integer from) {
        VK.actions().getHistory(mUid, mChatId, 40, from == null ? mAdapter.getCount() : 0, VK.model().isInvisible());
    }

    private void showHeaderButton() {
        mHeaderButton.setVisibility(View.VISIBLE);
        mHeaderProgressBar.setVisibility(View.GONE);
    }

    private void showHeaderProgress() {
        mHeaderButton.setVisibility(View.GONE);
        mHeaderProgressBar.setVisibility(View.VISIBLE);
    }

    private void removeHeader() {
        getListView().removeHeaderView(mHeader);

    }


    @Override
    protected void onResume() {
        super.onResume();
        VK.actions().registerObserver(mObserver);
        VK.bus().register(mObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.actions().unRegisterObserver(mObserver);
        VK.bus().unregister(mObserver);
    }


    @Override
    protected String getCustomTitle() {
        return mTitle;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                sideMenu.showMenu(true);
                return true;
            }
            case R.id.menu_attach: {
                showAttachWindow();
                return true;
            }
            case R.id.menu_emoji: {
                shoEmojiWindow();
                return true;
            }

        }
        return false;
    }

    private void shoEmojiWindow() {
        int[] loc = new int[2];
        View ab = getListView();
        ab.getLocationOnScreen(loc);
        mEmojiPopup.show(loc[1]);
        mMessageInput.requestFocus();
    }

    private void showAttachWindow() {
        int[] loc = new int[2];
        View ab = getListView();
        ab.getLocationOnScreen(loc);
        mAttachPopUp.show(loc[1]);


    }

    public void getHistory() {
        downloadHistory(null);
        showHeaderProgress();
    }


    private Cursor getCursor() {
        if (mChatId != null) {
            return VK.db().getGroupChatHistory(mChatId);
        } else {
            return VK.db().getChatHistory(mUid);
        }
    }


    public void sendMessage() {
        getListView().setSelection(mAdapter.getCount());
        VK.actions().sendMessage(mUid, mChatId, mMessageInput.getText().toString(),
                mAttachHandler.getFileNames(),
                mAttachHandler.getForward(),
                mAttachHandler.getGeo()
        );
        if (mAttachHandler.getFileNames().size() > 0) {
            showProgressBar(mAttachHandler.getFileNames().size());
        }
        mAttachHandler.clear();
        mAttachHandler.hide();

        mMessageInput.setText("");
    }

    private void showProgressBar(int size) {
        mImageProgress = new ProgressDialog(this);
        mImageProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mImageProgress.setMessage(getString(R.string.uploading_pictures));
        mImageProgress.setCancelable(true);
        mImageProgress.setMax(size);
        mImageProgress.show();
        mImageProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                VK.actions().cancelImageUploads();
            }
        });

    }


    @Override
    public void onAction(AttachPopUp.Action a) {
        switch (a) {
            case ACTION_GALLERY: {
                requestGallery();
                break;
            }
            case ACTION_PHOTO: {
                requestPhoto();
                break;
            }
            case ACTION_GEO: {
                requestGeo();
                break;
            }
        }
    }

    private void requestGallery() {
        Intent takePictureFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(takePictureFromGalleryIntent, REQUEST_PICK_FROM_GALLERY);
    }

    private void requestPhoto() {
        ContentValues values = new ContentValues();
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            //камера может работать только с sd карточкой.
            // Есть хак http://stackoverflow.com/questions/5252193/trouble-writing-internal-memory-android
            // но лучше я оставлю все так
            mImageUri =
                    getContentResolver().
                            insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_CAPTURE);
        } else {
            //show message "no sd card"
            Toast.makeText(this, getString(R.string.mount_sdcard), Toast.LENGTH_LONG).show();
            mAttachPopUp.dismiss();
        }


    }

    private void requestGeo() {
        Intent i = new Intent(this, LocationActivity.class);
        i.setAction(Intent.ACTION_PICK);
        startActivityForResult(i, REQUEST_PICK_LOCATION);
    }

    private class UserTypingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException ignored) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            mLastShowFooterTask = this;
            if (!footerIsVisible) {
                footerIsVisible = true;
                mFooter.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mLastShowFooterTask == this) {
                footerIsVisible = false;
                mFooter.setVisibility(View.GONE);
            }
        }
    }

    private boolean footerIsVisible = false;
    private UserTypingTask mLastShowFooterTask;

    private class ChatObserver extends BaseActionsObserver {

//        @SuppressWarnings("unchecked")
        @Subscribe
        public void someOneIsTyping(UserTypingEvent e) {
            if (mChatId != null && mChatId.equals(e.getChatId())) {
                mFooter.setText(getString(R.string.group_user_is_typing));//TODO
            } else if (mChatId == null && e.getChatId() == null && mUid.equals(e.getUid())) {
                mFooter.setText(getString(R.string.user_is_typing, mTitle));
            }
            new UserTypingTask().execute();

        }


        @Override
        public void messageChanged(Bundle data) {
            if (isTheChatTarget(data)) {
                reQuery();
            }
        }

        @Override
        public void gotHistory(Bundle data) {
            if (isTheChatTarget(data)) {
                reQuery();
                mAllCount = data.getInt("all_count");
                if (mAdapter.getCount() >= mAllCount) {
                    removeHeader();
                } else {
                    showHeaderButton();
                }
            }
        }

        @Override
        public void imageUploaded(Bundle resultData) {
            if (isTheChatTarget(resultData) && mImageProgress != null) {
                mImageProgress.incrementProgressBy(1);
            }
        }

        @Override
        public void messageDelivered(Bundle resultData) {


            if (isTheChatTarget(resultData) && mImageProgress != null) {
                if (mImageProgress.isShowing()) {
                    mImageProgress.dismiss();
                }
                mImageProgress = null;
            }
        }

        boolean isTheChatTarget(Bundle data) {
            Long uid = data.getLong("uid");
            Long chat_id = data.getLong("chat_id");
            if ((chat_id != null && chat_id.equals(mChatId)) ||
                    (uid != null && uid.equals(mUid))) {
                return true;
            } else {
                return false;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAttachPopUp.isShowing()) {
            mAttachPopUp.dismiss();
        }
        if (resultCode == Activity.RESULT_OK) {
            mAttachHandler.show();
            switch (requestCode) {
                case REQUEST_PICK_FROM_GALLERY: {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().
                            query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();
                        //filePath
                        mAttachHandler.handlePhoto(filePath);
                    } else {
                        try {
                            mAttachHandler.handlePhoto(data.getData().getPath());
                        } catch (Exception e) {
                            Toast.makeText(this, R.string.unable_to_load_picture_from_gallery, Toast.LENGTH_LONG).show();
                            // FlurryAgent.logEvent("gallery_pick_fail");
                            // FlurryAgent.logEvent("gallery_pick_fail" + data.toString());
                            // FlurryAgent.logEvent("gallery_pick_fail" + data.toString());

                        }
                    }
                    break;
                }
                case REQUEST_CAMERA_CAPTURE: {
//                    try {
                    Uri selectedImage = mImageUri;
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().
                            query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    //filePath
                    mAttachHandler.handlePhoto(filePath);
                    //TODO

                    break;
                }
                case REQUEST_PICK_LOCATION: {
                    int lat = data.getIntExtra(LocationActivity.EXTRA_LATITUDE, 0);
                    int lon = data.getIntExtra(LocationActivity.EXTRA_LONGITUDE, 0);
                    mAttachHandler.handleGeo(lat, lon);
                    break;
                }
            }
        }
    }

    private void reQuery() {
        Log.d(TAG, "REQUERRY");
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

        Cursor c = getCursor();
        mAdapter.changeCursor(c);

        int newCount = mAdapter.getCount();
        int showPosition = newCount - oldCount + firstVisiblePosition + 1;//header


        getListView().setSelectionFromTop(showPosition, top);


    }

    private void createSlideMenu() {
        sideMenu = new SlidingMenu(this);
        sideMenu.setMode(SlidingMenu.LEFT);
        sideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sideMenu.setFadeDegree(0.35f);
        sideMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        sideMenu.setMenu(R.layout.activity_chat_menu);
        sideMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                overridePendingTransition(0,0);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        sideMenu.showMenu(true);
    }
}

