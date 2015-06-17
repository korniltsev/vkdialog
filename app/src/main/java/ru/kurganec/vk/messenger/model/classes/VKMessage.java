package ru.kurganec.vk.messenger.model.classes;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: anatoly
 * Date: 16.06.12
 * Time: 22:36
 * todo remove
 */
@Deprecated
public class VKMessage {

    public static final int READ = 1;
    public static final int UNREAD = 0;

    public static final int OUT = 1;
    public static final int IN = 0;

   
    long mid;//ID сообщения. Не передаётся для пересланных сообщений.
   
    long uid;//автор сообщения
   
    long date;//дата отправки сообщения
   
    int read_state;//статус прочтения сообщения (0 – не прочитано, 1 – прочитано) Не передаётся для пересланных сообщений.

   
    int out;//тип сообщения (0 – полученное сообщение, 1 – отправленное сообщение). Не передаётся для пересланных сообщений.
   
    String title;//заголовок сообщения или беседы
   
    String body;//текст сообщения

   
    String attachments = "";

   

    String fwd_messages;//массив пересланных сообщений (если есть)
   
    Long chat_id = null;//   (только для групповых бесед) ID беседы

   
    boolean deleted = false;

   
    Float latitude = null;

   
    Float longitude = null;


   
    private VKProfile mProfile;
   
    private VKChat mChat;

    public VKMessage(long messageID, long from, long time, int read, int aOut, String aTitle, String aBody,
                     String aAttach, Long aChat_id) {
        mid = messageID;
        uid = from;
        date = time;
        read_state = read;
        out = aOut;
        title = aTitle;
        body = aBody;
        attachments = aAttach;
        chat_id = aChat_id;
    }


    public VKChat getChat() {
        return mChat;
    }

    public boolean isChat() {
        return chat_id != null;
    }

    public VKProfile getProfile() {
        return mProfile;
    }

    public void setProfile(VKProfile aProfile) {
        this.mProfile = aProfile;
    }

    /**
     * @param parse
     * @param aChatId передай null если не чат ;(
     * @throws JSONException
     */
    public VKMessage(JSONObject parse, Long aChatId) throws JSONException {
        mid = parse.getLong("mid");
        uid = parse.getLong("uid");
        date = parse.getLong("date");
        read_state = parse.getInt("read_state");
        out = parse.getInt("out");
        body = parse.getString("body");
        if (parse.has("title")) {
            title = parse.getString("title");
        } else {
            title = "";
        }

        if (parse.has("attachments")) {
            attachments = parse.get("attachments").toString();
        } else {
            attachments = "";
        }
        if (parse.has("fwd_messages")) {
            fwd_messages = parse.getJSONArray("fwd_messages").toString();
        } else {
            fwd_messages = "";
        }
        if (parse.has("chat_id")) {
            chat_id = parse.getLong("chat_id");
            mChat = VKChat.parseFromMessage(parse);
        } else if (aChatId != null) {
            mChat = new VKChat(aChatId);
            chat_id = aChatId;
        } else {
            chat_id = null;
        }

        if (parse.has("geo")) {
            JSONObject geo = parse.getJSONObject("geo");
            String coordinates = geo.getString("coordinates");
            String[] splited = coordinates.split(" ");
            latitude = Float.parseFloat(splited[0]);
            longitude = Float.parseFloat(splited[1]);
        }
    }

    public VKMessage() {
    }

    public static List<VKMessage> parseArray(JSONArray messages) {
        List<VKMessage> ret = new ArrayList<VKMessage>();

        //в 0 количество диалогов,  дальше диалоги с 1,  не больше 100
        for (int i = 1; i < messages.length(); ++i) {
            try {
                ret.add(new VKMessage(messages.getJSONObject(i), null));
            } catch (JSONException e) {
                continue;
            }
        }

        return ret;
    }

    public static List<VKMessage> parseChatArray(JSONArray messages, long chatId) {
        List<VKMessage> ret = new ArrayList<VKMessage>();

        //в 0 количество диалогов,  дальше диалоги с 1,  не больше 100
        for (int i = 1; i < messages.length(); ++i) {
            try {
                ret.add(new VKMessage(messages.getJSONObject(i), chatId));
            } catch (JSONException e) {
                continue;
            }
        }

        return ret;
    }

    public long getMid() {
        return mid;
    }

    public long getUid() {
        return uid;
    }

    public long getDate() {
        return date * 1000;
    }

    public int getReadState() {
        return read_state;
    }

    public int getOutState() {
        return out;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAttachments() {
        return attachments;
    }

    public String getFwd_messages() {
        return fwd_messages;
    }

    public long getChat_id() {
        return chat_id;
    }

//    public String getChat_active() {
//        return chat_active;
//    }
//
//    public int getUsers_count() {
//        return users_count;
//    }
//
//    public long getAdmin_id() {
//        return admin_id;
//    }
//
//    public boolean isDialog_head() {
//        return dialog_head;
//    }

    @Override
    public String toString() {
        return (mProfile == null ? "" : mProfile.toString()) + " - " + body;
    }


    public void setUID(long mProfileUID) {
        uid = mProfileUID;
    }

    public void setAttachments(String s) {
        attachments = s;
    }

    public void setDeleted(boolean b) {
        deleted = b;
    }


    public void setReadState(int read_state) {
        this.read_state = read_state;
    }


    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public JSONArray getJsonAttach(){
        return mJsonAttach;
    }

    private JSONArray mJsonAttach;
    public void createAttachmentsJson() {
        try {
            mJsonAttach = new JSONArray(getAttachments());
        } catch (JSONException e) {
            mJsonAttach = null;
        }
    }
}

