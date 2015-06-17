package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;

/**
 * User: anatoly
 * Date: 05.08.12
 * Time: 20:15
 */
public class SearchMessageEvent extends BaseEvent {
    public static enum Action {
        SEARCH_FINISHED, SEARCH_BOX_CLEARED
    }
    private Action mAction;
    public SearchMessageEvent(Bundle resultData, Action a) {
        super(resultData);
        mAction = a;
    }

    public Action getAction() {
        return mAction;
    }
}
