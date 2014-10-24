package com.firebase.sflivebus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextMarkerFactory {
    public static Bitmap buildMarker(Context context, String text) {
        ViewGroup container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.text_marker, null);
        TextView textView = (TextView) container.findViewById(R.id.text);
        textView.setText(text);

        return makeIcon(container);
    }

    /**
     * Creates an icon with the current content and style.
     * <p/>
     * This method is useful if a custom view has previously been set, or if text content is not
     * applicable.
     */
    private static Bitmap makeIcon(ViewGroup container) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        container.measure(measureSpec, measureSpec);

        int measuredWidth = container.getMeasuredWidth();
        int measuredHeight = container.getMeasuredHeight();

        container.layout(0, 0, measuredWidth, measuredHeight);

        Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        r.eraseColor(Color.TRANSPARENT);

        Canvas canvas = new Canvas(r);

        container.draw(canvas);
        return r;
    }
}
