package com.example.adam.tentaonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

/*
* Soruce code from
* Sue Smith
* http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202 2/4-15
* Modified by Adam Larsson
* */
public class SimpleDrawView extends View{
    //drawing path
    private Path drawPath, previewDrawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private boolean freeDraw=true,erase=false;
    private String shapeType;

    private float startX,startY,endX,endY;


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
        setLayerType(View.LAYER_TYPE_SOFTWARE, canvasPaint);
        //setupPaint();
    }

    public void setErase(boolean isErase){

        erase=isErase;

        if(erase)

            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

//this.setColor("#FFFFFFFF");

        else
            drawPaint.setXfermode(null);

    }

    public void setPaintColor(int color){drawPaint.setColor(color);}

    public void setStrokeWidth(int width){drawPaint.setStrokeWidth(width);}

    public void setFreeDraw(Boolean fd){
        freeDraw=fd;
    }

    public void setDrawShape(String ds){shapeType=ds;}

    public boolean getErase(){return erase;}

    public Bitmap getBit(){
        return canvasBitmap;
    }

    private void setupDrawing(){
        drawPath = new Path();
        previewDrawPath= new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(3);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //canvas.drawPath(drawPath, drawPaint);
        if(freeDraw){
            canvas.drawPath(drawPath, drawPaint);   //den som previewar
        }
        else{
            if(shapeType.equals("line")){
                canvas.drawLine(startX,startY,endX,endY,drawPaint); //den som previewar
            }
            else if(shapeType.equals("rectangle")){
                 canvas.drawRect(startX,startY,endX,endY,drawPaint);
            }

        }

    }

    public String BitMapToString(Bitmap bitmap) {
        String result="";
        if(bitmap!=null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
            byte[] b = baos.toByteArray();
            result = Base64.encodeToString(b, Base64.DEFAULT);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(freeDraw){
                    drawPath.moveTo(touchX, touchY);
                }
                else{
                    startX = event.getX();
                    startY = event.getY();
                    endX = event.getX();
                    endY = event.getY();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(freeDraw){
                    drawPath.lineTo(touchX, touchY);
                }
                else{
                    endX = event.getX();
                    endY = event.getY();
                }


                break;
            case MotionEvent.ACTION_UP:
                if(freeDraw){
                    drawPath.lineTo(touchX, touchY);
                    drawCanvas.drawPath(drawPath, drawPaint); //den som sparar
                    drawPath.reset();
                }
                else{
                    endX = event.getX();
                    endY = event.getY();
                    if(shapeType.equals("line")){
                        drawCanvas.drawLine(startX,startY,endX,endY,drawPaint);
                    }
                    else if(shapeType.equals("rectangle")){
                        drawCanvas.drawRect(startX,startY,endX,endY,drawPaint);
                    }
                }
                break;
            default:
                return false;
        }
        // Force a view to draw again
        invalidate();
        return true;
    }
}
