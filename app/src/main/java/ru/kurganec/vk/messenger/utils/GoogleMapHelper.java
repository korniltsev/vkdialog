package ru.kurganec.vk.messenger.utils;

import android.util.Log;

/**
 * static class. Helps to create url for static google maps picture
 * User: anatoly
 * Date: 21.08.12
 * Time: 19:22
 */
public class GoogleMapHelper {
    private static final String GOOGLE_MAP_URI = "http://maps.google.com/maps/api/staticmap?";

    public static String getUri(int latitude, int longitude, int h, int w) {
        float lat = (float) (0.000001 * latitude);
        float lon = (float) (0.000001 * longitude);
        return getUri(lat, lon, w, h);
    }

    public static String getUri(float latitude, float longitude,  int w, int h) {
        final  String size = String.format("%dx%d",
                w,
                h);
        String str =
                GOOGLE_MAP_URI + "center=" + latitude + "," + longitude + "&zoom=14&sensor=false&size=" +
                        size + "&markers=color:black%7C" + latitude + "," + longitude;

        Log.d("MAP", str);
        return str;
    }

}
