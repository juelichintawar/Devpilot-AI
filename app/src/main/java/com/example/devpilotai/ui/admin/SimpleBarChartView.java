package com.example.devpilotai.ui.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.devpilotai.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleBarChartView extends View {
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<BarData> data = new ArrayList<>();
    private float maxValue = 0;

    public static class BarData {
        public final String label;
        public final float value;
        public final int color;

        public BarData(String label, float value, int color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }

    public SimpleBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        barPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<BarData> data) {
        this.data = data;
        maxValue = 0;
        for (BarData d : data) {
            if (d.value > maxValue) maxValue = d.value;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data.isEmpty() || maxValue == 0) return;

        int width = getWidth();
        int height = getHeight();
        int barCount = data.size();
        float barWidth = (float) width / (barCount * 2);
        float spacing = (float) width / (barCount * 2);

        for (int i = 0; i < barCount; i++) {
            BarData d = data.get(i);
            float barHeight = (d.value / maxValue) * (height - 100);
            barPaint.setColor(d.color);

            float left = spacing / 2 + i * (barWidth + spacing);
            float top = height - 60 - barHeight;
            float right = left + barWidth;
            float bottom = height - 60;

            canvas.drawRect(left, top, right, bottom, barPaint);
            
            // Draw label
            textPaint.setColor(getContext().getColor(R.color.text_secondary));
            canvas.drawText(d.label, left + barWidth / 2, height - 20, textPaint);
            
            // Draw value
            canvas.drawText(String.valueOf((int)d.value), left + barWidth / 2, top - 10, textPaint);
        }
    }
}
