package com.example.adam.tentaonline;


import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;


public class TentaOnline extends ActionBarActivity implements AsyncResponse{

    private Menu menu;

    final int answerInputId=1000;
    final int questionLayoutId=3000;
    final int questionButtonId=5000;
    final int errorMessageBoxId=7000;
    final int drawPageButtonId=9000;

    boolean editad=false;

    Button nextButton;
     Button prevButton;

    LinearLayout mainLayout;

    JSONArray examArray;

    int questionId=0;
    int currentQuestion=-1;
    String courseCode,anonymityCode;
    LinearLayout myTestLayout;
    ArrayList<LinearLayout> pagesLayout = new ArrayList<>();

    LinearLayout questionButtonsLayout;
    LinearLayout questionButtonLayout;

    SparseArray<LinearLayout> drawPageButtons = new SparseArray<>();

    int currentDrawPage=0;

    Bitmap canvasBitmap;
    Canvas c;
    SimpleDrawView dw;

    SparseArray<ArrayList<String>> mapBitsString = new SparseArray<>();
    ArrayList<String> bpString = new ArrayList<>();


    LinearLayout pageLayoutHeader;
    Button lastClickedButton;
    Boolean currentlyDrawView=false;
    LinearLayout questionnumberLayout;

    PrettifyHighlighter highlighter = new PrettifyHighlighter();
    String highlighted;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenta_online);


        courseCode = getIntent().getStringExtra("courseCode");
        anonymityCode =getIntent().getStringExtra("anonymityCode");

        myTestLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        questionButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.weight=1;

        mainLayout=new LinearLayout(this);
        mainLayout.setLayoutParams(LLParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(getResources().getColor(R.color.lightblue));
        AndroidGet asyncGetExam = new AndroidGet();
        // Sets delegate variable in AndroidGet.java to this
        asyncGetExam.delegate = this;
        // Execute AndroidGet.java
        asyncGetExam.execute("android/get/get.php", courseCode);

    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){
        createExam(output);
        //this you will received result fired from async class of onPostExecute(result) method.
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
            nextButtonOnClick(nextButton,prevButton);
            prevButtonOnClick(nextButton,prevButton);

            questionButtonsLayout= new LinearLayout(this);
            addHeaderButton(headerObject,nextButton,prevButton);
            createQuestionPages(nextButton,prevButton);
            addNextPrevButton();



        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }



    private void nextButtonOnClick(final Button nextButton, final Button prevButton)
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
              if(currentlyDrawView){
                  drawNextButtonOnClick();
              }
              else{
                  myTestLayout.removeAllViews();
                  myTestLayout.addView(pagesLayout.get((currentQuestion + 1)));
                  currentQuestion++;
                  //
                  unclickButton();

                  Button currButton = (Button) questionButtonLayout.findViewById(currentQuestion + questionButtonId);
                  currButton.setBackgroundResource(R.drawable.button_shape_clicked);
                  lastClickedButton=currButton;

                  animatedQuestionScrollView();

                  addNextPrevButton();

                  //hideDrawButton();
                  currentDrawPage=0;
                  showPictureMark();
              }
            }
        });
    }


    private void prevButtonOnClick(final Button nextButton, final Button prevButton)
    {
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

              if(currentlyDrawView){
                  drawPrevButtonOnClick();
              }
              else{
                  myTestLayout.removeAllViews();
                  Log.d("Curr = ","" + currentQuestion);
                  Button currButton;
                  if(currentQuestion==0){
                      myTestLayout.addView(pageLayoutHeader);
                      currentQuestion--;

                      LinearLayout kk = questionButtonLayout;

                      currButton = (Button) questionButtonsLayout.getChildAt(0);

                  }
                  else{
                      myTestLayout.addView(pagesLayout.get((currentQuestion-1)));
                      currentQuestion--;

                      currButton = (Button) questionButtonLayout.findViewById(currentQuestion + questionButtonId);


                  }
                  currButton.setBackgroundResource(R.drawable.button_shape_clicked);
                  unclickButton();
                  lastClickedButton=currButton;

                  animatedQuestionScrollView();

                  addNextPrevButton();

                  //hideDrawButton();
                  currentDrawPage=0;
                  showPictureMark();
              }

            }
        });
    }

