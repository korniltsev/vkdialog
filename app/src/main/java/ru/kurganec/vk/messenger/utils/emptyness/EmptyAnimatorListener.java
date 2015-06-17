package ru.kurganec.vk.messenger.utils.emptyness;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.os.Build;

;

/**
 * User: anatoly
 * Date: 05.04.13
 * Time: 1:35
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EmptyAnimatorListener implements Animator.AnimatorListener {
    public EmptyAnimatorListener() {
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
