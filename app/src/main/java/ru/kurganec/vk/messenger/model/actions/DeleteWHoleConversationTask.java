package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import ru.kurganec.vk.messenger.api.VKApi;

/**
 * User: anatoly
 * Date: 05.04.13
 * Time: 0:20
 */
public class DeleteWHoleConversationTask extends BaseTask {
    Long chatId;
    Long uid;
    public DeleteWHoleConversationTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        if (args.containsKey("chat_id")){
            chatId = args.getLong("chat_id");
        } else {
            uid = args.getLong("uid");
        }

    }

    @Override
    public void run() {
        VKApi.deleteWholeConversation(uid, chatId);
    }
}
