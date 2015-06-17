package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

import static ru.kurganec.vk.messenger.model.VKService.Result.ACTION_FINISHED;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 12:31
 */
public abstract class BaseTask implements Runnable {
    public static final String TAG = "VK-CHAT-TASK";

    protected final ResultReceiver mReceiver;
    protected final Bundle mArgs;
    private final long mId;
    public static final String EXTRA_ARGS = VKService.EXTRA_ARGS;
    public static final String EXTRA_ID = "id";


    public BaseTask(ResultReceiver mReceiver, Bundle args) {
        this.mReceiver = mReceiver;
        this.mArgs = args;
        mId = genId();
        resetBundle();
    }

    final protected void resetBundle() {
        mReturnBundle = new Bundle();
        mReturnBundle.putLong(EXTRA_ID, mId);
        mReturnBundle.putBundle(EXTRA_ARGS, mArgs);
    }

    private static long sIdGen = 0;
    private static long genId(){
        return ++sIdGen;
    }

    protected Bundle mReturnBundle;

    protected void sendResult(int resultCode){
        mReceiver.send(resultCode, mReturnBundle);
    }

    @Override
    public void run() {
        mReceiver.send(VKService.Result.ACTION_STARTED, mReturnBundle);
    }

    protected void handleResponse(JSONObject json){
        sendResult(ACTION_FINISHED);
        if (json == null){
            if (VK.inst().isNetworkAvailable()){
                //toast todo sends result
//                Toast.makeText(VK.inst(), R.string.toast_something_went_wrong, Toast.LENGTH_SHORT).show();
                sendResult(VKService.Result.TASK_ERROR);
            } else {
                //offline mode
                //ignore
//                Toast.makeText(VK.inst(), R.string.debug_toast_offline, Toast.LENGTH_SHORT).show();
                sendResult(VKService.Result.NETWORK_ERROR);
            }
        } else if (json.has("error")) {
            handleError(json);
        }
    }


    protected void handleError(JSONObject json) {
        try {
            int error = json.getJSONObject("error").getInt("error_code");
            switch (error) {
                case 5:  //5 User authorization failed.
                    if (VK.model().getAccessToken()!= null){
                        VK.model().signOut();
                        sendResult(VKService.Result.SIGN_OUT_SUCCESSFUL);
                    }
                    break;
                case 12: //compilation error
                    throw new RuntimeException("CHECK VKApi" + json.toString());
                default:
                    throw new JSONException("unknown error");
            }
        } catch (JSONException e) {
            try {
                if (json.getString("error").equals("need_kaptcha")){

                }
            } catch (JSONException e1) {
                throw new RuntimeException("FTW,", e1);
            }

        }
    }

    public Bundle getArgs() {
        return mArgs;
    }
}
