package com.example.adam.tentaonline;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Adam on 2015-04-27.
 */
public class DrawHandler {


    Bitmap canvasBitmap;
    LinearLayout drawButtons;
    Canvas c;
    SimpleDrawView dw;
    SparseArray<ArrayList<String>> mapBitsString = new SparseArray<>();
    SparseArray<LinearLayout> drawPageButtons = new SparseArray<>();
    int currentDrawPage=0;
    final int drawPageButtonId=9000;
    final int drawButtonId=11000;
    Button selectedDrawBtn;
    LinearLayout questionNumberLayout;
    ActionBarActivity aba;
    ComponentCreator cc;
    HorizontalScrollView scroll;
    LinearLayout foundationLayout;

    public DrawHandler(ActionBarActivity aba, ComponentCreator cc, HorizontalScrollView scroll, LinearLayout foundationLayout){
        this.aba=aba;
        this.cc=cc;
        this.scroll=scroll;
        this.foundationLayout=foundationLayout;
    }

    public void getDrawView(int currentQuestion){

        if(mapBitsString.indexOfKey(currentQuestion)>=0  &&
                mapBitsString.get(currentQuestion).size()>currentDrawPage){
            canvasBitmap=StringToBitMap(mapBitsString.get(currentQuestion).get(currentDrawPage));
            Log.d("kom in","yes");
        }
        else{
            canvasBitmap = Bitmap.createBitmap(800, 905, Bitmap.Config.ARGB_8888);
        }
        canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        c=new Canvas(canvasBitmap);
        LinearLayout drawLayout = new LinearLayout(aba);
        drawLayout.setOrientation(LinearLayout.VERTICAL);

        questionNumberLayout = cc.createQuestionTitle(("Question " + (currentQuestion + 1)
                + " - image " + (currentDrawPage + 1)) ,30,aba);


        drawLayout.addView(questionNumberLayout);

        ViewGroup parent = (ViewGroup) drawButtons.getParent();
        if(parent!=null){
            parent.removeView(drawButtons);
        }
        drawLayout.addView(drawButtons);
        drawLayout.addView(createDrawPage(canvasBitmap,c));
        LinearLayout drawpage = new LinearLayout(aba);
        drawpage.addView(drawLayout);

        foundationLayout.removeAllViews();
        foundationLayout.addView(drawpage);

        saveBitmap(currentQuestion);
    }

    public SimpleDrawView createDrawPage(Bitmap b, Canvas c){
        Log.d("skapandef ", "" + BitMapToString(b));
        dw=new SimpleDrawView(aba,b,c);
        RelativeLayout.LayoutParams kte = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        dw.setLayoutParams(kte);
        return dw;
    }

    public void saveBitmap(int currentQuestion){
        if(dw!=null){

            LinearLayout x = (LinearLayout) foundationLayout.getChildAt(0);
            LinearLayout xx = (LinearLayout) x.getChildAt(0);
            SimpleDrawView xxx = (SimpleDrawView) xx.getChildAt(2);

            ArrayList<String> bpString;
            if(mapBitsString.indexOfKey(currentQuestion)<0){
                bpString=new ArrayList<>();
            }
            else{
                bpString=mapBitsString.get(currentQuestion);
            }
            if(bpString.size()>currentDrawPage){
                bpString.set(currentDrawPage,BitMapToString(xxx.getBit() ));
            }
            else{
                bpString.add(currentDrawPage,BitMapToString(xxx.getBit() ));
            }
            mapBitsString.put(currentQuestion, bpString);
        }
    }

