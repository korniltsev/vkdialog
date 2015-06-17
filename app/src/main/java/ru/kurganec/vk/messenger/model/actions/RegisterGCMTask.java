package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;

/**
 * User: anatoly
 * Date: 29.06.12
 * Time: 7:08
 */
public class RegisterGCMTask extends BaseTask {
    private String registrationId;

    public RegisterGCMTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        registrationId = args.getString("registrationId");
    }

    @Override
    public void run(){
        handleResponse(VKApi.registerDevice(registrationId));
    }

    @Override
    protected void handleResponse(JSONObject json) {
        if (json != null && "1".equals(json.optString("response", ""))){
            VK.model().registerGCMToken(registrationId);
        }
    }


}
