package ru.kurganec.vk.messenger.newui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.db.Message;
import ru.kurganec.vk.messenger.model.db.Profile;
import ru.kurganec.vk.messenger.utils.Joiner;
import ru.kurganec.vk.messenger.utils.TimeUtil;
import ru.kurganec.vk.messenger.utils.emoji.Emoji2;

import java.util.Arrays;
import java.util.HashMap;

//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 18:33
 */
public class ConversationsAdapter extends SmoothAdapter {

    private final int mRadius;
    private final int mMobileWidth;
    private final int mMobileHeight;
    private final boolean mShowSmallPhotos;
    private final int mAvatarSize;

    private Context mContext;



    private Drawable mGeoDrawable;
    private Drawable mAudioDrawable;
    private Drawable mPhotoDrawable;
    private Drawable mVideoDrawable;
    private Drawable mDocDrawable;

    private Drawable mAttachDrawable;
    private HashMap<Long, JSONArray> mAttachmentsCache = new HashMap<>();

    private Resources mResources;


    public ConversationsAdapter(Context context, Cursor c, ListView mList) {
        super(context, c, mList);
        mResources = context.getResources();

        mAttachDrawable = mResources.getDrawable(R.drawable.ic_content_attachment);
        mGeoDrawable = mAttachDrawable;
        mAudioDrawable = mAttachDrawable;
        mVideoDrawable = mAttachDrawable;
        mDocDrawable = mAttachDrawable;
        mPhotoDrawable = mAttachDrawable;
        mContext = context;

        mRadius = mResources.getDimensionPixelSize(R.dimen.conversation_online_circle);
        mMobileWidth = mResources.getDimensionPixelSize(R.dimen.conversation_mobile_width);
        mMobileHeight = mResources.getDimensionPixelSize(R.dimen.conversation_mobile_height);
        mShowSmallPhotos = VK.model().isShouldShowSmallPictures();
        mAvatarSize = context.getResources().getDimensionPixelSize(R.dimen.default_avatar_size);
    }

    private Drawable createMobileOnlineIcon(Resources res) {
        Drawable drawable = res.getDrawable(R.drawable.ic_mobile_online_light_selector);
        drawable.setBounds(0, 0, mMobileWidth, mMobileHeight);
        return drawable;
    }

