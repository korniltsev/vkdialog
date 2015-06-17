package ru.kurganec.vk.messenger.newui;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.utils.VKActivity;

/**
 * User: anatoly
 * Date: 22.08.12
 * Time: 13:44
 */
public class PreferencesActivity extends SherlockPreferenceActivity {

    private VKActivity.SignOutObserver mObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_prefs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mObserver = new VKActivity.SignOutObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.bus().unregister(mObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VK.bus().register(mObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.prefs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }

            case R.id.menu_logout:{
                VK.actions().signOut(true);
            }
        }
        return true;
    }


}
