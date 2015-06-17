package ru.kurganec.vk.messenger.api;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.utils.ApiExecutor;
import ru.kurganec.vk.messenger.utils.GetArguments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * User: anatoly
 * Date: 10.06.12
 * Time: 22:36
 */
public class VKApi {


    public static final int LONG_POLL_TIMEOUT = 20;



    public static class KEYS {
        public static final String GRANT_TYPE = "grant_type";
        public static final String PASSWORD = "password";
        public static final String CLIENT_ID = "client_id";
        public static final String CLIENT_SECRET = "client_secret";
        public static final String USERNAME = "username";

        public static final String ACCESS_TOKEN = "access_token";
        public static final String USER_ID = "user_id";
    }

    private static final String VK_APP_ID = "2983858";
    private static final String VK_APP_SECRET = "BcCd0UrnjsocGv7lcineuHtegno4";

    private static final String SIGN_IN_URI = "https://api.vk.com/oauth/token";
    public static final String BASE_METHOD = "https://api.vk.com";


    private static final String SIGN_UP_URI = "https://api.vk.com/method/auth.signup";
    private static final String SIGN_UP_CONFIRM_URI = "https://api.vk.com/method/auth.confirm";

    private static final String METHOD_DELETE_MESSAGES = "/method/messages.delete";
    private static final String METHOD_GET_LONG_POLL = "/method/messages.getLongPollServer";
    private static final String METHOD_GET_HISTORY = "/method/messages.getHistory";
    private static final String METHOD_SEND_MESSAGE = "/method/messages.send";
    private static final String METHOD_EXECUTE = "/method/execute";
    private static final String METHOD_GET_BY_PHONES = "/method/friends.getByPhones";
    private static final String METHOD_GET_SUGGESTION = "/method/friends.getSuggestions";
    private static final String METHOD_GET_PROFILE_UPLOAD_SERVER = "/method/photos.getProfileUploadServer";
    private static final String METHOD_SAVE_PROFILE_PHOTO = "/method/photos.saveProfilePhoto";
    private static final String METHOD_REGISTER_DEVICE = "/method/account.registerDevice";
    private static final String METHOD_UNREGISTER_DEVICE = "/method/account.unregisterDevice";
    private static final String USER_INFO = "uid,first_name,last_name,photo,photo_medium,photo_big,online,phone,can_write_private_message"; //TODO select photo!!!!
    private static final String METHOD_SEARCH_USERS = "/method/users.search";
    private static final String METHOD_GET_MESSAGE_PHOTO_UPLOAD_SERVER = "/method/photos.getMessagesUploadServer";
    private static final String METHOD_SAVE_MESSAGE_PHOTO = "/method/photos.saveMessagesPhoto";
    private static final String METHOD_GET_BY_ID = "/method/messages.getById";
    private static final String METHOD_I_AM_TYPING = "/method/messages.setActivity";
    private static final String METHOD_MARK_AS_READ = "/method/messages.markAsRead";
    private static final String METHOD_DELETE_DIALOG = "/method/messages.deleteDialog";
    private static final String METHOD_GET_VIDEO = "/method/video.get";
    private static final String METHOD_GET_CHAT = "/method/messages.getChat";
    private static final String METHOD_GET_PROFILES = "/method/getProfiles";
    private static final String METHOD_EDIT_CHAT = "/method/messages.editChat";
    private static final String METHOD_DELETE_CHAT_PARTICIPANT = "/method/messages.removeChatUser";
    private static final String METHOD_ADD_CHAT_PARTICIPANT = "/method/messages.addChatUser";
    private static final String METHOD_DELETE_FRIEND = "/method/friends.delete";
    private static final String METHOD_ADD_FRIEND = "/method/friends.add";
    private static final String METHOD_ADD_TO_VKDIALOG_GROUP = "/method/groups.join";
    private static final String EXECUTE_GET_FRIENDS =
            "var hints = API.friends.get({\"order\": \"hints\", \"fields\":\"" + USER_INFO + "\"});" +
                    "return {\"hints\":hints};";

    private static final String MAKE_ME_ONLINE_ACTION = "API.account.setOnline();";

