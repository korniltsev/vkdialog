package ru.kurganec.vk.messenger.utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 20:55
 */
public class GetArguments {

    private HashMap<String, String> args;

    public GetArguments() {
        this.args = new HashMap<String, String>();
    }

    public GetArguments(String key, Object value) {
        this();
        put(key, value);
    }

    public void put(String key, String value) {
        args.put(key, value);
    }

    public void put(String key, long value) {
        args.put(key, Long.toString(value));
    }

    public void put(String key, int value) {
        args.put(key, Integer.toString(value));
    }

    public void put(String key, Object value) {
        args.put(key, value.toString());
    }

    public int size() {
        return args.size();
    }

//    private List<NameValuePair>  list;
    public String getRequest() {
//        list = new ArrayList<NameValuePair>();
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (String key : args.keySet()) {
            String value = args.get(key);
            if (value == null){
                continue;
            }
            if (!first) {
                res.append('&');
            } else {
                first = false;
            }


//            if (encode){
//                try {
//                    res.append(key).append('=').append(URLEncoder.encode(args.get(key), "utf-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            } else {
                res.append(key).append('=').append(args.get(key));
//            list.add(new BasicNameValuePair(key, args.get(key)));
//            }


        }
        return res.toString();
    }

    public List<NameValuePair> get() {
        List<NameValuePair> res = new ArrayList<NameValuePair>();
        for (String key : args.keySet()) {
            res.add(new BasicNameValuePair(key, args.get(key)));
        }
        return res;
    }
}
