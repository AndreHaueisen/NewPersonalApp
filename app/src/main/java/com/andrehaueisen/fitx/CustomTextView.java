package com.andrehaueisen.fitx;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


/**
 * Created by andre on 11/29/2016.
 */

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);

        applyCustomFont(context);
        applyTextColor(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
        applyTextColor(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
        applyTextColor(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("Aller_Std_Lt.ttf", context);
        setTypeface(customFont);
    }

    private void applyTextColor(Context context){
        setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
    }
}
