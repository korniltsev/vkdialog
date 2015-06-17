package ru.kurganec.vk.messenger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import ru.kurganec.vk.messenger.R;

/**
 * User: anatoly
 * Date: 07.04.13
 * Time: 12:52
 */
public class TwoElementLargerListView extends ListView {

    private View mFooter;

    public TwoElementLargerListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void extend(Context context) {
        mFooter = LayoutInflater.from(context).inflate(R.layout.view_2_elements_footer, this, false);
        addFooterView(mFooter, null, false);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //this makes the list view instatiate 2 elements ahead
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (mFooter != null) {
            h += mFooter.getLayoutParams().height;
            setMeasuredDimension(w, h);
        }
    }



}
