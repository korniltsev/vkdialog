package ru.kurganec.vk.messenger.model;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import ru.kurganec.vk.messenger.model.actions.*;

import java.util.concurrent.*;

/**
 * User: anatoly
 * Date: 26.06.12
 * Time: 1:30
 */
public class VKService extends Service {
    private static final String TAG = "VK-CHAT-SERVICE";
    public static final String EXTRA_RESULT_RECEIVER = "RESULT_RECEIVER";
    public static final String EXTRA_REQUEST_CODE = "REQUEST_CODE";
    public static final String EXTRA_ARGS = "ARGS";


    public static class Result {
        public static final int ACTION_STARTED = 0;
        public static final int ACTION_FINISHED = 1;
        public static final int SIGN_IN_SUCCESSFUL = 2;
        public static final int SIGN_IN_FAILED = 3;
        public static final int SIGN_OUT_SUCCESSFUL = 4;
        public static final int MainActionPerformed = 5;
        public static final int FRIEND_LIST_UPDATED = 6;
        public static final int SEARCH_LIST_UPDATED = 7;
        public static final int PROFILE_HISTORY_UPDATED = 8;
        public static final int CHAT_HISTORY_UPDATED = 9;
        public static final int PROFILE_MESSAGE_SENT = 10;
        public static final int CHAT_MESSAGE_SENT = 11;

        public static final int SEARCH_RESULT = 12;
        public static final int GOT_LONG_POLL = 13;
        public static final int MESSAGE_CHANGED = 14;
        public static final int CHAT_MESSAGE_CHANGED = 15;
        public static final int USER_IS_TYPING = 16;
        public static final int NOTIFICATION_NEW_MESSAGE = 17;
        public static final int GOT_VIDEO = 18;
        public static final int VIDEO_ERROR = 19;
        public static final int CHAT_UPDATED = 20;
        public static final int CHAT_NAME_CHANGED = 23;
        public static final int PARTICIPANT_DELETED = 24;
        public static final int PARTICIPANT_NOT_DELETED = 25;
        public static final int PARTICIPANT_ADDED = 26;
        public static final int PARTICIPANT_NOT_ADDED = 27;
        public static final int FRIEND_NOT_ADDED = 28;
        public static final int FRIENDSHIP_REQUEST_SENT = 29;
        public static final int FRIENDSHIP_ACCEPTED = 30;
        public static final int SECOND_FRIENDSHIP_REQUEST = 31;

        public static final int FRIEND_DELETED = 32;
        public static final int FRIENDSHIP_REFUSED = 33;
        public static final int FRIEND_NOT_DELETED = 34;
        public static final int CHAT_WAS_NOT_UPDATED = 35;
        public static final int WRONG_CREDENTIALS = 36;
        public static final int NEED_CAPTCHA = 37;
        public static final int GOT_DIALOGS = 38;

        public static final int GOT_HISTORY = 39;
        public static final int SIGN_UP_OK = 40;
        public static final int SIGN_UP_FAIL = 41;
        public static final int SIGN_UP_CONFIRM_FAIL = 42;
        public static final int SIGN_UP_CONFIRMED = 43;
        public static final int GOT_CHAT_HISTORY = 44;
        public static final int DID_NOT_GOT_CHAT_HISTORY = 45;
        public static final int ImageUploaded = 46;
        public static final int MESSAGE_SENT = 47;
        public static final int MESSAGE_DELIVERED = 48;
        public static final int MESSAGE_WAS_NOT_SENT = 49;
        public static final int STARTED_UPLOADING_PHOTOS = 50;
        public static final int TASK_ERROR = 51;
        public static final int NETWORK_ERROR = 52;
        public static final int MESSAGE_SEARCH_FINISHED = 53;

    }

    /**
     * defines contracts of actions
     */
    public static class Request {
        public static final int SIGN_IN = 0;
        public static final int SIGN_OUT = 1;
        public static final int PERFORM_MAIN_ACTION = 2;
        public static final int UPDATE_FRIEND_LIST = 3;
        public static final int UPDATE_SEARCH_LIST = 4;
        public static final int GET_HISTORY = 5;
        public static final int SEND_MESSAGE = 6;
        public static final int GET_CHAT_HISTORY = 7;
        public static final int SEND_CHAT_MESSAGE = 8;
        public static final int SET_USER_PHOTO = 9;
        public static final int SEARCH_USERS = 10;
        public static final int GET_LONG_POLL = 11;

        public static final int START_LONG_POLL = 12;
        public static final int STOP_LONG_POLL = 13;
        public static final int LONG_POLL_RETRIEVING_FAILED = 14;
        public static final int REGISTER_C2DM = 15;


        public static final int RETRIEVE_MESSAGE = 16;
        public static final int I_AM_TYPING = 17;
        public static final int MARK_AS_READ = 18;
        public static final int UPDATE_FRIENDSHIP_REQUESTS = 19;
        public static final int GET_VIDEO = 20;


        public static final int UN_REGISTER_C2DM = 21;
        public static final int DELETE_MESSAGES = 22;
        public static final int UPDATE_CHAT = 23;
        public static final int SET_CHAT_NAME = 24;

