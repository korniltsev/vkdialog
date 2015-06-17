package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 05.08.12
 * Time: 19:38
 */
public class SearchMessageTask extends BaseTask {
    String q ;
    public SearchMessageTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        q = args.getString("q");
    }

    @Override
    public void run() {
        super.run();
        VK.db().msg().clearPreviousSearchResult();
        JSONObject object = VKApi.searchMessages(q);
        if (object== null){
            handleResponse(null);
            return;
        }

        try {

            JSONObject response = object.getJSONObject("response");
            JSONArray messages = response.getJSONArray("messages");
            JSONArray profiles = response.getJSONArray("profiles");
            VK.db().profiles().store(profiles);
            VK.db().msg().storeSearchResult(messages);
            handleResponse(response);
        } catch (JSONException e) {
            handleResponse(null);
        }
    }

    @Override
    protected void handleResponse(JSONObject json) {
//        super.handleResponse(json);
        sendResult(VKService.Result.MESSAGE_SEARCH_FINISHED);
    }
}
