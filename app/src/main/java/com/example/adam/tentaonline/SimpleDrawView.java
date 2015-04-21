package com.example.adam.tentaonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/*
* Soruce code from
* Sue Smith
* http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202 2/4-15
* Modified by Adam Larsson
* */
public class SimpleDrawView extends View{


    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private boolean freeDraw=true;


    public SimpleDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        setFocusable(true);
        setFocusableInTouchMode(true);
        //setupPaint();
    }

    public SimpleDrawView(Context context, Bitmap b, Canvas c) {
        super(context);
        setupDrawing();
        setFocusable(true);
        setFocusableInTouchMode(true);
        drawCanvas=c;
        canvasBitmap=b;
        //setupPaint();
    }

    public void setFreeDraw(Boolean fd){
        freeDraw=fd;
    }


    public Bitmap getBit(){
        return canvasBitmap;
    }


    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
//get drawing area setup for interaction
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                if(freeDraw){
                    drawPath.lineTo(touchX, touchY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if(!freeDraw){
                    drawPath.lineTo(touchX, touchY);
                }

                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                //spara bitmapen till array?
                break;
            default:
                return false;
        }
        // Force a view to draw again
        invalidate();
        return true;
    }
}
