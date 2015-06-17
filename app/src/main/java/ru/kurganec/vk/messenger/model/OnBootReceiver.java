package ru.kurganec.vk.messenger.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ru.kurganec.vk.messenger.model.actions.Actions;

/**
 * User: anatoly
 * Date: 13.06.12
 * Time: 18:31
 */
public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

        }
    }
}
