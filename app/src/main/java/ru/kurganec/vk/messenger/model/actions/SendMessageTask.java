package ru.kurganec.vk.messenger.model.actions;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.api.VKApi;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.VKService;

import java.util.ArrayList;

/**
 * User: anatoly
 * Date: 20.06.12
 * Time: 3:30
 */
public class SendMessageTask extends BaseTask {
    private Long uid;
    private Long chat_id;
    private String msg;
    private String[] photos;
    private String forward;
    private Integer latitude;
    private Integer longitude;

    public static final String TAG = "VK-CHAT-SEND-MESSAGE-TASK";



    private int res = 0;
    private boolean startedUploadingPhotos;
    private ArrayList<String> ids = null;

    private Boolean cancelled = false;

    public synchronized void cancel() {
        Log.d(TAG, "Cancelled");
            cancelled = true;

    }

    public synchronized boolean isCancelled() {
        return cancelled;
    }

    public SendMessageTask(ResultReceiver mReceiver, Bundle args) {
        super(mReceiver, args);
        if (args.containsKey("uid")) {
            uid = args.getLong("uid");
        }
        if (args.containsKey("chat_id")) {
            chat_id = args.getLong("chat_id");
        }
        msg = args.getString("msg");
        photos = args.getStringArray("photos");


        forward = args.getString("forward");
        if (args.containsKey("latitude")) {
            latitude = args.getInt("latitude");
            longitude = args.getInt("longitude");
        }

//        if (args.containsKey("ids")) {
//            String[] idsRaw = args.getStringArray("ids");
//
//            idsBeforeKaptchaNeeded = new ArrayList<String>();
//            for (String id : idsRaw) {
//                idsBeforeKaptchaNeeded.add(id);
//            }
//        }



        if (uid != null) {
            mReturnBundle.putLong("uid", uid);
        } else {
            mReturnBundle.putLong("chat_id", chat_id);
        }

        mReturnBundle.putInt("img_count", photos == null ? 0 : photos.length);

    }


    @Override
    public void run() {
        try {

            if (photos != null) {
                JSONObject ret = VKApi.getMessagePhotoUploadServer();
                String uploadUri = ret.getJSONObject("response").getString("upload_url");
                ids = new ArrayList<String>();
                startedUploadingPhotos = true;

                for (String fileName : photos) {
                    if (isCancelled()) {
                        break;
                    }
                    boolean shouldCompress = VK.model().shouldCompress();
                    JSONObject obj = VKApi.uploadMessagePhoto(uploadUri, fileName, shouldCompress);
                    if (obj == null) {
                        handleResponse(null);
                        return;
                    }
                    obj = VKApi.saveMessagePhoto(obj.getString("server"), obj.getString("photo"), obj.getString("hash"));
                    if (obj == null) {
                        handleResponse(null);
                        return;
                    }
                    String id = obj.getJSONArray("response").getJSONObject(0).getString("id");
                    ids.add(id);
                    res++;
                    publishProgress();
                }
            }


            if (!isCancelled()) {
                JSONObject obj =  VKApi.sendMessage(uid, chat_id, msg, ids, forward, latitude , longitude);
                handleResponse(obj);
            } else {
                Log.d(TAG, "Cancelled successfuly. Message was not sent");
            }
        } catch (JSONException e) {
            handleResponse(null);
        }

    }

    private void publishProgress() {
        if (!isCancelled()) {
            mReturnBundle.putInt("img_uploaded", res);
            sendResult(VKService.Result.ImageUploaded);
            Log.d(TAG, res + " images uploaded ");
        }

    }


    @Override
    protected void handleResponse(JSONObject json) {
        super.handleResponse(json);
        if (isCancelled()) {
            sendResult(VKService.Result.MESSAGE_WAS_NOT_SENT);
        }

        if (json != null) {
            if (json.has("response")) {
                sendResult(VKService.Result.MESSAGE_DELIVERED);
            } else {
                if (json.has("error")) {
                    try {
                        JSONObject err = json.getJSONObject("error");
                        if (err.has("captcha_img")) {
                            String captcha_img = err.getString("captcha_img");
                            long sid = err.getLong("captcha_sid");
                            mReturnBundle.putString("captcha_img", captcha_img);
                            mReturnBundle.putLong("captcha_sid", sid);
                            if (ids != null) {
                                String[] arr = new String[ids.size()];
                                arr = ids.toArray(arr);
                                mReturnBundle.putStringArray("ids", arr);
                            }
                            sendResult(VKService.Result.NEED_CAPTCHA);
                        }
                    } catch (JSONException ignore) {
                    }
                }
            }
        } else {
            sendResult(VKService.Result.MESSAGE_WAS_NOT_SENT);
        }
    }




}
