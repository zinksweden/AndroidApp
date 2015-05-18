package com.example.adam.tentaonline;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;

/**
 * Created by Adam on 2015-04-27.
 */
public class DrawHandler {

    Bitmap canvasBitmap;
    RelativeLayout drawButtons;
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
    MenuItem pictureMark;

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
        resetDrawButtons();
    }

    public SimpleDrawView createDrawPage(Bitmap b, Canvas c){
        dw=new SimpleDrawView(aba,b,c);
        RelativeLayout.LayoutParams kte = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        dw.setLayoutParams(kte);
        return dw;
    }

    public void saveBitmap(int currentQuestion){
        if(dw!=null && drawPageButtons.get(currentQuestion).getChildCount()>0){

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
            showPictureMark(currentQuestion);
        }
    }

    public void showPictureMark(int currentQuestion){
        MenuItem pictureIndicator = pictureMark;
        if(getMapBitsString().indexOfKey(currentQuestion)>=0 && getMapBitsString().get(currentQuestion).size()>0){
            pictureIndicator.setVisible(true);
            pictureIndicator.setTitle("x " + getMapBitsString().get(currentQuestion).size());
        }
        else{
            pictureIndicator.setVisible(false);
        }
    }

    public void setMenuItem(MenuItem pictureMark){
        this.pictureMark=pictureMark;
    }

    public void addDrawPageButton(final int currentQuestion, boolean first, final Button nextButton, final Button prevButton){

            int inc=0;
            if(drawPageButtons.indexOfKey(currentQuestion)>=0){
                inc=drawPageButtons.get(currentQuestion).getChildCount();
            }

            final Button b = cc.createButton(" Image " + (inc + 1) + " ",R.color.white,R.drawable.button_shape,aba);
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
                    enableDrawButton(currentQuestion, nextButton, prevButton);
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

    public void makeStartDrawingButtons(final int currentQuestion, final Button nextButton, final Button prevButton, boolean fromQuestion){
        if(fromQuestion){
            drawPageButtons.put(currentQuestion, new LinearLayout(aba));
        }

        LinearLayout.LayoutParams drawParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,90);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(70,70);
        btnParams.gravity= Gravity.CENTER_VERTICAL;

        LinearLayout pageLayout = new LinearLayout(aba);
        pageLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout drawButtonLayout = new LinearLayout(aba);
        drawButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        drawButtonLayout.setLayoutParams(drawParams);
        drawButtonLayout.setBackgroundColor(aba.getResources().getColor(R.color.lightlightgrey));

        Button bt = cc.createButton("",R.color.black,R.drawable.new_img,aba);
        bt.setLayoutParams(btnParams);

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrawPageButton(currentQuestion, true, nextButton, prevButton);
                getDrawView(currentQuestion);
                //showpicturemark();
            }
        });
        questionNumberLayout = cc.createQuestionTitle(("Question " + (currentQuestion + 1)
                + " - No image") ,30,aba);

        TextView txt = new TextView(aba);
        txt.setTextSize(20);
        txt.setText("No image created, maybe you want to make one");

        drawButtonLayout.addView(bt);
        pageLayout.addView(questionNumberLayout);
        pageLayout.addView(drawButtonLayout);
        pageLayout.addView(txt);
        foundationLayout.addView(pageLayout);
    }

    public void addDrawingButtons(final int currentQuestion, final Button nextButton, final Button prevButton){

        RelativeLayout.LayoutParams drawParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,90);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(70,70);
        btnParams.gravity= Gravity.CENTER_VERTICAL;
        LinearLayout.LayoutParams dropdownParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dropdownParams.gravity = Gravity.CENTER_VERTICAL;

        RelativeLayout.LayoutParams lleft = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,90);
        lleft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        LinearLayout left = new LinearLayout(aba);
        left.setLayoutParams(lleft);

        RelativeLayout.LayoutParams rright = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,90);
        rright.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        LinearLayout right = new LinearLayout(aba);
        right.setLayoutParams(rright);

        drawButtons = new RelativeLayout(aba);
        drawButtons.setBackgroundColor(aba.getResources().getColor(R.color.lightlightgrey));
        drawButtons.setLayoutParams(drawParams);

        Button btAdd = cc.createButton("",R.color.black,R.drawable.new_img,aba);
        btAdd.setLayoutParams(btnParams);

        btAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrawPageButton(currentQuestion, false, nextButton, prevButton);
                enableDrawButton(currentQuestion, nextButton, prevButton);
                showPictureMark(currentQuestion);
            }
        });

        Button btRemove = cc.createButton("",R.color.black,R.drawable.bin,aba);
        btRemove.setLayoutParams(btnParams);

        btRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(aba);
                builder.setTitle("Remove image");
                builder.setMessage("Are you sure you want to remove this image?");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        removeDrawPage(currentQuestion, nextButton, prevButton);
                        showPictureMark(currentQuestion);
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        final Button btEraser = cc.createButton("",R.color.black,R.drawable.eraser,aba);
        btEraser.setLayoutParams(btnParams);
        btEraser.setId(100+0);

        btEraser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dw.getErase()) {
                    btEraser.setBackgroundResource(R.drawable.eraser);
                    dw.setErase(false);
                } else {
                    btEraser.setBackgroundResource(R.drawable.eraser_clicked);
                    dw.setErase(true);
                }
            }
        });

        final Button btText = cc.createButton("",R.color.black,R.drawable.draw_text,aba);
        btText.setLayoutParams(btnParams);

        btText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(dw.getTextMode()){
                    dw.setTextMode(false);
                }
                else{
                    textToDraw();
                }
            }
        });

        Integer[] brushitems = new Integer[]{R.drawable.thin_line, R.drawable.medium_line,R.drawable.thick_line};
        Spinner brushSizeDropdown = createDropdown(brushitems, 1);
        brushSizeDropdown.setLayoutParams(dropdownParams);
        brushSizeDropdown.setOnItemSelectedListener(createOnSelectItem());

        Integer[] shapeItems = new Integer[]{R.drawable.free_draw,R.drawable.line,R.drawable.rectangle};
        Spinner shapeDropdown = createDropdown(shapeItems, 2);
        shapeDropdown.setLayoutParams(dropdownParams);
        shapeDropdown.setOnItemSelectedListener(createOnSelectItem());

        Integer[] colorItems = new Integer[]{R.drawable.color_black,R.drawable.color_red,R.drawable.color_blue};
        Spinner colorDropdown = createDropdown(colorItems, 3);
        colorDropdown.setLayoutParams(dropdownParams);
        colorDropdown.setOnItemSelectedListener(createOnSelectItem());

        left.addView(btAdd);
        left.addView(btRemove);

        right.addView(shapeDropdown);
        right.addView(btEraser);
        right.addView(brushSizeDropdown);
        right.addView(colorDropdown);
        right.addView(btText);

        drawButtons.addView(left);
        drawButtons.addView(right);
    }

    public Spinner createDropdown(Integer[] items, int id){
        Spinner spin = new Spinner(aba);
        spin.setId(2000+id);
        spin.setAdapter(new CustomSpinnerAdapter(aba,items));
        return spin;
    }

    public AdapterView.OnItemSelectedListener createOnSelectItem(){

         AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                switch (parent.getId()){
                    case 2000+1:
                        brushSizeDropdownChange(position);
                        break;
                    case 2000+2:
                        ShapeDropdownChange(position);
                        break;
                    case 2000+3:
                        ColorDropdownChange(position);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        return listener;
    }

    public void brushSizeDropdownChange(int position) {
        LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
        LinearLayout tt = (LinearLayout) t.getChildAt(0);
        SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
        switch (position) {
            case 0:
               ttt.setStrokeWidth(3);
                break;
            case 1:
                ttt.setStrokeWidth(10);
                break;
            case 2:
                ttt.setStrokeWidth(20);
                break;
        }
    }

    public void ShapeDropdownChange(int position){
        LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
        LinearLayout tt = (LinearLayout) t.getChildAt(0);
        SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
        switch (position) {
            case 0:
                ttt.setFreeDraw(true);
                break;
            case 1:
                ttt.setFreeDraw(false);
                ttt.setDrawShape("line");
                break;
            case 2:
                ttt.setFreeDraw(false);
                ttt.setDrawShape("rectangle");
                break;
        }
    }

    public void ColorDropdownChange(int position){
        LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
        LinearLayout tt = (LinearLayout) t.getChildAt(0);
        SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
        switch (position) {
            case 0:
                ttt.setPaintColor(Color.BLACK);
                break;
            case 1:
                ttt.setPaintColor(Color.RED);
                break;
            case 2:
                ttt.setPaintColor(Color.BLUE);
                break;
        }
    }

    public void resetDrawButtons(){

        for(int i=0;i<drawButtons.getChildCount();i++){
           if( drawButtons.getChildAt(i) instanceof Spinner){
               Spinner spin = (Spinner) drawButtons.getChildAt(i);
               spin.setSelection(0);
           }
        }
        Button btn = (Button) drawButtons.findViewById(100+0);
        btn.setBackgroundResource(R.drawable.eraser);
    }

    public void removeDrawPage(final int currentQuestion, final Button nextButton, final Button prevButton){

        drawPageButtons.get(currentQuestion).removeViewAt(currentDrawPage);

        mapBitsString.get(currentQuestion).remove(currentDrawPage);

        if(drawPageButtons.get(currentQuestion).getChildCount()<=0){
            foundationLayout.removeAllViews();
            makeStartDrawingButtons(currentQuestion, nextButton, prevButton,false);
        }
        else{
            if(currentDrawPage>0){
                currentDrawPage--;
            }
            getDrawView(currentQuestion);

            for(int i=currentDrawPage;i<drawPageButtons.get(currentQuestion).getChildCount();i++){
                Button btn = (Button) drawPageButtons.get(currentQuestion).getChildAt(i);
                btn.setId(drawPageButtonId + i);
                btn.setText(" Image " + (i + 1) + " ");
            }
            Button bt = (Button) drawPageButtons.get(currentQuestion).getChildAt(currentDrawPage);
            bt.setBackgroundResource(R.drawable.button_shape_clicked);
            selectedDrawBtn = bt;
        }
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

        enableDrawButton(currentQuestion, nextButton, prevButton);
        DrawAnimatedQuestionScrollView();
    }

    String text2add;
    public void textToDraw(){
        AlertDialog.Builder alert = new AlertDialog.Builder(aba);
        LinearLayout ll = new LinearLayout(aba);
        ll.setOrientation(LinearLayout.VERTICAL);
        final TextView text = new TextView(aba);
        text.setText("Ange texten");
        final EditText texten = new EditText(aba);
                //cc.createEditTextBox(1,0,8000,60,aba);
        //final EditText et = new EditText(aba);
        final TextView texts = new TextView(aba);
        texts.setText("Ange storlek");
        final Spinner textSize = new Spinner(aba);
        String[] textSizeItems = new String[]{"20","30","40"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (aba, android.R.layout.simple_spinner_item,textSizeItems);
        textSize.setAdapter(dataAdapter);

        ll.addView(text);
        ll.addView(texten);
        ll.addView(texts);
        ll.addView(textSize);
        alert.setView(ll);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                text2add = texten.getText().toString();
                LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
                LinearLayout tt = (LinearLayout) t.getChildAt(0);
                SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
                Typeface customFont = Typeface.createFromAsset(aba.getAssets(), "arial.ttf");



                ttt.setTextVariables(texten.getText().toString(),
                        Integer.valueOf(textSize.getSelectedItem().toString()),customFont);
                ttt.setTextMode(true);

            }
        });

        alert.show();


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