//drawPage next and prev
    private void drawPrevButtonOnClick(){
                if(currentDrawPage!=0){
                    saveBitmap();
                    currentDrawPage--;
                    switchToDrawPage();
                }
                enableDrawButton();
    }

    private void drawNextButtonOnClick(){
        Log.d("KLICKED","sda");
                saveBitmap();
                currentDrawPage++;
                switchToDrawPage();
                enableDrawButton();
                addDrawPageButton();
    }

    private void enableDrawButton(){

        nextButton.setEnabled(true);
        prevButton.setEnabled(true);

        if(currentDrawPage==0){
            prevButton.setEnabled(false);
        }

    }

    private void animatedQuestionScrollView(){
        HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        ObjectAnimator animator=ObjectAnimator.ofInt(scroll, "scrollX" ,lastClickedButton.getLeft() );
        animator.setDuration(800);
        animator.start();

    }

    private void createQuestionPages(final Button nextButton, final Button prevButton){
        try {
            for (int i = 0; i < examArray.length(); i++) {

                JSONObject questionObject = examArray.getJSONObject(i);

                LinearLayout pageLayout = new LinearLayout(this);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                pageLayout.setPadding(30,30,30,0);

                pageLayout.setLayoutParams(LLParams);
                pageLayout.setOrientation(LinearLayout.VERTICAL);
                pageLayout.setBackgroundColor(getResources().getColor(R.color.lightblue));
                pageLayout.setId(questionLayoutId + (i+1) );

                final Button questionButton = new Button(this);
                questionButton.setId(questionButtonId + i);
                questionButton.setText("  Question " + (i + 1) + "  ");
                questionButton.setBackgroundResource(R.drawable.button_shape);
                questionButton.setTextColor(Color.WHITE);


                questionButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myTestLayout.removeAllViews();
                        myTestLayout.addView(pagesLayout.get((questionButton.getId() - questionButtonId)));

                        unclickButton();
                        questionButton.setBackgroundResource(R.drawable.button_shape_clicked);

                        currentQuestion = (questionButton.getId() - questionButtonId);
                        lastClickedButton=questionButton;
                        animatedQuestionScrollView();
                        addNextPrevButton();

                        //hideDrawButton();
                        currentDrawPage=0;

                        showPictureMark();
                    }
                });

                //nn

                questionButtonsLayout.addView(questionButton);

                //questionButtonLayout.addView(questionButton);

                createQuestion(pageLayout, i, questionObject);
                LinearLayout fullquestion = new LinearLayout(this);
                fullquestion.setOrientation(LinearLayout.VERTICAL);
                fullquestion.setLayoutParams(LLParams);
                fullquestion.addView(questionnumberLayout);
                fullquestion.addView(pageLayout);
                pagesLayout.add(fullquestion);
                //

            }
            questionButtonLayout.addView(questionButtonsLayout);
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }



    public SimpleDrawView createDrawPage(Bitmap b, Canvas c){

        dw=new SimpleDrawView(this,b,c);
        RelativeLayout.LayoutParams kte = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        dw.setLayoutParams(kte);
        return dw;
    }

    public void addQuestionnumberText(String question, int textSize){

        TextView questionView = new TextView(this);
        questionnumberLayout = new LinearLayout(this);
        LinearLayout.LayoutParams tees = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        //tees.gravity=Gravity.CENTER_HORIZONTAL;
        questionView.setLayoutParams(tees);
        questionView.setGravity(Gravity.CENTER_HORIZONTAL);
        questionView.setText(" " + question + " ");
        questionView.setTextColor(Color.WHITE);
        questionView.setTextSize(textSize);
        questionView.setBackgroundColor(getResources().getColor(R.color.darkblue));


        questionnumberLayout.addView(questionView);
    }

    private void createQuestion(LinearLayout pageLayout, int i, JSONObject questionObject){

        try{
            addQuestionnumberText(("Question " + (i + 1)), 30);
            addText(questionObject.getString("Question"), 25, false, pageLayout);

            if (questionObject.getString("Type").equals("radio")) {
                addRadio(getOptionsArray(questionObject), pageLayout);
            }
            if (questionObject.getString("Type").equals("checkbox")) {
                addCheckbox(getOptionsArray(questionObject), pageLayout);
            }
            if (questionObject.getString("Type").equals("text")) {
                addTextbox(pageLayout);
            }
            if (questionObject.getString("Type").equals("code")) {
                addCode(pageLayout, questionObject.getString("Language"), questionObject.getString("Code"), questionObject.getString("Output"), questionObject.getString("ShowOutput"), questionObject.getString("ShowCompile"), questionObject.getString("HiddenCode"));
            }
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    /* Removes next/prev buttons and calls functions to add them */
    public void addNextPrevButton(){
        //buttonLayout.removeAllViews();
        nextButton.setEnabled(false);
        prevButton.setEnabled(false);
        addPrevButton(prevButton);
        addNextButton(nextButton);
    }

    /* Adds the next button if we are not on the last page */
    public void addNextButton(Button nextButton){
        if(currentQuestion<pagesLayout.size()-1){
          nextButton.setEnabled(true);

        }
    }

    /* Adds the prev button if we are not on the first page */
    public void addPrevButton(Button prevButton){
        if(currentQuestion>=0){
            prevButton.setEnabled(true);

        }
    }

    public void unclickButton(){

        currentlyDrawView=false;

        if(lastClickedButton.getId()==-1){
            lastClickedButton.setBackgroundResource(R.drawable.info_button);
        }
        else {
            lastClickedButton.setBackgroundResource(R.drawable.button_shape);
        }

    }

    /* Adds the header button to the bottom */
    public void addHeaderButton(JSONObject headerObject,final Button nextButton,final Button prevButton){

        pageLayoutHeader = new LinearLayout(this);
        LinearLayout.LayoutParams LLParamsHeader = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pageLayoutHeader.setLayoutParams(LLParamsHeader);
        pageLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        pageLayoutHeader.setBackgroundColor(getResources().getColor(R.color.lightblue));
        pageLayoutHeader.setId(questionLayoutId+0);

        addHeader(headerObject,pageLayoutHeader);
        myTestLayout.addView(pageLayoutHeader);

        final Button headerButton = new Button(this);
        headerButton.setText("Info");
        headerButton.setId(-1+0);
        //headerButton.setBackgroundResource(R.drawable.info_button);
        headerButton.setTextColor(getResources().getColor(R.color.white));

        headerButton.setBackgroundResource(R.drawable.info_button_clicked);
        lastClickedButton=headerButton;

        questionButtonsLayout.addView(headerButton);

        headerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                myTestLayout.addView(pageLayoutHeader);
                unclickButton();
                headerButton.setBackgroundResource(R.drawable.info_button_clicked);
                lastClickedButton=headerButton;
                animatedQuestionScrollView();
                currentQuestion=-1;
                addNextPrevButton();
                showPictureMark();
            }
        });
    }

    /* Adds the header/info page to the exam */
    public void addHeader(JSONObject headerObject,LinearLayout pageLayout){

        try{
            addText(headerObject.getString("Title"),50,true,pageLayout);
            addText("Course Director: " + headerObject.getString("Director"),20,true,pageLayout);
            addText("Help information: " + headerObject.getString("HelpInfo"),20,true,pageLayout);
            addText("Grading levels: " + headerObject.getString("GradingInfo"),20,true,pageLayout);
            addText(headerObject.getString("OtherInfo"),20,true,pageLayout);
            addText("__________________________________________",20,true,pageLayout);



            // Adds image to screen
          /*  ImageView imag = new ImageView(this);
                Picasso.with(this)
                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
                    .into(imag);
            pageLayout.addView(imag); */

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
    public void addTextbox(LinearLayout pageLayout){
        EditText edit = new EditText(this);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edit.setSingleLine(false);
        edit.setGravity(Gravity.TOP | Gravity.LEFT);
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        edit.setLayoutParams(lparams);
        edit.setId(answerInputId + questionId++);
        edit.setMinimumWidth(100);
        edit.setBackgroundResource(R.drawable.back_border);
        pageLayout.addView(edit);
    }

    /* Adds checkboxes with the answers */
    public void addCheckbox(ArrayList<String> options, LinearLayout pageLayout){

        LinearLayout checkboxLayout = new LinearLayout(this);
        checkboxLayout.setOrientation(LinearLayout.VERTICAL);
        checkboxLayout.setId(answerInputId+questionId++);

        for(int i=0;i<options.size();i++){
            final CheckBox checkBox = new CheckBox(this);
            checkBox.setText(options.get(i));
            checkBox.setId(0 + i);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (buttonView.isChecked()) {
                        checkBox.setBackgroundResource(R.drawable.answer_mark);
                    } else {
                        checkBox.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    }
                }
            });

            checkboxLayout.addView(checkBox);
        }
        pageLayout.addView(checkboxLayout);
    }


    /** Adds radio buttons with the answers */
    public void addRadio(ArrayList<String> options, LinearLayout pageLayout){

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setId(answerInputId + questionId++);

        LinearLayout.LayoutParams testparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0;i<options.size();i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(options.get(i));
            radioButton.setTextColor(getResources().getColor(R.color.black));
            //radioButton.setButtonDrawable(R.drawable.btn_radio_color);

            radioButton.setLayoutParams(testparams);


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

                    // checkedId is the RadioButton selected
                }
            });

            //radioButton.setBackgroundColor(getResources().getColor(R.color.bluegreen));

            radioGroup.addView(radioButton);
        }


        pageLayout.addView(radioGroup);
    }

    private String languageToExtension(String language){

        if(language.equals("c++")){
            return "cpp";
        }
        else{
            return language;
        }

    }

    public void addCode(final LinearLayout pageLayout, final String language, final String startCode, final String Output, final String ShowOutput, final String ShowCompile, final String HiddenStart){

        Button compileButton= new Button(this);
        compileButton.setText("Compile");
        compileButton.setTextColor(getResources().getColor(R.color.white));
        compileButton.setBackgroundColor( getResources().getColor(R.color.darkgrey) );

        final EditText errorMessageBox = new EditText(this);
        errorMessageBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        errorMessageBox.setSingleLine(false);
        errorMessageBox.setGravity(Gravity.TOP | Gravity.LEFT);
        errorMessageBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,3.0f));
        errorMessageBox.setId(errorMessageBoxId + questionId);

        errorMessageBox.setBackgroundResource(R.drawable.back_border);

        final EditText editCode = new EditText(this);
        editCode.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editCode.setSingleLine(false);
        editCode.setGravity(Gravity.TOP | Gravity.LEFT);

        editCode.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,7.0f));
        editCode.setId(answerInputId + questionId++);

        editCode.setGravity(Gravity.TOP);
        editCode.setBackgroundResource(R.drawable.back_border);



        highlighted = highlighter.highlight(languageToExtension(language), startCode);
        editCode.setText(Html.fromHtml(highlighted));

        editCode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_TAB && event.getAction() == KeyEvent.ACTION_DOWN ){       //Tab klickad
                    int start = Math.max(editCode.getSelectionStart(), 0);
                    int end = Math.max(editCode.getSelectionEnd(), 0);
                    editCode.getText().replace(Math.min(start, end), Math.max(start, end),
                            "\t" , 0, "\t".length());
                    return true;
                }

                return false;
            }
        });

        editCode.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                    highlighted = highlighter.highlight(languageToExtension(language), s.toString());

                if(editad){
                    editad=false;
                }else{
                    editad=true;
                    int x = editCode.getSelectionStart();
                    editCode.setText( Html.fromHtml(highlighted));
                    editCode.setSelection(x);
                }
            }
        });

        Button resetCodeButton = new Button(this);
        resetCodeButton.setText("Reset code");
        resetCodeButton.setTextColor(getResources().getColor(R.color.white));
        resetCodeButton.setBackgroundResource(R.drawable.button_shape);
        resetCodeButton.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f));

        resetCodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editCode.setText(startCode);
            }
        });

        compileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                errorMessageBox.setText("Loading, please wait.");
                compilationCheck(language, "1", editCode.getText().toString(), Output, ShowOutput, ShowCompile, HiddenStart);
            }
        });

        compileButton.setLayoutParams(new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,3.0f));

        LinearLayout buttonsLayout = new LinearLayout(this);
        buttonsLayout.setMinimumHeight(200);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1.0f));

        buttonsLayout.addView(compileButton);
        buttonsLayout.addView(resetCodeButton);

        pageLayout.addView(editCode);
        pageLayout.addView(buttonsLayout);
        pageLayout.addView(errorMessageBox);
    }




    public void compilationCheck(String language, String taskId, String editCode, String Output, String ShowOutput, String ShowCompile, String HiddenStart){
        AndroidGet asyncGetCodeCorrection = new AndroidGet();
        asyncGetCodeCorrection.delegate = this;
        asyncGetCodeCorrection.execute("src/kattis/kattisClone.php", language,taskId,anonymityCode,replaceTextInCode(HiddenStart,editCode),"Code",Output,ShowOutput,ShowCompile);
    }

    public void codeFinish(String output){
        LinearLayout layut = pagesLayout.get(currentQuestion);
        EditText errorMessageBox = (EditText) layut.findViewById(errorMessageBoxId + currentQuestion);
        errorMessageBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        errorMessageBox.setSingleLine(false);
        errorMessageBox.setText(output);

        Log.d("Coddsasesadtion resul", "CCC    " + output);
    }


    private String replaceTextInCode(String hiddenCode, String myCode){
        return hiddenCode.replaceAll( getString(R.string.regexCodePattern),myCode);
    }

    /** Adds question to the mainLayout view */
    public void addText(String question, int textSize, boolean centerHorizontal, LinearLayout pageLayout){

        TextView questionView = new TextView(this);
        if(centerHorizontal)
            questionView.setGravity(Gravity.CENTER_HORIZONTAL);

        questionView.setTextColor(Color.BLACK);
        questionView.setText(question);
        questionView.setTextSize(textSize);
        pageLayout.addView(questionView);
    }

    private void addDrawPageButton(){

        int pageindex=(lastClickedButton.getId() - questionButtonId);
        int inc=0;

        if(drawPageButtons.indexOfKey(pageindex)>=0){
            inc=drawPageButtons.get(pageindex).getChildCount();
        }

        if(drawPageButtons.indexOfKey(pageindex)<0 || drawPageButtons.get(pageindex).getChildCount()<=currentDrawPage){
            final Button b = new Button(this);

            b.setId(drawPageButtonId + inc );
            b.setText("Image " + (inc + 1));


            //
            b.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                     saveBitmap();
                     currentDrawPage=(b.getId()-drawPageButtonId);
                     switchToDrawPage();

                 }
            });

            LinearLayout butt;

            if(drawPageButtons.indexOfKey(pageindex)>=0){
                butt = drawPageButtons.get(pageindex);
            }
            else{
                butt = new LinearLayout(this);
            }
            butt.addView(b);

            drawPageButtons.put(pageindex,butt);
            }

    }

    private void switchToDrawPage(){

        int pageindex=(lastClickedButton.getId() - questionButtonId);



        if(mapBitsString.indexOfKey(pageindex)>=0  && mapBitsString.get(pageindex).size()>currentDrawPage){

            canvasBitmap=StringToBitMap(mapBitsString.get(pageindex).get(currentDrawPage));
        }
        else{
            canvasBitmap = Bitmap.createBitmap(800, 905, Bitmap.Config.ARGB_8888 );

        }
        canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);

        c=new Canvas(canvasBitmap);
        //
        LinearLayout drawLayout = new LinearLayout(this);
        drawLayout.setOrientation(LinearLayout.VERTICAL);
        addQuestionnumberText(("Question " + (currentQuestion+1) + " - image " + (currentDrawPage+1)),30);
        drawLayout.addView(questionnumberLayout);

        drawLayout.addView(createDrawPage(canvasBitmap,c));
        LinearLayout drawpage = new LinearLayout(this);
        drawpage.addView(drawLayout);

        myTestLayout.removeAllViews();
        myTestLayout.addView(drawpage);
    }

    private void saveBitmap(){

        int pageindex=(lastClickedButton.getId() - questionButtonId);

        if(mapBitsString.indexOfKey(pageindex)<0){
            bpString=new ArrayList<String>();
        }
        else{
            bpString=mapBitsString.get(pageindex);
        }

        if(bpString.size()>currentDrawPage){

            bpString.set(currentDrawPage,BitMapToString(dw.getBit()));
        }
        else{
            bpString.add(currentDrawPage,BitMapToString(dw.getBit()));
        }
        mapBitsString.put(pageindex,bpString);

    }

    private void showPictureMark(){
        MenuItem pictureIndicator = menu.findItem(R.id.pictureIndicator);
        if(mapBitsString.indexOfKey(currentQuestion)>=0){
            pictureIndicator.setVisible(true);
            pictureIndicator.setTitle("x " + mapBitsString.get(currentQuestion).size());
        }
        else{
            pictureIndicator.setVisible(false);
        }

    }

    /** Creates the dropdown menu with post exam button etc */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tenta_online, menu);
        this.menu=menu;
        return true;
    }

    /** Checks what answers was made on the questions */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.swapView && lastClickedButton.getId()!=-1) {



            myTestLayout.removeAllViews();
            questionButtonLayout.removeAllViews();

            if(currentlyDrawView){
                item.setTitle("Draw image");
                questionButtonLayout.addView(questionButtonsLayout);
                //hideDrawButton();
                myTestLayout.addView(pagesLayout.get((lastClickedButton.getId() - questionButtonId)));
                currentlyDrawView=false;
                saveBitmap();
                addNextPrevButton();
                showPictureMark();
            }
            else{
                item.setTitle("View question");
                addDrawPageButton();
                questionButtonLayout.addView(drawPageButtons.get((lastClickedButton.getId() - questionButtonId)));

                enableDrawButton();
                switchToDrawPage();
                currentlyDrawView=true;
                showPictureMark();


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
                    saveBitmap();
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

    public String BitMapToString(Bitmap bitmap) {


        String result="";
        if(bitmap!=null){

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
            byte[] b = baos.toByteArray();
            //Log.d("strolek" ,"" + b.length);

            result = Base64.encodeToString(b, Base64.DEFAULT);

        }
        return result;


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

            /////
                //bitMapArr.get(fråga).get(drasida);

            if(mapBitsString.indexOfKey(i)>=0){
                for (String s : mapBitsString.get(i)){
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


                //Bitmap bmp = SimpleDrawView.getBit();
            }
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }

        try{
            answersFinalObj.put("Student",anonymityCode);
            answersFinalObj.put("Answers",answersArr);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

        //Log.d("JSONHole", answersFinalObj.toString());

        AndroidPost asyncPostExam = new AndroidPost();
        asyncPostExam.delegate = this;

        asyncPostExam.execute("android/post/post.php",courseCode,anonymityCode,answersFinalObj.toString());

        Log.d("TENTAN","SUBMITTAD");
    }

    private void submitRadio(JSONObject answersObj,JSONArray answersArr,int i){
        RadioGroup radioGroup=(RadioGroup) pagesLayout.get(i).findViewById(answerInputId+i);
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        //answersObj = new JSONObject();
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
        //answersObj = new JSONObject();
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
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);            //SKA TA BORT STAVNINGSKONTROLL
        edit.setSingleLine(false);
        try{
            //answersObj = new JSONObject();
            answersObj.put("ID",i);
            answersObj.put("Answer", edit.getText());
            answersArr.put(answersObj);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

    }

    private void submitCode(JSONObject answersObj, JSONArray answersArr, int i){
        EditText codeEdit = (EditText) pagesLayout.get(i).findViewById(answerInputId+i);
        codeEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        codeEdit.setSingleLine(false);
        try{
            //answersObj = new JSONObject();
            answersObj.put("ID",i);
            answersObj.put("Answer", codeEdit.getText());
            answersArr.put(answersObj);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

    }

}




