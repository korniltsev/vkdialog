package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;

/**
 * User: anatoly
 * Date: 05.08.12
 * Time: 20:15
 */
public class BaseEvent {
    public Bundle resultData;

    public BaseEvent(Bundle resultData) {
        this.resultData = resultData;
    }

    public Bundle getResultData() {
        return resultData;
    }
}
