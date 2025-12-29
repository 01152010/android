package com.upad.view;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.upad.R;

public class ScreenViewDialog extends Dialog {
    public ScreenViewDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.layout_screen);
    }

}
