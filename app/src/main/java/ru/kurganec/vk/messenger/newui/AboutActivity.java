package ru.kurganec.vk.messenger.newui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.utils.ApiExecutor;

import java.io.IOException;

/**
 * User: anatoly
 * Date: 24.08.12
 * Time: 1:29
 */
public class AboutActivity extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            String license = ApiExecutor.convertStreamToString(getAssets().open("license.txt"));
            TextView labelLicense = (TextView) findViewById(R.id.label_license);
            labelLicense.setText(license);
        } catch (IOException ignored) {
        }

        String str = getString(R.string.about);
        TextView about = (TextView) findViewById(R.id.label_about);
        about.setText(Html.fromHtml(str));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId()){
            case android.R.id.home:{
                finish();
            }
        }
        return true;
    }
}
