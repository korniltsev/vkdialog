package ru.kurganec.vk.messenger.utils.emptyness;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
* User: anatoly
* Date: 07.04.13
* Time: 15:42
*/
public class EmptyTarget implements Target

{
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
//    @Override
//    public void onLoadingStarted(String imageUri, View view) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void onLoadingCancelled(String imageUri, View view) {
//        //To change body of implemented methods use File | Settings | File Templates.
//    }
}