    private static final String EXECUTE_MAIN =
//            "API.activity.online();" +
            "var dialogs = API.messages.getDialogs({\"count\":50, \"offset\":0});\n" +
                    "var users = dialogs@.uid;\n" +
                    "var profiles = API.getProfiles({\"uids\":users, \"fields\":\" " + USER_INFO + "\"});\n" +
                    "var friends = API.friends.get({\"order\":\"hints\", \"fields\":\"" + USER_INFO + "\"});\n" +
//                    "var req = API.friends.getRequests() ;" +
//                    "var reqProfiles = API.getProfiles({\"uids\":req, \"fields\":\"" + USER_INFO + "\"});" +
                    "var me = API.getProfiles({\"uids\":\"%s\", \"fields\":\"" + USER_INFO + "\"});\n" +
                    "return {\"dialogs\":dialogs, \"profiles\": profiles,\"friends\":friends,  \"me\":me};\n";

    private static final String EXECUTE_GET_DIALOGS =
            "var dialogs = API.messages.getDialogs({\"count\":%d, \"offset\":%d});" +
                    "var users = dialogs@.uid;" +
                    "var profiles = API.getProfiles({\"uids\":users, \"fields\":\" " + USER_INFO + "\"});" +
                    "return {\"dialogs\":dialogs, \"profiles\": profiles};";


    private static final String EXECUTE_INIT =
            "var client = API.getProfiles({\"uids\":\"%s\", \"fields\":\"" + USER_INFO + "\"});" +
                    "var dialogs = API.messages.getDialogs({\"count\":20, \"offset\":0});" +
                    "var friends = API.friends.get({\"order\":\"hints\", \"fields\":\"" + USER_INFO + "\"});" +
                    "var profiles = API.getProfiles({\"uids\":dialogs@.uid, \"fields\":\" " + USER_INFO + "\"});" +
                    "return {\"dialogs\":dialogs, \"profiles\": profiles, " +
                    "\"client\":client, \"friends\":friends, " +
                    " \"long-poll\":API.messages.getLongPollServer()};";

    private static final String EXECUTE_UPDATE_SEARCH =
            "var req = API.friends.getRequests() ;" +
                    "var sug = API.friends.getSuggestions({\"filter\":\"mutual\"});" +
                    "var reqProfiles = API.getProfiles({\"uids\":req, \"fields\":\"" + USER_INFO + "\"});" +
                    "var sugProfiles = API.getProfiles({\"uids\":sug@.uid ,\"fields\":\"" + USER_INFO + "\"});" +
                    "return { \"reqProfiles\":reqProfiles, \"sugProfiles\":sugProfiles};";

    private static final String EXECUTE_UPDATE_REQUESTS =
            "var req = API.friends.getRequests() ;" +
                    "var reqProfiles = API.getProfiles({\"uids\":req, \"fields\":\"" + USER_INFO + "\"});" +
                    "return { \"requests\":reqProfiles};";

    private static final String EXECUTE_GET_HISTORY =
//            "API.activity.online();" +
            "var hist = API.messages.getHistory({\"%s\":%d, \"count\":%d, \"offset\":%d});" +
                    "var profiles = API.getProfiles({\"uids\":hist@.from, \"fields\":\"" + USER_INFO + "\"});" +
                    "return { \"hist\":hist, \"profiles\":profiles};";

    private static final String EXECUTE_SEARCH_MESSAGE = "" +
            "var messages = API.messages.search({\"q\":\"%s\", \"count\":100});" +
            "var profiles = API.getProfiles({\"uids\":messages@.uid, \"fields\":\"" + USER_INFO + "\"});" +
            "return {\"messages\": messages, \"profiles\":profiles};";


