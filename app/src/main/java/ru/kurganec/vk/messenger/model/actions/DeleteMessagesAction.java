package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;

/**
 * User: anatoly
 * Date: 06.07.12
 * Time: 3:42
 */
public class DeleteMessagesAction  extends BaseAction{
    private String mids ;
    public DeleteMessagesAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mids = args.getString("mids");
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        return VKApi.deleteMessages(mids);
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
    }
}
