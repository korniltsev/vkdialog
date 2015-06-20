package ru.kurganec.vk.messenger.newui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.AuthEvent;
import ru.kurganec.vk.messenger.utils.BaseActionsObserver;

// import com.flurry.android.FlurryAgent;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 12:17
 */
public class SignInActivity extends SherlockFragmentActivity implements View.OnClickListener {
    private SignInObserver mObserver = new SignInObserver();
    private EditText mLoginInput,
            mPassInput;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mLoginInput = (EditText) findViewById(R.id.input_login);
        mPassInput = (EditText) findViewById(R.id.input_pass);

        Button btn = (Button) findViewById(R.id.btn_sign_in);
        btn.setOnClickListener(this);

        MainActivity.showTelegramPromo(this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // FlurryAgent.onStartSession(this, "9R8JCSZXCWBTPGWR52MY");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VK.model().isAppSignedIn()) {
            startMainActivity();
        } else {
            VK.actions().registerObserver(mObserver);
        }

        VK.bus().register(mObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.actions().unRegisterObserver(mObserver);
        VK.bus().unregister(mObserver);
    }

    protected String getCustomTitle() {
        return null;
    }

    private void startMainActivity() {
        Intent main = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    @Override
    public void onClick(View view) {
        VK.actions().signIn(mLoginInput.getText().toString(),
                mPassInput.getText().toString());
        mProgress = ProgressDialog.show(this, "VK", " please wait");
    }

    private class SignInObserver extends BaseActionsObserver {

        @Subscribe
        public void onAuth(AuthEvent e){
            if (mProgress != null) {
                mProgress.dismiss();
            }
            switch (e.getAction() ){
                case AUTH_FAILED:{
                    Toast.makeText(SignInActivity.this, getString(R.string.password_incorrect), Toast.LENGTH_LONG)
                            .show();
                    break;
                }
                case SIGNED_IN:{
                    setupFlurry();
                    startMainActivity();
                    break;
                }
            }

        }




        AlertDialog mCaptchaDialog;
        EditText mCaptchaInput;

        @Override
        public void captchaRequired(String captchaUri, final long captcha_sid, Bundle resultData) {
            AlertDialog.Builder builder;

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.window_kaptcha, null);
            ImageView imgCaptcha = (ImageView) layout.findViewById(R.id.img_kaptcha);
            Picasso.with(VK.inst())
                    .load(captchaUri)
                    .into(imgCaptcha);
//            ImageLoader.getInstance().displayImage(captchaUri, imgCaptcha);
            mCaptchaInput = (EditText) layout.findViewById(R.id.input_kaptcha);
            Button btnSendCaptcha = (Button) layout.findViewById(R.id.btn_send_kaptcha);
            btnSendCaptcha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VK.actions().signIn(getLogin(), getPass(), mCaptchaInput.getText().toString(),
                            captcha_sid);
                    mCaptchaDialog.dismiss();
                }
            });
            builder = new AlertDialog.Builder(SignInActivity.this);
            builder.setView(layout);
            builder.setCancelable(false);
            mCaptchaDialog = builder.create();
            mCaptchaDialog.show();
        }

    }

    private void setupFlurry() {
//        Cursor c = VK.db().profiles().get(VK.model().getUserID());
//        c.moveToFirst();
//        long uid = c.getLong(c.getColumnIndex(Profile.UID));
        // FlurryAgent.setUserId(String.valueOf(uid));
//        c.close();
    }

    private String getPass() {
        return mPassInput.getText().toString();
    }

    private String getLogin() {
        return mLoginInput.getText().toString();
    }
}
