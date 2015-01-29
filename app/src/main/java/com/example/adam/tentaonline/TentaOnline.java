package com.example.adam.tentaonline;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TentaOnline extends ActionBarActivity {

    int number=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenta_online);

        //LinearLayout minlayouttest = (LinearLayout) findViewById(R.id.linearLayout1);


        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.weight=1;

        LinearLayout mainlayout=new LinearLayout(this);
        mainlayout.setLayoutParams(LLParams);
        mainlayout.setOrientation(LinearLayout.VERTICAL);
        //minlayout.setBackgroundColor(Color.RED);

        //getString(R.string.questions);

        String teststring = "{\"Questions\":[{\"Type\":\"radio\",\"Question\":\"dada\",\"Options\":[\"dada\",\"da\"]},{\"Type\":\"radio\",\"Question\":\"dadada\",\"Options\":[\"dada\",\"dada\"]},{\"Type\":\"radio\",\"Question\":\"dada\",\"Options\":[\"dada\",\"dada\"]}]}";

        JSONObject jsonobject;
        try{
            jsonobject = new JSONObject(teststring);
            Log.d("Success"," " + jsonobject.toString());
            JSONArray jsonarr = jsonobject.getJSONArray("Questions");
            JSONObject jsonobj = jsonarr.getJSONObject(0);

            Log.d("Typen is ", "" +  jsonobj.getString("Type"));


        }catch (Throwable t){
            Log.d("Fail"," " + t);
        }





        ArrayList<String> answers = new ArrayList<>();
        answers.add("Answer 1");
        answers.add("Answer 2");

        addText(mainlayout, "Fråga 1");
        addRadio(mainlayout, answers);

        addText(mainlayout, "Fråga 2");
        addRadio(mainlayout, answers);

       // minlayouttest.addView(minlayout);

        setContentView(mainlayout);
    }

    /*Adds radio buttons with the answers*/
    public void addRadio(LinearLayout layouten, ArrayList<String> answers){

        RadioGroup rgrp = new RadioGroup(this);

        rgrp.setId(1000+number);

        for(int i=0;i<answers.size();i++){
            RadioButton rbn = new RadioButton(this);
            rbn.setText(answers.get(i));
            rgrp.addView(rbn);
        }
        number++;
        layouten.addView(rgrp);

    }

    /*Adds text*/
    public void addText(LinearLayout layout, String text){

            TextView textview = new TextView(this);
            textview.setText(text);
            textview.setTextSize(50);
            layout.addView(textview);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tenta_online, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*Checks if submit exam was clicked*/
        if (id == R.id.menuSubmit) {

            for(int i=0;i<number;i++){
                RadioGroup rg=(RadioGroup)findViewById(1000+i);
                String value = ((RadioButton)findViewById(rg.getCheckedRadioButtonId() )).getText().toString();
                Log.d("Testing","Submit clicked " + value);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
