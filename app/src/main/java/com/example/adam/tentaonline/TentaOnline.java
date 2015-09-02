package com.example.adam.tentaonline;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import se.ifilip.h2a.H2A;

public class TentaOnline extends ActionBarActivity implements AsyncResponse{

    //id:s for components
    final int answerInputId=1000;
    final int questionLayoutId=3000;
    final int questionButtonId=5001;
    final int errorMessageBoxId=7000;

    //buttons for navigating back and forth between pages
    Button nextButton;
    Button prevButton;

    //indicator for which question we are on
    int currentQuestion=-1;
    String examId, studentId;

    //stores the layout for the question/draw-page buttons
    LinearLayout questionButtonsLayout;

    //indicators for which button was last clicked
    Button selectedBtn;

    JSONArray examArray; //stores the questions of the exam
    LinearLayout foundationLayout; //the layout the shows the questions/draw-pages
    ArrayList<LinearLayout> pagesLayout = new ArrayList<>(); //stores the layout for each question
    LinearLayout pageButtonLayout; //used to display the question/draw-page buttons
    LinearLayout pageLayoutHeader; //stores the header-page
    Boolean currentlyDrawView=false; //used to check if we are in draw-page or question page
    LinearLayout questionNumberLayout; //stores the layout for the question header
    ComponentCreator cc = new ComponentCreator();
    DrawHandler dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tenta_online);

        examId = getIntent().getStringExtra("examId");
        studentId =getIntent().getStringExtra("studentId");

        foundationLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        pageButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
        HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);

        dh = new DrawHandler(this,cc,scroll,foundationLayout);

        final AndroidGet asyncGetExam = new AndroidGet();
        asyncGetExam.delegate = this;
        asyncGetExam.execute("android/get/get.php", examId);
    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){
        createExam(output);
    }

    /** Create the exam components  */
    public void createExam(final String jsonExamString){
        try{
            JSONArray examContentArr = new JSONArray(jsonExamString);
            JSONObject headerObject = new JSONObject(examContentArr.getString(1));
            JSONObject examObject = new JSONObject(examContentArr.getString(0));
            examArray = examObject.getJSONArray("Exam");

            nextButton = (Button) findViewById(R.id.nextArrow);
            prevButton = (Button) findViewById(R.id.prevArrow);
            nextButtonOnClick();
            prevButtonOnClick();

            questionButtonsLayout= new LinearLayout(this);
            createInfoPage(headerObject);
            createQuestionPages();
            enableNavigationButtons();
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    private void nextButtonOnClick(){
        nextButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
              if(currentlyDrawView){
                  dh.drawNextButtonOnClick(currentQuestion,nextButton,prevButton);
              }
              else{
                  foundationLayout.removeAllViews();
                  foundationLayout.addView(pagesLayout.get((currentQuestion + 1)));
                  currentQuestion++;

                  unselectQuestionButton();

                  Button currButton = (Button) pageButtonLayout.findViewById(
                          currentQuestion + questionButtonId);
                  currButton.setBackgroundResource(R.drawable.button_shape_clicked);
                  selectedBtn =currButton;

                  animatedQuestionScrollView();
                  enableNavigationButtons();

                  dh.showPictureMark(currentQuestion);
              }
            }
        });
    }

    private void prevButtonOnClick(){
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              if(currentlyDrawView){
                  dh.drawPrevButtonOnClick(currentQuestion,nextButton,prevButton);
              }
              else{
                  foundationLayout.removeAllViews();
                  Button currButton;
                  if(currentQuestion==0){
                      foundationLayout.addView(pageLayoutHeader);
                      currentQuestion--;
                      currButton = (Button) questionButtonsLayout.getChildAt(0);
                  }
                  else{
                      foundationLayout.addView(pagesLayout.get((currentQuestion - 1)));
                      currentQuestion--;
                      currButton = (Button) pageButtonLayout.findViewById(
                              currentQuestion + questionButtonId);
                  }
                  currButton.setBackgroundResource(R.drawable.button_shape_clicked);
                  unselectQuestionButton();
                  selectedBtn =currButton;

                  animatedQuestionScrollView();

                  enableNavigationButtons();

                  dh.showPictureMark(currentQuestion);
              }
            }
        });
    }

    private void animatedQuestionScrollView(){
        if(currentlyDrawView){
            dh.DrawAnimatedQuestionScrollView();
        }
        else{
            HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
            ObjectAnimator animator;
            animator=ObjectAnimator.ofInt(scroll, "scrollX", selectedBtn.getLeft());
            animator.setDuration(800);
            animator.start();
        }
    }

    private void createQuestionPages(){
        try {
            for (int i = 0; i < examArray.length(); i++) {
                JSONObject questionObject = examArray.getJSONObject(i);

                LinearLayout pageLayout = new LinearLayout(this);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                LLParams.setMargins(0,0,0,30);

                pageLayout.setPadding(30,30,30,0);

                pageLayout.setLayoutParams(LLParams);
                pageLayout.setOrientation(LinearLayout.VERTICAL);
                pageLayout.setBackgroundColor(getResources().getColor(R.color.lightblue));
                pageLayout.setId(questionLayoutId + (i+1) );


                final Button questionButton = cc.createButton("  Question " + (i + 1) + "  ",R.color.white,R.drawable.button_shape,this);
                questionButton.setId(questionButtonId + i);

                questionButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    foundationLayout.removeAllViews();
                    foundationLayout.addView(pagesLayout.get(
                            (questionButton.getId() - questionButtonId)));

                    unselectQuestionButton();
                    questionButton.setBackgroundResource(R.drawable.button_shape_clicked);

                    currentQuestion = (questionButton.getId() - questionButtonId);
                    selectedBtn =questionButton;
                    animatedQuestionScrollView();
                    enableNavigationButtons();

                    dh.showPictureMark(currentQuestion);
                    }
                });

                questionButtonsLayout.addView(questionButton);

                createQuestion(pageLayout, i, questionObject);
                LinearLayout fullquestion = new LinearLayout(this);
                fullquestion.setOrientation(LinearLayout.VERTICAL);
                fullquestion.setLayoutParams(LLParams);
                fullquestion.addView(questionNumberLayout);
                fullquestion.addView(pageLayout);
                pagesLayout.add(fullquestion);
            }
            pageButtonLayout.addView(questionButtonsLayout);

        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    public void createQuestionTitle(String question, int textSize){
        questionNumberLayout = cc.createQuestionTitle(question,textSize,this);
    }

    private void createQuestion(LinearLayout pageLayout, int i, JSONObject questionObject){
        try{
            createQuestionTitle(("Question " + (i + 1)), 30);
            addText(questionObject.getString("Question"), 25, false, pageLayout,true);

            if (questionObject.getString("Type").equals("radio")) {
                addRadio(getOptionsArray(questionObject), pageLayout, i);
            }
            if (questionObject.getString("Type").equals("checkbox")) {
                addCheckbox(getOptionsArray(questionObject), pageLayout, i);
            }
            if (questionObject.getString("Type").equals("text")) {
                addTextBox(pageLayout, i);
            }
            if (questionObject.getString("Type").equals("code")) {
                addCode(pageLayout, questionObject.getString("Language"),
                    questionObject.getString("Code"), questionObject.getString("Output"),
                    questionObject.getString("ShowOutput"), questionObject.getString("ShowCompile"),
                    questionObject.getString("HiddenCode"), i);
            }

        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    /* Removes next/prev buttons and calls functions to add them */
    public void enableNavigationButtons(){
        enableNavigateBackward();
        enableNavigateForward();
    }

    /* Adds the next button if we are not on the last page */
    public void enableNavigateForward(){
        if(currentQuestion<pagesLayout.size()-1){
            nextButton.setEnabled(true);
        }
        else{
            nextButton.setEnabled(false);
        }
    }

    /* Adds the prev button if we are not on the first page */
    public void enableNavigateBackward(){
        if(currentQuestion>=0){
            prevButton.setEnabled(true);
        }
        else{
            prevButton.setEnabled(false);
        }
    }

    public void unselectQuestionButton(){
        currentlyDrawView=false;
        if(selectedBtn.getId()==questionButtonId-1){
            selectedBtn.setBackgroundResource(R.drawable.info_button);
        }
        else {
            selectedBtn.setBackgroundResource(R.drawable.button_shape);
        }
    }

    /* Adds the header button to the bottom */
    public void createInfoPage(JSONObject headerObject){
        pageLayoutHeader = new LinearLayout(this);
        LinearLayout.LayoutParams LLParamsHeader = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pageLayoutHeader.setLayoutParams(LLParamsHeader);
        pageLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        pageLayoutHeader.setBackgroundColor(getResources().getColor(R.color.lightblue));
        pageLayoutHeader.setId(questionLayoutId+0);

        addHeader(headerObject,pageLayoutHeader);
        foundationLayout.addView(pageLayoutHeader);

        final Button headerButton = cc.createButton("Info",R.color.white,R.drawable.info_button_clicked,this);
        headerButton.setId(questionButtonId-1);
        selectedBtn = headerButton;
        questionButtonsLayout.addView(headerButton);

        headerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            foundationLayout.removeAllViews();
            foundationLayout.addView(pageLayoutHeader);
            unselectQuestionButton();
            headerButton.setBackgroundResource(R.drawable.info_button_clicked);
            selectedBtn =headerButton;
            animatedQuestionScrollView();
            currentQuestion=-1;
            enableNavigationButtons();
            dh.showPictureMark(currentQuestion);
            }
        });
    }

    /* Adds the header/info page to the exam */
    public void addHeader(JSONObject headerObject,LinearLayout pageLayout){
        try{
            addText(headerObject.getString("Title"),50,true,pageLayout,false);
            addText("Course Director: " + headerObject.getString("Director"),
                    20,true,pageLayout,false);
            addText("Help information: " + headerObject.getString("HelpInfo"),
                    20,true,pageLayout,false);
            addText("Grading levels: " + headerObject.getString("GradingInfo"),
                    20,true,pageLayout,false);
            addText(headerObject.getString("OtherInfo"),20,true,pageLayout,false);
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    /* Returns the options as an ArrayList */
    public ArrayList<String> getOptionsArray(JSONObject questionObject){
        ArrayList<String> options = null;
        try {
            JSONArray optionsArray = questionObject.getJSONArray("Options");
            options = new ArrayList<>();
            for (int i = 0; i < optionsArray.length(); i++) {
                options.add(optionsArray.getString(i));
            }
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
        return options;
    }

    /* Adds a textBox to the exam */
    public void addTextBox(LinearLayout pageLayout, int questionId){
        EditText edit = cc.createEditTextBox(questionId, answerInputId, 500, this);
        pageLayout.addView(edit);
    }

    /* Adds checkboxes with the answers */
    public void addCheckbox(ArrayList<String> options, LinearLayout pageLayout, int questionId){
        LinearLayout checkboxLayout = new LinearLayout(this);
        checkboxLayout.setOrientation(LinearLayout.VERTICAL);
        checkboxLayout.setId(answerInputId+questionId);

        for(final CheckBox checkbox : cc.createCheckboxes(options,this) ){
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (buttonView.isChecked()) {
                        checkbox.setBackgroundResource(R.drawable.answer_mark);
                    } else {
                        checkbox.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    }
                }
            });
            checkboxLayout.addView(checkbox);
        }
        pageLayout.addView(checkboxLayout);
    }

    /** Adds radio buttons with the answers */
    public void addRadio(ArrayList<String> options, LinearLayout pageLayout, int questionId){

        LinearLayout.LayoutParams radioParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        RadioGroup radioGroup =  cc.createRadiobuttons(options,radioParams,this);
        radioGroup.setId(answerInputId + questionId);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int count = group.getChildCount();
                for (int i=0;i<count;i++) {
                    View o = group.getChildAt(i);
                    o.setBackgroundColor(getResources().getColor(R.color.lightblue));
                }
                group.findViewById(checkedId).setBackgroundResource(R.drawable.answer_mark);
            }
        });
        pageLayout.addView(radioGroup);
    }

    public void addCode(final LinearLayout pageLayout, final String language,
                        final String startCode,final String Output, final String ShowOutput,
                        final String ShowCompile, final String HiddenStart, int questionId){

        Button compileButton = cc.createButton("Compile",R.color.white,R.drawable.compile_button,this);

        final EditText errorMessageBox = cc.createEditTextBox(questionId, errorMessageBoxId, 250, this);
        final EditText editCode = cc.createEditTextBox(questionId, answerInputId, 500, this);

        editCode.setText(startCode);

        editCode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_TAB && event.getAction() == KeyEvent.ACTION_DOWN) {
                    int start = Math.max(editCode.getSelectionStart(), 0);
                    int end = Math.max(editCode.getSelectionEnd(), 0);
                    editCode.getText().replace(Math.min(start, end), Math.max(start, end),
                            "\t", 0, "\t".length());
                    return true;
                }
                else{
                    Log.d("Testing keycode  ","" +keyCode);
                }
                return false;
            }
        });
        editCode.addTextChangedListener(new TextWatcher() {
            int _before = -1,_count = -1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                _before = before;
                _count = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_before != _count) {
                    int selectionStart = editCode.getSelectionStart();
                    editCode.setText( s.toString() );
                    editCode.setSelection(selectionStart);
                }

            }
        });
        Button resetCodeButton =  cc.createButton("Reset code",R.color.white,R.drawable.button_shape,this);
        resetCodeButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f));

        resetCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editCode.setText(startCode);
            }
        });
        compileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                errorMessageBox.setText("Loading, please wait.");
                compilationCheck(language, "1", editCode.getText().toString(),
                        Output, ShowOutput, ShowCompile, HiddenStart);
            }
        });
        compileButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,LinearLayout.LayoutParams.WRAP_CONTENT,3.0f));

        LinearLayout buttonsLayout = new LinearLayout(this);
        //buttonsLayout.setMinimumHeight(200);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,0,1.0f));

        buttonsLayout.addView(compileButton);
        buttonsLayout.addView(resetCodeButton);

        pageLayout.addView(editCode);
        pageLayout.addView(buttonsLayout);
        pageLayout.addView(errorMessageBox);
    }

    public void compilationCheck(String language, String taskId, String editCode, String Output,
                                 String ShowOutput, String ShowCompile, String HiddenStart){
        AndroidGet asyncGetCodeCorrection = new AndroidGet();
        asyncGetCodeCorrection.delegate = this;
        asyncGetCodeCorrection.execute("src/kattis/kattisClone.php", language,taskId, studentId,
                replaceTextInCode(HiddenStart,editCode),"Code",Output,ShowOutput,ShowCompile);
    }

    public void codeFinish(String output){
        LinearLayout layut = pagesLayout.get(currentQuestion);
        EditText errorMessageBox = (EditText) layut.findViewById
                (errorMessageBoxId + currentQuestion);
        errorMessageBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        errorMessageBox.setSingleLine(false);
        errorMessageBox.setText(output);
    }

    private String replaceTextInCode(String hiddenCode, String myCode){
        return hiddenCode.replaceAll("<sc-begin>(.*)</sc-end>",myCode);
    }

    /** Adds question to the mainLayout view */
    public void addText(String question, int textSize, boolean centerHorizontal,
                        LinearLayout pageLayout, boolean htmlCoded){

        TextView questionView = new TextView(this);
        if(centerHorizontal) {
            questionView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        questionView.setTextColor(Color.BLACK);
        if(htmlCoded){
            questionView.setText(H2A.convertHtml(question,this));
        }
        else{
            questionView.setText(question);
            questionView.setTextSize(textSize);
        }
        pageLayout.addView(questionView);


    }

    /** Creates the dropdown menu with post exam button etc */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tenta_online, menu);
        dh.setMenuItem(menu.findItem(R.id.pictureIndicator));
        return true;
    }

    /** Checks what answers was made on the questions */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.swapView && selectedBtn.getId()!=questionButtonId-1) {
            if(currentlyDrawView){
                dh.saveBitmap(currentQuestion);
                foundationLayout.removeAllViews();
                pageButtonLayout.removeAllViews();
                item.setTitle("Draw image");
                pageButtonLayout.addView(questionButtonsLayout);
                foundationLayout.addView(pagesLayout.get(
                        (selectedBtn.getId() - questionButtonId)));
                currentlyDrawView=false;
                enableNavigationButtons();
                dh.showPictureMark(currentQuestion);
            }
            else{
                foundationLayout.removeAllViews();
                pageButtonLayout.removeAllViews();
                item.setTitle("View question");

                dh.addDrawingButtons(currentQuestion,nextButton,prevButton);
                dh.setDrawPage(0);

                if(dh.getDrawPageButtons().indexOfKey(currentQuestion)<0 || dh.getDrawPageButtons().get(currentQuestion).getChildCount()<=0 ){
                    dh.makeStartDrawingButtons(currentQuestion,nextButton,prevButton,true);
                    pageButtonLayout.addView(dh.getDrawPageButtons().get(
                            (currentQuestion)));
                }
                else{
                    dh.enableDrawButton(currentQuestion,nextButton,prevButton);
                    dh.getDrawView(currentQuestion);
                    pageButtonLayout.addView(dh.getDrawPageButtons().get(
                            (currentQuestion)));

                }
                currentlyDrawView=true;
                dh.showPictureMark(currentQuestion);
            }
        }
        // Checks if submit exam was clicked
        if (id == R.id.submitExam) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure?");
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
                    if(currentlyDrawView){
                        dh.saveBitmap(currentQuestion);
                    }
                    submitExam();
                    dialog.dismiss();
                }

            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void submitExam(){
        JSONObject answersObj=null;
        JSONArray answersArr = new JSONArray();
        JSONObject answersFinalObj = new JSONObject();
        JSONArray imageArr;
        Bitmap emptyBitmap = Bitmap.createBitmap(800, 905, Bitmap.Config.ARGB_8888);
        try{
            for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);
                answersObj = new JSONObject();
                imageArr=new JSONArray();

                if(dh.getMapBitsString().indexOfKey(i)>=0){
                    for (String s : dh.getMapBitsString().get(i)){
                        if (!StringToBitMap(s).sameAs(emptyBitmap)) {
                            imageArr.put(s);
                        }
                    }
                }
                if(imageArr.length()>0){
                    answersObj.put("Image",imageArr);
                }
                else{
                    answersObj.put("Image","");
                }
                switch (questionObject.getString("Type")){

                    case "radio":
                        submitRadio(answersObj,answersArr,i);
                        break;

                    case "checkbox":
                        submitCheck(answersObj,answersArr,i);
                        break;

                    case "text":
                        submitText(answersObj,answersArr,i);
                        break;

                    case "code":
                        submitCode(answersObj,answersArr,i);
                        break;
                }
            }
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
        try{
            answersFinalObj.put("Student", studentId);
            answersFinalObj.put("Answers",answersArr);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
        AndroidPost asyncPostExam = new AndroidPost();
        asyncPostExam.delegate = this;
        asyncPostExam.execute("android/post/post.php",examId,
                studentId,answersFinalObj.toString());
    }

    private void submitRadio(JSONObject answersObj,JSONArray answersArr,int i){
        RadioGroup radioGroup=(RadioGroup) pagesLayout.get(i).findViewById(answerInputId+i);
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(
                radioGroup.getCheckedRadioButtonId());
        int optionNumber= radioGroup.indexOfChild(radioButton);
        try{
            answersObj.put("ID",i);
            if(optionNumber<0){answersObj.put("Answer","");}
            else{answersObj.put("Answer", "option" + optionNumber);}
            answersArr.put(answersObj);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
    }

    private void submitCheck(JSONObject answersObj,JSONArray answersArr, int i){
        LinearLayout checkboxLayout=(LinearLayout) pagesLayout.get(i).findViewById(answerInputId+i);
        JSONObject answerObj = new JSONObject();
        JSONArray ansOptArr = new JSONArray();
        for(int j=0;j<checkboxLayout.getChildCount();j++){
            CheckBox checkboxAtPosition=(CheckBox)checkboxLayout.getChildAt(j);
            if(checkboxAtPosition.isChecked()){
                ansOptArr.put("option" + checkboxAtPosition.getId());
            }
        }
        try{
            answersObj.put("ID",i);
            answersObj.put("Answer",ansOptArr);
            answersArr.put(answersObj);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
    }

    private void submitText(JSONObject answersObj, JSONArray answersArr, int i){
        EditText edit = (EditText) pagesLayout.get(i).findViewById(answerInputId+i);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edit.setSingleLine(false);
        try{
            answersObj.put("ID",i);
            answersObj.put("Answer", edit.getText());
            answersArr.put(answersObj);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
    }

    private void submitCode(JSONObject answersObj, JSONArray answersArr, int i){
        EditText codeEdit = (EditText) pagesLayout.get(i).findViewById(answerInputId+i);
        codeEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        codeEdit.setSingleLine(false);
        try{
            answersObj.put("ID",i);
            answersObj.put("Answer",codeEdit.getText().toString());
            answersArr.put(answersObj);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
    }

}




