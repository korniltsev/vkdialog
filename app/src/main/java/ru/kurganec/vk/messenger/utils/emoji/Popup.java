package ru.kurganec.vk.messenger.utils.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.utils.emptyness.EmptyOnPageChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Popup extends PopupWindow {
    //    private LinearLayout mEmojiLines;
    private final ViewPager pager;

    private final RadioGroup radio;
    private EditText mEditor;

    private final int[]pages = {R.id.page_1,R.id.page_2,R.id.page_3,R.id.page_4,R.id.page_5};
    public Popup(View root) {
        super(root,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                false
        );
        final Context c = root.getContext();
        final Resources res = c.getResources();
        setFocusable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        final LayoutInflater viewFactory = LayoutInflater.from(c);
        pager = (ViewPager) root.findViewById(R.id.pager);
        radio = (RadioGroup) root.findViewById(R.id.radio);
        for (int i = 0; i < 5; i++) {
            int id = c.getResources().getIdentifier("page_" + (i+1), "id", c.getPackageName());
            RadioButton viewById = (RadioButton) root.findViewById(id);
            long key = Emoji2.data[i][0];
            Emoji2.EmojiDrawable d = Emoji2.bigEmoji.get(key);
            if (d != null){
                Drawable copy = d.copy();
                viewById.setCompoundDrawables(null,copy,null,null);
            }
        }
//        page1  = (RadioButton) root.findViewById(R.id.page_1);
//        page2  = (RadioButton) root.findViewById(R.id.page_2);
//        page3  = (RadioButton) root.findViewById(R.id.page_3);
//        page4  = (RadioButton) root.findViewById(R.id.page_4);
//        page5  = (RadioButton) root.findViewById(R.id.page_5);
//        page1.setButtonDrawable(Emoji2.bigEmoji.get(Emoji2.data[0][0]).copy());
//        page2.setButtonDrawable(Emoji2.bigEmoji.get(Emoji2.data[1][0]).copy());
//        page3.setButtonDrawable(Emoji2.bigEmoji.get(Emoji2.data[2][0]).copy());
//        page4.setButtonDrawable(Emoji2.bigEmoji.get(Emoji2.data[3][0]).copy());
//        page5.setButtonDrawable(Emoji2.bigEmoji.get(Emoji2.data[4][0]).copy());
        radio.check(R.id.page_1);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0, pagesLength = pages.length; i < pagesLength; i++) {
                    int page = pages[i];
                    if (page == checkedId){
                        pager.setCurrentItem(i, true);
                    }
                }
            }
        });
        pager.setOnPageChangeListener(new EmptyOnPageChangeListener(){
            @Override
            public void onPageSelected(int i) {
                radio.check(pages[i]);
            }
        });
        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Emoji2.data.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View res = viewFactory.inflate(R.layout.view_emoji_popup_page, container, false);
                GridView grid = (GridView) res.findViewById(R.id.list);
                ArrayList<Long> ids = new ArrayList<>();
                for (long l : Emoji2.data[position]) {
                    ids.add(l);
                }
                grid.setAdapter(new EmojiPageAdapter(c, ids));
                container.addView(res);
                return res;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        setWidth(res.getDimensionPixelSize(R.dimen.emoji_popup_width));
        setHeight(res.getDimensionPixelSize(R.dimen.emoji_popup_width));
    }


    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
    }

    private String convert(long l)
    {
        String s = "";
        for (int i = 0; i < 4; i++)
        {
            int j = (int)(65535L & l >> 16 * (3 - i));
            if (j != 0)
            {
                s = (new StringBuilder()).append(s).append((char)j).toString();
            }
        }

        return s;
    }
    private void injectSmile( Long smileId) {

        String str = convert(smileId);

        String stringify = mEditor.getText().toString();

        String leftPart = stringify.substring(0, mEditor.getSelectionStart());
        int cursorPosition = mEditor.getSelectionEnd();
        String rightPart = stringify.substring(cursorPosition, stringify.length());

        mEditor.setText(Emoji2.replaceEmoji(leftPart + " " + str + " " + rightPart));
        try {
            mEditor.setSelection(leftPart.length() + str.length() + 2);
        } catch (IndexOutOfBoundsException ignore) {
            mEditor.setSelection(mEditor.getText().toString().length());
        }
    }

    public void show(int top) {
        showAtLocation(getContentView(), Gravity.TOP | Gravity.RIGHT, 0, top);
    }

    public void setEditor(EditText messageInput) {
        mEditor = messageInput;
    }


    private class EmojiPageAdapter extends ArrayAdapter<Long> {



        private final LayoutInflater viewFactory;
        private View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag instanceof Long){
                    injectSmile((Long)tag);
                    dismiss();
                    mEditor.requestFocus();
                }
            }
        };

        public EmojiPageAdapter(Context context, List<Long> ids) {
            super(context, 0, ids);
            viewFactory = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = viewFactory.inflate(R.layout.view_emoji_item, parent, false);
            }
            ImageButton btn = (ImageButton) convertView;
            Long item = getItem(position);
            Emoji2.EmojiDrawable drawable = Emoji2.bigEmoji.get(item);
            btn.setImageDrawable(drawable);
            btn.setTag(item);
            btn.setOnClickListener(l);
            return convertView;
        }
    }
}
