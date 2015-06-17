package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;
import ru.kurganec.vk.messenger.model.db.Message;

/**
 * User: anatoly
 * Date: 04.07.12
 * Time: 4:02
 */
public class RetrieveMessageTask extends BaseTask {
    private long mid;

    private int out;
    private int  read;
    private long chat_id;
    private long uid;



    public RetrieveMessageTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mid = args.getLong("mid");
    }

    @Override
    public void run(){
        JSONObject ret = VKApi.retrieveMessage(mid);
        if (ret == null) {
            handleResponse(null);
            return ;
        }
        try {
            JSONObject response = ret.getJSONArray("response").getJSONObject(1);
            out = response.getInt(Message.OUT);
            read = response.getInt(Message.READ_STATE);
            chat_id = response.optLong(Message.CHAT_ID, Long.MIN_VALUE);
            uid = response.getLong(Message.UID);
            VK.db().msg().insert(response);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        handleResponse(ret);
        return ;
    }


    @Override
    protected void handleResponse(JSONObject json) {//don't call supper
        if (json != null) {
            if (out == Message.STATE_IN && read == Message.STATE_UNREAD) {
                sendResult(VKService.Result.NOTIFICATION_NEW_MESSAGE);
            }
            if (chat_id!= Long.MIN_VALUE){
                mReturnBundle.putLong("chat_id", chat_id);
            } else {
                mReturnBundle.putLong("uid", uid);
            }
            sendResult(VKService.Result.MESSAGE_CHANGED);


        }
    }
}
