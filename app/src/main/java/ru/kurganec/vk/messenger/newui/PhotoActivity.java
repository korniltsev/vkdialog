package ru.kurganec.vk.messenger.newui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.UnderlinePageIndicator;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.db.Message;
import ru.kurganec.vk.messenger.newui.fragments.PhotoFragment;
import ru.kurganec.vk.messenger.utils.VKActivity;

import java.util.ArrayList;

/**
 * User: anatoly
 * Date: 03.08.12
 * Time: 17:41
 */
public class PhotoActivity extends VKActivity implements View.OnClickListener {
    public static final String EXTRA_MID = "ru.kurganec.vk.messenger.newui.PhotoActivity.EXTRA_MID";
    public static final String EXTRA_PHOTO_POSITION = "ru.kurganec.vk.messenger.newui.PhotoActivity.EXTRA_POSITION";


    private UnderlinePageIndicator mIndicator;
    private ViewPager mPager;
    private ArrayList<String> attachedPhotos;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_MID) || !extras.containsKey(EXTRA_PHOTO_POSITION)){
            finish();
            return;
        }
        long mid = extras.getLong(EXTRA_MID);
        int currentPhotoPosition = extras.getInt(EXTRA_PHOTO_POSITION);

        Cursor message = null;
        attachedPhotos = new ArrayList<String>();
        try {
            message = VK.db().msg().get(mid);
            message.moveToFirst();
            JSONArray arr = new JSONArray(message.getString(message.getColumnIndex(Message.ATTACHMENTS)));
            /*[{"type":"photo",
                "photo":{"text":"",
                        "height":960,
                        "src_small":"http:\/\/cs306315.userapi.com\/v306315284\/2778\/Ksvljv7b6Ps.jpg",
                        "created":1352283408,
                        "src_xxbig":"http:\/\/cs306315.userapi.com\/v306315284\/277c\/GKHyamU4xGQ.jpg",
                        "width":640,"owner_id":402284,"pid":291794505,"access_key":"2cdce50752bbb25cf2",
                        "src":"http:\/\/cs306315.userapi.com\/v306315284\/2779\/NmFSEgTyU0I.jpg","aid":-3,
                        "src_xbig":"http:\/\/cs306315.userapi.com\/v306315284\/277b\/tSobegvbRok.jpg",
                        "src_big":"http:\/\/cs306315.userapi.com\/v306315284\/277a\/pscxYRiBANY.jpg"}}]*/
            for (int i = 0; i < arr.length(); ++i) {
                JSONObject attachment = arr.getJSONObject(i);
                if (attachment.getString("type").equals("photo")){
//todo просмотр больших фото
//                    [3] = {java.util.LinkedHashMap$LinkedEntry@4376}"src" -> "https://pp.vk.me/c616922/v616922596/18d09/bDxO8eGdlT4.jpg"
//                            [4] = {java.util.LinkedHashMap$LinkedEntry@4379}"src_big" -> "https://pp.vk.me/c616922/v616922596/18d0a/OCfBUUp7EMQ.jpg"
//                            [5] = {java.util.LinkedHashMap$LinkedEntry@4382}"src_small" -> "https://pp.vk.me/c616922/v616922596/18d08/iAKFwsXEBn0.jpg"
//                            [6] = {java.util.LinkedHashMap$LinkedEntry@4385}"src_xbig" -> "https://pp.vk.me/c616922/v616922596/18d0b/3vi_PjLFRV0.jpg"
//                            [7] = {java.util.LinkedHashMap$LinkedEntry@4388}"src_xxbig" -> "https://pp.vk.me/c616922/v616922596/18d0c/Xx4B0FBd2Xc.jpg"
//                            [8] = {java.util.LinkedHashMap$LinkedEntry@4391}"src_xxxbig" -> "https://pp.vk.me/c616922/v616922596/18d0d/I1aUuvbwu2E.jpg"
                    attachedPhotos.add(attachment.getJSONObject("photo").getString("src_big"));
                }
            }
        } catch (Exception e) {
            //TODO LOG EVENT!!!!!
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new PhotosAdapter(getSupportFragmentManager(), attachedPhotos));
        mIndicator.setViewPager(mPager);
        mIndicator.setFadeDelay(1000);
        mIndicator.setCurrentItem(currentPhotoPosition);

    }


    @Override
    protected String getCustomTitle() {
        return getResources().getQuantityString(R.plurals.attached_photo, attachedPhotos.size());
    }

    @Override
    public void onClick(View v) {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    private class PhotosAdapter extends FragmentPagerAdapter {



        private final ArrayList<String> attachedPhotos;

        public PhotosAdapter(FragmentManager fm, ArrayList<String> attachedPhotos) {
            super(fm);
            this.attachedPhotos = attachedPhotos;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString(PhotoFragment.ARG_IMAGE_URL, attachedPhotos.get(position));
            return Fragment.instantiate(PhotoActivity.this, PhotoFragment.class.getName(), args);
        }

        @Override
        public int getCount() {
            return attachedPhotos.size();
        }
    }
}
