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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2015-02-02.
 */
public class AndroidPost extends AsyncTask<String,String,String> {
    public AsyncResponse delegate=null;
    protected String doInBackground(String... param) {
        InputStream is = null;
        try {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpClient httpclient = new DefaultHttpClient(params);
            HttpPost httppost = new HttpPost("http://83.183.12.45/" + param[0]);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("course_code",param[1]));
            nameValuePairs.add(new BasicNameValuePair("student_id", param[2]));
            nameValuePairs.add(new BasicNameValuePair("answer", param[3]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

            Log.d("test2","" + httppost.getEntity().getContent().toString());

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            String result = sb.toString();
            Log.d("Result", "" + result);
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        return "";
    }
    @Override
    protected void onPostExecute(String result) {}
}
