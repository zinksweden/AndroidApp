package com.example.adam.tentaonline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.UUID;


/**
 * Created by Adam on 2015-02-01.
 */
public class StartScreen extends ActionBarActivity {

    Button button;

    //private SimpleDrawView drawView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        //drawView = (SimpleDrawView)findViewById(R.id.drawing);
        addListenerOnButton();
    }
/*
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
*/
    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.button);

/*
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Bitmap bmp = SimpleDrawView.getBit();

                Log.d("XX","KLICKAD");
                Log.d("XX", " " + BitMapToString(bmp));


                //drawView.destroyDrawingCache();
            }

        });
*/

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText courseCode = (EditText) findViewById(R.id.editText);
                EditText anonymityCode = (EditText) findViewById(R.id.editText2);


                Intent intent = new Intent(context, TentaOnline.class);
                intent.putExtra("courseCode",courseCode.getText().toString());
                intent.putExtra("anonymityCode",anonymityCode.getText().toString());
                startActivity(intent);

            }

        });


    }

}
