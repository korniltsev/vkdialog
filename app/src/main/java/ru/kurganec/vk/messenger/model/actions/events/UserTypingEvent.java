package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;

/**
 * User: anatoly
 * Date: 21.08.12
 * Time: 15:38
 */
public class UserTypingEvent extends BaseEvent {
    private Long chatId;
    private Long uid;

    public Long getUid() {
        return uid;
    }

    /**
     *
     * @return chatId if an user is typing in chat
     */
    public Long getChatId() {
        return chatId;
    }

    public UserTypingEvent(Bundle resultData) {
        super(resultData);
        if (resultData.containsKey("chat_id")) {
            chatId = resultData.getLong("chat_id");
        }
        uid = resultData.getLong("uid");

    }
}