    public static String enumerate(List<?> col) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Object obj : col) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }
            sb.append(obj.toString());

        }
        return sb.toString();
    }


    public static JSONObject oauthAuthorization(String login, String pass, String captchakey, Long captchaSid) {
        List<NameValuePair> args = new ArrayList<NameValuePair>(6);
        args.add(new BasicNameValuePair(KEYS.GRANT_TYPE, KEYS.PASSWORD));
        args.add(new BasicNameValuePair(KEYS.CLIENT_ID, VK_APP_ID));
        args.add(new BasicNameValuePair(KEYS.CLIENT_SECRET, VK_APP_SECRET));
        args.add(new BasicNameValuePair(KEYS.USERNAME, login));
        args.add(new BasicNameValuePair(KEYS.PASSWORD, pass));
        args.add(new BasicNameValuePair("scope", "notify,friends,photos,audio,video,docs,messages,groups"));

        if (captchaSid != null) {
            args.add(new BasicNameValuePair("captcha_sid", String.valueOf(captchaSid)));
            args.add(new BasicNameValuePair("captcha_key", String.valueOf(captchakey)));
        }

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(SIGN_IN_URI);
        try {
            method.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
            HttpResponse response = client.execute(method);
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject signUp(String login, String name, String lastName) {
        List<NameValuePair> args = new ArrayList<NameValuePair>(6);

        args.add(new BasicNameValuePair(KEYS.CLIENT_ID, VK_APP_ID));
        args.add(new BasicNameValuePair(KEYS.CLIENT_SECRET, VK_APP_SECRET));
        args.add(new BasicNameValuePair("phone", login));
        args.add(new BasicNameValuePair("first_name", name));
        args.add(new BasicNameValuePair("last_name", lastName));
        args.add(new BasicNameValuePair("test_mode", "1"));

        String sid = VK.model().getSignUpSid();
        if (sid != null) {
            args.add(new BasicNameValuePair("sid", sid));
        }


        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(SIGN_UP_URI);

        try {
            method.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
            HttpResponse response = client.execute(method);
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static JSONObject confirmSignUp(String phone, String code, String password) {
        List<NameValuePair> args = new ArrayList<NameValuePair>(6);

        args.add(new BasicNameValuePair(KEYS.CLIENT_ID, VK_APP_ID));
        args.add(new BasicNameValuePair(KEYS.CLIENT_SECRET, VK_APP_SECRET));
        args.add(new BasicNameValuePair("phone", phone));
        args.add(new BasicNameValuePair("code", code));
        args.add(new BasicNameValuePair("password", password));
        args.add(new BasicNameValuePair("test_mode", "1"));

        String sid = VK.model().getSignUpSid();
        if (sid != null) {
            args.add(new BasicNameValuePair("sid", sid));
        }


        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(SIGN_UP_CONFIRM_URI);

        try {
            method.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
            HttpResponse response = client.execute(method);
            String result = EntityUtils.toString(response.getEntity());
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public static JSONObject getHistory(Long uid, Long chatId, int count, int offset, boolean isInvisible) {
        String formatted;
        if (isInvisible) {
            formatted = EXECUTE_GET_HISTORY;
        } else {
            formatted = MAKE_ME_ONLINE_ACTION + EXECUTE_GET_HISTORY;
        }

        if (chatId != null) {
            formatted = String.format(formatted, "chat_id", chatId, count, offset);
        } else {
            formatted = String.format(formatted, "uid", uid, count, offset);
        }
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", formatted));

    }

    public static JSONObject getChatHistory(long chatId, int count, int offset) {
        GetArguments arguments = new GetArguments("chat_id", chatId);
        arguments.put("count", count);
        arguments.put("offset", offset);
        arguments.put("fields", "title,attachments,fwd_messages");
        return ApiExecutor.executeGetMethod(METHOD_GET_HISTORY, arguments);
    }


    /**
     * (параметр order="hints" в методе VKApi friends.get)
     *
     * @return массив друзей
     */
    public static JSONObject getFriends() {
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", EXECUTE_GET_FRIENDS));
    }


    public static JSONObject getLongPollServer() {
        return ApiExecutor.executeGetMethod(METHOD_GET_LONG_POLL);
    }

    public static JSONObject getLongPollUpdates(String URI, long ts, String key) {
        GetArguments args = new GetArguments("ts", Long.toString(ts));
        args.put("key", key);
        args.put("act", "a_check");
        args.put("wait", String.valueOf(LONG_POLL_TIMEOUT));
        args.put("mode", "2");
        return ApiExecutor.executeGetRequest(URI, args);
    }


    public static JSONObject getDialogs(int count, int offset) {
        String code = String.format(EXECUTE_GET_DIALOGS, count, offset);
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", code));
    }


    public static JSONObject syncContactBook(List<String> phoneNumbers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < phoneNumbers.size(); ++i) {
            sb.append(phoneNumbers.get(i));
            if (i != phoneNumbers.size() - 1) {
                sb.append(",");
            }
        }
        GetArguments args = new GetArguments("phones", sb.toString());
        args.put("fields", USER_INFO);
        return ApiExecutor.executeGetMethod(METHOD_GET_BY_PHONES, args);

    }

    public static JSONObject getSuggestions() {
        return ApiExecutor.executeGetMethod(METHOD_GET_SUGGESTION, new GetArguments("filter", "contacts"));
    }

    public static JSONObject init(long uid) {
        String formattedCode = String.format(EXECUTE_INIT, String.valueOf(uid));
        Log.d("VKLOL", "CODE: " + formattedCode);
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", formattedCode));
    }

    public static JSONObject getProfileUploadServer() {
        return ApiExecutor.executeGetMethod(METHOD_GET_PROFILE_UPLOAD_SERVER);
    }

//    public static JSONObject uploadProfilePhoto(String uri, String mFileName) {
//        return ApiExecutor.uploadFile(uri, new File(mFileName), "photo", );
//    }

    public static JSONObject saveProfilePhoto(String server, String photos, String hash) {
        GetArguments args = new GetArguments("server", server);
        args.put("photo", photos);
        args.put("hash", hash);
        return ApiExecutor.executeGetMethod(METHOD_SAVE_PROFILE_PHOTO, args);
    }

    public static JSONObject updateSearchList() {
        //TODO тысячи заявок,
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", EXECUTE_UPDATE_SEARCH));
    }

    public static JSONObject searchUsers(String q, int count, int offset) {
        GetArguments args = new GetArguments("q", q);
        args.put("count", count);
        args.put("offset", offset);
        args.put("fields", USER_INFO);
        return ApiExecutor.executeGetMethod(METHOD_SEARCH_USERS, args);
    }

    public static JSONObject registerDevice(String registrationId) {
        GetArguments args = new GetArguments("token", registrationId);
        return ApiExecutor.executeGetMethod(METHOD_REGISTER_DEVICE, args);
    }

    public static JSONObject getMessagePhotoUploadServer() {
        return ApiExecutor.executeGetMethod(METHOD_GET_MESSAGE_PHOTO_UPLOAD_SERVER, new GetArguments());
    }

    public static JSONObject uploadMessagePhoto(String uploadUri, String fileName, boolean doCompress) {
        return ApiExecutor.uploadFile(uploadUri, new File(fileName), "photo", doCompress);
    }

    public static JSONObject saveMessagePhoto(String server, String photo, String hash) {
        GetArguments args = new GetArguments("server", server);
        args.put("photo", photo);
        args.put("hash", hash);
        return ApiExecutor.executeGetMethod(METHOD_SAVE_MESSAGE_PHOTO, args);


    }

    public static final String EXECUTE_SEND_MESSAGE = "" +
            "var response = API.messages.send(%s);\n" +
            "return {\"response\":response};";

    public static JSONObject sendMessage(Long uid, Long chat_id, String msg, ArrayList<String> ids, String forward,
                                         Integer latitude, Integer longitude) {
        JSONObject o = new JSONObject();
        try {
            o.put("message", msg);
            if (uid != null) {
                o.put("uid", uid.longValue());
            } else {
                o.put("chat_id", chat_id.longValue());
            }

            if (ids != null) {
                o.put("attachment", enumerate(ids));
            }
            if (forward != null) {
                o.put("forward_messages", forward);
            }
            if (latitude != null) {
                o.put("lat", 0.000001 * latitude);
                o.put("long", 0.000001 * longitude);
            }


            o.put("guid", System.currentTimeMillis());

            String formatted = String.format(EXECUTE_SEND_MESSAGE, o.toString());

            return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", formatted));
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONObject retrieveMessage(long mid) {
        GetArguments args = new GetArguments("mid", mid);
        return ApiExecutor.executeGetMethod(METHOD_GET_BY_ID, args);
    }

    public static JSONObject iAmTyping(Long uid, Long chatId) {
        GetArguments args = new GetArguments("type", "typing");
        if (uid == null) {
            args.put("chat_id", chatId);
        } else {
            args.put("uid", uid);
        }
        return ApiExecutor.executeGetMethod(METHOD_I_AM_TYPING, args);
    }

    public static JSONObject markAsRead(String mids) {
        return ApiExecutor.executeGetMethod(METHOD_MARK_AS_READ, new GetArguments("mids", mids));
    }

    public static JSONObject updateRequests() {
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", EXECUTE_UPDATE_REQUESTS));
    }

    public static JSONObject getVideo(String videos) {
        return ApiExecutor.executeGetMethod(METHOD_GET_VIDEO, new GetArguments("videos", videos));
    }

    public static JSONObject unRegisterDevice(String registrationId) {
        return ApiExecutor.executeGetMethod(METHOD_UNREGISTER_DEVICE, new GetArguments("token", registrationId));
    }


    public static JSONObject deleteMessages(String mids) {
        return ApiExecutor.executeGetMethod(METHOD_DELETE_MESSAGES, new GetArguments("mids", mids));
    }

    public static JSONObject updateChat(long chat_id) {
        return ApiExecutor.executeGetMethod(METHOD_GET_CHAT, new GetArguments("chat_id", chat_id));
    }

    public static JSONObject getProfiles(List<Long> hasNoProfiles) {
        GetArguments args = new GetArguments("uids", enumerate(hasNoProfiles));
        args.put("fields", USER_INFO);
        return ApiExecutor.executeGetMethod(METHOD_GET_PROFILES, args);
    }


    public static JSONObject editChat(long chatId, String name) {
        GetArguments args = new GetArguments("chat_id", chatId);
        args.put("title", name);

        return ApiExecutor.executeGetMethod(METHOD_EDIT_CHAT, args);
    }

    public static JSONObject deleteChatParticipant(long chat_id, long uid) {
        GetArguments args = new GetArguments("chat_id", chat_id);
        args.put("uid", uid);

        return ApiExecutor.executeGetMethod(METHOD_DELETE_CHAT_PARTICIPANT, args);
    }

    public static JSONObject addFriend(long uid) {
        GetArguments args = new GetArguments("uid", uid);
        return ApiExecutor.executeGetMethod(METHOD_ADD_FRIEND, args);
    }

    public static JSONObject deleteFriend(long uid) {
        GetArguments args = new GetArguments("uid", uid);
        return ApiExecutor.executeGetMethod(METHOD_DELETE_FRIEND, args);
    }

    public static JSONObject addChatParticipant(long chat_id, long uid) {
        GetArguments args = new GetArguments("chat_id", chat_id);
        args.put("uid", uid);
        return ApiExecutor.executeGetMethod(METHOD_ADD_CHAT_PARTICIPANT, args);
    }

    public static JSONObject executeMain(boolean isInvisibleMode) {
        String code;
        if (isInvisibleMode) {
            code = EXECUTE_MAIN;
        } else {
            code = MAKE_ME_ONLINE_ACTION + EXECUTE_MAIN;
        }
        String formatted = String.format(code, VK.model().getUserID());
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", formatted));

    }

    public static JSONObject searchMessages(String q) {
        String formatted = String.format(EXECUTE_SEARCH_MESSAGE, q);
        return ApiExecutor.executeGetMethod(METHOD_EXECUTE, new GetArguments("code", formatted));

    }

    public static void deleteWholeConversation(Long uid, Long chatId) {
        if (chatId == null) {
            ApiExecutor.executeGetMethod(METHOD_DELETE_DIALOG, new GetArguments("uid", uid));
        } else {
            ApiExecutor.executeGetMethod(METHOD_DELETE_DIALOG, new GetArguments("chat_id", chatId));
        }

    }

    public static JSONObject addToVKDialogGroup() {
        return ApiExecutor.executeGetMethod(METHOD_ADD_TO_VKDIALOG_GROUP, new GetArguments("gid", 43184732));
    }

}
