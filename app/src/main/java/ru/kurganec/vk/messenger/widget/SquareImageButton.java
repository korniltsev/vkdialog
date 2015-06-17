package ru.kurganec.vk.messenger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class SquareImageButton extends ImageButton {
    public SquareImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
