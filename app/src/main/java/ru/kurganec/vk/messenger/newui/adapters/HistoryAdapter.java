package ru.kurganec.vk.messenger.newui.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.db.Message;
import ru.kurganec.vk.messenger.newui.LocationActivity;
import ru.kurganec.vk.messenger.newui.PhotoActivity;
import ru.kurganec.vk.messenger.newui.VideoActivity;
import ru.kurganec.vk.messenger.utils.GoogleMapHelper;
import ru.kurganec.vk.messenger.utils.TimeUtil;
import ru.kurganec.vk.messenger.utils.emoji.Emoji2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: anatoly
 * Date: 19.07.12
 * Time: 1:40
 */
public class HistoryAdapter extends SmoothAdapter {
    private final LayoutInflater mViewFactory;

    public class MessageHolder {
        //ui
        public View root;
        public TextView body;
        public LinearLayout attachments;
        public TextView time;

        //data
        public long mid;
        public long read_state;
        public String messageBody;

        public MessageHolder(View root) {
            this.root = root;

            body = (TextView) root.findViewById(R.id.label_body);
            attachments = (LinearLayout) root.findViewById(R.id.list_attach);
            time = (TextView) root.findViewById(R.id.label_time);


        }
    }


//    private  final int ATTACH_WIDTH ;
//    private final int ATTACH_HEIGHT ;

    private Context mContext;


    private Map<Long, JSONArray> attachCache = new HashMap<Long, JSONArray>();

