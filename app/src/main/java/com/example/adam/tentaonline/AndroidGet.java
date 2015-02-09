package com.example.adam.tentaonline;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.params.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Adam on 2015-01-30.
 */
public class AndroidGet extends AsyncTask<String,String,String> {
    public AsyncResponse delegate=null;

    protected String doInBackground(String... param) {

        StringBuilder sb = null;
        InputStream is = null;
        String result = null;


        //http post
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            //Log.d("KURs",urls[0]);

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);

            HttpClient httpclient = new DefaultHttpClient(params);

            //Why to use 10.0.2.2
            HttpPost httppost = new HttpPost("http://83.183.12.45/android/get.php?course_code=" + URLEncoder.encode(param[0], "UTF-8"));  //ändra foldername
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

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.d("Exam", "TESTING2");
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return result;

    }

    @Override
    protected void onPostExecute(String result) {

        try {
            delegate.processFinish(result);

        }catch (Exception e) {
            Log.e("log_tag", "Error parsing data "+e.toString());

        }

    }

}



