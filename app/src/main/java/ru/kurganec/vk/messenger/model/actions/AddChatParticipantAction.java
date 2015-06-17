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
 * Date: 06.07.12
 * Time: 9:45
 */
public class AddChatParticipantAction extends BaseAction{
    private long uid;
    private long chat_id;

    public AddChatParticipantAction(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        uid = args.getLong("uid");
        chat_id =args.getLong("chat_id");
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {

        return  VKApi.addChatParticipant(chat_id, uid);




    }

    @Override
    protected void onPostExecute(JSONObject json) {
        super.onPostExecute(json);
        if (json!= null){
            VK.actions().updateChat(chat_id);
            try {
                if (json.has("response") && json.getInt("response") == 1){
                    sendResult(VKService.Result.PARTICIPANT_ADDED);
                } else {
                    sendResult(VKService.Result.PARTICIPANT_NOT_ADDED);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }  else {
            sendResult(VKService.Result.PARTICIPANT_NOT_ADDED);
        }
    }
}
