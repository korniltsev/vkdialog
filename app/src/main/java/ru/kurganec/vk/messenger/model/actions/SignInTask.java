package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;


// import com.flurry.android.FlurryAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.api.*;

import static ru.kurganec.vk.messenger.model.VKService.Result;

/**
 * User: anatoly
 * Date: 12.06.12
 * Time: 19:52
 */
public class SignInTask extends BaseTask {


    private final String mPhone;
    private final String mPassword;
    private String captcha_key;
    private Long captcha_sid;

    public SignInTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mPhone = args.getString("phone");
        mPassword = args.getString("password");
        if (args.containsKey("captcha_key")) {
            captcha_key = args.getString("captcha_key");
            captcha_sid = args.getLong("captcha_sid");
        }
    }


    @Override
    public void run() {
        final JSONObject json = VKApi.oauthAuthorization(mPhone, mPassword, captcha_key, captcha_sid);

        if (json == null) {
            handleResponse(null);
            return;
        }

        try {
            VK.model().setAccessToken(json.get("access_token").toString());
            VK.model().storeUserID(json.getLong("user_id"));
            JSONObject obj = VKApi.init(json.getLong("user_id"));
            if (obj == null){
                resetAuth(obj);
                return;
            }




            JSONObject resp = obj.getJSONObject("response");

            if (resp.optBoolean("dialogs", true)) {
                JSONArray dialogs = resp.getJSONArray("dialogs");
                VK.db().msg().storeMessages(dialogs);
            }
            if ( resp.optBoolean( "friends", true ) ) {
                JSONArray arr = resp.getJSONArray( "friends" );
                VK.db().profiles().storeFriends( arr );
            }
            if (resp.optBoolean("profiles", true)) {
                JSONArray profiles = resp.getJSONArray("profiles");
                VK.db().profiles().store(profiles);
            }


            if (resp.optBoolean("client", true)) {
                JSONArray client = resp.getJSONArray("client");
                VK.db().profiles().store(client);
            }

            JSONObject longPoll = resp.getJSONObject("long-poll");
            VK.model().storeLongPollKey(longPoll.getString("key"));
            VK.model().storeLongPollTS(Long.parseLong(longPoll.getString("ts")));
            VK.model().storeLongPollURI("http://" + longPoll.getString("server"));

        } catch (JSONException e) {
            // FlurryAgent.logEvent(TAG + ":" + e.getMessage());
            resetAuth(json);
            return;
        }
        handleResponse(json);
    }

    private void resetAuth(JSONObject obj) {
        VK.model().setAccessToken(null);
        VK.model().storeUserID(null);
        handleResponse(obj);
    }

    @Override
    public void handleResponse(JSONObject json){
        super.handleResponse(json);
        if (json != null) {
            if (json.has("access_token")) {
                sendResult(Result.SIGN_IN_SUCCESSFUL);
            } else {
                try {
                    String err = json.getString("error");
                    if (err.equalsIgnoreCase("invalid_client")) {
                        sendResult(Result.SIGN_IN_FAILED);
                        // FlurryAgent.logEvent("incorrect password");
                    } else if (err.equals("need_captcha")) {
                        mReturnBundle.putString("captcha_img", json.getString("captcha_img"));
                        mReturnBundle.putLong("captcha_sid", json.getLong("captcha_sid"));
                        sendResult(Result.NEED_CAPTCHA);
                        // FlurryAgent.logEvent("captcha required");
                    }
                } catch (JSONException e) {
                    sendResult(Result.SIGN_IN_FAILED);
                }
            }
        } else {
            sendResult(Result.SIGN_IN_FAILED);
        }
    }

}


