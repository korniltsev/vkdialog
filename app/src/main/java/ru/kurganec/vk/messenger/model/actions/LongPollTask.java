package ru.kurganec.vk.messenger.model.actions;

import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;
import ru.kurganec.vk.messenger.model.classes.VKMessage;
import ru.kurganec.vk.messenger.model.db.Message;


public class LongPollTask extends BaseTask {
    private static final String TAG = "VKDialog-LongPoll";
    public static final int TRY_COUNT = 20;

    //TODO обработку всю делать в одельном треде, здесь только уведомления!!!
    //todo ts rewrite!!!
    private String mURI;
    private long mTimeSync;
    private String mKey;
    private volatile boolean cancelled;
    private boolean firstUpdate = true;
//    private volatile boolean crashedToMany;

    public LongPollTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mURI = VK.model().getLongPollURI();
        mKey = VK.model().getLongPollKey();
        mTimeSync = VK.model().getLongPollTimeSync();

        Log.d(TAG, "CREATED");
    }

    public synchronized void cancel() {
        Log.d(TAG, "cancelled");
        cancelled = true;
    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    int tries = 0;

    @Override
    public void run() {
        Log.d(TAG, "STARTED");
        status = STATUS_STARTED;
        super.run();
        if (mURI == null) {
            handleResponse(null);
            return;
        }
        while (!isCancelled()) {
            JSONObject res = VKApi.getLongPollUpdates(mURI, mTimeSync, mKey);
            if (res == null) {
                Log.e(TAG, "response = null");
                tries++;
                if (tries++ < TRY_COUNT) {
                    continue;
                } else {
                    status = STATUS_STOPPED_IO;
                    Log.e(TAG, "stopped IO");
                    break;
                }
            }
            try {
                if (res.has("failed")) {
                    //get new LongPoll, stop current
                    Log.e(TAG, "stopped wrong uri");
                    VK.actions().getLongPoll();
                    status = STATUS_STOPPED_WRONG_URI;
                    break;
                }

                if (isCancelled()) {
                    break;
                } else {
                    mTimeSync = res.getLong("ts");
                    VK.model().storeLongPollTS(mTimeSync);
                }
            } catch (JSONException e) {
                Log.e(TAG, "LONGOPLL EXCEPTION", e);
                if (tries++ < TRY_COUNT) {
                    continue;
                } else {
                    status = STATUS_STOPPED_JSON;
                    break;
                }
            }
            resetBundle();
            handleUpdates(res);
            if (firstUpdate) {
                firstUpdate = false;
            }
        }

        Log.d(TAG, "Long poll stopped");
    }

    private volatile int status = STATUS_STOPPED;
    public static int STATUS_STARTED = 0;
    public static int STATUS_STOPPED = 1;
    public static int STATUS_STOPPED_JSON = 2;
    public static int STATUS_STOPPED_IO = 3;
    public static int STATUS_STOPPED_WRONG_URI = 4;

    @Override
    protected void handleResponse(JSONObject json) {
        VK.model().storeLongPollTS(mTimeSync);
        Log.d(TAG, "POL thread destoryed");
    }

    private void handleUpdates(JSONObject res) {
        if (res == null) {
            return;
        }
        Log.d(TAG, " new response : " + res);

        try {
            JSONArray updates = res.getJSONArray("updates");
            for (int i = 0; i < updates.length(); ++i) {
                JSONArray upd = updates.getJSONArray(i);
                int code = upd.getInt(0);
                switch (code) {
                        case 0://        0,$message_id,0 -- удаление сообщения с указанным local_id
                        Log.d(TAG, "LONGPOLL" + updates.toString());
                        break;
                    case 1://        1,$message_id,$flags -- замена флагов сообщения (FLAGS:=$flags)
                        Log.d(TAG, "ЗАМЕНА ФЛАГОВ" + updates.toString());
                        break;
                    case 2://        2,$message_id,$mask[,$user_id] -- установка флагов сообщения (FLAGS|=$mask)
                        updateMessage(upd);
                        break;
                    case 3://        3,$message_id,$mask[,$user_id] -- сброс флагов сообщения (FLAGS&=~$mask)
                        resetMessage(upd);
                        break;
                    case 4://        4,$message_id,$flags,$from_id,$timestamp,$subject,$text,$attachments -- добавление нового сообщения
                        handleNewMessage(upd);
                        break;
                    case 8://        8,-$user_id,0 -- друг $user_id стал онлайн
                        handleFriendBecomeOnline(upd);
                        break;
                    case 9://        9,-$user_id,$flags -- друг $user_id стал оффлайн ($flags равен 0, если пользователь покинул сайт
                        //                (например, нажал выход) и 1, если оффлайн по таймауту (например, статус away))
                        handleFriendBecomeOffline(upd);
                        break;
                    case 51://        51,$chat_id,$self -- один из параметров (состав, тема) беседы $chat_id были изменены.
                        //        $self - были ли изменения вызываны самим пользователем
                        handleChatChanged(upd);
                        break;
                    case 61://        61,$user_id,$flags -- пользователь $user_id начал набирать текст в диалоге.
                        //                событие должно приходить раз в ~5 секунд при постоянном наборе текста. $flags = 1
                        handleUserIsTyping(upd);
                        break;
                    case 62://        62,$user_id,$chat_id -- пользователь $user_id начал набирать текст в беседе $chat_id.
                        handleChatUserIsTyping(upd);
                        break;
                    case 70://        70,$user_id,$call_id -- пользователь $user_id совершил звонок имеющий идентификатор $call_id,
                        //                дополнительную информацию о звонке можно получить используя метод voip.getCallInfo.
                        // ignore
                        Log.d("VKLOL", "lol мне кто-то звонит " + updates.toString());
                        break;
                    default:
                        Log.d("VKLOL", "LONGPOLL" + updates.toString());
                        break;

                }
            }

        } catch (JSONException e) {
            Log.e(TAG, res.toString(), e);
        }
    }

    /**
     * +1	UNREAD	сообщение не прочитано
     * +2	OUTBOX	исходящее сообщение
     * +4	REPLIED	на сообщение был создан ответ
     * +8	IMPORTANT	помеченное сообщение
     * +16	CHAT	сообщение отправлено через чат
     * +32	FRIENDS	сообщение отправлено другом
     * +64	SPAM	сообщение помечено как "Спам"
     * +128	DELЕTЕD	сообщение удалено (в корзине)
     * +256	FIXED	сообщение проверено пользователем на спам
     * +512	MEDIA	сообщение содержит медиаконтент
     *
     * @param upd
     * @throws JSONException
     */
    private void updateMessage(JSONArray upd) throws JSONException { // 2,$message_id,$mask[,$user_id] -- установка флагов сообщения (FLAGS|=$mask)
        long mid = upd.getLong(1);
        int mask = upd.getInt(2);
        Log.d("VKLOL", "установка ФЛАГОВ" + upd.toString());
        Cursor c = VK.db().msg().get(mid);
        if (!c.moveToFirst()) {
//            VK.actions().retrieveMessage(mid);
            //the message is deleted :O
        } else {
            if ((mask & 128) == 128) {
                VK.db().msg().setDeleted(mid, true);
            }
            if ((mask & 64) == 64) {
                VK.db().msg().setDeleted(mid, true);
            }
            if ((mask & 1) == 1) {
                VK.db().msg().setReadState(mid, Message.STATE_UNREAD);
            }

            if (!c.isNull(c.getColumnIndex(Message.CHAT_ID))) {
                mReturnBundle.putLong("chat_id", c.getLong(c.getColumnIndex(Message.CHAT_ID)));
                sendResult(VKService.Result.CHAT_MESSAGE_CHANGED);
            } else {
                mReturnBundle.putLong("uid", c.getLong(c.getColumnIndex(Message.UID)));
                sendResult(VKService.Result.MESSAGE_CHANGED);
            }
        }
        c.close();

    }


    private void resetMessage(JSONArray upd) throws JSONException {  // 3,$message_id,$mask[,$user_id] -- сброс флагов сообщения (FLAGS&=~$mask)
        long mid = upd.getLong(1);
        int mask = upd.getInt(2);
        Cursor c = VK.db().msg().get(mid);
        if (!c.moveToFirst()) {
            VK.actions().retrieveMessage(mid);
        } else {
            if ((mask & 128) == 128) {
                VK.db().msg().setDeleted(mid, false);
            }
            if ((mask & 64) == 64) {
                VK.db().msg().setDeleted(mid, false);
            }
            if ((mask & 1) == 1) {
                VK.db().msg().setReadState(mid, Message.STATE_READ);
            }

            if (!c.isNull(c.getColumnIndex(Message.CHAT_ID))) {
                mReturnBundle.putLong("chat_id", c.getLong(c.getColumnIndex(Message.CHAT_ID)));
                sendResult(VKService.Result.CHAT_MESSAGE_CHANGED);
            } else {
                mReturnBundle.putLong("uid", c.getLong(c.getColumnIndex(Message.UID)));
                sendResult(VKService.Result.MESSAGE_CHANGED);
            }
        }
        Log.d("VKLOL", "сброс флагов" + upd.toString());
    }

    /**
     * 61,$user_id,$flags -- пользователь $user_id начал набирать текст в диалоге.
     * событие должно приходить раз в ~5 секунд при постоянном наборе текста. $flags = 1
     *
     * @param upd
     */
    private void handleChatUserIsTyping(JSONArray upd) throws JSONException {
        long uid = upd.getLong(1);
        long chatId = upd.getLong(2);
        mReturnBundle.putLong("uid", uid);
        mReturnBundle.putLong("chat_id", chatId);
        sendResult(VKService.Result.USER_IS_TYPING);
        Log.d(TAG, "user is typing");
    }

    /**
     * //        61,$user_id,$flags -- пользователь $user_id начал набирать текст в диалоге.
     * //                событие должно приходить раз в ~5 секунд при постоянном наборе текста. $flags = 1
     *
     * @param upd
     */
    private void handleUserIsTyping(JSONArray upd) throws JSONException {
        mReturnBundle.putLong("uid", upd.getLong(1));
        sendResult(VKService.Result.USER_IS_TYPING);
        Log.d(TAG, upd.getLong(1) + " is typing");
    }

    /**
     * 51,$chat_id,$self -- один из параметров (состав, тема) беседы $chat_id были изменены.
     * //        $self - были ли изменения вызываны самим пользователем
     *
     * @param upd
     */
    private void handleChatChanged(JSONArray upd) throws JSONException {
        //TODO
        VK.actions().updateChat(upd.getLong(1));
    }

    /**
     * 9,-$user_id,$flags -- друг $user_id стал оффлайн ($flags равен 0, если пользователь покинул сайт
     * (например, нажал выход) и 1, если оффлайн по таймауту (например, статус away))
     *
     * @param upd
     */
    private void handleFriendBecomeOffline(JSONArray upd) throws JSONException {
        //TODO
//        VKProfile friend = VK.db().getProfile(-upd.getLong(1));
//        if (friend == null) {
//            //ftw should never happen
//            //TODO retreive friend from vk
//        } else {
//            friend.setOnline(false);
//            VK.db().updateProfile(friend);
//            Log.d("VKLOL", "friend become offline " + friend);
//            sendResult(VKService.Result.FRIEND_LIST_UPDATED);
////            mManager.notifyFriendsUpdated();//TODO test
//        }
    }

    /**
     * 9,-$user_id,$flags -- друг $user_id стал оффлайн ($flags равен 0, если пользователь покинул сайт
     * (например, нажал выход) и 1, если оффлайн по таймауту (например, статус away))
     *
     * @param upd
     * @throws JSONException
     */
    private void handleFriendBecomeOnline(JSONArray upd) throws JSONException {
        //TODO
//        VKProfile friend = VK.db().getProfile(-upd.getLong(1));
//        if (friend == null) {
//            //ftw should never happen
//            //TODO retreive friend from vk
//        } else {
//            friend.setOnline(true);
//            VK.db().updateProfile(friend);
//            Log.d("VKLOL", "friend become online " + friend);
//            sendResult(VKService.Result.FRIEND_LIST_UPDATED);
//
//        }

    }

    /**
     * //        4,$message_id,$flags,$from_id,$timestamp,$subject,$text,$attachments -- добавление нового сообщения
     *
     * @param upd
     * @throws JSONException
     */
    private void handleNewMessage(JSONArray upd) throws JSONException {

        long messageID = upd.getLong(1);


        int flag = upd.getInt(2);
        long from = upd.getLong(3);
        long time = upd.getLong(4);
        String title = upd.getString(5);
        String body = upd.getString(6);
        //    +1	UNREAD	сообщение не прочитано
        //    +2	OUTBOX	исходящее сообщение
        //    +4	REPLIED	на сообщение был создан ответ
        //    +8	IMPORTANT	помеченное сообщение
        //    +16	CHAT	сообщение отправлено через чат
        //    +32	FRIENDS	сообщение отправлено другом
        //    +64	SPAM	сообщение помечено как "Спам"
        //    +128	DELЕTЕD	сообщение удалено (в корзине)
        //    +256	FIXED	сообщение проверено пользователем на спам
        //    +512	MEDIA	сообщение содержит медиаконтент
        int unread = flag & 1;//TODO STATE_UNREAD
        int read = unread == 1 ? 0 : 1;
        int out = (flag >> 1) & 1;
        boolean chat = from >= 2000000000l;//WHAT A FUCK VK?????!?!??!
        Long chat_id = null;
        if (chat) {
            chat_id = from - 2000000000l; //WHAT A FUCK VK?????!?!??!
        }
        String attach = "";

        JSONObject l = upd.getJSONObject(7);
        if (chat) {
            if (l.length() > 1) {
                VK.actions().retrieveMessage(messageID);
                return;
            }
            from = l.getLong("from");

        } else if (l.length() != 0) {
            if (!l.has("emoji") || l.length() > 1) {
                VK.actions().retrieveMessage(messageID);
                return;
            }
        }


        Cursor p = VK.db().profiles().get(from);


        if (!p.moveToFirst()) {
            //retrieve the user info
            //todo
        } else {
            if (out == VKMessage.IN && read == VKMessage.UNREAD && !firstUpdate) {
                sendResult(VKService.Result.NOTIFICATION_NEW_MESSAGE);
            }

            VK.db().msg().insert(messageID, from, time, read, out, title, body, attach, /*forward*/ chat_id);


            if (chat_id != null) {
                mReturnBundle.putLong("chat_id", chat_id);
            } else {
                mReturnBundle.putLong("uid", from);
            }
            sendResult(VKService.Result.MESSAGE_CHANGED);
            Log.d("VKLOL", "got message " + body);
        }

    }


    public boolean isCrashedToMany() {
        return status == STATUS_STOPPED_IO || status == STATUS_STOPPED_JSON || status == STATUS_STOPPED_WRONG_URI;
    }

    public int getStatus() {
        return status;
    }
}
