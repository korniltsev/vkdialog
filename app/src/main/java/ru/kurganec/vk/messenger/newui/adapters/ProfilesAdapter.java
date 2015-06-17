package ru.kurganec.vk.messenger.newui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.db.Profile;
import ru.kurganec.vk.messenger.utils.Joiner;

import java.util.Arrays;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 23:19
 */
public class ProfilesAdapter extends SmoothAdapter {
    private final boolean mShowSmallPictures;
    private final int mAvatarSize;
    LayoutInflater mFactory;



    public ProfilesAdapter(Context context, Cursor c) {
        super(context, c, null);
        mFactory = LayoutInflater.from(context);
        mShowSmallPictures = VK.model().isShouldShowSmallPictures();
        mAvatarSize = context.getResources().getDimensionPixelSize(R.dimen.default_avatar_size);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_profile, viewGroup, false);
        v.setTag(new ProfileHolder(v));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ProfileHolder holder = (ProfileHolder) view.getTag();
        String photo ;
        if (mShowSmallPictures){
            photo= cursor.getString(cursor.getColumnIndex(Profile.PHOTO));
        } else {
            photo= cursor.getString(cursor.getColumnIndex(Profile.PHOTO_BIG));
        }
        String firstName = cursor.getString(cursor.getColumnIndex(Profile.FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndex(Profile.LAST_NAME));
        holder.uid = cursor.getLong(cursor.getColumnIndex(Profile.UID));
        int online = cursor.getInt(cursor.getColumnIndex(Profile.ONLINE));
        int mobileOnline = cursor.getInt(cursor.getColumnIndex(Profile.ONLINE_MOBILE));
        holder.name.setText(Joiner.on(" ").join(Arrays.asList(firstName, lastName)));

        if (mobileOnline == 1){
            holder.mobileOnline.setVisibility(View.VISIBLE);
            holder.online.setVisibility(View.INVISIBLE);
        } else {
            holder.online.setVisibility(online == 1 ? View.VISIBLE : View.INVISIBLE);
            holder.mobileOnline.setVisibility(View.INVISIBLE);
        }

        Picasso.with(VK.inst())
                .load(photo)
                .centerCrop()
                .resize(mAvatarSize,mAvatarSize)
                .placeholder(R.drawable.ic_profile_avatar_stub)
                .into(holder.avatar);

    }

    public class ProfileHolder {
        public ImageView avatar;
        public TextView name;
        public ImageView online;
        public Long uid;
        public ImageView mobileOnline;

        ProfileHolder(View root) {
            avatar = (ImageView) root.findViewById(R.id.img_avatar);
            online = (ImageView) root.findViewById(R.id.ic_online);
            mobileOnline = (ImageView) root.findViewById(R.id.ic_online_mobile);
            name = (TextView) root.findViewById(R.id.label_name);

        }
    }
}
