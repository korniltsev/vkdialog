package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;

/**
 * User: anatoly
 * Date: 22.08.12
 * Time: 15:31
 */
public class AuthEvent extends BaseEvent {
    public static enum Action{
        SIGNED_IN, SIGNED_OUT, AUTH_FAILED
    }

    private  Action a;

    public AuthEvent(Bundle resultData, Action a) {
        super(resultData);
        this.a = a;
    }

    public Action getAction() {

        return a;
    }


}
