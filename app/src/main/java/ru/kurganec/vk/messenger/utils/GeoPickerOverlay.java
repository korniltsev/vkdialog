package ru.kurganec.vk.messenger.utils;

import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import ru.kurganec.vk.messenger.model.VK;

/**
 * User: anatoly
 * Date: 03.08.12
 * Time: 14:37
 */
public class GeoPickerOverlay extends ItemizedOverlay {

    private OverlayItem geo ;



    public GeoPickerOverlay(Drawable drawable) {
        super(boundCenterBottom(drawable));
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return geo;
    }

    @Override
    public int size() {
        return geo == null ? 0 : 1;
    }



    @Override
    public boolean onTap(GeoPoint geoPoint, MapView mapView) {
        geo = new OverlayItem( geoPoint, null, null);
        populate();
        VK.bus().post(geoPoint);
        return true;
    }

}
