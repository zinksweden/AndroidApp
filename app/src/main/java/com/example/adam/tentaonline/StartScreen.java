package com.example.adam.tentaonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by Adam on 2015-02-01.
 */
public class StartScreen extends ActionBarActivity {

    Button button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText text = (EditText) findViewById(R.id.editText);

                Intent intent = new Intent(context, TentaOnline.class);
                intent.putExtra("courseCode",text.getText().toString());
                startActivity(intent);

            }

        });

    }

}
