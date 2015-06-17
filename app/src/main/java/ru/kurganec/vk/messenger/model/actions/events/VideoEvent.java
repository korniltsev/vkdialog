package ru.kurganec.vk.messenger.model.actions.events;

import android.os.Bundle;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 07.09.12
 * Time: 19:56
 * To change this template use File | Settings | File Templates.
 */
public class VideoEvent extends BaseEvent {
    public static enum Action {
        GOT_VIDEO, ERROR
    }
    private Action mAction;

    public VideoEvent(Bundle resultData, Action mAction) {
        super(resultData);
        this.mAction = mAction;
    }

    public Action getAction() {
        return mAction;
    }


}
