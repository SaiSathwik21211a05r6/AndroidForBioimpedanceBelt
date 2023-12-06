/**package com.example.eegreader.Graph;

import android.content.Context;
import android.widget.TextView;

import com.example.eegreader.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent); // Assuming you have a TextView with this ID in your marker layout
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // Display the frequency and (p1, p2) values at the selected point
        tvContent.setText("Frequency: " + e.getX() + "\n(p1, p2): " + e.getY());
    }
}
*/