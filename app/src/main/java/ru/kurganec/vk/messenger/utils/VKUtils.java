package ru.kurganec.vk.messenger.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.newui.adapters.HistoryAdapter;

/**
 * User: anatoly
 * Date: 07.04.13
 * Time: 15:00
 */
public class VKUtils {
    public static String getAvatarCacheName(Context c, String uri){
        int avaSize = c.getResources().getDimensionPixelSize(R.dimen.default_avatar_size);
        return String.format("%s_%dx%d", uri, avaSize, avaSize);
    }

    public static Bitmap cropChatBitmap(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;

    }


    public static void copyMessage(Context c, HistoryAdapter.MessageHolder tag) {
        if (Build.VERSION.SDK_INT >= 11){//honeycomb
            ClipboardManager clipboard = (ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", tag.messageBody);
            clipboard.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(tag.messageBody);
        }
    }
}
