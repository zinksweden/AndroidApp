package com.example.adam.tentaonline;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Adam on 2015-02-02.
 */
public class AndroidPost extends AsyncTask<String,String,String> {
    public AsyncResponse delegate=null;


    protected String doInBackground(String... param) {


        InputStream is = null;
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
            HttpPost httppost = new HttpPost("http://83.183.12.45/android/post.php?course_code=" + URLEncoder.encode(param[0], "UTF-8") + "&student_id=" + URLEncoder.encode(param[1], "UTF-8") + "&answer=" + URLEncoder.encode(param[2], "UTF-8"));  //ändra foldername
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }




        return "";
    }


    @Override
    protected void onPostExecute(String result) {

    }
}
