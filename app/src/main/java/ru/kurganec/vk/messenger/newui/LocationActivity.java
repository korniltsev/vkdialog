package ru.kurganec.vk.messenger.newui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.squareup.otto.Subscribe;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;
import ru.kurganec.vk.messenger.utils.GeoPickerOverlay;
import ru.kurganec.vk.messenger.utils.SingleItemOverlay;

/**
 * User: anatoly
 * Date: 03.08.12
 * Time: 12:16
 */
public class LocationActivity extends MapActivity implements View.OnClickListener {
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_LATITUDE = "latitude";


    private Button btnPick;
    private static final String TAG = "VK-CHAT-LOCATION";

    private GeoPoint mPickedPoint;
    private MapView mMapView;


    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.activity_location);
        btnPick = (Button) findViewById(R.id.btn_pick_location);
        mMapView = (MapView) findViewById(R.id.map);

        if (getIntent().getAction().equals(Intent.ACTION_PICK)) {
            showPicker();
        } else if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            showViewer();
        } else {
            throw new IllegalStateException("unknown action" + getIntent().getAction());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
            ActionBar ab = getActionBar();
            ab.setTitle(R.string.location);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        VK.bus().register(this);
    }

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

    private void showViewer() {
        btnPick.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        GeoPoint p = new GeoPoint(extras.getInt(EXTRA_LATITUDE), extras.getInt(EXTRA_LONGITUDE));
        mMapView.getOverlays().add(new SingleItemOverlay(getResources().getDrawable(R.drawable.ic_location_place), p));
        mMapView.getController().setCenter(p);
        mMapView.getController().setZoom(14);
    }

    private void showPicker() {
        btnPick.setOnClickListener(this);
        btnPick.setEnabled(false);

        mMapView.getOverlays().add(new GeoPickerOverlay(getResources().getDrawable(R.drawable.ic_location_place)));
    }

    @Subscribe
    public void locationPicked(GeoPoint p) {
        btnPick.setEnabled(true);
        mPickedPoint = p;
    }

    @Override
    public void onClick(View v) {
        Intent i = getIntent();
        i.putExtra(EXTRA_LATITUDE, mPickedPoint.getLatitudeE6());
        i.putExtra(EXTRA_LONGITUDE, mPickedPoint.getLongitudeE6());
        setResult(RESULT_OK, i);
        finish();
    }


    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }


}
