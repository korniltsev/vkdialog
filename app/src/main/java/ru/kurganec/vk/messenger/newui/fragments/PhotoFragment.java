package ru.kurganec.vk.messenger.newui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import uk.co.senab.photoview.PhotoView;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 07.11.12
 * Time: 15:43
 * To change this template use File | Settings | File Templates.
 */
public class PhotoFragment extends Fragment {
    public static final String ARG_IMAGE_URL = "ru.kurganec.vk.messenger.newui.fragments.PhotoFragment.ImageUrl";
    private String mURI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mURI = getArguments().getString(ARG_IMAGE_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View ret = inflater.inflate(R.layout.fragment_photo, container, false);
        final PhotoView p = (PhotoView) ret.findViewById(R.id.img_photo);
        //todo load image twice largs
        Picasso.with(VK.inst())
                .load(mURI)
//                .centerCrop()
//                .resize(mAvatarSize,mAvatarSize)
//                .placeholder(R.drawable.ic_profile_avatar_stub)
                .into(p);
//        ImageLoader.getInstance().displayImage(mURI, p );
        return ret;
    }
}
