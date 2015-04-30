package com.example.adam.tentaonline;

import android.animation.ObjectAnimator;
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
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URLEncoder;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TentaOnline extends ActionBarActivity implements AsyncResponse{

    //id:s for components
    final int answerInputId=1000;
    final int questionLayoutId=3000;
    final int questionButtonId=5001;
    final int errorMessageBoxId=7000;
    //final int drawPageButtonId=9000;

    //buttons for navigating back and forth between pages
    Button nextButton;
    Button prevButton;

    //indicator for which question we are on
    int currentQuestion=-1;
    String examId, studentId;

    //stores the layout for the question/draw-page buttons
    LinearLayout questionButtonsLayout;
    //SparseArray<LinearLayout> drawPageButtons = new SparseArray<>();

    //used to create a draw-page
    //Bitmap canvasBitmap;
    //Canvas c;
    //SimpleDrawView dw;

    //indicators for which button was last clicked
    Button selectedBtn;
    //Button selectedDrawBtn;

    private Menu menu; //used to acess menu items
    JSONArray examArray; //stores the questions of the exam
    LinearLayout foundationLayout; //the layout the shows the questions/draw-pages
    ArrayList<LinearLayout> pagesLayout = new ArrayList<>(); //stores the layout for each question
    LinearLayout pageButtonLayout; //used to display the question/draw-page buttons
    //int currentDrawPage=0; //indicator for which draw-page we are on
    //SparseArray<ArrayList<String>> mapBitsString = new SparseArray<>(); //stores the pictures drawn as strings
    LinearLayout pageLayoutHeader; //stores the header-page
    Boolean currentlyDrawView=false; //used to check if we are in draw-page or question page
    LinearLayout questionNumberLayout; //stores the layout for the question header
   // LinearLayout drawButtons; //stores the buttons for the draw page e.g change to line draw
    ComponentCreator cc = new ComponentCreator();
    DrawHandler dh;

    String texten="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tenta_online);

        examId = getIntent().getStringExtra("examId");
        studentId =getIntent().getStringExtra("studentId");
        String exam =getIntent().getStringExtra("exam");

        foundationLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        pageButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
        HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScroll);

        dh = new DrawHandler(this,cc,scroll,foundationLayout);

        createExam(exam);
    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){}

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

                  showPictureMark();
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
                  Log.d("curr ", "" + currentQuestion);
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

                  showPictureMark();
              }
            }
        });
    }
