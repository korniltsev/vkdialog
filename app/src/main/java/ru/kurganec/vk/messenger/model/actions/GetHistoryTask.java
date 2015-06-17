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
 * Date: 20.06.12
 * Time: 0:31
 */
public class GetHistoryTask extends BaseTask {

    private final boolean mIsInvisible;
    private Long mUID;
    private Long mCHatId;

    private int mCount;
    private int mOffset;
    int allCount;


    public GetHistoryTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        if (args.containsKey("chat_id")) {
            mCHatId = mArgs.getLong("chat_id");
        } else {
            mUID = args.getLong("uid");
        }
        mCount = args.getInt("count");
        mOffset = args.getInt("offset");
        mIsInvisible = args.getBoolean("isInvisible");
    }

    @Override
    public void run() {
        super.run();
        JSONObject json = VKApi.getHistory(mUID, mCHatId, mCount, mOffset, mIsInvisible);

        if (json == null) {
            handleResponse(null);
            return;
        }
        try {

            JSONObject resp = json.getJSONObject("response");
            JSONArray hist = resp.getJSONArray("hist");
            if (resp.optBoolean("profiles", true)) {
                JSONArray profiles = resp.getJSONArray("profiles");
                VK.db().profiles().store(profiles);
            }


            allCount = hist.getInt(0);

            //я меняю    uid потому что мне не нравится как вк их предоставляет
            // т.е. скажем исходящее сообщение к пользователю uid будет равен моему, и как мне его потом из
            // базы выдрать? fuu, vk!
            for (int i = 1; i < hist.length(); ++i) {
                JSONObject msg = hist.getJSONObject(i);
                if (mCHatId == null) {
                    msg.put("uid", mUID);
                } else {
                    msg.put("chat_id", mCHatId);
                }
            }

            VK.db().msg().storeMessages(hist);


        } catch (JSONException e) {
            Log.e(TAG, "chat_active is known issue, if the problem is not in chat_active u should fix this!!!!", e);

        }
        handleResponse(json);
    }


    @Override
    protected void handleResponse(JSONObject json) {
        super.handleResponse(json);
        if (json != null) {

            if (mCHatId == null) {
                mReturnBundle.putLong("uid", mUID);
            } else {
                mReturnBundle.putLong("chat_id", mCHatId);
            }
            mReturnBundle.putInt("all_count", allCount);
            sendResult(VKService.Result.GOT_HISTORY);
        }
    }


}
