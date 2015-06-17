package ru.kurganec.vk.messenger.newui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import ru.kurganec.vk.messenger.R;

public class MessageBubbleContainer extends LinearLayout {
    private final int maxWidth;

    public MessageBubbleContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        final int timeLabelWidth = context.getResources().getDimensionPixelSize(R.dimen.message_time_width);
        maxWidth = context.getResources().getDisplayMetrics().widthPixels - timeLabelWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


}