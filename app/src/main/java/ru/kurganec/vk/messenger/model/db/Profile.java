package ru.kurganec.vk.messenger.model.db;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 13:45
 */
public class Profile {
    static final String TABLE_NAME = "profiles";
    public static final String UID = "uid";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PHOTO_BIG = "photo_big";
    public static final String PHOTO_MEDIUM = "photo_medium";
    public static final String PHOTO = "photo";
    public static final String PHONE = "phone";
    public static final String ONLINE = "online";
    public static final String CAN_WRITE_PM = "can_write_private_message";

    public static final String LOWER_FIRST_NAME = "lower_first_name";
    public static final String LOWER_LAST_NAME = "lower_last_name";
    public static final String ONLINE_MOBILE = "online_mobile";

    public static class Hint {
        public static final String TABLE_NAME= "hints";
        public static final String UID = Profile.UID;
        public static final String HINT = "hint";
    }

    public static class Request {
        public static final String TABLE_NAME= "requests";
        public static final String UID = Profile.UID;
    }


}
