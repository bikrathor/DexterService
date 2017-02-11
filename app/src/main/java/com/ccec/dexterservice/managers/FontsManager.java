package com.ccec.dexterservice.managers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;

/**
 * Created by sand on 28-03-2016.
 */
public class FontsManager {
    private static Typeface font;

    public static Typeface getRegularTypeface(Context context) {
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Regular.ttf");
        return font;
    }

    public static Typeface getBoldTypeface(Context context) {
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        return font;
    }

    public static Typeface getLightTypeface(Context context) {
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        return font;
    }

    public static SpannableString actionBarTypeface(Context context, String s) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(s);
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return mNewTitle;
    }
}
