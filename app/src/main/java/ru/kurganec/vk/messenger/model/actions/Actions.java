package ru.kurganec.vk.messenger.model.actions;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;
import ru.kurganec.vk.messenger.model.actions.events.AuthEvent;
import ru.kurganec.vk.messenger.model.actions.events.SearchMessageEvent;
import ru.kurganec.vk.messenger.model.actions.events.UserTypingEvent;
import ru.kurganec.vk.messenger.model.actions.events.VideoEvent;
import ru.kurganec.vk.messenger.model.classes.VKChat;
import ru.kurganec.vk.messenger.model.classes.VKProfile;
import ru.kurganec.vk.messenger.utils.Joiner;

import java.util.*;

import static ru.kurganec.vk.messenger.model.VKService.*;

/**
 * User: anatoly
 * Date: 10.06.12
 * Time: 23:18
 */


public class Actions {

    public static final int MIN_5 = 5000;
    private final Handler mHandler;


    public static interface Observer {
        void actionStarted(Bundle id);

        void actionSopped(Bundle id);

        void loggedIn();

        void loginFailed();

        boolean loggedOut();

        void captchaRequired(String captcha_img, long captcha_sid, Bundle resultData);

        void gotHistory(Bundle data);

        void messageChanged(Bundle uid);


        void friendListUpdated();


        void historyUpdated(long profileUID);

        void mainActionPerformed();


        void userIsTyping(long uid);

        void chatHistoryUpdated(long mChatId);

        void searchListUpdated();

        void searchResult(long taskId);

        void gotVideo(String video);

        void videoError();

        void chatUpdated(long chat_id);

        void gotDialogs();


        void signUpFail();

        void signUpOk();

        void signUpConfirmFail();

        void signUpConfirmed();

        void gotChatHistory(long chat_id, int all_count);

        void imageUploaded(Bundle resultData);

        void messageDelivered(Bundle resultData);

        void startedUploadingPhoto(Bundle resultData);

        void messageWasNotSent(Bundle resultData);


    }


    public static final String TAG = "VKACTIONS";
    private final Context mAppContext;
    private final VKServiceReceiver mResultReceiver;

    public Actions(Context appContext) {
        mAppContext = appContext;
        mHandler = new Handler();
        mResultReceiver = new VKServiceReceiver(mHandler);

    }

