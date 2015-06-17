package ru.kurganec.vk.messenger.utils;

import android.os.Bundle;
import ru.kurganec.vk.messenger.model.actions.Actions;


/**
 * User: anatoly
 * Date: 14.06.12
 * Time: 18:09
 * An empty observer, to simplify creation more specific ones
 */
public class BaseActionsObserver implements Actions.Observer {
    @Override
    public void actionStarted(Bundle data) {


    }

    @Override
    public void actionSopped(Bundle data) {

    }

    @Override
    public void loggedIn() {

    }


    @Override
    public void loginFailed() {

    }

    @Override
    public boolean loggedOut() {
        return false;
    }

    @Override
    public void captchaRequired(String captcha_img, long captcha_sid, Bundle resultData) {

    }

    @Override
    public void gotHistory(Bundle data) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void friendListUpdated() {

    }

    @Override
    public void historyUpdated(long profileUID) {

    }

    @Override
    public void mainActionPerformed() {

    }

    @Override
    public void messageChanged(Bundle uid) {

    }

    @Override
    public void userIsTyping(long uid) {

    }

    @Override
    public void chatHistoryUpdated(long mChatId) {

    }



    @Override
    public void searchListUpdated() {

    }

    @Override
    public void searchResult(long taskId) {

    }

    @Override
    public void gotVideo(String video) {

    }

    @Override
    public void videoError() {

    }

    @Override
    public void chatUpdated(long chat_id) {

    }

    @Override
    public void gotDialogs() {

    }



    @Override
    public void signUpFail() {

    }

    @Override
    public void signUpOk() {

    }

    @Override
    public void signUpConfirmFail() {

    }

    @Override
    public void signUpConfirmed() {

    }

    @Override
    public void gotChatHistory(long chat_id, int all_count) {


    }

    @Override
    public void imageUploaded(Bundle resultData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void messageDelivered(Bundle resultData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void startedUploadingPhoto(Bundle resultData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void messageWasNotSent(Bundle resultData) {

    }


}
