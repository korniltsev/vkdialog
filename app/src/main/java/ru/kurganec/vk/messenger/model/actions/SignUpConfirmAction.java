package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 07.07.12
 * Time: 19:43
 */
public class SignUpConfirmAction extends BaseAction{
    String login;
    String code;
    String password;
    public SignUpConfirmAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        login = args.getString("login");
        code = args.getString("code");
        password = args.getString("password");
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
       return VKApi.confirmSignUp(login, code, password);
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        if (json!= null && json.has("response")){
            sendResult(VKService.Result.SIGN_UP_CONFIRMED);
            VK.model().storeSignUpSid(null);
        } else {
            sendResult(VKService.Result.SIGN_UP_CONFIRM_FAIL);
        }
    }
}
