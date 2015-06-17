package ru.kurganec.vk.messenger.utils;

import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * User: anatoly
 * Date: 03.08.12
 * Time: 19:06
 */
public class SingleItemOverlay extends ItemizedOverlay {
    final public OverlayItem point;
    public SingleItemOverlay(Drawable drawable, GeoPoint geoPoint) {
        super(boundCenterBottom(drawable))  ;
        point = new OverlayItem(geoPoint, null, null);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return point;
    }

    @Override
    public int size() {
        return 1;
    }
}