    public void addDrawPageButton(final int currentQuestion, boolean first, final Button nextButton, final Button prevButton){

            int inc=0;
            if(drawPageButtons.indexOfKey(currentQuestion)>=0){
                inc=drawPageButtons.get(currentQuestion).getChildCount();
            }

            final Button b = cc.createButton("Image " + (inc + 1),R.color.white,R.drawable.button_shape,aba);
            b.setId(drawPageButtonId + inc );

            if(first){
                b.setBackgroundResource(R.drawable.button_shape_clicked);
                unselectDrawButton();
                selectedDrawBtn =b;
            }

            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    LinearLayout x = (LinearLayout) foundationLayout.getChildAt(0);
                    LinearLayout xx = (LinearLayout) x.getChildAt(0);
                    SimpleDrawView xxx = (SimpleDrawView) xx.getChildAt(2);
                    unselectDrawButton();
                    saveBitmap(currentQuestion);
                    currentDrawPage=(b.getId()-drawPageButtonId);
                    getDrawView(currentQuestion);
                    b.setBackgroundResource(R.drawable.button_shape_clicked);
                    selectedDrawBtn = b;
                    DrawAnimatedQuestionScrollView();
                    enableDrawButton(currentQuestion,nextButton,prevButton);
                }
            });

            LinearLayout butt;
            if(drawPageButtons.indexOfKey(currentQuestion)>=0){
                butt = drawPageButtons.get(currentQuestion);
            }
            else{
                butt = new LinearLayout(aba);
            }
            butt.addView(b);
            drawPageButtons.put(currentQuestion, butt);
            if(mapBitsString.indexOfKey(currentQuestion)>=0){
                createAndSavePicture(currentQuestion,aba);
            }

    }

    public void DrawAnimatedQuestionScrollView(){
        ObjectAnimator animator;
        animator=ObjectAnimator.ofInt(scroll, "scrollX", selectedDrawBtn.getLeft());
        animator.setDuration(800);
        animator.start();
    }

    public void createAndSavePicture(int currentQuestion, ActionBarActivity aba){

        if(mapBitsString.indexOfKey(currentQuestion)>=0  &&
                mapBitsString.get(currentQuestion).size()>currentDrawPage){
            canvasBitmap=StringToBitMap(mapBitsString.get(currentQuestion).get(currentDrawPage));
        }
        canvasBitmap = Bitmap.createBitmap(800, 905, Bitmap.Config.ARGB_8888 );

        canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        c=new Canvas(canvasBitmap);

        createDrawPage(canvasBitmap,c);

        ArrayList<String> bpString;
        bpString=mapBitsString.get(currentQuestion);
        bpString.add(mapBitsString.get(currentQuestion).size(),BitMapToString(dw.getBit()));
        mapBitsString.put(currentQuestion,bpString);
    }

    public void addDrawingButtons(final int currentQuestion, final Button nextButton, final Button prevButton){
        LinearLayout.LayoutParams drawParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        drawButtons = new LinearLayout(aba);
        drawButtons.setOrientation(LinearLayout.HORIZONTAL);
        drawButtons.setBackgroundColor(aba.getResources().getColor(R.color.lightbrown));
        drawButtons.setLayoutParams(drawParams);
        Button bt = new Button(aba);
        bt.setText("New page");

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrawPageButton(currentQuestion,false,nextButton,prevButton);
                enableDrawButton(currentQuestion,nextButton,prevButton);
                //showpicturemark();
            }
        });
        drawButtons.addView(bt);

        for(int i=0;i<2;i++){
            Button btn = new Button(aba);
            btn.setId(drawButtonId+i);
            btn.setOnClickListener(drawButtonListener(btn));
            drawButtons.addView(btn);
        }

    }

    private View.OnClickListener drawButtonListener(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
                LinearLayout tt = (LinearLayout) t.getChildAt(0);
                SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);

                switch (v.getId()){
                    case drawButtonId + 0:
                        ttt.setFreeDraw(false);
                    break;
                    case drawButtonId + 1:
                        ttt.setFreeDraw(true);
                        break;
                }
            }
        };
    }

    public void unselectDrawButton(){
        if(selectedDrawBtn !=null){
            selectedDrawBtn.setBackgroundResource(R.drawable.button_shape);
        }
    }

    //drawPage next and prev
    public void drawPrevButtonOnClick(int currentQuestion,Button nextButton,Button prevButton){
        if(currentDrawPage!=0){
            saveBitmap(currentQuestion);
            currentDrawPage--;
            getDrawView(currentQuestion);

            unselectDrawButton();
            LinearLayout buttonLayout = drawPageButtons.get(
                    (currentQuestion));
            Button b = (Button) buttonLayout.findViewById(drawPageButtonId + currentDrawPage);
            b.setBackgroundResource(R.drawable.button_shape_clicked);
            selectedDrawBtn =b;
            DrawAnimatedQuestionScrollView();
        }
        enableDrawButton(currentQuestion, nextButton, prevButton);
    }

    public void drawNextButtonOnClick(int currentQuestion,Button nextButton,Button prevButton){
        saveBitmap(currentQuestion);
        currentDrawPage++;
        getDrawView(currentQuestion);

        unselectDrawButton();
        LinearLayout buttonLayout = drawPageButtons.get(
                (currentQuestion));
        Button b = (Button) buttonLayout.findViewById(drawPageButtonId + currentDrawPage);
        b.setBackgroundResource(R.drawable.button_shape_clicked);
        selectedDrawBtn = b;

        enableDrawButton(currentQuestion,nextButton,prevButton);
        DrawAnimatedQuestionScrollView();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
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

    public void enableDrawButton(int currentQuestion,Button nextButton,Button prevButton){
        nextButton.setEnabled(true);
        prevButton.setEnabled(true);
        if(currentDrawPage==0){
            prevButton.setEnabled(false);
        }

        if(drawPageButtons.indexOfKey(currentQuestion)<0 ||  drawPageButtons.get(currentQuestion).getChildCount()<=currentDrawPage+1){
            nextButton.setEnabled(false);
        }
    }

    public SparseArray<ArrayList<String>> getMapBitsString(){
        return mapBitsString;
    }

    public SparseArray<LinearLayout>  getDrawPageButtons(){
        return drawPageButtons;
    }

    public void setDrawPage(int drawPage){
        currentDrawPage=drawPage;
    }



}
