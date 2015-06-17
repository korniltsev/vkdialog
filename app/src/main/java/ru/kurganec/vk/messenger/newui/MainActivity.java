package ru.kurganec.vk.messenger.newui;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.newui.fragments.RatingDialog;
import ru.kurganec.vk.messenger.utils.TimeUtil;
import ru.kurganec.vk.messenger.utils.VKActivity;
import ru.kurganec.vk.messenger.utils.emoji.Emoji2;

/**
 * User: anatoly
 * Date: 13.07.12
 * Time: 12:16
 */
public class MainActivity extends VKActivity {
    public static final String STATE_MENU = "ru.kurganec.vk.messenger.newui.MainActivity.menu_state";
    private SlidingMenu sideMenu;
//    DrawerLayout mDrawer;
//    private EmptyDrawerListener mActionBarInvalidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sideMenu = new SlidingMenu(this);
         Emoji2.load();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        TimeUtil.refresh();//todo remove ftw?
        if (!VK.model().isAppSignedIn()) {
            Intent signIn = new Intent(this, SignInActivity.class);
            startActivity(signIn);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

//        if (VK.model().shouldTryToAddToGroup()) {
//            VK.actions().tryAddToGroup();
//        }


        sideMenu.setMode(SlidingMenu.LEFT);
        sideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        sideMenu.setShadowWidthRes(R.dimen.shadow_width);
//        sideMenu.setShadowDrawable(R.drawable.shadow);
//        sideMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sideMenu.setFadeDegree(0.35f);
        sideMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        sideMenu.setMenu(R.layout.activity_main_menu);
        sideMenu.setBehindOffset(getResources().getDimensionPixelSize(R.dimen.drawer_width));
        sideMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                supportInvalidateOptionsMenu();
            }
        });
        sideMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                supportInvalidateOptionsMenu();
            }
        });

        if (VK.model().showRateDialog()){
            new RatingDialog().show(getSupportFragmentManager(), "rate");
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_MENU)
                && savedInstanceState.getBoolean(STATE_MENU)) {
            sideMenu.showMenu(false);
//            mDrawer.openDrawer(Gravity.RIGHT);
        }
    }


    @Override
    protected String getCustomTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem newMsg = menu.findItem(R.id.menu_write_new_msg);
        if (sideMenu.isMenuShowing()){
            newMsg.setVisible(false);
        }



        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_write_new_msg: {
                sideMenu.showMenu(true);
                break;
            }
            case R.id.menu_preferences: {
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            }
            case R.id.menu_about: {
                startActivity(new Intent(this, AboutActivity.class));
                break;
            }
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_MENU, sideMenu.isMenuShowing());
    }

    @Override
    public void onBackPressed() {
        if (sideMenu.isMenuShowing()){
            sideMenu.showContent(true);
        } else {
            super.onBackPressed();
        }
    }
}
