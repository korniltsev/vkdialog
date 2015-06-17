package ru.kurganec.vk.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import ru.kurganec.vk.messenger.model.VK;

/**
 * User: anatoly
 * Date: 05.08.12
 * Time: 23:20
 */
public class GCMIntentService extends GCMBaseIntentService {
    public static final String TAG = "VK-CHAT-GCM";


    @Override
    protected void onMessage(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras.containsKey("msg_id") && VK.actions().clientCount() <= 0){
            long mid = Long.parseLong(extras.getString("msg_id"));
            VK.actions().retrieveMessage(mid);
        }
        Log.d(TAG, intent.getExtras().toString());
    }

    @Override
    protected void onError(Context context, String errorId) {

        Log.d(TAG, errorId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.d(TAG, registrationId);
        String prevToken = VK.model().getGCMToken();
        if (!registrationId.equals(prevToken)){
            VK.actions().registerGCM(registrationId);
        }
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.d(TAG, registrationId);
    }

    @Override
    protected String[] getSenderIds(Context context) {
        return new String[]{"465521759576"};
    }
}
