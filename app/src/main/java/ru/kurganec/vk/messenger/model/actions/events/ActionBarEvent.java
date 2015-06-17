package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 23.09.12
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public class ActionBarEvent extends BaseEvent {
    public static enum Action {
        SHOW, HIDE
    }
    private Action action;

    public ActionBarEvent(Bundle resultData, Action action) {
        super(resultData);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}
