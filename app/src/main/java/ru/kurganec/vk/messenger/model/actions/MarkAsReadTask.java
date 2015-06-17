package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 04.07.12
 * Time: 23:36
 */
public class MarkAsReadTask extends BaseTask{
    String mids;
    public MarkAsReadTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mids = args.getString("mids");
    }

    @Override
    public void run(){
        VK.db().msg().markAsRead(mids);
        sendResult(VKService.Result.MESSAGE_CHANGED);
        handleResponse(VKApi.markAsRead(mids));
    }

}
