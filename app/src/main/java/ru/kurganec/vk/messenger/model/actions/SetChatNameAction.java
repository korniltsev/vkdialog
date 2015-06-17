package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 06.07.12
 * Time: 9:26
 */
public class SetChatNameAction  extends BaseAction{
    long chatId;
    String name;
    public SetChatNameAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        chatId = args.getLong("chat_id");
        name = args.getString("name");
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        return VKApi.editChat(chatId, name);
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        if (json!= null){
            try {
                if (json.has("response")&&json.getInt("response")==1){
                    sendResult(VKService.Result.CHAT_NAME_CHANGED);
                }
            } catch (JSONException ignore) {
            }
        }
    }
}
