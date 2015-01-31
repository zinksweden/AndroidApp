package com.example.adam.tentaonline;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Adam on 2015-01-30.
 */
public class AndroidGet extends AsyncTask<String,String,Void> {
    public AsyncResponse delegate=null;
    String result = null;

    protected Void doInBackground(String... urls) {




        StringBuilder sb = null;
        InputStream is = null;


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //http post
        try {
            Log.d("Exam","Print1");
            HttpClient httpclient = new DefaultHttpClient();

            //Why to use 10.0.2.2
            HttpPost httppost = new HttpPost("http://10.0.2.2/android/get.php");  //ändra foldername
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        //convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");

            String line = "0";
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.d("Exam","TESTING2");
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return null;

    }

    @Override
    protected void onPostExecute(Void v) {
        String ct_name;

        try {

            Log.d("Test","adsffs");

            Log.d("Test","" +result);

            delegate.processFinish(result);


            //Log.d("Test","" +jArray.getString(0));
            /*JSONObject json_data = null;

            //  ArrayList<String> testsql = new ArrayList<>();

            for (int i = 0; i < jArray.length(); i++) {
                json_data = jArray.getJSONObject(i);
                //ct_name = json_data.getString("EXAM");//here "Name" is the column name in database
                //Log.d("Exam","" +ct_name);
                //  testsql.add(ct_name);
            }*/
        }catch (Exception e) {
            Log.e("log_tag", "Error parsing data "+e.toString());

    }



    }



}



