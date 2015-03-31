package com.example.adam.tentaonline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TentaOnline extends ActionBarActivity implements AsyncResponse{

    LinearLayout mainLayout;

    JSONArray examArray;
    //int number=0;
    //int textBoxNumber=0;
    //int checkboxLayoutNumber=0;
    //int codeNumber =0;
    int questionId=0;
    int currentQuestion=-1;
    String courseCode,anonymityCode;
    LinearLayout myTestLayout;
    ArrayList<LinearLayout> pagesLayout = new ArrayList<>();
    LinearLayout pageLayoutHeader;

    LinearLayout questionnumberLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenta_online);

        courseCode = getIntent().getStringExtra("courseCode");
        anonymityCode =getIntent().getStringExtra("anonymityCode");

        myTestLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.weight=1;

        mainLayout=new LinearLayout(this);
        mainLayout.setLayoutParams(LLParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(getResources().getColor(R.color.lightblue));
        Log.d("bbbbbbbbbb","bbbbb");
        AndroidGet asyncGetExam = new AndroidGet();
        // Sets delegate variable in AndroidGet.java to this
        asyncGetExam.delegate = this;
        // Execute AndroidGet.java
        asyncGetExam.execute("android/get.php", courseCode);

    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){
        Log.d("outen","TTT    " + output);
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

            //final RelativeLayout navButtonLayout = (RelativeLayout) findViewById(R.id.linearLayout3);

            final Button nextButton = (Button) findViewById(R.id.nextArrow);
            final Button prevButton = (Button) findViewById(R.id.prevArrow);

            nextButtonOnClick(nextButton,prevButton);
            prevButtonOnClick(nextButton,prevButton);

            addHeaderButton(headerObject,nextButton,prevButton);
            createQuestionPages(nextButton,prevButton);
            addNextPrevButton(nextButton,prevButton);

        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    private Button createNextButton(){
        final Button nextButton = new Button(this);
        nextButton.setText("Next");
        nextButton.setBackgroundResource(R.drawable.button_shape);

        return nextButton;
    }

    private void nextButtonOnClick(final Button nextButton, final Button prevButton)
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                myTestLayout.addView(pagesLayout.get((currentQuestion + 1)));
                currentQuestion++;
                addNextPrevButton( nextButton, prevButton);
            }
        });
    }

    private Button createPrevButton(){
        final Button prevButton = new Button(this);
        prevButton.setText("Prev");
        prevButton.setBackgroundResource(R.drawable.button_shape);

        return prevButton;
    }
    private void prevButtonOnClick(final Button nextButton, final Button prevButton)
    {
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                if(currentQuestion==0){
                    myTestLayout.addView(pageLayoutHeader);
                }
                else{
                    myTestLayout.addView(pagesLayout.get((currentQuestion-1)));
                }
                currentQuestion--;
                addNextPrevButton(nextButton,prevButton);
            }
        });
    }


    private void createQuestionPages(final Button nextButton, final Button prevButton){
        try {
            LinearLayout questionButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
            for (int i = 0; i < examArray.length(); i++) {

                JSONObject questionObject = examArray.getJSONObject(i);

                LinearLayout pageLayout = new LinearLayout(this);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                pageLayout.setPadding(30,30,30,0);

                pageLayout.setLayoutParams(LLParams);
                pageLayout.setOrientation(LinearLayout.VERTICAL);
                pageLayout.setBackgroundColor(getResources().getColor(R.color.lightblue));
                pageLayout.setId(5001 + i);

                final Button questionButton = new Button(this);
                questionButton.setId(6000 + i);
                questionButton.setText("Question " + (i + 1));
                questionButton.setBackgroundResource(R.drawable.button_shape);
                questionButton.setTextColor(Color.WHITE);


                questionButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myTestLayout.removeAllViews();
                        myTestLayout.addView(pagesLayout.get((questionButton.getId() - 6000)));
                        currentQuestion = (questionButton.getId() - 6000);
                        addNextPrevButton(nextButton, prevButton);
                    }
                });

                questionButtonLayout.addView(questionButton);
                createQuestion(pageLayout, i, questionObject);
                LinearLayout fullquestion = new LinearLayout(this);
                fullquestion.setOrientation(LinearLayout.VERTICAL);
                fullquestion.setLayoutParams(LLParams);
                fullquestion.addView(questionnumberLayout);
                fullquestion.addView(pageLayout);
                pagesLayout.add(fullquestion);
                //pagesLayout.add(pageLayout);
            }
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
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
    public void addNextPrevButton(Button nextButton, Button prevButton){
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
          /*  RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            buttonLayout.addView(nextButton,params);*/
        }
    }

    /* Adds the prev button if we are not on the first page */
    public void addPrevButton(Button prevButton){
        if(currentQuestion>=0){
            prevButton.setEnabled(true);
           /* RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            buttonLayout.addView(prevButton,params);*/
        }
    }

    /* Adds the header button to the bottom */
    public void addHeaderButton(JSONObject headerObject,final Button nextButton,final Button prevButton){

        pageLayoutHeader = new LinearLayout(this);
        LinearLayout.LayoutParams LLParamsHeader = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pageLayoutHeader.setLayoutParams(LLParamsHeader);
        pageLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        pageLayoutHeader.setBackgroundColor(getResources().getColor(R.color.lightblue));
        pageLayoutHeader.setId(5000 + 0);

        addHeader(headerObject,pageLayoutHeader);
        myTestLayout.addView(pageLayoutHeader);

        Button headerButton = new Button(this);
        headerButton.setText("Info");
        headerButton.setBackgroundResource(R.drawable.button_shape);
        headerButton.setTextColor(getResources().getColor(R.color.white));

        final LinearLayout headerButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
        headerButtonLayout.addView(headerButton);

        headerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                myTestLayout.addView(pageLayoutHeader);
                currentQuestion=-1;
                addNextPrevButton(nextButton,prevButton);
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
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        edit.setLayoutParams(lparams);
        edit.setId(1000 + questionId++);
        edit.setMinimumWidth(100);
        edit.setBackgroundResource(R.drawable.back_border);
        pageLayout.addView(edit);
    }

    /* Adds checkboxes with the answers */
    public void addCheckbox(ArrayList<String> options, LinearLayout pageLayout){

        LinearLayout checkboxLayout = new LinearLayout(this);
        checkboxLayout.setOrientation(LinearLayout.VERTICAL);
        checkboxLayout.setId(1000+questionId++);

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
        radioGroup.setId(1000 + questionId++);

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

    public void addCode(LinearLayout pageLayout, final String language, final String startCode, final String Output, final String ShowOutput, final String ShowCompile, final String HiddenStart){                //FIXA MED FUNKTIONER

        //final LinearLayout.LayoutParams mparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1.0f);


        Button compileButton= new Button(this);
        compileButton.setText("Compile");
        compileButton.setTextColor(getResources().getColor(R.color.white));
        compileButton.setBackgroundResource(R.drawable.button_shape);

        //final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText errorMessageBox = new EditText(this);
        errorMessageBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        errorMessageBox.setSingleLine(false);
        errorMessageBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,3.0f));
        errorMessageBox.setId(10000+ questionId);
        //errorMessageBox.setMaxHeight(200);
        //errorMessageBox.setMinHeight(200);

        errorMessageBox.setBackgroundResource(R.drawable.back_border);



        final EditText editCode = new EditText(this);
        editCode.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editCode.setSingleLine(false);

        editCode.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,7.0f));
        editCode.setId(1000 + questionId++);

        //editCode.setMinHeight(400);
        //editCode.setMaxHeight(400);
        editCode.setGravity(Gravity.TOP);
        editCode.setBackgroundResource(R.drawable.back_border);

        editCode.setText(startCode);

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
        asyncGetCodeCorrection.execute("src/db/post/kattisClone.php", language,taskId,anonymityCode,editCode+HiddenStart,"Code",Output,ShowOutput,ShowCompile);
    }

    public void codeFinish(String output){
        LinearLayout layut = pagesLayout.get(currentQuestion);
        EditText errorMessageBox = (EditText) layut.findViewById(10000 + currentQuestion);
        errorMessageBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        errorMessageBox.setSingleLine(false);
        errorMessageBox.setText(output);

        Log.d("Coddsasesadtion resul", "CCC    " + output);
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



    /** Creates the dropdown menu with post exam button etc */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tenta_online, menu);
        return true;
    }

    /** Checks what answers was made on the questions */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


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


    private void submitExam(){
        JSONObject answersObj=null;
        JSONArray answersArr = new JSONArray();
        JSONObject answersFinalObj = new JSONObject();

        try{
            for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);

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
            answersFinalObj.put("Student",anonymityCode);
            answersFinalObj.put("Answers",answersArr);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

        Log.d("JSONHole", answersFinalObj.toString());

        AndroidPost asyncPostExam = new AndroidPost();
        asyncPostExam.delegate = this;

        asyncPostExam.execute("android/post.php",courseCode,anonymityCode,answersFinalObj.toString());


    }

    private void submitRadio(JSONObject answersObj,JSONArray answersArr,int i){
        RadioGroup radioGroup=(RadioGroup) pagesLayout.get(i).findViewById(1000+i);
        RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        answersObj = new JSONObject();
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
        LinearLayout checkboxLayout=(LinearLayout) pagesLayout.get(i).findViewById(1000+i);
        answersObj = new JSONObject();
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
        EditText edit = (EditText) pagesLayout.get(i).findViewById(1000+i);
        edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);            //SKA TA BORT STAVNINGSKONTROLL
        edit.setSingleLine(false);
        try{
            answersObj = new JSONObject();
            answersObj.put("ID",i);
            answersObj.put("Answer", edit.getText());
            answersArr.put(answersObj);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

    }

    private void submitCode(JSONObject answersObj, JSONArray answersArr, int i){
        EditText codeEdit = (EditText) pagesLayout.get(i).findViewById(1000+i);
        codeEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        codeEdit.setSingleLine(false);
        try{
            answersObj = new JSONObject();
            answersObj.put("ID",i);
            answersObj.put("Answer", codeEdit.getText());
            answersArr.put(answersObj);

        }catch (JSONException e){
            Log.d("Threw exception"," " + e);
        }

    }

}




