package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VKService;

/**
 * User: anatoly
 * Date: 05.07.12
 * Time: 7:35
 */
public class GetVideoTask extends BaseTask{
    private String videos;

    public GetVideoTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        videos = args.getString("videos");
    }

    @Override
    public void run(){

        try {
            JSONObject obj = VKApi.getVideo(videos);
            if(obj == null){
                handleResponse(null);
                return;
            }
            handleResponse(obj.getJSONArray("response").getJSONObject(1));
        } catch (JSONException e) {
            handleResponse(null);
        }
    }

    @Override
    public void handleResponse(JSONObject json){
        if (json != null){
            mReturnBundle.putString("video", json.toString());
            sendResult(VKService.Result.GOT_VIDEO);
        } else {
            sendResult(VKService.Result.VIDEO_ERROR);
        }
    }
}
