package ru.omegapps.wisherapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

public class MainUtils {

    public static View.OnFocusChangeListener hideKeyboardOnUnfocused(Context activity){
        return (v, hasFocus) -> {
            if(!hasFocus)
                hideKeyboard(v, activity);
        };
    }

    public static void hideKeyboard(View view, Context activity) {
        InputMethodManager inputMethodManager =(InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideProgressBar(RelativeLayout progressBar, Activity activity) {
        if(activity != null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
