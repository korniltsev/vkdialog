package ru.kurganec.vk.messenger.utils;


import android.app.Activity;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.squareup.otto.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.AuthEvent;

// import com.crittercism.app.Crittercism;
// import com.flurry.android.FlurryAgent;

/**
 * User: anatoly
 * Date: 28.07.12
 * Time: 1:39
 */
public abstract class VKActivity extends SherlockFragmentActivity {

    private SignOutObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mObserver = new SignOutObserver(this);
        VK.notifications().removeAllNotifications();
        // Crittercism.init(getApplicationContext(), "504b6a0f067e7c1598000003");


        if (VK.model().isAppSignedIn()) {
            JSONObject metadata = new JSONObject();
            try {
                metadata.put("user_id", VK.model().getUserID());
                // Crittercism.setMetadata(metadata);
            } catch (JSONException ignored) { }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // FlurryAgent.onStartSession(this, "9R8JCSZXCWBTPGWR52MY");
        getSupportActionBar().setTitle(getCustomTitle());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // FlurryAgent.onEndSession(this);
    }

    protected String getCustomTitle() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VK.bus().register(mObserver);
        if (!VK.model().isAppSignedIn()) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.bus().unregister(mObserver);
    }

    public static class SignOutObserver {
        final Activity a;

        public SignOutObserver(Activity a) {
            this.a = a;
        }

        @Subscribe
        public void loggedOut(AuthEvent e) {
            if (e.getAction().equals(AuthEvent.Action.SIGNED_OUT)) {
                a.finish();
            }
        }


    }




//    View menu;
//    FlyInMenuLayout container;
//
//    public final void setMenuView(int resId){
//        menu = getLayoutInflater().inflate(resId, null);
//    }
//
//
//    public final void setUpSliding(int containerId){
//        setUpSliding((FlyInMenuLayout) getLayoutInflater().inflate(containerId, null));
//    }
//
//    public final void setUpSliding(FlyInMenuLayout l){
//        l.setAlignMenuRight(true);
//        l.setMenuMargin(getResources().getDimensionPixelSize(R.dimen.menu_margin));
////        l.setMenuMode(FlyInMenuLayout.MenuMode.PERSPECTIVE);
//        container = l;
//        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
//        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
//        decor.removeView(decorChild);
//
//        container.addView(menu);
//        container.addView(decorChild);
//        menu.setTag("menu");
//        decorChild.setTag("host");
//
//        decor.addView(container, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//        ));
//
//        TypedArray a = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowBackground});
//        int background = a.getResourceId(0, 0);
//        decorChild.setBackgroundResource(background);
//        a.recycle();
//        getWindow().setBackgroundDrawable(null);
//
//
//
//    }
//
//
//    final public void setMenuOpened() {
//        container.setOpenedOnStart();
//    }
//
//    public FlyInMenuLayout getContainer() {
//        return container;
//    }

}
