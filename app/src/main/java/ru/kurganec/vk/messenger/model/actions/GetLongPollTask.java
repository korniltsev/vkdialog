package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 18.06.12
 * Time: 19:12
 */
public class GetLongPollTask extends BaseTask {

    public static final String TAG = "VKDialog-GetLongPoll";

    public GetLongPollTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
    }

    @Override
    public void run() {
        JSONObject ret = VKApi.getLongPollServer();
        JSONObject response = null;
        if (ret == null) {
            handleResponse(null);
            return;
        }
        try {
            response = ret.getJSONObject("response");
            VK.model().storeLongPollKey(response.getString("key"));
            VK.model().storeLongPollTS(Long.parseLong(response.getString("ts")));
            VK.model().storeLongPollURI("http://" + response.getString("server"));
            Log.d(TAG, "SERVER: " + response.getString("server"));
        } catch (JSONException e) {
            Log.e(TAG, "fial: " + ret.toString(), e);
        }
        handleResponse(response);
    }

    @Override
    protected void handleResponse(JSONObject json) {
        super.handleResponse(json);
        if (json != null) {
            if (json.has("server")) {
                sendResult(VKService.Result.GOT_LONG_POLL);
            } else {
                sendResult(VKService.Request.LONG_POLL_RETRIEVING_FAILED);
                Log.e("VKLOL", "why, check!!!"); //TODO
            }
        }
    }

}
