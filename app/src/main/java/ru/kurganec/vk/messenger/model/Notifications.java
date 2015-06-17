package ru.kurganec.vk.messenger.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.db.Message;
import ru.kurganec.vk.messenger.model.db.Profile;
import ru.kurganec.vk.messenger.newui.ChatActivity;

/**
 * User: anatoly
 * Date: 05.07.12
 * Time: 0:07
 */
public class Notifications {


    private static int NOTIFICATION_ID = 0;
    private final VK mAppContext;
    NotificationManager mManager;



    public Notifications(VK vk) {
        mAppContext = vk;
        mManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void removeAllNotifications() {
        mManager.cancelAll();
    }

    public void notifyNewMessage() {


        if (!VK.model().notificationsEnabled()) {
            return;
        }

        if (VK.actions().clientCount() > 0) {//DO not notify when the activity is shown
            return;
        }

        final String ringtone = VK.model().getNotificationRingtone();

        Cursor c = VK.db().msg().getUnread();
        c.moveToFirst();
        if (c.getCount() > 0) {
            final String firstName = c.getString(c.getColumnIndex(Profile.FIRST_NAME));
            final String lastName = c.getString(c.getColumnIndex(Profile.LAST_NAME));

            final String body = c.getString(c.getColumnIndex(Message.BODY));
            String photoUri = c.getString(c.getColumnIndex(Profile.PHOTO_BIG));
            Long uid = c.getLong(c.getColumnIndex(Profile.UID));
            Long chatId = null;
            if (!c.isNull(c.getColumnIndex(Message.CHAT_ID))) {
                chatId = c.getLong(c.getColumnIndex(Message.CHAT_ID));
            }
            final String ticker = lastName + " " + firstName + " : " + body;


            final Intent notificationIntent = new Intent(mAppContext, ChatActivity.class);
            if (chatId != null) {
                notificationIntent.putExtra(ChatActivity.EXTRA_CHAT_ID, chatId);
            } else {
                notificationIntent.putExtra(ChatActivity.EXTRA_UID, uid);
            }

            Picasso.with(VK.inst()).load(photoUri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                    showNotification(bitmap, ticker, body, notificationIntent, lastName, firstName, ringtone);
                }

                @Override
                public void onBitmapFailed(Drawable drawable) {
                    showNotification(null, ticker, body, notificationIntent, lastName, firstName, ringtone);
                }

                @Override
                public void onPrepareLoad(Drawable drawable) {

                }
            });



        }
//
    }

    private void showNotification(Bitmap loadedImage, String ticker, String body, Intent notificationIntent, String lastName, String firstName, String ringtone) {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(mAppContext)
                .setSmallIcon(R.drawable.ic_app_bw)
                .setAutoCancel(true)
                .setTicker(Html.fromHtml(ticker))
                .setContentText(Html.fromHtml(body))
                .setContentIntent(PendingIntent.getActivity(mAppContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(lastName + " " + firstName) ;
//                .setLargeIcon(loadedImage);
        if (loadedImage != null){
            nb.setLargeIcon(loadedImage);
        }
        int def = Notification.DEFAULT_LIGHTS ;


        if (VK.model().vibrationEnabled()){
            def |= Notification.DEFAULT_VIBRATE;
        }
        nb.setDefaults(def);
        if (ringtone != null) {
            nb.setSound(Uri.parse(ringtone));
        }

        Notification n = nb.build();
        mManager.notify(NOTIFICATION_ID, n);
    }


}
