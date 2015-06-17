package ru.kurganec.vk.messenger.newui.fragments;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import ru.kurganec.vk.messenger.R;

/**
 * User: anatoly
 * Date: 14.03.14
 * Time: 16:26
 */
public class RatingDialog extends DialogFragment implements RatingBar.OnRatingBarChangeListener {

    public static final String ARG_ANNOUNCEMENT_ID = "AnnouncementId";
    RatingBar bar;



    public RatingDialog() {

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_rating, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        bar = (RatingBar) view.findViewById(R.id.rating_bar);
        bar.setOnRatingBarChangeListener(this);
        getDialog().setTitle(R.string.please_rate_app);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        dismiss();


        if (rating < 4){
            showToast();
        } else {
            goToMarket();
        }
    }

    private void goToMarket() {
        try {
            final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } catch (ActivityNotFoundException e) {
            // there is no market on device
        }
    }

    private void showToast() {
        Toast.makeText(getActivity(), R.string.thank_for_response, Toast.LENGTH_SHORT).show();
    }
}
