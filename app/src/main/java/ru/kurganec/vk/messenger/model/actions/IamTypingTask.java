package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import ru.kurganec.vk.messenger.api.VKApi;

/**
 * User: anatoly
 * Date: 04.07.12
 * Time: 22:55
 */
public class IamTypingTask extends BaseTask{
    private Long uid;
    private Long chatId;
    public IamTypingTask(ResultReceiver mReceiver, Bundle args)  {
        super(mReceiver , args);
        if (args.containsKey("uid")){
            uid = args.getLong("uid");
        } else {
            chatId = args.getLong("chat_id");
        }
    }



    @Override
    public void run(){
        VKApi.iAmTyping(uid, chatId);
        Log.d(TAG, "I am typing: uid: " + uid + " , chat_id: "  + chatId);
    }


}
