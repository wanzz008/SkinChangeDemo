package com.wzz.skinchangedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MyView extends View {

    public MyView(Context context) {
        this(context, null );
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs , 1 );
    }

    public MyView(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