    private Drawable createOnlineIcon(Resources res) {
        Drawable drawable = res.getDrawable(R.drawable.circle_online_light_selector);
        drawable.setBounds(0, 0, mRadius, mRadius);
        return drawable;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_conversation, viewGroup, false);
        v.setTag(new ConversationHolder(v));
        return v;
    }

    public class ConversationHolder {
        public  final ImageView avatar;
        public  final TextView date;
        public  final TextView name;
        public  final TextView body;
        public  final View online;
        public  final View icReply;

        public  Long chat_id;
        public  Long uid;
        public  long mid;



        ConversationHolder(View root) {
            avatar = (ImageView) root.findViewById(R.id.img_avatar);
            date = (TextView) root.findViewById(R.id.label_time);
            name = (TextView) root.findViewById(R.id.label_name);
            body = (TextView) root.findViewById(R.id.label_body);
            online = root.findViewById(R.id.ic_online);
            icReply = root.findViewById(R.id.ic_reply);

        }
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ConversationHolder holder = (ConversationHolder) view.getTag();
        long mid = cursor.getLong(cursor.getColumnIndex(Message.MID));
        String photo;
        if (mShowSmallPhotos) {
            photo = cursor.getString(cursor.getColumnIndex(Profile.PHOTO));
        } else {
            photo = cursor.getString(cursor.getColumnIndex(Profile.PHOTO_BIG));
        }
        String firstName = cursor.getString(cursor.getColumnIndex(Profile.FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(Profile.LAST_NAME));
        String body = cursor.getString(cursor.getColumnIndex(Message.BODY));
        long date = cursor.getLong(cursor.getColumnIndex(Message.DATE));
        holder.uid = cursor.getLong(cursor.getColumnIndex(Profile.UID));
        holder.mid = mid;
        int readState = cursor.getInt(cursor.getColumnIndex(Message.READ_STATE));
        int out = cursor.getInt(cursor.getColumnIndex(Message.OUT));
        int online = cursor.getInt(cursor.getColumnIndex(Profile.ONLINE));
        int onlineMobile = cursor.getInt(cursor.getColumnIndex(Profile.ONLINE_MOBILE));
        String chatTitle = null;
        Long latitude = null;
        if (!cursor.isNull(cursor.getColumnIndex(Message.LATITUDE))) {
            latitude = cursor.getLong(cursor.getColumnIndex(Message.LATITUDE));
        }
        if (!cursor.isNull(cursor.getColumnIndex(Message.Chat.CHAT_ID))) {
            holder.chat_id = cursor.getLong(cursor.getColumnIndex(Message.Chat.CHAT_ID));
            chatTitle = cursor.getString(cursor.getColumnIndex(Message.Chat.TITLE));
        } else {
            holder.chat_id = null;
        }

        JSONArray attachments = null;
        HashMap<String, Integer> counter = new HashMap<String, Integer>();
        if (!cursor.isNull(cursor.getColumnIndex(Message.ATTACHMENTS))) {
            attachments = mAttachmentsCache.get(mid);
            try {
                if (attachments == null) {
                    attachments = new JSONArray(cursor.getString(cursor.getColumnIndex(Message.ATTACHMENTS)));
                    mAttachmentsCache.put(mid, attachments);
                }
                for (int i = 0; i < attachments.length(); ++i) {
                    JSONObject attach = attachments.getJSONObject(i);
                    Integer attachTypeCount = counter.get(attach.getString("type"));
                    if (attachTypeCount == null) {
                        attachTypeCount = 0;
                    }
                    counter.put(attach.getString("type"), ++attachTypeCount);
                }
            } catch (JSONException e) {
                attachments = null;
            }
        }


        if (holder.chat_id == null) {
            holder.name.setText(Joiner.on(" ").join(Arrays.asList(lastName, firstName)));
        } else {
            holder.name.setText(chatTitle);
        }

        Drawable icOnline = null;
        if (onlineMobile == 1) {
            icOnline = createMobileOnlineIcon(mResources);
        } else if (online == 1) {
            icOnline = createOnlineIcon(mResources);
        }

        holder.name.setCompoundDrawables(icOnline, null, null, null);


        Drawable attach = null;
        if (body == null || body.length() <= 0) {
            if (attachments != null) {
                Integer photoCount = counter.get("photo");
                Integer videoCount = counter.get("video");
                Integer audioCount = counter.get("audio");
                if (photoCount != null && photoCount > 0) {
                    body = context.getResources().getQuantityString(R.plurals.photos, photoCount);
                    attach = mPhotoDrawable;
                } else if (videoCount != null && videoCount > 0) {
                    body = context.getResources().getQuantityString(R.plurals.videos, videoCount);
                    attach = mVideoDrawable;
                } else if (audioCount != null && audioCount > 0) {
                    body = context.getResources().getQuantityString(R.plurals.audios, audioCount);
                    attach = mAudioDrawable;
                } else {
                    Log.e(TAG, "Unimplemented attachment");//TODO
                    body = context.getString(R.string.unimplemented_attachment);
                    attach = mAttachDrawable;
                }
            }
        }

        if (TextUtils.isEmpty(body)) {
            if (latitude != null) {
                body = context.getString(R.string.location);
                attach = mGeoDrawable;
            }
        }

        holder.body.setText(Emoji2.replaceEmoji(body));
        holder.date.setText(TimeUtil.getTimeLabel(date, true));
        if (attach != null) { //can be null if (the body is not empty
            attach.setBounds(0, 0, (int) holder.body.getTextSize() + 10, (int) holder.body.getTextSize() + 10);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.body.getLayoutParams();
        if (Message.STATE_OUT == out ){
            holder.icReply.setVisibility(View.VISIBLE);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.ic_reply);
        } else {
            holder.icReply.setVisibility(View.GONE);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.img_avatar);
        }
        holder.body.setLayoutParams(layoutParams);

        holder.body.setCompoundDrawables(attach == null ? null : attach.mutate().getConstantState().newDrawable(), null, null, null);

//        view.setTag(R.id.tag_uid, holder.uid);
        Picasso.with(VK.inst())
                .load(photo)
                .centerCrop()
                .resize(mAvatarSize,mAvatarSize)
                .placeholder(R.drawable.ic_profile_avatar_stub)
                .into(holder.avatar)


        ;
//        ImageLoader.getInstance().displayImage(photo, holder.avatar, listener);

//        Bitmap inMemoryCached = ImageLoader.getInstance().getMemoryCache().get(VKUtils.getAvatarCacheName(mContext, photo));
//        if (inMemoryCached != null) {
//            ImageLoader.getInstance().displayImage(photo, holder.avatar);
//        } else {
//            if (state != SCROLL_STATE_FLING) {
//            } else {
//                holder.avatar.setImageResource(R.drawable.ic_profile_avatar_stub);
//            }
//        }


        //message read state
        if (readState == Message.STATE_READ) view.setBackgroundResource(R.drawable.list_selector);
        else view.setBackgroundResource(R.drawable.list_selector_unread);

    }


}
