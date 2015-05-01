package com.example.adam.tentaonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * Created by Adam on 2015-02-01.
 */
public class StartScreen extends ActionBarActivity implements AsyncResponse {
    Button loadExamButton;
    EditText examId ,studentId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        addListenerOnButton();
    }
    public void addListenerOnButton() {
        loadExamButton = (Button) findViewById(R.id.button);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        loadExamButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d("hit","hit");
                examId = (EditText) findViewById(R.id.editText);
                studentId = (EditText) findViewById(R.id.editText2);
            if (!examId.getText().toString().trim().equals("") &&
                    !studentId.getText().toString().trim().equals("")) {
                loadExam();
            } else {
                builder.setTitle("Failed");
                builder.setMessage("Both fields need to be filled");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            }

        });
    }

    private void loadExam(){
        final AndroidGet asyncGetExam = new AndroidGet();
        asyncGetExam.delegate = this;
        asyncGetExam.execute("android/get/exist.php", examId.getText().toString());
    }

    @Override
    public void processFinish(String output) {
        Log.d("kommer","" + output);
        if(!output.trim().equals("null")){
            final Context context = this;
            Log.d("test1","kommer");
            Intent intent = new Intent(context, TentaOnline.class);
            intent.putExtra("examId",examId.getText().toString());
            intent.putExtra("studentId",studentId.getText().toString());
            //intent.putExtra("exam",output);
            Log.d("test2","kommer");

            if(getIntent().getBooleanExtra("ex", false)){
                Log.e("Hi", "It was false");
            } else {
                Log.e("Hi", "It was true");
            }


            startActivity(intent);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Failed");
            builder.setMessage("This exam does not exist");
            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }

            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void codeFinish(String output) {}
}
