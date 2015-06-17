package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;

/**
 * User: anatoly
 * Date: 09.06.13
 * Time: 22:57
 */
public class AddToGroupTask extends BaseTask {


    public AddToGroupTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
    }

    @Override
    public void run() {
        JSONObject o = VKApi.addToVKDialogGroup();
        if (o.optInt("success", 0) == 1){
            VK.model().addedToGroup();
        }
    }
}
