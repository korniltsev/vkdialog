package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 22.06.12
 * Time: 0:34
 */
public class GetDialogsTask extends BaseTask{
    private final int mOffset;



    public GetDialogsTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        mOffset = args.getInt("offset");
    }

    @Override
    public void run(){
        JSONObject res =  VKApi.getDialogs(50, mOffset);

        if (res == null){
            handleResponse(null);
            return ;
        }
        try {
            JSONObject response = res.getJSONObject("response");
            if (response.optBoolean("dialogs", true)) {
                JSONArray dialogs = response.getJSONArray("dialogs");
                JSONArray profiles = response.getJSONArray("profiles");
                VK.db().profiles().store(profiles);
                VK.db().msg().storeMessages(dialogs);
                VK.model().storeConversationsCount(dialogs.getInt(0));
            }
//            JSONArray dialogs = response.getJSONArray("dialogs");
//            JSONArray profiles = response.getJSONArray("profiles");
//
//
//            VK.db().store(VKProfile.parseArray(profiles));
//            HashMap<Long, VKProfile> profileList =VKProfile.parseArrayToSet(profiles);
//            List<VKMessage> messages = VKMessage.parseArray(dialogs);
//
//            int count = dialogs.getInt(0);
//            VK.model().storeConversationsCount(count);
//
//            List<VKChat> chats = new ArrayList<VKChat>();
//            for (VKMessage msg: messages){
//                msg.setProfile(profileList.get(msg.getUid()));
//                VKChat chat = msg.getChat();
//                if (chat != null){
//                    chats.add(chat);
//                }
//            }
//            Log.d("VKLOL", "got " + messages.size() + " old messages");
//            VK.db().store(profileList.values());
//            VK.db().storeChats(chats);
//            VK.db().storeMessages(messages);

        } catch (JSONException e) {
            Log.e("VKLOL", "JSONERROR " + res);
        }

        handleResponse(res);
    }

    @Override
    protected void handleResponse(JSONObject json) {
        super.handleResponse(json);
        if (json!= null){
            sendResult(VKService.Result.GOT_DIALOGS);
        }
    }

}
