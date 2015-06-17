package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 07.07.12
 * Time: 19:43
 */
public class SignUpAction extends BaseAction{
    String login;
    String name;
    String lastName;
    public SignUpAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        login = args.getString("login");
        name = args.getString("name");
        lastName = args.getString("last_name");
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject obj =  VKApi.signUp(login, name, lastName);
        try {
            JSONObject response =obj.getJSONObject("response");
            String sid = response.getString("sid");
            VK.model().storeSignUpSid(sid);
            return obj;
        } catch (JSONException e) {
            return null;
        }


    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        if (json!= null && json.has("response")){
            sendResult(VKService.Result.SIGN_UP_OK);
        } else {
            sendResult(VKService.Result.SIGN_UP_FAIL);
        }
    }
}
