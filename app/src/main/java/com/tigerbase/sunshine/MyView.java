package com.tigerbase.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class MyView extends View
{
    private final String LOG_TAG = MyView.class.getSimpleName();
    private final int STROKE_WIDTH = 4;

    private Paint _compassBackground;
    private Paint _compassBorder;
    private Paint _compassNeedle;
    private int _myWidth;
    private int _myHeight;
    private double _angleInDegrees;

    public MyView(Context context)
    {
        super(context);
        initializeCompass();
    }

    public MyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initializeCompass();
    }

    public MyView(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs, defaultStyle);
        initializeCompass();
    }

    private void initializeCompass()
    {
        Log.v(LOG_TAG, "initializeCompass");

        _compassBackground = new Paint();
        _compassBackground.setStyle(Paint.Style.FILL);
        _compassBackground.setColor(getResources().getColor(R.color.sunshine_light_blue));

        _compassBorder = new Paint();
        _compassBorder.setStyle(Paint.Style.STROKE);
        _compassBorder.setStrokeWidth(STROKE_WIDTH);
        _compassBorder.setColor(getResources().getColor(R.color.sunshine_blue));

        _compassNeedle = new Paint();
        _compassNeedle.setStyle(Paint.Style.STROKE);
        _compassNeedle.setStrokeWidth(4);
        _compassNeedle.setColor(getResources().getColor(R.color.black));

        _angleInDegrees = 0;
    }

    public void setAngleInDegrees(double angleInDegrees)
    {
        Log.v(LOG_TAG, "setAngleInDegrees");
        _angleInDegrees = angleInDegrees;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event)
    {
        Log.v(LOG_TAG, "dispatchPopulateAccessibilityEvent");
        String text = "Compass Image " + Integer.toString((int)_angleInDegrees) + " degrees";
        Log.v(LOG_TAG, "dispatchPopulateAccessibilityEvent: " + text);
        event.getText().add(text);
        return true;
    }

    public double getAngleInDegrees()
    {
        Log.v(LOG_TAG, "getAngleInDegrees");
        return _angleInDegrees;
    }

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec)
    {
        Log.v(LOG_TAG, "onMeasure");
        int hSpecMode = MeasureSpec.getMode(hMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(hMeasureSpec);
        _myHeight = hSpecSize;

        int wSpecMode = MeasureSpec.getMode(wMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(wMeasureSpec);
        _myWidth = wSpecSize;

        Log.v(LOG_TAG, "onMeasure: hSpecMode = " + Integer.toString(hSpecMode));
        Log.v(LOG_TAG, "onMeasure: hSpecSize = " + Integer.toString(hSpecSize));
        Log.v(LOG_TAG, "onMeasure: wSpecMode = " + Integer.toString(wSpecMode));
        Log.v(LOG_TAG, "onMeasure: wSpecSize = " + Integer.toString(wSpecSize));

        switch (hSpecMode)
        {
            case MeasureSpec.EXACTLY:
                _myHeight = hSpecSize;
                Log.v(LOG_TAG, "onMeasure: hSpecMode == Exactly");
                break;
            case MeasureSpec.AT_MOST:
                Log.v(LOG_TAG, "onMeasure: hSpecMode == AT_MOST");
                _myHeight = Math.min(_myHeight, 200);
                break;
        }

        switch (wSpecMode)
        {
            case MeasureSpec.EXACTLY:
                Log.v(LOG_TAG, "onMeasure: wSpecMode == EXACTLY");
                _myWidth = wSpecSize;
                break;
            case MeasureSpec.AT_MOST:
                Log.v(LOG_TAG, "onMeasure: wSpecMode == AT_MOST");
                _myWidth = Math.min(_myWidth, 200);
                break;
        }

        Log.v(LOG_TAG, "onMeasure: (" + Integer.toString(_myWidth) + ", " + Integer.toString(_myHeight) + ")");
        setMeasuredDimension(_myWidth, _myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        float centerX = _myWidth / 2;
        float centerY = _myHeight / 2;
        float radius = centerX;
        float needleRadius = radius - 5;

        canvas.drawCircle(centerX, centerY, radius, _compassBackground);
        canvas.drawCircle(centerX, centerY, radius - STROKE_WIDTH / 2, _compassBorder);

        double angleInRadians = (_angleInDegrees - 90) * ((float)Math.PI / 180);
        float needleX = centerX + needleRadius * (float)Math.cos(angleInRadians);
        float needleY = centerY + needleRadius * (float)Math.sin(angleInRadians);

        canvas.drawLine(centerX, centerY, needleX, needleY, _compassNeedle);

        if (((AccessibilityManager)getContext().getSystemService(Context.ACCESSIBILITY_SERVICE)).isEnabled())
        {
            //Log.v(LOG_TAG, "onDraw: Accessibility view text changed");
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }

}
