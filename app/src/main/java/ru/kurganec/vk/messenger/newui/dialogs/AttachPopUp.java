package ru.kurganec.vk.messenger.newui.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.*;
import ru.kurganec.vk.messenger.R;

import java.util.ArrayList;

/**
 * User: anatoly
 * Date: 03.07.12
 * Time: 2:47
 */
public class AttachPopUp extends PopupWindow implements AdapterView.OnItemClickListener {
    public static enum Action{
        ACTION_PHOTO,
        ACTION_GALLERY,
        ACTION_GEO
    }

    private ListView mList;
    private ArrayAdapter<Action> mAdapter;
    private AttachActionListener mObserver;


    public AttachPopUp(View root) {
        super(root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
        );
        mAdapter = new AttachAdapter(root.getContext(), R.layout.view_attach_action);
        mList = (ListView) root.findViewById(android.R.id.list);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

    }

    public static AttachPopUp build( Context context){

        View root =  View.inflate(context, R.layout.dialog_attach, null);
        return new AttachPopUp(root);
    }

    public void show() {
        show(0);
    }

    public void show(int top) {
//        showAtLocation(getContentView(), Gravity.TOP, 0, top);
        showAtLocation(getContentView(), Gravity.TOP, 0, top);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (mObserver != null){
            mObserver.onAction(mAdapter.getItem(position));
        }
    }



    public void setAttachListener(AttachActionListener listener){
        mObserver = listener;
    }


    public static interface AttachActionListener{
        void onAction(Action a);
    }

    private class AttachAdapter extends ArrayAdapter<Action> {
        private ArrayList<Action> mData = new ArrayList<Action>(3);
        {
            mData.add(Action.ACTION_PHOTO);
            mData.add(Action.ACTION_GALLERY);
            mData.add(Action.ACTION_GEO);
        }
        public AttachAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Action getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View ignoredConvertView, ViewGroup viewGroup) {
            Action a = getItem(position);
            View root = LayoutInflater.from(getContext()).inflate(R.layout.view_attach_action, viewGroup, false);
            TextView text = (TextView) root.findViewById(R.id.label_action);

            switch (a){
                case ACTION_GALLERY:{

                    text.setText(getContext().getString(R.string.attach_from_gallery));
                    text.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_gallery, 0, 0 ,0);
                    break;
                }
                case ACTION_GEO:{

                    text.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_mylocation, 0, 0 ,0);
                    text.setText(getContext().getString(R.string.attach_from_geo));
                    break;
                }
                case ACTION_PHOTO:{

                    text.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_camera, 0, 0 ,0);
                    text.setText(getContext().getString(R.string.attach_from_photo));
                    break;
                }
            }

            return root;
        }
    }

}
