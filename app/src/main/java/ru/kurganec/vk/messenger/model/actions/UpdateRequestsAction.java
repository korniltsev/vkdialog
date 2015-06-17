package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.classes.VKProfile;

import java.util.List;

/**
 * User: anatoly
 * Date: 05.07.12
 * Time: 3:40
 */
public class UpdateRequestsAction extends BaseAction {
    public UpdateRequestsAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject ret = VKApi.updateRequests();
        if (ret == null){
            return null;
        }
        try {
        JSONObject response = null;

            response = ret.getJSONObject("response");

        if (response.optBoolean("requests", true)) {
            JSONArray requests = response.getJSONArray("requests");
            List<VKProfile> requestList = VKProfile.parseArray(requests);
            for (VKProfile p: requestList){
                p.setRequest(true);
            }
//            VK.db().profiles().store(requestList);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
