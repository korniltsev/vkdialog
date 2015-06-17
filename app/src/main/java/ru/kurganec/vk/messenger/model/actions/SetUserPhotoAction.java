//package ru.kurganec.vk.messenger.model.actions;
//
//import android.os.Bundle;
//import android.os.ResultReceiver;
//import android.util.Log;
//import org.json.JSONException;
//import org.json.JSONObject;
//import ru.kurganec.vk.messenger.api.VKApi;
//
///**
// * User: anatoly
// * Date: 24.06.12
// * Time: 1:21
// */
//public class SetUserPhotoAction extends BaseAction {
//    private final String mFileName ;
//
//
//
//
//    public SetUserPhotoAction(ResultReceiver mReceiver, Bundle args) {
//        super(mReceiver, args);
//        this.mFileName = args.getString("img");
//    }
//    @Override
//    protected JSONObject doInBackground(Void... strings) {
//        try {
//            JSONObject ret =  VKApi.getProfileUploadServer();
//            if (ret == null){
//                return null;
//            }
//            JSONObject response = ret.getJSONObject("response");
//            String uri = response.getString("upload_url");
//            JSONObject photoInfo = VKApi.uploadProfilePhoto(uri, mFileName);
//            if (photoInfo == null){
//                return null;
//            }
//            String server = photoInfo.getString("server");
//            String photo = photoInfo.getString("photo");
//            String hash = photoInfo.getString("hash");
//            return VKApi.saveProfilePhoto(server, photo, hash); //TODO обновить данные пользователя в базе
//        } catch (JSONException e) {
//            Log.e("VKLOL", "SET USER PHOT FAILED", e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void onPostExecute(JSONObject json) {
//        super.onPostExecute(json);
//        if (json != null) {
//
//        }
//    }
//}
