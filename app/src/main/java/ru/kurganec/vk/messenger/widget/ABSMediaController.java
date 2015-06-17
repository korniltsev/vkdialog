package ru.kurganec.vk.messenger.widget;

import android.widget.MediaController;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 23.09.12
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
public class ABSMediaController extends MediaController{
    private SherlockFragmentActivity mActivity;

    public ABSMediaController(SherlockFragmentActivity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    public void show() {
        super.show();
        mActivity.getSupportActionBar().show();
//        VK.bus().post(new ActionBarEvent(null, ActionBarEvent.Action.SHOW));
    }

    @Override
    public void hide() {
        super.hide();
        mActivity.getSupportActionBar().hide();
//        VK.bus().post(new ActionBarEvent(null, ActionBarEvent.Action.HIDE));
    }
}
