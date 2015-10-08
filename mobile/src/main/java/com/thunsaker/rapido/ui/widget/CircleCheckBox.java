package com.thunsaker.rapido.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CircleCheckBox extends CheckBox {
    public CircleCheckBox(Context context) {
        this(context, null);
    }

    public CircleCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
