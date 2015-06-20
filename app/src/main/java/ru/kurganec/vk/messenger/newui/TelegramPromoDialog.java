package ru.kurganec.vk.messenger.newui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;

public class TelegramPromoDialog extends DialogFragment {

    public static final String PREF_NAME = "PREF_NAME";
    public static final String PREF_CLICKED = "PrefCLicked";
    private ImageView img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.telegram_promo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        img = ((ImageView) view.findViewById(R.id.promo_image));
        Picasso.with(getActivity())
                .load("https://pp.vk.me/c618824/v618824596/1972b/UG9F3paM5fE.jpg")
                .into(img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStore();
                dismiss();
            }
        });
        getDialog().setTitle("Телеграмм на русском");
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        //
    }

    private void openStore() {
        getActivity()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(PREF_CLICKED, true)
                .commit();
        //        https://play.google.com/store/apps/details?id=ru.korniltsev.telegram
        try {
            String appPackageName = "ru.korniltsev.telegram";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } catch (ActivityNotFoundException e) {
            // there is no market on device
        }
    }
}
