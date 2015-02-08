package com.example.adam.tentaonline;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class TentaOnline extends ActionBarActivity implements AsyncResponse{
    LinearLayout mainLayout;

    JSONArray examArray;
    int number=0;
    int textBoxNumber=0;
    String courseCode,anonymityCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenta_online);

        courseCode = getIntent().getStringExtra("courseCode");
        anonymityCode =getIntent().getStringExtra("anonymityCode");

        //Log.d("START","program has started");
        LinearLayout myTestLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.weight=1;

        mainLayout=new LinearLayout(this);
        mainLayout.setLayoutParams(LLParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(0xFFFFF);

        //String testString = "{\"Exam\":[{\"Type\":\"radio\",\"Question\":\"What is 5*5?\",\"Options\":[\"32\",\"45\",\"25\"]},{\"Type\":\"radio\",\"Question\":\"What was Albert Einstein's favorite color?\",\"Options\":[\"Blue\",\"Red\"]}]}";
        //String testString ="{\\\"Exam\\\":[{\\\"Type\\\":\\\"radio\\\",\\\"Question\\\":\\\"Fraga1\\\",\\\"Options\\\":[\\\"Answer1\\\",\\\"Answer2\\\"]}]}";
        //createQuestions(testString);

        AndroidGet asyncGetExam = new AndroidGet();
        // Sets delegate variable in AndroidGet.java to this
        asyncGetExam.delegate = this;
        // Execute AndroidGet.java
        asyncGetExam.execute(courseCode);



        myTestLayout.addView(mainLayout);
        setContentView(myTestLayout);
    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){

        createQuestions(output);
        //this you will received result fired from async class of onPostExecute(result) method.
    }

    /** Checks the questionType and calls the corresponding functions */
    public void createQuestions(final String TjsonExamString){


        try{

            JSONArray examContentArr = new JSONArray(TjsonExamString);
            JSONObject headerObject = new JSONObject(examContentArr.getString(1));
            addHeader(headerObject);
            //  String formatedJsonExamString=jarr.getString(0).substring(jarr.getString(0).indexOf("{"), jarr.getString(0).lastIndexOf("}") + 1);
            JSONObject examObject = new JSONObject(examContentArr.getString(0));
            //JSONArray examArray
            examArray = examObject.getJSONArray("Exam");

            for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);
                if(questionObject.getString("Type").equals("radio")){
                    addRadioQuestion(questionObject);
                }
                if(questionObject.getString("Type").equals("text")){
                    addTextboxQuestion(questionObject);
                }
            }


        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }


    }


    public void addHeader(JSONObject headerObject){
            //headerObject.getString("");

        try{

            addText(headerObject.getString("Title"),50,true);
            addText("Course Director: " + headerObject.getString("Director"),20,true);
            addText("Help information: " + headerObject.getString("HelpInfo"),20,true);
            addText("Grading levels: " + headerObject.getString("GradingInfo"),20,true);
            addText(headerObject.getString("OtherInfo"),20,true);
            addText("______________________________",20,true);




        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }



    }


    /** Adds a multiple choise (radio) question */
    public void addRadioQuestion(JSONObject questionObject){

        try{
            addText(questionObject.getString("Question"),25,false);
            JSONArray optionsArray = questionObject.getJSONArray("Options");
            ArrayList<String> options = new ArrayList<>();

            for (int i=0;i<optionsArray.length();i++){
                options.add(optionsArray.getString(i));
            }
            addRadio(options);
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }
    }

    public void addTextboxQuestion(JSONObject questionObject){
        try{
            addText(questionObject.getString("Question"),25,false);
            addTextbox();
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }


    }

    public void addTextbox(){
        EditText edit = new EditText(this);
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        edit.setLayoutParams(lparams);
        edit.setId(2000+textBoxNumber);
        textBoxNumber++;
        //edit.setBackgroundColor(0xffeeeeee);
        edit.setMinimumWidth(100);
        mainLayout.addView(edit);


    }

    /** Adds radio buttons with the answers */
    public void addRadio(ArrayList<String> options){

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setId(1000 + number);

        for(int i=0;i<options.size();i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(options.get(i));
            radioGroup.addView(radioButton);
        }
        number++;
        mainLayout.addView(radioGroup);

    }

    /** Adds question to the mainLayout view */
    public void addText(String question, int textSize, boolean centerHorizontal){

        TextView questionView = new TextView(this);
        if(centerHorizontal)
            questionView.setGravity(Gravity.CENTER_HORIZONTAL);

        questionView.setText(question);
        questionView.setTextSize(textSize);
        mainLayout.addView(questionView);
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
            try{


             for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);

                if(questionObject.getString("Type").equals("radio")){
                    RadioGroup radioGroup=(RadioGroup)findViewById(1000+radioNumber);
                    RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                    answersObj = new JSONObject();
                    radioNumber++;
                    try{
                        answersObj.put("ID",i);
                        answersObj.put("answer", "option" + radioGroup.indexOfChild(radioButton));
                        answersArr.put(answersObj);

                    }catch (JSONException e){
                        Log.d("Threw exception"," " + e);
                    }
                }

                if(questionObject.getString("Type").equals("text")){
                    EditText edit = (EditText)findViewById(2000+textNumber);
                    textNumber++;
                    try{
                        answersObj = new JSONObject();
                        answersObj.put("ID",i);
                        answersObj.put("answer", edit.getText());
                        answersArr.put(answersObj);

                    }catch (JSONException e){
                        Log.d("Threw exception"," " + e);
                    }
                }
            }
            }catch (Throwable t){
            Log.d("Threw exception"," " + t);
            }

            //examArray.length();
            /*for(int i=0;i<number;i++){
                RadioGroup radioGroup=(RadioGroup)findViewById(1000+i);
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                answersObj = new JSONObject();
                try{
                    answersObj.put("ID",i);
                    answersObj.put("answer", "option" + radioGroup.indexOfChild(radioButton));
                    answersArr.put(answersObj);

                }catch (JSONException e){
                    Log.d("Threw exception"," " + e);
                }

            }*/
            try{
                answersFinalObj.put("Student",anonymityCode);
                answersFinalObj.put("Answers",answersArr);

            }catch (JSONException e){
                Log.d("Threw exception"," " + e);
            }

            Log.d("JSONHole", answersFinalObj.toString());

            AndroidPost asyncPostExam = new AndroidPost();
            asyncPostExam.delegate = this;

           // {"Student":"kalle","Answers":[{"ID":0,"Answer":"option1"},{"ID":1,"Answer":"option2"}]}

            asyncPostExam.execute(courseCode,anonymityCode,answersFinalObj.toString());


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




