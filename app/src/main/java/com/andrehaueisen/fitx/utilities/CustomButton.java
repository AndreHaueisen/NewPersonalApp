package com.andrehaueisen.fitx.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.andrehaueisen.fitx.R;

/**
 * Created by andre on 12/4/2016.
 */

public class CustomButton extends Button {

    public CustomButton(Context context) {
        super(context);

        applyCustomFont(context);
        applyTextColor(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
        applyTextColor(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
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