        public static final int DELETE_CHAT_PARTICIPANT = 25;
        public static final int ADD_CHAT_PARTICIPANT = 26;
        public static final int ADD_FRIEND = 27;
        public static final int DELETE_FRIEND = 28;
        public static final int GET_DIALOGS = 29;
        public static final int SIGN_UP = 30;
        public static final int SIGN_UP_CONFIRM = 31;
        public static final int SEARCH_MESSAGE = 32;
        public static final int CANCEL_MESSAGE = 33;
        public static final int DELETE_WHOLE_CONVERSATION = 34;
        public static final int ADD_TO_GROUP = 35;
    }


    private ResultReceiver mReceiver;
    private ExecutorService mExecutor;
    private ExecutorService mLongPollExecutor;
    private LongPollTask mLongPollTask = null;
    private SendMessageTask mLastSendMessageTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("VKLOL", "SERVICE CREATED");
        mExecutor = Executors.newFixedThreadPool(4);
        mLongPollExecutor = Executors.newSingleThreadExecutor();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("VKLOL", "SERVICE DESTORYED");
        mExecutor.shutdown();
        mLongPollExecutor.shutdown();
        mLongPollExecutor = null;
        mExecutor = null;
    }


    @Override
    @SuppressWarnings("unchecked")//
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {//если сервис восстановлен после уничтожения системой
            // не делаем ничего ;)
            return START_STICKY;
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mReceiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);
            int codeOfRequest = extras.getInt(EXTRA_REQUEST_CODE);
            Bundle args = extras.getBundle(EXTRA_ARGS);
            if (args == null) {
                args = new Bundle();
            }
            args.putInt(EXTRA_REQUEST_CODE, codeOfRequest);

            if (codeOfRequest == Request.SIGN_OUT) {//;(;(
                mExecutor.submit(new SignOutTask(mReceiver, args));
                return START_STICKY;
            }
//            if (codeOfRequest == Request.UN_REGISTER_C2DM){
//                new UnRegisterC2DMAction(mReceiver, args).submit();
//                return START_STICKY;
//            }
//            if (!VK.inst().isNetworkAvailable()){//don't spawn threads if network is unavailable
//                return START_STICKY;
//            }
            Log.d(TAG, String.format("Handling request: %d", codeOfRequest));
            switch (codeOfRequest) {
                case Request.SIGN_IN: {
                    mExecutor.submit(new SignInTask(mReceiver, args));
                    break;
                }
                case Request.PERFORM_MAIN_ACTION: {
                    mExecutor.submit(new MainTask(mReceiver, args));
                    break;
                }

                case Request.GET_DIALOGS: {
                    mExecutor.submit(new GetDialogsTask(mReceiver, args));
                    break;
                }

                case Request.GET_HISTORY: {
                    mExecutor.submit(new GetHistoryTask(mReceiver, args));
                    break;
                }

                case Request.SEND_MESSAGE: {
                    mLastSendMessageTask = new SendMessageTask(mReceiver, args);
                    mExecutor.submit(mLastSendMessageTask);
                    break;
                }

                case Request.GET_LONG_POLL: {
                    mExecutor.submit(new GetLongPollTask(mReceiver, args));
                    break;
                }
                case Request.START_LONG_POLL: {
                    if (mLongPollTask != null && (mLongPollTask.isCancelled() || mLongPollTask.isCrashedToMany())
                            || mLongPollTask == null) {
                        mLongPollTask = new LongPollTask(mReceiver, args);
                        mLongPollExecutor.submit(mLongPollTask);
                    }
                    break;
                }
                case Request.STOP_LONG_POLL: {
                    if (mLongPollTask != null) {
                        mLongPollTask.cancel();
                        mLongPollTask = null;
                    }
                    stopSelf();
                    break;
                }
                case Request.REGISTER_C2DM: {
                    RegisterGCMTask action = new RegisterGCMTask(mReceiver, args);
                    mExecutor.submit(action);
                    break;
                }
                case Request.RETRIEVE_MESSAGE: {
                    mExecutor.submit(new RetrieveMessageTask(mReceiver, args));
                    break;
                }
                case Request.I_AM_TYPING: {
                    mExecutor.submit(new IamTypingTask(mReceiver, args));
                    break;
                }
                case Request.MARK_AS_READ: {
                    mExecutor.submit(new MarkAsReadTask(mReceiver, args));
                    break;
                }
                case Request.SEARCH_MESSAGE: {
                    mExecutor.submit(new SearchMessageTask(mReceiver, args));
                    break;
                }
                case Request.CANCEL_MESSAGE: {
                    mLastSendMessageTask.cancel();
                    break;
                }
                case Request.GET_VIDEO: {
                    mExecutor.submit(new GetVideoTask(mReceiver, args));
                    break;
                }

                case Request.DELETE_WHOLE_CONVERSATION: {
                    mExecutor.submit(new DeleteWHoleConversationTask(mReceiver, args));
                    break;
                }

                case Request.ADD_TO_GROUP:{
                    mExecutor.submit(new AddToGroupTask(mReceiver, args));
                }

            }
        } else {
            Log.e(TAG, "Ftw no args passed to service");
        }
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "WTF someone bound the service");
        return null;
    }


}
