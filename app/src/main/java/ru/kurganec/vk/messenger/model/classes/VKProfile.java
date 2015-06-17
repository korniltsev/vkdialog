package ru.kurganec.vk.messenger.model.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 23:34
 * "uid,first_name,last_name,photo,photo_medium,photo_big,online"
 */


public class VKProfile {
   
    private long uid;
   
    private String first_name;
   
    private String last_name;
   
    private String photo;
   
    private String photo_big;
   
    private String photo_medium;
   
    private String phone;

    public String getPhone() {
        return phone;
    }
    //   
//    boolean friend = false;


    public static final int ONLINE =1;
    public static final int OFFLINE =0;
    /**
     * 1 - online,
     * 0 - offline
     */
   
    private int online;
   
    private Integer hint = null;
   
    private boolean suggestion = false;
   
    private boolean request = false;
   
    private boolean search = false;

   
    private boolean can_write_private_message = true;

   
    private boolean friendship_request_sent = false;
    /**
     *
     */
    public VKProfile(JSONObject obj) throws JSONException {
        uid = obj.getLong("uid");
        first_name = obj.getString("first_name");
        last_name = obj.getString("last_name");
        photo = obj.getString("photo") ;
        photo_medium = obj.getString("photo_medium") ;
        photo_big = obj.getString("photo_big") ;
        online = obj.getInt("online");
        if (obj.has("phone")){
            phone = obj.getString("phone");
        }
        can_write_private_message = obj.getInt("can_write_private_message") != 0;

    }

    public VKProfile() {

    }


    public long getUid() {
        return uid;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPhotoBig() {
        return photo_big;
    }

    public String getPhotoMedium() {
        return photo_medium;
    }

    public int getOnline() {
        return online;
    }

    public Integer getHint() {
        return hint;
    }

    public static List<VKProfile> parseArray(JSONArray arr, int from) throws JSONException {
        ArrayList< VKProfile> ret =  new ArrayList<VKProfile>(arr.length());//new HashMap<Long, VKProfile>();
        for (int i = from; i < arr.length(); ++i){
            VKProfile c = new VKProfile(arr.getJSONObject(i));
            ret.add(c);
        }

        return  ret;
    }
    public static List<VKProfile> parseArray(JSONArray arr) throws JSONException {
     return parseArray(arr, 0);
    }


    public static HashMap<Long ,VKProfile> parseArrayToSet(JSONArray arr) throws JSONException {
        HashMap<Long ,VKProfile> ret =  new HashMap<Long ,VKProfile>();//new HashMap<Long, VKProfile>();
        for (int i = 0; i < arr.length(); ++i){
            VKProfile c = new VKProfile(arr.getJSONObject(i));
            ret.put(c.getUid(), c);
        }

        return  ret;
    }

    public void setHint(Integer i) {
        hint = i;
    }

    public boolean isFriend() {
        return hint != null;
    }

//    public void setFriend(boolean aFriend) {
//        friend = aFriend;
//    }

    @Override
    public String toString() {
        return last_name + " " + first_name;
    }

    public void setOnline(boolean aOnline) {
        online = aOnline ? ONLINE :OFFLINE;

    }

    public void setSuggestion(boolean b) {
        suggestion = b;
    }

    public void setRequest(boolean b) {
        request = b;
    }

    public boolean isRequest() {
        return request;
    }

    public boolean isSuggestion() {
        return suggestion;
    }

    public boolean getRequest() {
        return request;
    }

    public String getDisplayName(){
        return last_name + " " + first_name;
    }

    public void setSearch(boolean b) {
        search = b;

    }

    public boolean canWriteMessages() {
        return can_write_private_message;
    }

    public boolean isFriendship_request_sent() {
        return friendship_request_sent;
    }

    public void setFriendship_request_sent(boolean friendship_request_sent) {
        this.friendship_request_sent = friendship_request_sent;
    }
}
