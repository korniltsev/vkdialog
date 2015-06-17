package ru.kurganec.vk.messenger.newui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.VideoEvent;
import ru.kurganec.vk.messenger.utils.VKActivity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 07.09.12
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
public class CustomVideoActivity extends VKActivity implements  MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {
    public static final String EXTRA_VIDEO_JSON = "video-json";
    private JSONObject mVideoPreview;
    private SurfaceView mPlayerDisplay;
    private MediaPlayer mPlayer;
    private boolean mPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_video);
        mPlayerDisplay = (SurfaceView) findViewById(R.id.surface_player);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_VIDEO_JSON)){
            finish();
        } else {
            try {
                mVideoPreview = new JSONObject(extras.getString(EXTRA_VIDEO_JSON));
                VK.actions().getVideo(mVideoPreview.getString("owner_id"),
                        mVideoPreview.getString("vid"));
            } catch (JSONException e) {
                finish();
            }
        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        VK.bus().register(this);
        if (mPlayer != null && mPaused ){
            mPlayer.start();
            mPaused = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.bus().unregister(this);
        if (mPlayer != null && mPlayer.isPlaying()){
            mPlayer.pause();
            mPaused = true;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlayer!= null ){
            mPlayer.release();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return true;
    }

    @Subscribe
    public void videoInfoDownloaded(VideoEvent e){
        /*
        *  {"files":{"mp4_240":"http:\/\/cs12987.userapi.com\/u5716843\/video\/3501984ba0.240.mp4"}
            "duration":57
            "title":"Обкуренный баскетболист :)))"
            "image_medium":"http:\/\/cs12987.userapi.com\/u5716843\/video\/l_d36bd2bd.jpg"
            "player":"http:\/\/vk.com\/video_ext.php?oid=113482354&id=160879432&hash=6a92b1004a1cc64e"
            "views":0
            "description":""
            "link":"video160879432"
            "owner_id":113482354
            "image":"http:\/\/cs12987.userapi.com\/u5716843\/video\/m_5b95fff1.jpg"
            "date":1316372222
            "vid":160879432}
        */
        try {
            JSONObject videoJson = new JSONObject(e.getResultData().getString("video"));
            HashMap<String, String> files= new HashMap<String, String>();
            JSONObject filesUris = videoJson.getJSONObject("files");
            Iterator i = filesUris.keys();
            String lastUri = null;
            while (i.hasNext()){
                String key = i.next().toString();
                lastUri = filesUris.getString(key);
                files.put(key, lastUri);
            }

            if (files.size()>0){
                display(lastUri);
            }
        } catch (JSONException ignored) {
        }

    }
    private void display(String videoUri){
        mPlayer = MediaPlayer.create(this, Uri.parse(videoUri), mPlayerDisplay.getHolder());
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.prepareAsync();
    }


    @Override
    public void onPrepared(MediaPlayer mp) {

            //Get the dimensions of the video
            int videoWidth = mPlayer.getVideoWidth();
            int videoHeight = mPlayer.getVideoHeight();

            //Get the width of the screen
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

            //Get the SurfaceView layout parameters
            android.view.ViewGroup.LayoutParams lp = mPlayerDisplay.getLayoutParams();

            //Set the width of the SurfaceView to the width of the screen
            lp.width = screenWidth;

            //Set the height of the SurfaceView to match the aspect ratio of the video
            //be sure to cast these as floats otherwise the calculation will likely be 0
            lp.height = (int) (((float)videoHeight / (float)videoWidth) * (float)screenWidth);

            //Commit the layout parameters
            mPlayerDisplay.setLayoutParams(lp);

            mPlayer.start();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
