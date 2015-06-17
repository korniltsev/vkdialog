package ru.kurganec.vk.messenger.model.db;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 14:46
 */
public class Message {
    static final String TABLE_NAME = "messages";

    public static final String MID = "mid";
    public static final String UID = "uid";
    public static final String DATE = "date";
    public static final String READ_STATE = "read_state";
    public static final String OUT = "out";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String DELETED = "deleted";
    public static final String CHAT_ID = "chat_id";
    public static final String ATTACHMENTS = "attachments";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";



    public static final String OBJECT_GEO = "geo";
    public static class Chat {
        static final String TABLE_NAME = "chats";
        public static final String CHAT_ID = "chat_id";
        public static final String CHAT_ACTIVE = "chat_active";
        public static final String USERS_COUNT = "users_count";
        public static final String ADMIN_ID = "admin_id";
        public static final String TITLE = "title";
    }

    public static class SearchResult {
        static final String TABLE_NAME = "search_result";
        public static final String MID = "mid";
    }


    public static final int STATE_READ = 1;
    public static final int STATE_UNREAD = 0;

    public static final int STATE_OUT = 1;
    public static final int STATE_IN = 0;
}
