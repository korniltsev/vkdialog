package ru.kurganec.vk.messenger.model.classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: anatoly
 * Date: 19.06.12
 * Time: 2:23
 */

public class VKChat {
   
    Long chat_id ; //   (только для групповых бесед) ID беседы
    /**
     * нигде не используется ну да ладно, вдруг понадобится
     */
   
    String chat_active;//(только для групповых бесед) ID последних участников беседы, разделённых запятыми, но не более 6.

   
    int users_count;//(только для групповых бесед) количество участников в беседе
   
    long admin_id;//(только для групповых бесед) ID создателя беседы
    /**
     * это поле не приходит через json, я его добавляю сам
     */
   
    String chat_participants;
   
    String title;

    public String getChatParticipants() {
        return chat_participants;
    }

    public void setParticipants(String chat_participants) {
        this.chat_participants = chat_participants;
    }

    public VKChat() {
    }

    public VKChat(Long aChatId) {
        chat_id = aChatId;
    }

    public static VKChat parseFromMessage(JSONObject msg) throws JSONException {
        long chat_id = msg.getLong("chat_id");
        String chat_active = msg.getString("chat_active");
        int user_count = msg.getInt("users_count");
        String title = msg.getString("title");
        if (msg.has("admin_id")){
            long  admin_id = msg.getLong("admin_id");
            return new VKChat(chat_id, chat_active, user_count, admin_id, title);
        } else {
            return new VKChat(chat_id, chat_active, user_count, title);
        }


    }

    private VKChat(long chat_id, String chat_active, int users_count, long admin_id, String title) {
        this.chat_id = chat_id;
        this.chat_active = chat_active;
        this.users_count = users_count;
        this.admin_id = admin_id;
        this.title = title;
    }

    private VKChat(long chat_id, String chat_active, int users_count,  String title) {
        this.chat_id = chat_id;
        this.chat_active = chat_active;
        this.users_count = users_count;
        this.admin_id = admin_id;
        this.title = title;
    }

    public long getChat_id() {
        return chat_id;
    }

    public String getChat_active() {
        return chat_active;
    }

    public int getUsers_count() {
        return users_count;
    }

    public long getAdmin_id() {
        return admin_id;
    }

    public void setUsers_count(int count) {
        users_count = count;


    }

    public String getTitle() {
        return title;
    }
}