    public HistoryAdapter(Context ctx, Cursor c) {
        super(ctx, c, null);
        mContext = ctx;
        mViewFactory = LayoutInflater.from(ctx);
//        ATTACH_WIDTH = ctx.getResources().getDimensionPixelSize(R.dimen.attach_width);
//        ATTACH_HEIGHT = ctx.getResources().getDimensionPixelSize(R.dimen.attach_heigh);
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor c = (Cursor) getItem(position);
        return c.getInt(c.getColumnIndex(Message.OUT));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        int type = cursor.getInt(cursor.getColumnIndex(Message.OUT));
        View v;
        if (type == Message.STATE_OUT) {
            v = LayoutInflater.from(context).inflate(R.layout.view_message_out, viewGroup, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.view_message_in, viewGroup, false);
        }
        v.setTag(new MessageHolder(v));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //minus photo, uid, out
        final MessageHolder holder = (MessageHolder) view.getTag();
        String body = cursor.getString(cursor.getColumnIndex(Message.BODY));
        final long mid = cursor.getLong(cursor.getColumnIndex(Message.MID));
        int read_state = cursor.getInt(cursor.getColumnIndex(Message.READ_STATE));
        long time = cursor.getLong(cursor.getColumnIndex(Message.DATE));
        int out = cursor.getInt(cursor.getColumnIndex(Message.OUT));


        holder.messageBody = body;
        JSONArray attach = attachCache.get(mid);
        if (!cursor.isNull(cursor.getColumnIndex(Message.ATTACHMENTS)) && attach == null) {
            try {
                attach = new JSONArray(cursor.getString(cursor.getColumnIndex(Message.ATTACHMENTS)));
                attachCache.put(mid, attach);
            } catch (JSONException ignore) {
            }
        }
        Integer lat = null;
        Integer lon = null;
        if (!cursor.isNull(cursor.getColumnIndex(Message.LATITUDE))) {
            lat = cursor.getInt(cursor.getColumnIndex(Message.LATITUDE));
            lon = cursor.getInt(cursor.getColumnIndex(Message.LONGITUDE));
        }

        if (body == null || body.length() <= 0) {
            holder.body.setVisibility(View.GONE);
        } else {
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(Emoji2.replaceEmoji(body));//, (int) holder.body.getTextSize() + 10));//Html.fromHtml(body));
        }

        holder.mid = mid;
        holder.read_state = read_state;

        holder.time.setText(TimeUtil.getTimeLabel(time, true));

        if (read_state == Message.STATE_READ) {
            holder.root.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_unread);
        }
        if (attach != null || lat != null) //we have attachments
        {
            holder.attachments.setVisibility(View.VISIBLE);
            holder.attachments.removeAllViews();

            if (lat != null) {//geo attached
                View attachView = createAttachView(holder.attachments);
                ImageView map = (ImageView) attachView.findViewById(R.id.img);
                final Integer finalLon = lon;
                final Integer finalLat = lat;

                attachView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, LocationActivity.class);
                        i.setAction(Intent.ACTION_VIEW);
                        i.putExtra(LocationActivity.EXTRA_LATITUDE, finalLat);
                        i.putExtra(LocationActivity.EXTRA_LONGITUDE, finalLon);
                        mContext.startActivity(i);
                    }
                });
                //download image
                ViewGroup.LayoutParams lp = attachView.getLayoutParams();
                String uri = GoogleMapHelper.getUri(lat, lon, lp.width, lp.height);
                Picasso.with(mContext).load(uri).into(map);
                //                pushView(holder.attachments, map);
            }
            try {
                if (attach != null) {
                    int photoCount = 0;
                    for (int i = 0; i < attach.length(); ++i) {
                        JSONObject obj = attach.getJSONObject(i);
                        String type = obj.getString("type");
                        if (type.equals("photo")) {
                            JSONObject attachPhoto = obj.getJSONObject("photo");
                            final String photoUrl = attachPhoto.getString("src_big");
                            View attachView = createAttachView(holder.attachments);
                            final ImageView pic = (ImageView) attachView.findViewById(R.id.img);
                            final int currentPosition = photoCount++;
                            attachView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(mContext, PhotoActivity.class);
                                    i.putExtra(PhotoActivity.EXTRA_MID, mid);
                                    i.putExtra(PhotoActivity.EXTRA_PHOTO_POSITION, currentPosition);
                                    mContext.startActivity(i);
                                }
                            });
                            //download image
                            Picasso.with(mContext).load(photoUrl).into(pic);
                            //                            pushView(holder.attachments, pic);
                        } else if (type.equals("video")) {
                            final JSONObject videoJson = obj.getJSONObject("video");
                            View attachView = createAttachView(holder.attachments);
                            final ImageView video = (ImageView) attachView.findViewById(R.id.img);
                            final String img = videoJson.getString("image");
//                            attachView.setTag(videoJson);
                            Picasso.with(mContext).load(img).into(video);
                            attachView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO uncomment
                                    Intent i = new Intent(mContext, VideoActivity.class);
                                    i.putExtra(VideoActivity.EXTRA_VIDEO_JSON, videoJson.toString());
                                    mContext.startActivity(i);
                                }
                            });
//                            pushView(holder.attachments, video);
                        }
                    }
                }
            } catch (JSONException e) {//if something goes wrong we don't show anything
                holder.attachments.setVisibility(View.GONE);
            }
        } else {
            holder.attachments.setVisibility(View.GONE);
        }

    }



    private View createAttachView(LinearLayout attachments) {
        View res = mViewFactory.inflate(R.layout.view_photo_attach, attachments, false);
        attachments.addView(res);
        return res;
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int newState) {
        super.onScrollStateChanged(absListView, newState);
        if (newState == SCROLL_STATE_IDLE) {
            List<Long> unreadMids = new ArrayList<Long>();
            for (int i = 0; i < absListView.getChildCount(); ++i) {
                Object holder = absListView.getChildAt(i).getTag();
                if (holder != null && holder instanceof MessageHolder) {
                    MessageHolder h = (MessageHolder) holder;
                    if (h.read_state == Message.STATE_UNREAD) {
                        unreadMids.add(h.mid);
                    }
                }
            }
            if (unreadMids.size() > 0) {
                VK.actions().markAsRead(unreadMids);
            }

        }
    }


}
