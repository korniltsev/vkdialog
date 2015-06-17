package ru.kurganec.vk.messenger.utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: anatoly
 * Date: 25.07.12
 * Time: 1:29
 */
public class AttachHandler  {
    public static final int MAX_SIZE = 10;
    private FrameLayout wrapper;
    LinearLayout horizontalListView;
    LayoutInflater inflater;
    ArrayList<String> fileNames = new ArrayList<String>();
    String forward;
    Integer [] geo;


    public AttachHandler(FrameLayout wrapper) {
        this.wrapper = wrapper;
        inflater = LayoutInflater.from(wrapper.getContext());
        horizontalListView = (LinearLayout) wrapper.findViewById(R.id.list_attach);
        clear();

    }

    public boolean shown() {
        return wrapper.getVisibility() == View.VISIBLE;
    }

    private HashMap<String, View> attaches = new HashMap<String, View>();

    public void handlePhoto(final String fileName) {
        if (fileNames.contains(fileName)) {
            return;
        }
        fileNames.add(fileName);
        View newAttach = inflater.inflate(R.layout.view_attached, horizontalListView, false);
        ImageView btn = (ImageView) newAttach.findViewById(R.id.btn_attached);
        btn.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        String s = "file://" + fileName;
        Picasso.with(VK.inst())
                .load(fileName)
                .into(btn);
//        ImageLoader.getInstance().displayImage(s, btn);
        ImageButton btnDelete = (ImageButton) newAttach.findViewById(R.id.btn_delete_attach);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePhoto(fileName);
            }
        });

        horizontalListView.addView(newAttach, 0);
        attaches.put(fileName, newAttach);

    }



    private void removePhoto(String fileName) {
        if (fileNames.remove(fileName)) {
            View v = attaches.get(fileName);
            horizontalListView.removeView(v);
        } else {
            Log.e("VKLOL", "FTW removed photo doesnot exist");
        }

    }

    public void clear() {
        hide();
        horizontalListView.removeAllViews();
        fileNames.clear();
        forward = null;
        geo = null;

    }

    public void handleGeo(int lat, int lon) {
        geo = new Integer[2];
        geo[0] = lat;
        geo[1] = lon;
        View newAttach = inflater.inflate(R.layout.view_attached, horizontalListView, false);
        ImageView btn = (ImageView) newAttach.findViewById(R.id.btn_attached);
        btn.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ViewGroup.LayoutParams lp = btn.getLayoutParams();
        Picasso.with(VK.inst())
                .load(GoogleMapHelper.getUri(lat, lon, lp.width, lp.height))
                .into(btn);


        horizontalListView.addView(newAttach, 0);
        ImageButton btnDelete = (ImageButton) newAttach.findViewById(R.id.btn_delete_attach);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeGeo();
            }
        });

        attaches.put("geo", newAttach);



    }

    public void handleForward(String fwd) {
        forward = fwd;
    }


    private void removeGeo() {
        geo = null;
        View v = attaches.get("geo");
        horizontalListView.removeView(v);

    }

    public int size() {
        int count = 0;
        count += fileNames.size();
        count += forward != null ? 1 : 0;
        if (geo != null) {
            count += 1;
        }
        return count;
    }

    public void show() {
        wrapper.setVisibility(View.VISIBLE);
    }

    public void hide() {
        wrapper.setVisibility(View.GONE);
    }

    public boolean empty() {
        return fileNames.size() == 0 && forward == null && geo == null;
    }


    public Integer[] getGeo() {
        return geo;
    }



    public String getForward() {
        return forward;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }


}