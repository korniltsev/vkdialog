package ru.kurganec.vk.messenger.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {
    public static void hideKeyboard(View anchor) {
        Context c = anchor.getContext();
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(anchor.getWindowToken(), 0);
    }

    public static void showKeyboard(View anchor) {
        Context c = anchor.getContext();
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(anchor, InputMethodManager.SHOW_IMPLICIT);
    }
}
