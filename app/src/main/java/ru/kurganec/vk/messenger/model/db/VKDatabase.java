package ru.kurganec.vk.messenger.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.utils.Joiner;
import ru.kurganec.vk.messenger.utils.Translit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 1:18
 */
public class VKDatabase extends SQLiteOpenHelper {


    private static final int DB_VERSION = 16;
    private static final String DB_NAME = "db";


    private MessageHelper mMessagesHelper;
    private ProfileHelper mProfileHelper;
    private static final String TAG = "VK-CHAT-DATABASE";


    @SuppressWarnings("unchecked")
    public VKDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        mMessagesHelper = new MessageHelper();
        mProfileHelper = new ProfileHelper();
    }

    public MessageHelper msg() {
        return mMessagesHelper;
    }

    public ProfileHelper profiles() {
        return mProfileHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        initDB(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion <= 15 && newVersion > 15){
            updateSince16DB(sqLiteDatabase);
        }
    }

    private void updateSince16DB(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("ALTER TABLE " + Profile.TABLE_NAME +
                " ADD COLUMN " +Profile.ONLINE_MOBILE + " INTEGER ");
    }

    private void initDB(SQLiteDatabase sqLiteDatabase) {
        createProfilesTable(sqLiteDatabase);
        createHintsTable(sqLiteDatabase);
        createRequestsTable(sqLiteDatabase);
        createMessagesTable(sqLiteDatabase);
        createChatsTable(sqLiteDatabase);
        createSearchResultTable(sqLiteDatabase);
    }

    private void createSearchResultTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Message.SearchResult.TABLE_NAME + "(" +
                Message.SearchResult.MID + " INTEGER PRIMARY KEY ) "
        );

    }


    private void createMessagesTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + Message.TABLE_NAME + "(" +
                        Message.MID + " INTEGER PRIMARY KEY," +
                        Message.UID + " INTEGER NOT NULL," +
                        Message.DATE + " INTEGER NOT NULL," +
                        Message.READ_STATE + " INTEGER NOT NULL," +
                        Message.OUT + " INTEGER NOT NULL, " +
                        Message.BODY + " TEXT, " +
                        Message.TITLE + " TEXT ," +
                        Message.DELETED + " INTEGER NOT NULL," +
                        Message.ATTACHMENTS + " TEXT," +
                        Message.LATITUDE + " INTEGER," +
                        Message.LONGITUDE + " INTEGER," +
                        Message.CHAT_ID + " INTEGER)");


    }

    private void dropDB(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table " + Profile.Request.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + Profile.Hint.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + Profile.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + Message.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + Message.Chat.TABLE_NAME);
        sqLiteDatabase.execSQL("drop table " + Message.SearchResult.TABLE_NAME);

    }

    private void createChatsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + Message.Chat.TABLE_NAME + "(" +
                        Message.Chat.CHAT_ID + " INTEGER PRIMARY KEY," +
                        Message.Chat.CHAT_ACTIVE + " TEXT," +
                        Message.Chat.USERS_COUNT + " INTEGER NOT NULL," +
                        Message.Chat.ADMIN_ID + " INTEGER NOT NULL," +
                        Message.Chat.TITLE + " TEXT )"

        );
    }

    private void createProfilesTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + Profile.TABLE_NAME + "(" +
                        Profile.UID + " INTEGER PRIMARY KEY," +
                        Profile.FIRST_NAME + " TEXT , " +
                        Profile.LAST_NAME + " TEXT , " +
                        Profile.LOWER_FIRST_NAME + " TEXT , " +
                        Profile.LOWER_LAST_NAME + " TEXT , " +
                        Profile.PHOTO_BIG + " TEXT NOT NULL," +
                        Profile.PHOTO_MEDIUM + " TEXT NOT NULL," +
                        Profile.PHOTO + " TEXT NOT NULL," +
                        Profile.PHONE + " TEXT ," +
                        Profile.ONLINE + " INTEGER," +
                        Profile.ONLINE_MOBILE + " INTEGER," +
                        Profile.CAN_WRITE_PM + " INTEGER NOT NULL)"
        );
    }

    private void createRequestsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Profile.Request.TABLE_NAME + "(" +
                Profile.Request.UID + " INTEGER PRIMARY KEY ) "

        );

    }

    private void createHintsTable(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Profile.Hint.TABLE_NAME + "(" +
                Profile.Hint.UID + " INTEGER PRIMARY KEY, " +
                Profile.Hint.HINT + " INTEGER UNIQUE NOT NULL)"
        );
    }


    public Cursor queryConversations() {
        SQLiteDatabase db = getWritableDatabase();


        Cursor ret = db.rawQuery(" SELECT  b.mid _id, b.mid mid, b.body body, b.date date, b.read_state read_state, b.out out, " +
                "                 b.attachments attachments, b.latitude latitude,   " +
                "                 p.uid uid, p.first_name first_name, p.last_name last_name, p.photo_big photo_big, p.online online, " +
                "                 null chat_id, null title, p.online_mobile online_mobile, p.photo photo   " +
                "                 FROM ( " +
                "                 select max(mid) as lastmid   " +
                "                 from messages m1 WHERE " +
                "                 chat_id IS NULL  " +
                "                 AND deleted = 0  " +
                "                 GROUP by uid " +
                "                 ) a  " +
                "                 INNER JOIN messages b " +
                "                 ON b.mid = a.lastmid  " +
                "                 INNER JOIN profiles p  " +
                "                 ON p.uid = b.uid  " +
                "" +
                " UNION  " +
                "                SELECT b.mid _id, b.mid mid, b.body body, b.date date, b.read_state read_state, b.out out,  " +
                "                 b.attachments attachments, b.latitude latitude,   " +
                "                 p.uid uid, p.first_name first_name, p.last_name last_name, p.photo_big photo_big, p.online online, " +
                "                 c.chat_id chat_id, c.title title,  p.online_mobile online_mobile , p.photo photo" +
                "                FROM ( " +
                "                select max(mid) as lastmid FROM " +
                "                 messages  m1 WHERE " +
                "                 chat_id IS NOT NULL  " +
                "                AND deleted = 0  " +
                "                GROUP by chat_id " +
                "                ) a  " +
                "                INNER JOIN messages b  " +
                "                 ON b.mid = a.lastmid  " +
                "                 INNER JOIN chats c  " +
                "                 ON b.chat_id = c.chat_id  " +
                "                 INNER JOIN profiles p  " +
                "                 ON p.uid = b.uid " +
                "                 ORDER BY mid DESC " +
                "                 LIMIT 50", new String[0]);

        return ret;
    }


    //old orm methods


    public String getUserDisplayInfo(Long uid) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {Profile.FIRST_NAME, Profile.LAST_NAME};
        String[] selectionArgs = {uid.toString()};
        Cursor profile = db.query(Profile.TABLE_NAME, columns, Profile.UID + " = ?", selectionArgs, null, null, null);
        try {
            if (profile.moveToFirst()) {
                return Joiner.on(" ").
                        join(Arrays.asList(profile.getString(profile.getColumnIndex(Profile.LAST_NAME)),
                                profile.getString(profile.getColumnIndex(Profile.FIRST_NAME))));

            }
            return null;
        } finally {
            profile.close();
        }
    }

    public String getChatDisplayInfo(Long mChatId) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {Message.Chat.TITLE};
        String[] selectionArgs = {mChatId.toString()};
        Cursor chat = db.query(Message.Chat.TABLE_NAME, columns, Message.Chat.CHAT_ID + " = ?", selectionArgs, null, null, null);
        try {
            if (chat.moveToFirst()) {
                return chat.getString(chat.getColumnIndex(Message.Chat.TITLE));
            }
            return null;
        } finally {
            chat.close();
        }
    }


    public Cursor getChatHistory(long uid) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                " SELECT " + Message.MID + " _id, " +
                        " body, mid, read_state, date, out, attachments, latitude, longitude  " +
                        " FROM " + Message.TABLE_NAME +
                        " WHERE " + Message.UID + " = ? AND " + Message.CHAT_ID + " IS NULL ",
                new String[]{String.valueOf(uid)});

    }


    public Cursor getGroupChatHistory(Long mChatId) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(//todo optimize quer
                "SELECT m." + Message.MID + " as _id, m.*, p." + Profile.PHOTO_BIG +
                        " FROM " + Message.TABLE_NAME + " m " +
                        " INNER JOIN " + Profile.TABLE_NAME + " p " +
                        " ON p." + Profile.UID + " = m." + Message.UID +
                        " WHERE m." + Message.CHAT_ID + " = ?",
                new String[]{String.valueOf(mChatId)});

    }



    public void clearDB() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + Profile.Request.TABLE_NAME);
        sqLiteDatabase.execSQL("delete from " + Profile.Hint.TABLE_NAME);
        sqLiteDatabase.execSQL("delete from " + Profile.TABLE_NAME);
        sqLiteDatabase.execSQL("delete from " + Message.TABLE_NAME);
        sqLiteDatabase.execSQL("delete from " + Message.Chat.TABLE_NAME);
        sqLiteDatabase.execSQL("delete from " + Message.SearchResult.TABLE_NAME);
    }

    public void deleteConversation(Long uid, Long chat_id) {
        if (chat_id == null){
            //delete uid
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from " + Message.TABLE_NAME + " where " + Message.CHAT_ID  + " is null  and " + Message.UID + " = " + uid);
        } else {
            //delete chat_id
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from " + Message.TABLE_NAME + " where " + Message.CHAT_ID + " = " + chat_id);
        }
    }


    public class MessageHelper {


        public void setDeleted(long mid, boolean deleted) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(Message.DELETED, deleted);
            db.update(Message.TABLE_NAME, cv, Message.MID + " = ?", new String[]{String.valueOf(mid)});
        }

        public Cursor get(long mid) {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery("SELECT * FROM " + Message.TABLE_NAME + " " +
                    "WHERE " + Message.MID + " = ? ", new String[]{String.valueOf(mid)});
        }

        public void setReadState(long mid, int read) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(Message.READ_STATE, read);
            db.update(Message.TABLE_NAME, cv, Message.MID + " = ?", new String[]{String.valueOf(mid)});
        }

        public void insert(long messageID, long from, long time, int read, int out, String title, String body, String attach, Long chat_id) {
            ContentValues cv = new ContentValues();
            cv.put(Message.MID, messageID);
            cv.put(Message.UID, from);
            cv.put(Message.READ_STATE, read);
            cv.put(Message.OUT, out);
            cv.put(Message.TITLE, title);
            cv.put(Message.BODY, body);
            cv.put(Message.CHAT_ID, chat_id);
            cv.put(Message.DATE, time);
            cv.put(Message.DELETED, 0);
            SQLiteDatabase db = getWritableDatabase();
            db.replace(Message.TABLE_NAME, null, cv);
        }

        public void insert(JSONObject obj) throws JSONException {
            ContentValues cv = new ContentValues();
            cv.put(Message.MID, obj.getLong(Message.MID));
            cv.put(Message.UID, obj.getLong(Message.UID));
            cv.put(Message.READ_STATE, obj.getInt(Message.READ_STATE));
            cv.put(Message.OUT, obj.getInt(Message.OUT));
            cv.put(Message.TITLE, obj.getString(Message.TITLE));
            cv.put(Message.BODY, obj.getString(Message.BODY));
            if (obj.has(Message.CHAT_ID)) {
                cv.put(Message.CHAT_ID, obj.getLong(Message.CHAT_ID));
            }
            if (obj.has(Message.OBJECT_GEO)) {
                JSONObject geo = obj.getJSONObject(Message.OBJECT_GEO);
                String[] coords = geo.getString("coordinates").split(" ");

                int lat, lon;
                float flat, flon;
                flat = Float.parseFloat(coords[0]);
                flon = Float.parseFloat(coords[1]);
                lat = (int) (flat * 1000000);
                lon = (int) (flon * 1000000);


                cv.put(Message.LATITUDE, lat);
                cv.put(Message.LONGITUDE, lon);
            }
            if (obj.has(Message.ATTACHMENTS)) {
                cv.put(Message.ATTACHMENTS, obj.getString(Message.ATTACHMENTS));
            }
            cv.put(Message.DATE, obj.getLong(Message.DATE));
            cv.put(Message.DELETED, 0);
            SQLiteDatabase db = getWritableDatabase();
            db.replace(Message.TABLE_NAME, null, cv);
        }


        public Cursor getUnread() {
            return getWritableDatabase().rawQuery("select m.*, p.* from " + Message.TABLE_NAME + " m " +
                    " inner join " + Profile.TABLE_NAME + " p " +
                    " on m." + Message.UID + " = p." + Profile.UID +
                    " where " + Message.READ_STATE + " = ? and " + Message.OUT + " = ? order by m." + Message.MID + " desc",
                    new String[]{String.valueOf(Message.STATE_UNREAD), String.valueOf(Message.STATE_IN)});
        }

        public int getUnreadCount() {
            Cursor c = getWritableDatabase().rawQuery("select count() from " + Message.TABLE_NAME + "" +
                    " where " + Message.READ_STATE + " = ? and " + Message.OUT + " = ?",
                    new String[]{String.valueOf(Message.STATE_UNREAD), String.valueOf(Message.STATE_IN)});
            c.moveToFirst();
            int ret = c.getInt(0);
            c.close();
            return ret;
        }

        public void markAsRead(String mids) {
            getWritableDatabase().rawQuery("update " + Message.TABLE_NAME + " " +
                    "set " + Message.READ_STATE + " = ? where " + Message.MID + " in (?)",
                    new String[]{String.valueOf(Message.STATE_READ), mids});
        }

        /**
         * @param messages less than 100 messages in array
         * @throws JSONException
         */
        public void storeSearchResult(JSONArray messages) throws JSONException {
            List<Long> mids = storeMessages(messages);
            StringBuilder sb = new StringBuilder();
            sb.append("REPLACE INTO " + Message.SearchResult.TABLE_NAME);
            for (int i = 0; i < mids.size(); ++i) {
                sb.append(i == 0 ? " SELECT " : " UNION SELECT ");
                sb.append(mids.get(i)).append(" as ").append(Message.SearchResult.MID).append(" ");

            }

            SQLiteDatabase db = getWritableDatabase();
//            db.execSQL("delete from " + Message.SearchResult.TABLE_NAME);
            if (mids.size() > 0) {
                db.execSQL(sb.toString());
            }

        }

        public ArrayList<Long> storeMessages(JSONArray messages) throws JSONException {
            StringBuilder msgSB = new StringBuilder();
            StringBuilder chatSB = new StringBuilder();
            ArrayList<Long> mids = new ArrayList<Long>();

            msgSB.append("REPLACE INTO ").append(Message.TABLE_NAME).append(' ');
            chatSB.append("REPLACE INTO ").append(Message.Chat.TABLE_NAME).append(' ');
            int chatsCount = 0;
            for (int i = 1; i < messages.length(); ++i) {

                JSONObject msg = messages.getJSONObject(i);
                mids.add(msg.getLong(Message.MID));
                msgSB.append(i == 1 ? " SELECT " : " UNION SELECT ");
                msgSB.append(msg.getLong(Message.MID)).append(" as ").append(Message.MID).append(",");
                msgSB.append(msg.getLong(Message.UID)).append(" as ").append(Message.UID).append(",");
                msgSB.append(msg.getLong(Message.DATE)).append(" as ").append(Message.DATE).append(",");
                msgSB.append(msg.getInt(Message.READ_STATE)).append(" as ").append(Message.READ_STATE).append(",");
                msgSB.append(msg.getInt(Message.OUT)).append(" as ").append(Message.OUT).append(",");
                msgSB.append(DatabaseUtils.sqlEscapeString(msg.getString(Message.BODY))).append(" as ")
                        .append(Message.BODY).append(",");
                if (msg.has(Message.TITLE)) {
                    msgSB.append(DatabaseUtils.sqlEscapeString(msg.getString(Message.TITLE))).append(" as ")
                            .append(Message.TITLE).append(",");
                } else {
                    msgSB.append("null").append(" as ")
                            .append(Message.TITLE).append(",");
                }
                msgSB.append("0").append(" as ").append(Message.DELETED).append(", ");
                String attach = null;
                if (msg.has(Message.ATTACHMENTS)) {
                    attach = DatabaseUtils.sqlEscapeString(msg.getString(Message.ATTACHMENTS));
                }


                msgSB.append(attach).append(" as ").append(Message.ATTACHMENTS).append(",");

                Integer lat = null, lon = null;
                if (msg.has(Message.OBJECT_GEO)) {
                    JSONObject geo = msg.getJSONObject(Message.OBJECT_GEO);
                    String[] coords = geo.getString("coordinates").split(" ");


                    float flat, flon;
                    flat = Float.parseFloat(coords[0]);
                    flon = Float.parseFloat(coords[1]);
                    lat = (int) (flat * 1000000);
                    lon = (int) (flon * 1000000);

                }
                msgSB.append(lat).append(" as ").append(Message.LATITUDE).append(",");
                msgSB.append(lon).append(" as ").append(Message.LONGITUDE).append(", ");

                if (msg.has(Message.Chat.CHAT_ID)) {
                    chatSB.append(chatsCount++ == 0 ? " SELECT " : " UNION SELECT ");
                    chatSB.append(msg.getLong(Message.Chat.CHAT_ID)).append(" as ").append(Message.Chat.CHAT_ID).append(",");
                    chatSB.append(DatabaseUtils.sqlEscapeString(msg.getString(Message.Chat.CHAT_ACTIVE))).append(" as ")
                            .append(Message.Chat.CHAT_ACTIVE).append(",");
                    chatSB.append(msg.getInt(Message.Chat.USERS_COUNT)).append(" as ").append(Message.Chat.USERS_COUNT).append(",");
                    chatSB.append(msg.getLong(Message.Chat.ADMIN_ID)).append(" as ").append(Message.Chat.ADMIN_ID).append(",");
                    chatSB.append(DatabaseUtils.sqlEscapeString(msg.getString(Message.Chat.TITLE))).append(" as ")
                            .append(Message.Chat.TITLE).append(" ");

                    msgSB.append(msg.getLong(Message.CHAT_ID)).append(" as ").append(Message.Chat.CHAT_ID).append(' ');
                } else {
                    msgSB.append(" NULL ").append(" as ").append(Message.Chat.CHAT_ID).append(' ');
                }

            }
            SQLiteDatabase db = getWritableDatabase();
            if (messages.length() > 1) {
                db.execSQL(msgSB.toString());
            }
            if (chatsCount > 0) {
                db.execSQL(chatSB.toString());
            }
            return mids;
        }

        public void clearPreviousSearchResult() {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from " + Message.SearchResult.TABLE_NAME);
        }

        public Cursor getSearchResult() {
            SQLiteDatabase db = getWritableDatabase();
            return db.rawQuery("SELECT  m." + Message.SearchResult.MID + " _id,  * from " + Message.TABLE_NAME + " m " +
                    " INNER JOIN " + Message.SearchResult.TABLE_NAME + " s " +

                    " ON m." + Message.MID + " = s." + Message.SearchResult.MID + " " +
                    " INNER JOIN " + Profile.TABLE_NAME + " p " +
                    "ON p." + Profile.UID + " = m." + Message.UID + " " +
                    "ORDER BY m." + Message.MID + " DESC", new String[0]);
        }

        public int getOffset(Long mMidToDisplay, Long mUid, Long mChatId) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c;
            if (mChatId == null) {
                String str = "select count(mid) from " + Message.TABLE_NAME + " " +
                        "where " + Message.MID + "<= ? and " + Message.UID + " = ? and " +
                        Message.CHAT_ID + " is null ";
                Log.d(TAG, str);
                c = db.rawQuery(str,
                        new String[]{String.valueOf(mMidToDisplay), String.valueOf(mUid)});
            } else {
                c = db.rawQuery("select count() from " + Message.TABLE_NAME + " " +
                        "where " + Message.MID + "<= ? and " +
                        Message.CHAT_ID + " = ? ",
                        new String[]{String.valueOf(mMidToDisplay), String.valueOf(mChatId)});
            }
            c.moveToFirst();
            int ret = c.getInt(0);
            c.close();
            return ret;//
        }
    }

    public class ProfileHelper {

        /**
         * there is a limitations in sqlite on maximum amount of compound selects
         * I use a little hack to speed up multiple insertions which uses multiple compound selects
         * so we have to use less than 500 selects. I use 450.
         */
        public static final int PORTION_SIZE = 450;

        public Cursor get(long uid) {
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery("SELECT * FROM " + Profile.TABLE_NAME + " " +
                    "WHERE " + Profile.UID + " = ? ", new String[]{String.valueOf(uid)});
        }


        /**
         * stores 450 profiles starting from specific position
         *
         * @param profiles
         * @param from
         * @return
         */
        private List<Long> storePortion(SQLiteDatabase db, JSONArray profiles, int from) throws JSONException {
            StringBuilder sb = new StringBuilder();
            ArrayList<Long> ids = new ArrayList<Long>(profiles.length());
            sb.append("REPLACE INTO ").append(Profile.TABLE_NAME);
            for (int i = from; i < from + PORTION_SIZE && i < profiles.length(); ++i) {
                JSONObject friend = profiles.getJSONObject(i);
                ids.add(friend.getLong(Profile.UID));

                sb.append(i % PORTION_SIZE == 0 ? " SELECT " : " UNION SELECT ");

                sb.append(friend.getLong(Profile.UID));
                sb.append(" AS ").append(Profile.UID).append(",");

                String firstName = DatabaseUtils.sqlEscapeString(friend.getString(Profile.FIRST_NAME));
                sb.append(firstName);
                sb.append(" AS ").append(Profile.FIRST_NAME).append(",");


                String lastName = DatabaseUtils.sqlEscapeString(friend.getString(Profile.LAST_NAME));
                sb.append(lastName);
                sb.append(" AS ").append(Profile.LAST_NAME).append(",");
                //hack
                sb.append(firstName.toLowerCase()).append(" AS ").append(Profile.LOWER_FIRST_NAME).append(", ");
                sb.append(lastName.toLowerCase()).append(" AS ").append(Profile.LOWER_LAST_NAME).append(", ");

                sb.append(DatabaseUtils.sqlEscapeString(friend.getString(Profile.PHOTO_BIG)));
                sb.append(" AS ").append(Profile.PHOTO_BIG).append(",");

                sb.append(DatabaseUtils.sqlEscapeString(friend.getString(Profile.PHOTO_MEDIUM)));
                sb.append(" AS ").append(Profile.PHOTO_MEDIUM).append(",");

                sb.append(DatabaseUtils.sqlEscapeString(friend.getString(Profile.PHOTO)));
                sb.append(" AS ").append(Profile.PHOTO).append(",");

                sb.append(DatabaseUtils.sqlEscapeString(friend.optString(Profile.PHONE)));
                sb.append(" AS ").append(Profile.PHONE).append(",");

                sb.append(friend.getInt(Profile.ONLINE));
                sb.append(" AS ").append(Profile.ONLINE).append(",");

                int from_mobile = friend.optInt(Profile.ONLINE_MOBILE, 0);
                sb.append(from_mobile);
                sb.append(" AS ").append(Profile.ONLINE_MOBILE).append(",");

                sb.append(friend.getInt(Profile.CAN_WRITE_PM));
                sb.append(" AS ").append(Profile.CAN_WRITE_PM).append(" ");


            }

            db.execSQL(sb.toString());
            return ids;
        }


        public List<Long> store(JSONArray profiles) throws JSONException {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();


            ArrayList<Long> ids = new ArrayList<Long>(profiles.length());
            try {
                int i;
                for (i = 0; i < profiles.length() / PORTION_SIZE; ++i) {
                    ids.addAll(storePortion(db, profiles, i * PORTION_SIZE));
                }
                if (profiles.length() % PORTION_SIZE != 0) {
                    ids.addAll(storePortion(db, profiles, i * PORTION_SIZE));
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            return ids;
        }


        public void storeFriends(JSONArray friends) throws JSONException {
            SQLiteDatabase db = getWritableDatabase();
            List<Long> ids = store(friends);

            db.beginTransaction();
            try {
                db.execSQL("delete from " + Profile.Hint.TABLE_NAME);
                int i;
                for (i = 0; i < ids.size() / PORTION_SIZE; ++i) {
                    storeFriendsHintsPortion(db, ids, i * PORTION_SIZE);
                }
                if (ids.size() % PORTION_SIZE != 0) {
                    storeFriendsHintsPortion(db, ids, i * PORTION_SIZE);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }


        }

        private void storeFriendsHintsPortion(SQLiteDatabase db, List<Long> ids, int from) {
            StringBuilder sb = new StringBuilder();
            sb.append("REPLACE INTO " + Profile.Hint.TABLE_NAME);
            for (int i = from; i < from + PORTION_SIZE && i < ids.size(); ++i) {
                if (i % PORTION_SIZE == 0) {
                    sb.append(" SELECT ");
                    sb.append(ids.get(i)).append(" as ").append(Profile.Hint.UID).append(",");
                    sb.append(i).append(" as ").append(Profile.Hint.HINT).append(" ");
                } else {
                    sb.append(" UNION SELECT ").append(ids.get(i)).append(",").append(i).append(' ');
                }
            }
            db.execSQL(sb.toString());
        }


        public Cursor queryFriends(String query, boolean online) {
            query = query.toLowerCase();
            final String translit = Translit.translit(query, false);
            final String translitReversed = Translit.translit(query, true);
            Log.d("VKDialog", String.format("%s, %s", translit, translitReversed));
            SQLiteDatabase db = getReadableDatabase();
            return db.rawQuery("SELECT p.uid _id, p.uid uid, p.photo_big photo_big, " +
                    " p.first_name first_name, p.last_name last_name, p.online online , p.online_mobile online_mobile, p.photo photo " +
                    "FROM profiles p " +
                    "INNER JOIN hints h " +
                    "ON p.uid = h.uid " +
                    " WHERE " + (online ? "p.online=1 and":"") +
                    " (p.lower_last_name like '%" + query + "%' or p.lower_first_name like '%" + query + "%' " +
                    " or p.lower_last_name like '%" + translit + "%' or p.lower_first_name like '%" + translit + "%' " +
                    " or p.lower_last_name like '%" + translitReversed + "%' or p.lower_first_name like '%" + translitReversed + "%') " +
                    "ORDER BY h.hint"
                    , null);
        }


        public Cursor queryFriends() {
            return queryFriends("", false);
        }

        public Cursor queryOnlineFriends() {
            return queryFriends("", true);
        }

        public Cursor queryOnlineFriends(String q) {
            return queryFriends(q, true);
        }

        public void storeRequests(JSONArray requests) throws JSONException {
            SQLiteDatabase db = getWritableDatabase();
            List<Long> ids = store(requests);
            db.beginTransaction();
            try {
                db.execSQL("delete from " + Profile.Request.TABLE_NAME);
                int i ;
                for (i = 0; i < ids.size() / PORTION_SIZE; ++i) {
                    storeRequestPortion(db, ids, i * PORTION_SIZE);
                }
                if (ids.size() % PORTION_SIZE != 0) {
                    storeRequestPortion(db, ids, i * PORTION_SIZE);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        private void storeRequestPortion(SQLiteDatabase db, List<Long> ids, int from) {
            StringBuilder sb = new StringBuilder();
            sb.append("REPLACE INTO " + Profile.Request.TABLE_NAME);
            for (int i = from; i < from + PORTION_SIZE && i < ids.size(); ++i) {
                if (i % PORTION_SIZE == 0) {
                    sb.append(" SELECT ");
                    sb.append(ids.get(i)).append(" as ").append(Profile.Hint.UID).append(" ");
                } else {
                    sb.append(" UNION SELECT ").append(ids.get(i)).append(" ");
                }
            }
            db.execSQL(sb.toString());
        }

    }

}
