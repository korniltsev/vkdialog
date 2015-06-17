package ru.kurganec.vk.messenger.newui.fragments;

import android.database.Cursor;
import ru.kurganec.vk.messenger.model.VK;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 23:56
 */
public class OnlineFriendPickerFragment extends FriendPickerFragment {
    @Override
    protected Cursor getData() {
        return VK.db().profiles().queryOnlineFriends();
    }

    @Override
    protected Cursor getData(String query) {
        return VK.db().profiles().queryOnlineFriends(query);
    }
}