/*
    //drawPage next and prev
    private void drawPrevButtonOnClick(){
        if(currentDrawPage!=0){
            saveBitmap();
            currentDrawPage--;
            switchToDrawPage();

            unselectDrawButton();
            LinearLayout buttonLayout = drawPageButtons.get(
                    (selectedBtn.getId() - questionButtonId));
            Button b = (Button) buttonLayout.findViewById(drawPageButtonId + currentDrawPage);
            b.setBackgroundResource(R.drawable.button_shape_clicked);
            selectedDrawBtn =b;
            animatedQuestionScrollView();
        }
        enableDrawButton();
    }
*/
    /*
    private void drawNextButtonOnClick(){
        saveBitmap();
        currentDrawPage++;
        switchToDrawPage();
        enableDrawButton();

        unselectDrawButton();
        LinearLayout buttonLayout = drawPageButtons.get(
                (selectedBtn.getId() - questionButtonId));
        Button b = (Button) buttonLayout.findViewById(drawPageButtonId + currentDrawPage);
        b.setBackgroundResource(R.drawable.button_shape_clicked);
        selectedDrawBtn =b;
        animatedQuestionScrollView();
    }
*/
/*
    private void enableDrawButton(){
        nextButton.setEnabled(true);
        prevButton.setEnabled(true);
        if(currentDrawPage==0){
            prevButton.setEnabled(false);
        }
        if(drawPageButtons.indexOfKey(currentQuestion)<0 ||  drawPageButtons.get(currentQuestion).getChildCount()<=currentDrawPage+1){
            nextButton.setEnabled(false);
        }
    }
*/
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

                    showPictureMark();
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
/*
    public SimpleDrawView createDrawPage(Bitmap b, Canvas c){
        dw=new SimpleDrawView(this,b,c);
        RelativeLayout.LayoutParams kte = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        dw.setLayoutParams(kte);
        return dw;
    }
*/
    public void createQuestionTitle(String question, int textSize){
        questionNumberLayout = cc.createQuestionTitle(question,textSize,this);
        //questionNumberLayout.addView(cc.createQuestionTitle(question,textSize,this));
    }

    private String makeHtmlText(String text){
        return text.replace(" ","&nbsp;").replace("\n","<br>").replace("\t","&emsp;");
    }

    private void createQuestion(LinearLayout pageLayout, int i, JSONObject questionObject){
        try{
            createQuestionTitle(("Question " + (i + 1)), 30);
            addText(makeHtmlText(questionObject.getString("Question")), 25, false, pageLayout,true);

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
            showPictureMark();
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
        EditText edit = cc.createEditTextBox(questionId, 0, answerInputId, LinearLayout.LayoutParams.MATCH_PARENT, this);
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

    private String languageToExtension(String language){
        if(language.equals("c++")){
            return "cpp";
        }
        else{
            return language;
        }
    }

    public void addCode(final LinearLayout pageLayout, final String language,
                        final String startCode,final String Output, final String ShowOutput,
                        final String ShowCompile, final String HiddenStart, int questionId){

        Button compileButton = cc.createButton("Compile",R.color.white,R.drawable.compile_button,this);

        final EditText errorMessageBox = cc.createEditTextBox(questionId, 3.0f, errorMessageBoxId, 0, this);
        final EditText editCode = cc.createEditTextBox(questionId, 7.0f, answerInputId, 0, this);

        final PrettifyHighlighter highlighter = new PrettifyHighlighter();

        //editCode.setText(Html.fromHtml(
         //       highlighter.highlight(languageToExtension(language), startCode)));

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
               /* if (_before != _count){
                    char ch=  s.charAt(start + count - 1);
                    Log.d("tecknet ",""+ch);
                    texten+=ch;
                    Log.d("texten ", "" +texten);
                }*/

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (_before != _count) {
                    int selectionStart = editCode.getSelectionStart();
                    //Spanned ht = Html.fromHtml(highlighter.highlight(languageToExtension(language),s.toString()));
                    //String sht = ht.toString();
                    //String katt =  ht.replace("&nbsp;"," ").replace("<br>","\n").replace("&emsp;","\t");
                    //String katt = sht.replaceAll("%20"," ").replaceAll("%0D%0A","\n");
                    /*try{

                       String name = new String(sht.getBytes("ISO-8859-1"), "UTF-8");

                    Log.d("Stingeförst ",""+name);
                    Log.d("Stingen ","" + URLEncoder.encode(sht,"UTF-8") );
                    }catch (Exception e){
                        //%C2%A0 space
                        //%0A enter
                        //%E2%80%83 tab
                    }*/

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
        buttonsLayout.setMinimumHeight(200);
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
        return hiddenCode.replaceAll( getString(R.string.regexCodePattern),myCode);
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
            questionView.setText(Html.fromHtml(question));
        }
        else{
            questionView.setText(question);
        }
        questionView.setTextSize(textSize);
        pageLayout.addView(questionView);
    }
/*
    private void unselectDrawButton(){
        if(selectedDrawBtn !=null){
            selectedDrawBtn.setBackgroundResource(R.drawable.button_shape);
        }
    }
*/
    /*
    private void addDrawPageButton(boolean first){
        int inc=0;
        if(drawPageButtons.indexOfKey(currentQuestion)>=0){
            inc=drawPageButtons.get(currentQuestion).getChildCount();
        }

            final Button b = cc.createButton("Image " + (inc + 1),R.color.white,R.drawable.button_shape,this);
            b.setId(drawPageButtonId + inc );

        if(first){
            b.setBackgroundResource(R.drawable.button_shape_clicked);
            unselectDrawButton();
            selectedDrawBtn =b;
        }


            b.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                 unselectDrawButton();
                 saveBitmap();
                 currentDrawPage=(b.getId()-drawPageButtonId);
                 switchToDrawPage();
                 b.setBackgroundResource(R.drawable.button_shape_clicked);
                 selectedDrawBtn = b;
                 animatedQuestionScrollView();
                 }
            });
            LinearLayout butt;
            if(drawPageButtons.indexOfKey(currentQuestion)>=0){
                butt = drawPageButtons.get(currentQuestion);
            }
            else{
                butt = new LinearLayout(this);
            }
            butt.addView(b);
            drawPageButtons.put(currentQuestion,butt);
        if(mapBitsString.indexOfKey(currentQuestion)>=0){
            createAndSavePicture();
        }

    }
*/
    /*
    public void createAndSavePicture(){

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
*/
    /*
    private void switchToDrawPage(){

        if(mapBitsString.indexOfKey(currentQuestion)>=0  &&
                mapBitsString.get(currentQuestion).size()>currentDrawPage){
            canvasBitmap=StringToBitMap(mapBitsString.get(currentQuestion).get(currentDrawPage));
        }
        else{
            canvasBitmap = Bitmap.createBitmap(800, 905, Bitmap.Config.ARGB_8888 );
        }
        canvasBitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, true);
        c=new Canvas(canvasBitmap);
        LinearLayout drawLayout = new LinearLayout(this);
        drawLayout.setOrientation(LinearLayout.VERTICAL);
        createQuestionTitle(("Question " + (currentQuestion + 1) + " - image " +
                (currentDrawPage + 1)), 30);
        drawLayout.addView(questionNumberLayout);

        ViewGroup parent = (ViewGroup) drawButtons.getParent();
        if(parent!=null){
            parent.removeView(drawButtons);
        }
        drawLayout.addView(drawButtons);
        drawLayout.addView(createDrawPage(canvasBitmap,c));
        LinearLayout drawpage = new LinearLayout(this);
        drawpage.addView(drawLayout);

        foundationLayout.removeAllViews();
        foundationLayout.addView(drawpage);

        enableDrawButton();
        saveBitmap();
        showPictureMark();
    }
    */
/*
    private void addDrawingButtons(){
        LinearLayout.LayoutParams drawParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        drawButtons = new LinearLayout(this);
        drawButtons.setOrientation(LinearLayout.HORIZONTAL);
        drawButtons.setBackgroundColor(getResources().getColor(R.color.lightbrown));
        drawButtons.setLayoutParams(drawParams);
        Button bt = new Button(this);
        bt.setText("Lines");

        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrawPageButton(false);
                enableDrawButton();


            LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
            LinearLayout tt = (LinearLayout) t.getChildAt(0);
            SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
            ttt.setFreeDraw(false);

            }
        });
        Button bt2 = new Button(this);
        bt2.setText("Free Draw");

        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            LinearLayout t = (LinearLayout) foundationLayout.getChildAt(0);
            LinearLayout tt = (LinearLayout) t.getChildAt(0);
            SimpleDrawView ttt = (SimpleDrawView) tt.getChildAt(2);
            ttt.setFreeDraw(true);
            }
        });
        drawButtons.addView(bt);
        drawButtons.addView(bt2);
    }
*/

/*
    private void saveBitmap(){
        if(dw!=null){
            ArrayList<String> bpString;
            int pageindex=(selectedBtn.getId() - questionButtonId);
            if(mapBitsString.indexOfKey(pageindex)<0){
                bpString=new ArrayList<>();
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
    }
*/
    private void showPictureMark(){
        MenuItem pictureIndicator = menu.findItem(R.id.pictureIndicator);
        if(dh.getMapBitsString().indexOfKey(currentQuestion)>=0){
            pictureIndicator.setVisible(true);
            pictureIndicator.setTitle("x " + dh.getMapBitsString().get(currentQuestion).size());
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
                showPictureMark();
            }
            else{
                foundationLayout.removeAllViews();
                pageButtonLayout.removeAllViews();
                item.setTitle("View question");
                dh.addDrawingButtons(currentQuestion,nextButton,prevButton);
                dh.setDrawPage(0);
                if(dh.getDrawPageButtons().indexOfKey(currentQuestion)<0){
                    dh.addDrawPageButton(currentQuestion,true,nextButton,prevButton);
                }
                pageButtonLayout.addView(dh.getDrawPageButtons().get(
                        (currentQuestion)));
                dh.enableDrawButton(currentQuestion,nextButton,prevButton);

                dh.getDrawView(currentQuestion);


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
                    dh.saveBitmap(currentQuestion);
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
        Log.d("TENTAN", "SUBMITTAD" + answersFinalObj);
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
        Log.d("coden   ", "" + codeEdit.getText().toString());
        try{
            answersObj.put("ID",i);
            //answersObj.put("Answer", codeEdit.getText().toString().toString());
            answersObj.put("Answer",codeEdit.getText().toString());
            answersArr.put(answersObj);
        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }
    }

}