    public void signIn(String login, String pass, String captcha, Long sid) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("phone", login);
        args.putString("password", pass);
        if (captcha != null) {
            args.putString("captcha_key", captcha);
            args.putLong("captcha_sid", sid);
        }
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SIGN_IN);
        
        mAppContext.startService(i);

    }

    public void signIn(String login, String pass) {
        signIn(login, pass, null, null);
    }

    public void signOut(boolean restartApp) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putBoolean("restart", restartApp);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SIGN_OUT);
        
        mAppContext.startService(i);
    }

    public void performMainAction(boolean isInvisible) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putBoolean("isInvisible", isInvisible );
        i.putExtra(EXTRA_REQUEST_CODE, Request.PERFORM_MAIN_ACTION);
        i.putExtra(EXTRA_ARGS, args);
        mAppContext.startService(i);

         initGCM();
    }

    public void initGCM() {
        AccountManager am = AccountManager.get(mAppContext);
        Account[] googleAccounts = am.getAccountsByType("com.google");
        if (googleAccounts.length > 0 && VK.model().isAppSignedIn()) {
            GCMRegistrar.checkDevice(mAppContext);
            GCMRegistrar.checkManifest(mAppContext);
            GCMRegistrar.register(mAppContext, "465521759576");
        }




    }



    public void updateSearchList() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.UPDATE_SEARCH_LIST);
        
        mAppContext.startService(i);
    }

    public void getHistory(Long uid, Long chatId, int count, int offset, boolean isInvisible) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        if (chatId == null) {
            args.putLong("uid", uid);
        } else {
            args.putLong("chat_id", chatId);
        }

        args.putInt("count", count);
        args.putInt("offset", offset);
        args.putBoolean("isInvisible", isInvisible);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.GET_HISTORY);
        
        mAppContext.startService(i);
    }




    public void sendMessage(Long uid, Long chat_id, String msg, ArrayList<String> fileNames,
                            String forward, Integer[] geo) {
        if (msg == null || msg.length() <= 0) {
            if (fileNames.size() <= 0 && (forward == null || forward.length() <= 0)
                    && geo == null) {
                return;
            }
        }


        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        if (uid != null) {
            args.putLong("uid", uid);
        } else {
            args.putLong("chat_id", chat_id);
        }


        if (msg != null) {
            args.putString("msg", msg);
        }
        if (forward != null && forward.length() > 0) {
            args.putString("forward", forward);
        }

        if (fileNames != null && fileNames.size() > 0) {
            String[] arr = new String[fileNames.size()];
            fileNames.toArray(arr);
            args.putStringArray("photos", arr);
        }
        if (geo != null) {
            args.putInt("latitude", geo[0]);
            args.putInt("longitude", geo[1]);
        }

        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SEND_MESSAGE);
        
        mAppContext.startService(i);
    }



    public void setUserPhoto(String filePath) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("img", filePath);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SET_USER_PHOTO);
        
        mAppContext.startService(i);

    }

    public void searchUser(String q) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("q", q);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SEARCH_USERS);
        
        mAppContext.startService(i);
    }

    public void getLongPoll() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.GET_LONG_POLL);
        
        mAppContext.startService(i);
    }

    public void startLongPoll() {
        if (VK.model().getLongPollURI() == null) {
            getLongPoll();
        } else {
            Intent i = getDefaultIntent();
            i.putExtra(EXTRA_REQUEST_CODE, Request.START_LONG_POLL);
            
            mAppContext.startService(i);
        }
    }

    private void stopLongPoll() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.STOP_LONG_POLL);
        
        mAppContext.startService(i);
    }

    /**
     * id, args
     */
    private HashMap<Long, Bundle> actions = new HashMap<Long, Bundle>();

    public void registerGCM(String registrationId) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("registrationId", registrationId);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.REGISTER_C2DM);
        
        mAppContext.startService(i);
    }

    public void retrieveMessage(long messageID) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("mid", messageID);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.RETRIEVE_MESSAGE);
        
        mAppContext.startService(i);
    }

    public void iAmTyping(Long uid, Long chatId) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        if (uid != null) {
            args.putLong("uid", uid);
        } else {
            args.putLong("chat_id", chatId);
        }
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.I_AM_TYPING);
        
        mAppContext.startService(i);
    }

    public void markAsRead(List<Long> unread) {

        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("mids", Joiner.on(",").join(unread));
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.MARK_AS_READ);
        
        mAppContext.startService(i);

    }


    public void updateFriendshipRequests() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.UPDATE_FRIENDSHIP_REQUESTS);
        
        mAppContext.startService(i);
    }


    public int clientCount() {
        return mSubscribers.size();
    }

    public void getVideo(String ownerId, String vid) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("videos", Joiner.on("_").join(Arrays.asList(ownerId, vid)));
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.GET_VIDEO);
        
        mAppContext.startService(i);
    }

    public void unRegisterC2DM() {
        Intent i = getDefaultIntent();

        i.putExtra(EXTRA_REQUEST_CODE, Request.UN_REGISTER_C2DM);
        
        mAppContext.startService(i);

    }

    public void deleteWholeConversation(Long uid, Long chat_id) {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.DELETE_WHOLE_CONVERSATION);
        Bundle args = new Bundle();
        if(chat_id!= null){
            args.putLong("chat_id", chat_id);
        } else {
            args.putLong("uid", uid);
        }
        i.putExtra(EXTRA_ARGS, args);
        mAppContext.startService(i);
    }


    public void iAmTypingInChat(long chat_id) {
        //TODO

    }

    public void updateChat(long chat_id) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("chat_id", chat_id);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.UPDATE_CHAT);
        
        mAppContext.startService(i);

    }

    public void setChatName(String text, VKChat mChat) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("chat_id", mChat.getChat_id());
        args.putString("name", text);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SET_CHAT_NAME);
        
        mAppContext.startService(i);

    }

    public void deleteChatParticipant(VKChat mChat, VKProfile participant) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("chat_id", mChat.getChat_id());
        args.putLong("uid", participant.getUid());
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.DELETE_CHAT_PARTICIPANT);
        
        mAppContext.startService(i);

    }

    public void addParticipant(long uid, VKChat chat) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("chat_id", chat.getChat_id());
        args.putLong("uid", uid);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.ADD_CHAT_PARTICIPANT);
        
        mAppContext.startService(i);
    }

    public void addFriend(long uid) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("uid", uid);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.ADD_FRIEND);
        
        mAppContext.startService(i);
    }

    public void deleteFriend(long uid) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putLong("uid", uid);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.DELETE_FRIEND);
        
        mAppContext.startService(i);
    }

    public void getDialogs(int offset) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putInt("offset", offset);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.GET_DIALOGS);
        
        mAppContext.startService(i);
    }


    public void signUp(String login, String name, String lastName) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("login", login);
        args.putString("name", name);
        args.putString("last_name", lastName);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SIGN_UP);
        
        mAppContext.startService(i);

    }

    public void confirmSignUp(String login, String code, String pass) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("login", login);
        args.putString("code", code);
        args.putString("password", pass);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SIGN_UP_CONFIRM);
        
        mAppContext.startService(i);

    }

    public void retrieveProfile(Long userID) {


    }

    public void searchMessage(String query) {
        Intent i = getDefaultIntent();
        Bundle args = new Bundle();
        args.putString("q", query);
        i.putExtra(EXTRA_ARGS, args);
        i.putExtra(EXTRA_REQUEST_CODE, Request.SEARCH_MESSAGE);
        mAppContext.startService(i);

    }

    public void tryAddToGroup() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.ADD_TO_GROUP);
        mAppContext.startService(i);
    }


    /**
     * cancels sending last message with images
     */
    public void cancelImageUploads() {
        Intent i = getDefaultIntent();
        i.putExtra(EXTRA_REQUEST_CODE, Request.CANCEL_MESSAGE);
        mAppContext.startService(i);
        Log.d(TAG, "message sending cancelled ");
    }

    private Intent getDefaultIntent() {
        Intent i = new Intent(mAppContext, VKService.class);
        i.putExtra(EXTRA_RESULT_RECEIVER, mResultReceiver);
        return i;
    }

    private class VKServiceReceiver extends ResultReceiver {
        public VKServiceReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case Result.ACTION_STARTED: {
//                    Log.d("VKLOL-RESULT", "action started");
                    notifyActionStarted(resultData);
                    actions.put(resultData.getLong("id"), resultData);
                    break;
                }
                case Result.ACTION_FINISHED: {
//                    Log.d("VKLOL-RESULT", "action stopped");
                    notifyActionStopped(resultData);
                    actions.remove(resultData.getLong("id"));
                    if (actions.size() <= 0) {
                        Intent i = getDefaultIntent();
                        mAppContext.stopService(i);
                    }
                    break;
                }
                case Result.SIGN_IN_SUCCESSFUL: {
                    VK.bus().post(new AuthEvent(resultData, AuthEvent.Action.SIGNED_IN));
                    break;
                }
                case Result.SIGN_IN_FAILED: {
                    VK.bus().post(new AuthEvent(resultData, AuthEvent.Action.AUTH_FAILED));
                    break;
                }

                case Result.SIGN_OUT_SUCCESSFUL: {
                    VK.bus().post(new AuthEvent(resultData, AuthEvent.Action.SIGNED_OUT));
                    //TODO why this class does this
                    if (resultData.getBundle(EXTRA_ARGS).getBoolean("restart")) {
                        VK.restartApp();
                    }
                    break;
                }
                case Result.MainActionPerformed: {
//                    Log.d("VKLOL-RESULT", "MainTask Performed");
                    notifyMainActionPerformed();
                    break;

                }
                case Result.FRIEND_LIST_UPDATED: {
//                    Log.d("VKLOL-RESULT", "friend list updated");
                    notifyFriendsUpdated();
                    break;
                }
                case Result.SEARCH_LIST_UPDATED: {
//                    Log.d("VKLOL-RESULT", "search list updated");
                    notifySearchListUpdated();
                    break;
                }
                case Result.PROFILE_HISTORY_UPDATED: {
//                    Log.d("VKLOL-RESULT", "profileHistory updated");
                    notifyHistoryUpdated(resultData.getLong("uid"));
                    break;
                }

                case Result.CHAT_HISTORY_UPDATED: {
//                    Log.d("VKLOL-RESULT", "chat History updated");
                    notifyChatHistoryUpdated(resultData.getLong("chat_id"));
                    break;
                }
                case Result.SEARCH_RESULT: {
//                    Log.d("VKLOL-RESULT", "chat History updated");
                    notifySearchResult(resultData.getLong("id"));

                    break;
                }
                case Result.GOT_LONG_POLL: {
//                    Log.d("VKLOL-RESULT", "got long poll, starting...");
                    startLongPoll();
                    break;
                }

                case Result.MESSAGE_CHANGED: {
                    notifyMessageChanged(resultData);
                    break;
                }
                case Result.USER_IS_TYPING: {
//                    Log.d("VKLOL-RESULT", "USER_IS_TYPING.");
//                    notifyUserTyping(resultData.getLong("uid"));
                    VK.bus().post(new UserTypingEvent(resultData));
                    break;

                }
                case Result.NOTIFICATION_NEW_MESSAGE: {
//                    Log.d("VKLOL-RESULT", "USER_IS_TYPING.");
                    VK.notifications().notifyNewMessage();
                }

                case Result.GOT_VIDEO: {
                    VK.bus().post(new VideoEvent(resultData, VideoEvent.Action.GOT_VIDEO));
                    break;
                }

                case Result.VIDEO_ERROR: {
                    VK.bus().post(new VideoEvent(resultData, VideoEvent.Action.ERROR));
                    break;
                }

                case Result.CHAT_UPDATED: {
                    notifyChatUpdated(resultData.getLong("chat_id"));
                    break;
                }
                case Result.NEED_CAPTCHA: {
                    notifyKaptchaRequired(resultData.getString("captcha_img"),
                            resultData.getLong("captcha_sid"),
                            resultData);
                    break;
                }

                case Result.GOT_DIALOGS: {
                    notifyGotDialogs();
                    break;
                }
                case Result.GOT_HISTORY: {
                    notifyGotHistory(resultData);
                    break;
                }
                case Result.SIGN_UP_OK: {
                    notifySignUpOk();
                    break;
                }
                case Result.SIGN_UP_FAIL: {
                    notifySignUpFail();
                    break;
                }
                case Result.SIGN_UP_CONFIRMED: {
                    notifySignUpCOnfirmed();
                    break;
                }
                case Result.SIGN_UP_CONFIRM_FAIL: {
                    notifySignUpCOnfirmFail();
                    break;
                }

                case Result.GOT_CHAT_HISTORY: {
                    notifyGotChatHistory(resultData.getLong("chat_id"), resultData.getInt("all_count"));
                    break;
                }

                case Result.STARTED_UPLOADING_PHOTOS: {
                    notifyStartedUploadingPhotos(resultData);
                    break;
                }

                case Result.ImageUploaded: {
                    notifyImageUploaded(resultData);
                    break;
                }

                case Result.MESSAGE_DELIVERED: {
                    notifyMessageDelivered(resultData);
                    break;
                }
                case Result.MESSAGE_WAS_NOT_SENT: {
                    notifyMessageWasNotSent(resultData);
                    break;
                }

                case Result.TASK_ERROR: {
                    Toast.makeText(mAppContext, mAppContext.getString(R.string.task_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                case Result.NETWORK_ERROR: {
                    Toast.makeText(mAppContext, mAppContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    break;
                }

                //use bus instead
                case Result.MESSAGE_SEARCH_FINISHED: {
                    VK.bus().post(new SearchMessageEvent(resultData, SearchMessageEvent.Action.SEARCH_FINISHED));
                    break;
                }

            }
        }

    }


    private void notifyMessageWasNotSent(Bundle resultData) {
        for (Observer obs : mSubscribers) {
            obs.messageWasNotSent(resultData);
        }


    }


    private void notifyMessageDelivered(Bundle resultData) {
        for (Observer obs : mSubscribers) {
            obs.messageDelivered(resultData);
        }
    }

    private void notifyImageUploaded(Bundle resultData) {
        for (Observer obs : mSubscribers) {
            obs.imageUploaded(resultData);
        }
    }

    private void notifyStartedUploadingPhotos(Bundle resultData) {
        for (Observer obs : mSubscribers) {
            obs.startedUploadingPhoto(resultData);
        }

    }

    private void notifyGotChatHistory(long chat_id, int all_count) {
        for (Observer obs : mSubscribers) {
            obs.gotChatHistory(chat_id, all_count);
        }

    }

    private void notifySignUpCOnfirmed() {
        for (Observer obs : mSubscribers) {
            obs.signUpConfirmed();
        }

    }

    private void notifySignUpCOnfirmFail() {
        for (Observer obs : mSubscribers) {
            obs.signUpConfirmFail();
        }

    }


    private void notifySignUpOk() {
        for (Observer obs : mSubscribers) {
            obs.signUpOk();
        }


    }

    private void notifySignUpFail() {
        for (Observer obs : mSubscribers) {
            obs.signUpFail();
        }


    }

    private void notifyGotHistory(Bundle data) {
        for (Observer obs : mSubscribers) {
            obs.gotHistory(data);
        }

    }

    private void notifyGotDialogs() {
        for (Observer obs : mSubscribers) {
            obs.gotDialogs();
        }

    }

    private void notifyChatUpdated(long chat_id) {
        for (Observer obs : mSubscribers) {
            obs.chatUpdated(chat_id);
        }
    }


    private void notifyVideoError() {
        for (Observer obs : mSubscribers) {
            obs.videoError();
        }
    }

    private void notifyGotVideo(String video) {
        for (Observer obs : mSubscribers) {
            obs.gotVideo(video);
        }
    }

    /**
     * Keeps track of all current registered observers.
     */
    HashSet<Observer> mSubscribers = new HashSet<Observer>();




    public void registerObserver(Observer observer) {
        if (mSubscribers.size() == 0 && VK.model().isAppSignedIn()) {
            startLongPoll();
            Log.d(TAG, String.format("Starting LONG POLL, observers size: %d", mSubscribers.size()));
        } else {
            Log.d(TAG, String.format("alreadyStarted, observers size: %d", mSubscribers.size()));
        }
        mSubscribers.add(observer);
    }

    public void unRegisterObserver(Observer observer) {
        mSubscribers.remove(observer);
        if (mSubscribers.size() <= 0) {
            Log.d(TAG, String.format("STOPPING LONG POLL, observers size: %d", mSubscribers.size()));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSubscribers.size()<=0){//still no observers after 5 secs
                        stopLongPoll();
                    }
                }
            }, MIN_5);

        }
    }


    void notifyLoggedIn() {
        for (Observer obs : mSubscribers) {
            obs.loggedIn();
        }
    }

    void notifyLoggedOut() {
        for (Observer obs : mSubscribers) {
            if (obs.loggedOut())
                break;
        }
    }

    void notifyActionStarted(Bundle data) {
        for (Observer obs : mSubscribers) {
            obs.actionStarted(data);
        }
    }

    void notifyActionStopped(Bundle data) {
        for (Observer obs : mSubscribers) {
            obs.actionSopped(data);
        }
    }

    void notifyKaptchaRequired(String captcha_img, long captcha_sid, Bundle resultData) {
        for (Observer obs : mSubscribers) {
            obs.captchaRequired(captcha_img, captcha_sid, resultData);
        }
    }

    void notifyLoginFailed() {
        for (Observer obs : mSubscribers) {
            obs.loginFailed();
        }
    }


    public void notifyFriendsUpdated() {
        for (Observer obs : mSubscribers) {
            obs.friendListUpdated();
        }
    }

    public void notifyMainActionPerformed() {
        for (Observer obs : mSubscribers) {
            obs.mainActionPerformed();
        }

    }

    public void notifyHistoryUpdated(long mProfileUID) {
        for (Observer obs : mSubscribers) {
            obs.historyUpdated(mProfileUID);
        }

    }

    public void notifyMessageChanged(Bundle data) {
        for (Observer obs : mSubscribers) {
            obs.messageChanged(data);
        }


    }

    public void notifyUserTyping(long uid) {
        for (Observer obs : mSubscribers) {
            obs.userIsTyping(uid);
        }

    }

    public void notifyChatHistoryUpdated(long mChatId) {
        for (Observer obs : mSubscribers) {
            obs.chatHistoryUpdated(mChatId);
        }

    }


    public void notifySearchListUpdated() {
        for (Observer obs : mSubscribers) {
            obs.searchListUpdated();
        }
    }

    public void notifySearchResult(long taskId) {
        for (Observer obs : mSubscribers) {
            obs.searchResult(taskId);
        }
    }



}


