package com.example.adam.tentaonline;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;




public class TentaOnline extends ActionBarActivity implements AsyncResponse{
    LinearLayout mainLayout;
    AndroidGet asyncGetExam = new AndroidGet();
    int number=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenta_online);

        // LinearLayout myTestLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.weight=1;

        mainLayout=new LinearLayout(this);
        mainLayout.setLayoutParams(LLParams);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        //String testString = "{\"Exam\":[{\"Type\":\"radio\",\"Question\":\"What is 5*5?\",\"Options\":[\"32\",\"45\",\"25\"]},{\"Type\":\"radio\",\"Question\":\"What was Albert Einstein's favorite color?\",\"Options\":[\"Blue\",\"Red\"]}]}";


        // Sets delegate variable in AndroidGet.java to this
        asyncGetExam.delegate = this;
        // Execute AndroidGet.java
        asyncGetExam.execute();

        setContentView(mainLayout);
    }

    /** Gets called when the AndroidGet.java finish executing*/
    public void processFinish(String output){
        Log.d("Output ",output);
        createQuestions(output);
        //this you will received result fired from async class of onPostExecute(result) method.
    }

    /** Checks the questionType and calls the corresponding functions */
    public void createQuestions(String testString){

        try{
            JSONObject examObject = new JSONObject(testString);
            JSONArray examArray = examObject.getJSONArray("Exam");

            for(int i=0;i< examArray.length();i++){
                JSONObject questionObject = examArray.getJSONObject(i);
                if(questionObject.getString("Type").equals("radio")){
                    addRadioQuestion(questionObject);
                }
            }
        }catch (Throwable t){
            Log.d("Threw exception"," " + t);
        }

    }


    /** Adds a multiple choise (radio) question */
    public void addRadioQuestion(JSONObject questionObject){

        try{
            addText(questionObject.getString("Question"));
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
    public void addText(String question){

        TextView questionView = new TextView(this);
        questionView.setText(question);
        questionView.setTextSize(50);
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
            for(int i=0;i<number;i++){
                RadioGroup radioGroup=(RadioGroup)findViewById(1000+i);
                String value = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId() )).getText().toString();
                Log.d("Testing","Submit clicked " + value);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




