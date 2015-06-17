package ru.kurganec.vk.messenger.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ru.kurganec.vk.messenger.R;

import static ru.kurganec.vk.messenger.api.VKApi.KEYS.ACCESS_TOKEN;
import static ru.kurganec.vk.messenger.api.VKApi.KEYS.USER_ID;

/**
 * User: anatoly
 * Date: 13.06.12
 * Time: 19:33
 */
public class Model {

    private SharedPreferences mPreferences;
//    private VKDatabase mDatabaseHelper;

    private String mAccessToken = null;
    private String mSecret = null;
    private static final String PREF_USER_ID = USER_ID;
    public static final long NOT_SYNCED = 0;
    public static final String MODEL_PREFERENCES = "MODEL";
    public static final String PREF_LONG_POLL_URI = "LONG_POLL_URI";
    public static final String PREF_LONG_POLL_TS = "LONG_POLL_TS";
    public static final String PREF_LONG_POLL_KEY = "LONG_POLL_KEY";
    private static final String PREF_LAST_SYNC = "LAST_TIME_SYNC";
    private static final String PREF_SECRET = "PREF_SECRET";
    private static final String PREF_NOTIFICATION = "PREF_NOTIFICATION_TYPE";
    private static final String PREF_CONVERSATION_COUNT = "PREF_CONVERSATION_COUNT";
    private static final String PREF_SIGN_UP_SID = "SIGNUPSID";

    private Context mAppContext;

    public Model(Context appContext) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);

        mAppContext = appContext;

    }


    public boolean isAppSignedIn() {
        return mPreferences.getString(ACCESS_TOKEN, null) != null;
    }

    public String getAccessToken() {
        if (mAccessToken == null) {
            mAccessToken = mPreferences.getString(ACCESS_TOKEN, null);
        }
        return mAccessToken;
    }

    public void setAccessToken( String tokenValue) {
        mAccessToken = tokenValue;
        storePref(ACCESS_TOKEN, tokenValue);
    }

    private void storePref(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value == null){
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.commit();
    }

    private void storePref(String key, Long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value == null){
            editor.remove(key);
        } else {
            editor.putLong(key, value);
        }
        editor.commit();
    }

    private void storePref(String key, Integer value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value == null){
            editor.remove(key);
        } else {
            editor.putInt(key, value);
        }
        editor.commit();
    }

    private String getPref(String key) {
        return mPreferences.getString(key, null);
    }

    public String getLongPollURI() {
        return mPreferences.getString(PREF_LONG_POLL_URI, null);
    }

    public long getLongPollTimeSync() {
        return mPreferences.getLong(PREF_LONG_POLL_TS, 0);
    }

    public String getLongPollKey() {
        return mPreferences.getString(PREF_LONG_POLL_KEY, null);
    }

    public Long getUserID() {
        long res = mPreferences.getLong(PREF_USER_ID, -1);
        return res == -1 ? null : res;
    }

    public void storeLongPollURI(String uri) {
        storePref(PREF_LONG_POLL_URI, uri);
    }

    public void storeLongPollTS(Long timeSync) {
        storePref(PREF_LONG_POLL_TS, timeSync);

    }

    public void storeLongPollKey(String k) {
        storePref(PREF_LONG_POLL_KEY, k);
    }

    public void storeUserID(Long user_id) {
        storePref(PREF_USER_ID, user_id);
    }

    public void signOut() {
        setAccessToken(null);
    }

    public long getLastSyncTime() {
        return mPreferences.getLong(PREF_LAST_SYNC, NOT_SYNCED);

    }

    public void storeLastSyncTime(Long l) {
        storePref(PREF_LAST_SYNC, l);
    }



    public void storeConversationsCount(Integer count) {
        storePref(PREF_CONVERSATION_COUNT, count);
    }
    public Integer getConversationCount(){
        int ret =  mPreferences.getInt(PREF_CONVERSATION_COUNT, -1);
        if (ret == -1){
            return  null;
        } else {
            return ret;
        }
    }

    public String getSignUpSid() {
        return mPreferences.getString(PREF_SIGN_UP_SID, null);

    }

    public void storeSignUpSid(String sid) {
        storePref(PREF_SIGN_UP_SID, sid);

    }

    public boolean notificationsEnabled() {
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_do_notify), true);
    }

    public boolean vibrationEnabled() {
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_do_vibrate), true);
    }

    public boolean soundEnabled() {
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_do_sound), true);
    }

    public String getNotificationRingtone(){
        return mPreferences.getString(mAppContext.getString(R.string.pref_ringtone), null);
    }

    /**
     *
     * @return находится ли пользователь в режиме невидимки
     */
    public boolean isInvisible(){
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_invis), false);
    }

    public boolean isShouldShowSmallPictures() {
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_small_avatars), false);
    }

    public boolean shouldTryToAddToGroup(){
        return mPreferences.getBoolean(mAppContext.getString(R.string.pref_should_try_to_add_to_group), true);
    }
    public boolean addedToGroup(){
        return mPreferences.edit().putBoolean(mAppContext.getString(R.string.pref_should_try_to_add_to_group), true).commit();
    }

    public void registerGCMToken(String gcmToken){
        mPreferences.edit().putString("gcm_token", gcmToken).commit();
    }


    public String getGCMToken(){
        return mPreferences.getString("gcm_token", null);
    }

    public boolean showRateDialog() {
        String key = "rate_app" ;
        int count = mPreferences.getInt(key, 0);
        mPreferences.edit().putInt(key, count + 1).commit();
        return count == 5;
    }

    public boolean shouldCompress() {
        return mPreferences.getBoolean(mAppContext.getResources().getString(R.string.do_resize_image_upload), false);
    }
}