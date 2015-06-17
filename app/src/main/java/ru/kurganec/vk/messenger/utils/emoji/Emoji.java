package ru.kurganec.vk.messenger.utils.emoji;

import android.content.Context;
import android.view.View;
import ru.kurganec.vk.messenger.R;

// import com.flurry.android.FlurryAgent;


/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 22.09.12
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class Emoji {



    public static Popup buildPopup(Context c) {
        View root = View.inflate(c, R.layout.view_emoji_popup, null);
        return new Popup(root);
    }
}
