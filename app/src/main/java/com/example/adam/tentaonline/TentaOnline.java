package com.example.adam.tentaonline;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TentaOnline extends ActionBarActivity implements AsyncResponse{

    LinearLayout mainLayout;

    JSONArray examArray;
    int number=0;
    int textBoxNumber=0;
    int checkboxLayoutNumber=0;
    String courseCode,anonymityCode;
    LinearLayout myTestLayout;
    ArrayList<LinearLayout> pagesLayout = new ArrayList<>();
    int currentQuestion=-1;
    LinearLayout pageLayoutHeader;


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
        mainLayout.setBackgroundColor(0xFFFFF);

        AndroidGet asyncGetExam = new AndroidGet();
        // Sets delegate variable in AndroidGet.java to this
        asyncGetExam.delegate = this;
        // Execute AndroidGet.java
        asyncGetExam.execute(courseCode);

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

            final RelativeLayout navButtonLayout = (RelativeLayout) findViewById(R.id.linearLayout3);
            final Button nextButton = createNextButton();
            final Button prevButton = createPrevButton();

            nextButtonOnClick(navButtonLayout,nextButton,prevButton);
            prevButtonOnClick(navButtonLayout,nextButton,prevButton);

            addHeaderButton(headerObject,navButtonLayout,nextButton,prevButton);
            createQuestionPages(navButtonLayout,nextButton,prevButton);
            addNextPrevButton(navButtonLayout,nextButton,prevButton);

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
    private void nextButtonOnClick(final RelativeLayout navButtonLayout, final Button nextButton, final Button prevButton)
    {
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                myTestLayout.addView(pagesLayout.get((currentQuestion + 1)));
                currentQuestion++;
                addNextPrevButton(navButtonLayout, nextButton, prevButton);
            }
        });
    }

    private Button createPrevButton(){
        final Button prevButton = new Button(this);
        prevButton.setText("Prev");
        prevButton.setBackgroundResource(R.drawable.button_shape);

        return prevButton;
    }
    private void prevButtonOnClick(final RelativeLayout navButtonLayout, final Button nextButton, final Button prevButton)
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
                addNextPrevButton(navButtonLayout,nextButton,prevButton);
            }
        });
    }


    private void createQuestionPages(final RelativeLayout navButtonLayout, final Button nextButton, final Button prevButton){
        try {
            LinearLayout questionButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
            for (int i = 0; i < examArray.length(); i++) {

                JSONObject questionObject = examArray.getJSONObject(i);

                LinearLayout pageLayout = new LinearLayout(this);
                LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                pageLayout.setLayoutParams(LLParams);
                pageLayout.setOrientation(LinearLayout.VERTICAL);
                pageLayout.setBackgroundColor(0xFFFFF);
                pageLayout.setId(5001 + i);

                final Button questionButton = new Button(this);
                questionButton.setId(6000 + i);
                questionButton.setText("Question " + (i + 1));
                questionButton.setBackgroundResource(R.drawable.button_shape);

                questionButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        myTestLayout.removeAllViews();
                        myTestLayout.addView(pagesLayout.get((questionButton.getId() - 6000)));
                        currentQuestion = (questionButton.getId() - 6000);
                        addNextPrevButton(navButtonLayout, nextButton, prevButton);
                    }
                });

                questionButtonLayout.addView(questionButton);
                createQuestion(pageLayout,i,questionObject);
                pagesLayout.add(pageLayout);
            }
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    private void createQuestion(LinearLayout pageLayout, int i, JSONObject questionObject){

        try{
            addText(("Question " + (i + 1)), 30, true, pageLayout);
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
        }
        catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    /* Removes next/prev buttons and calls functions to add them */
    public void addNextPrevButton(RelativeLayout buttonLayout, Button nextButton, Button prevButton){
        buttonLayout.removeAllViews();
        addPrevButton(buttonLayout,prevButton);
        addNextButton(buttonLayout,nextButton);
    }

    /* Adds the next button if we are not on the last page */
    public void addNextButton(RelativeLayout buttonLayout, Button nextButton){
        if(currentQuestion<pagesLayout.size()-1){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            buttonLayout.addView(nextButton,params);
        }
    }

    /* Adds the prev button if we are not on the first page */
    public void addPrevButton(RelativeLayout buttonLayout, Button prevButton){
        if(currentQuestion>=0){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            buttonLayout.addView(prevButton,params);
        }
    }

    /* Adds the header button to the bottom */
    public void addHeaderButton(JSONObject headerObject,final RelativeLayout testButtonsLayout,final Button nextButton,final Button prevButton){

        pageLayoutHeader = new LinearLayout(this);
        LinearLayout.LayoutParams LLParamsHeader = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        pageLayoutHeader.setLayoutParams(LLParamsHeader);
        pageLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        pageLayoutHeader.setBackgroundColor(0xFFFFF);
        pageLayoutHeader.setId(5000 + 0);

        addHeader(headerObject,pageLayoutHeader);
        myTestLayout.addView(pageLayoutHeader);

        Button headerButton = new Button(this);
        headerButton.setText("Info");
        headerButton.setBackgroundResource(R.drawable.button_shape);

        final LinearLayout headerButtonLayout = (LinearLayout) findViewById(R.id.linearLayout2);
        headerButtonLayout.addView(headerButton);

        headerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myTestLayout.removeAllViews();
                myTestLayout.addView(pageLayoutHeader);
                currentQuestion=-1;
                addNextPrevButton(testButtonsLayout,nextButton,prevButton);
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
            addText("______________________________",20,true,pageLayout);

            // Adds image to screen
            ImageView imag = new ImageView(this);
                Picasso.with(this)
                    .load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")
                    .into(imag);
            pageLayout.addView(imag);

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
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        edit.setLayoutParams(lparams);
        edit.setId(2000 + textBoxNumber);
        textBoxNumber++;
        edit.setMinimumWidth(100);
        edit.setBackgroundResource(R.drawable.back_border);
        pageLayout.addView(edit);
    }

    /* Adds checkboxes with the answers */
    public void addCheckbox(ArrayList<String> options, LinearLayout pageLayout){

        LinearLayout checkboxLayout = new LinearLayout(this);
        checkboxLayout.setOrientation(LinearLayout.VERTICAL);
        checkboxLayout.setId(3000+checkboxLayoutNumber);

        checkboxLayoutNumber++;

        for(int i=0;i<options.size();i++){
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(options.get(i));
            checkBox.setId(0 + i);
            checkboxLayout.addView(checkBox);
        }
        pageLayout.addView(checkboxLayout);
    }

    /** Adds radio buttons with the answers */
    public void addRadio(ArrayList<String> options, LinearLayout pageLayout){

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setId(1000 + number);

        for(int i=0;i<options.size();i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(options.get(i));
            radioGroup.addView(radioButton);
        }
        number++;
        pageLayout.addView(radioGroup);
    }

    /** Adds question to the mainLayout view */
    public void addText(String question, int textSize, boolean centerHorizontal, LinearLayout pageLayout){

        TextView questionView = new TextView(this);
        if(centerHorizontal)
            questionView.setGravity(Gravity.CENTER_HORIZONTAL);

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
        if (id == R.id.menuSubmit) {
            JSONObject answersObj;
            JSONArray answersArr = new JSONArray();
            JSONObject answersFinalObj = new JSONObject();
            int radioNumber=0;
            int textNumber=0;
            int checkboxNumber=0;
            try{

             for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);

                if(questionObject.getString("Type").equals("radio")){
                    RadioGroup radioGroup=(RadioGroup) pagesLayout.get(i).findViewById(1000+radioNumber);
                    RadioButton radioButton = (RadioButton)radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    answersObj = new JSONObject();
                    radioNumber++;
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

                if(questionObject.getString("Type").equals("checkbox")){
                    LinearLayout checkboxLayout=(LinearLayout) pagesLayout.get(i).findViewById(3000+checkboxNumber);
                    answersObj = new JSONObject();
                    JSONObject answerObj = new JSONObject();
                    checkboxNumber++;
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

                if(questionObject.getString("Type").equals("text")){
                    EditText edit = (EditText) pagesLayout.get(i).findViewById(2000+textNumber);
                    textNumber++;
                    try{
                        answersObj = new JSONObject();
                        answersObj.put("ID",i);
                        answersObj.put("Answer", edit.getText());
                        answersArr.put(answersObj);

                    }catch (JSONException e){
                        Log.d("Threw exception"," " + e);
                    }
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

            asyncPostExam.execute(courseCode,anonymityCode,answersFinalObj.toString());

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




