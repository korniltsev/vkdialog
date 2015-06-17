package ru.kurganec.vk.messenger.model.actions;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

import  static ru.kurganec.vk.messenger.model.VKService.Result.*;

/**
 * User: anatoly
 * Date: 12.06.12
 * Time: 19:58
 */
public abstract class BaseAction extends AsyncTask<Void, JSONObject, JSONObject> {

    protected final ResultReceiver mReceiver;
    protected final Bundle mArgs;
    private final long mId;
    public static final String EXTRA_ARGS = VKService.EXTRA_ARGS;
    public static final String EXTRA_ID = "id";


    public BaseAction(ResultReceiver mReceiver, Bundle args) {
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
    protected void onPreExecute() {
        sendResult(ACTION_STARTED);
    }

    @Override
    protected void onCancelled() {
        sendResult(ACTION_FINISHED);
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        sendResult(ACTION_FINISHED);
        if (json == null){
            if (VK.inst().isNetworkAvailable()){
                //toast

            } else {
                //offline mode
                //ignore

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
//                        VK.model().signOut();
//                        sendResult(SIGN_OUT_SUCCESSFUL);
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



}
