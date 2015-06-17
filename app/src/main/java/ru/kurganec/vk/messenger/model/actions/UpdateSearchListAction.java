package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VKService;
import ru.kurganec.vk.messenger.model.classes.VKProfile;

import java.util.List;

/**
 * User: anatoly
 * Date: 25.06.12
 * Time: 0:26
 */
public class UpdateSearchListAction extends BaseAction {
    public UpdateSearchListAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
    }


    @Override
    protected JSONObject doInBackground(Void... strings) {
        JSONObject ret = VKApi.updateSearchList();
        if (ret == null) {
            return null;
        }
        try {
            JSONObject response = ret.getJSONObject("response");

            if (response.optBoolean("sugProfiles",true)){
                JSONArray sugProfiles = response.getJSONArray("sugProfiles");
                List<VKProfile> suggestions = VKProfile.parseArray(sugProfiles);
                for (VKProfile p: suggestions){
                    p.setSuggestion(true);
                }
                long time = System.currentTimeMillis();
//                VK.db().profiles().store(suggestions);
                Log.d("VKLOL", "stored usggestions in " + (System.currentTimeMillis() - time));
            }

            if (response.optBoolean("reqProfiles", true)){
                JSONArray reqProfiles = response.getJSONArray("reqProfiles");
                List<VKProfile> suggestions = VKProfile.parseArray(reqProfiles);
                for (VKProfile p: suggestions){
                    p.setRequest(true);
                }
//                VK.db().profiles().store(suggestions);
                Log.d("VKLOL", "stored requests  ");
            }


            return ret;
        } catch (JSONException e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        if (json!= null){
            sendResult(VKService.Result.SEARCH_LIST_UPDATED);
        }

    }
}
