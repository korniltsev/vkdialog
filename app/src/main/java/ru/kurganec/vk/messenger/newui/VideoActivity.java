package ru.kurganec.vk.messenger.newui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.model.actions.events.VideoEvent;
import ru.kurganec.vk.messenger.utils.VKActivity;
import ru.kurganec.vk.messenger.widget.ABSMediaController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import com.flurry.android.FlurryAgent;

/**
 * Created with IntelliJ IDEA.
 * User: anatoly
 * Date: 23.09.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class VideoActivity extends VKActivity implements  MediaPlayer.OnErrorListener {

    public static final String EXTRA_VIDEO_JSON = "video-json";
    private JSONObject mVideoPreview;

    private VideoView mVideoView;
    private MediaController mController ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setOnErrorListener(this);
        mController = new ABSMediaController(this);
        mController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        mVideoView.setMediaController(mController);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(EXTRA_VIDEO_JSON)) {
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

        // FlurryAgent.logEvent("opened video");
    }


    @Override
    protected void onResume() {
        super.onResume();
        VK.bus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VK.bus().unregister(this);
    }

    @Subscribe
    public void videoInfoDownloaded(VideoEvent e) {
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
            HashMap<String, String> files = new HashMap<String, String>();
            JSONObject filesUris = videoJson.getJSONObject("files");
            Iterator i = filesUris.keys();
            String lastUri = null;
            while (i.hasNext()) {
                String key = i.next().toString();
                lastUri = filesUris.getString(key);
                files.put(key, lastUri);
            }

            if (files.size() == 1) {
                play(Uri.parse(lastUri));
            } else if (files.size() > 1) {
                showChooser(files);
            } else {
                onError(null, 0, 0);
            }
        } catch (JSONException exception) {
            // FlurryAgent.logEvent("video error " + exception.toString());
        } catch (NullPointerException npe){
            finish();
        }

    }
    private AlertDialog mQualityChooser;
    private void showChooser(final HashMap<String, String> files) {
        final CharSequence[] items = files.keySet().toArray(new CharSequence[files.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.pick_video_quility));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemPosition) {
                mQualityChooser.dismiss();
                play(Uri.parse(files.get(items[itemPosition])));
            }
        });
        mQualityChooser = builder.create();
        mQualityChooser.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        mQualityChooser.show();
    }

    private void play(Uri videoUri) {

        if (videoSupported(videoUri)){
            mVideoView.setVideoURI(videoUri);
            mVideoView.start();
            // FlurryAgent.logEvent("video started");
        } else {
            String uri = videoUri.toString();
            if (uri.endsWith(".flv")){
                try {
                    uri = "http://vk.com/video" +  mVideoPreview.getLong("owner_id") + "_" + mVideoPreview.getLong("vid");
                } catch (JSONException e) {
                    // FlurryAgent.logEvent("video error " + e.toString());
                    onError(null, 0, 0);
                    finish();
                    return;
                }
            }
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            // FlurryAgent.logEvent("video opened");
        }
    }

    private static List<String> capableVideosExtensions = new LinkedList<String>();
    static {
        capableVideosExtensions.add("3gp");
        capableVideosExtensions.add("mp4");
        capableVideosExtensions.add("ts");
        capableVideosExtensions.add("aac");
        capableVideosExtensions.add("mkv");
        capableVideosExtensions.add("webm");
    }
    private static boolean videoSupported(Uri videoUri) {
        Pattern p = Pattern.compile("^.*(.3gp|.mp4|.ts|.aac|.mkv|.webm)$");
        Matcher m = p.matcher(videoUri.getLastPathSegment());

        return m.matches();
//        if (m.matches()){
//            String ext = m.group(1);
//            return capableVideosExtensions.contains(ext);
//        } else {
//            return false;
//        }



    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, getString(R.string.video_error), Toast.LENGTH_LONG).show();
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }

        }
        return true;
    }
}
