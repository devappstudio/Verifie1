package com.devappstudio.verifie;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by root on 10/15/16.
 */

public class MyTextView extends TextView {
    public MyTextView(Context context,AttributeSet attrs)
    {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"nueva.ttf"));

    }
}
