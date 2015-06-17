package ru.kurganec.vk.messenger.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * User: anatoly
 * Date: 05.07.12
 * Time: 1:21
 */
public class TimeUtil {
    public static SimpleDateFormat sDayFormat = new SimpleDateFormat("dd MMM");
    public static SimpleDateFormat sTimeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat sLongFormat = new SimpleDateFormat("dd MMM HH:mm");


    private static Calendar currentTime = Calendar.getInstance();
    private static Calendar labelTime = Calendar.getInstance();

    public static void refresh(){
        currentTime = Calendar.getInstance();
    }

    /**
     * @param date unix time in seconds
     * @return
     */
    public static CharSequence getTimeLabel(long date, boolean shortFormat) {
        Calendar c = labelTime;
        c.setTimeInMillis(date * 1000);
        Calendar current = currentTime;
        rewindTo12(c);
        rewindTo12(current);
        SimpleDateFormat f;
        if (c.equals(current)){
            f = sTimeFormat;
        } else {
            f = sDayFormat;
        }
        if (!shortFormat){
            f = sLongFormat;
        }
        c.setTimeInMillis(date * 1000);
        return f.format(c.getTime());
    }



    private static void rewindTo12(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}
