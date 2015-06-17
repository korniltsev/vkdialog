package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 26.06.12
 * Time: 4:07
 */
public class SignOutTask extends BaseTask {
    public SignOutTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
    }



    @Override
    public void run(){
        VK.model().signOut();
        VK.db().clearDB();

        //DONT DELETE !
//        AccountManager am = AccountManager.get(VK.inst());
//        Account[] previous = am.getAccountsByType(VK.inst().getString(R.string.account_type));

//        for (Account a: previous){
//            am.removeAccount(a,null, null);
//        }
        VK.actions().unRegisterC2DM();

        handleResponse(null);

    }

    @Override
    protected void handleResponse(JSONObject json) {
        sendResult(VKService.Result.SIGN_OUT_SUCCESSFUL);
    }
}
