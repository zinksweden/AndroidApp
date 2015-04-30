package com.example.adam.tentaonline;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adam on 2015-01-30.
 */
public class AndroidGet extends AsyncTask<String,String,String> {
    public AsyncResponse delegate=null;
    Boolean isCode=false;
    protected String doInBackground(String... param) {
        StringBuilder sb = null;
        InputStream is = null;
        String result = null;
        int x=0;
        try {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 10000);
            HttpConnectionParams.setSoTimeout(params, 10000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpClient httpclient = new DefaultHttpClient(params);
            HttpGet httpget;
            if(param.length>4 && param[5]=="Code"){
                if(param[1].equals("c++")){param[1]="cpp";}
                isCode=true;
                JSONObject fileMap = new JSONObject();
                fileMap.put("name", param[3] + "." + param[1]);
                fileMap.put("content", param[4]);
                httpget = new HttpGet("http://83.183.12.45/" + param[0]+ "?language=" + param[1] +
                        "&taskId="  + param[2] +
                        "&file=" + URLEncoder.encode(fileMap.toString(), "UTF-8")+ "&Output="  +
                        URLEncoder.encode(param[6],"UTF-8") +  "&ShowOutput=" + param[7] +
                        "&ShowCompile=" + param[8] );
            }
            else{
                isCode=false;
                httpget = new HttpGet("http://83.183.12.45/" + param[0]+ "?course_code=" +
                        URLEncoder.encode(param[1], "UTF-8"));
            }
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
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
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if(isCode){
                delegate.codeFinish(result);
            }
            else {
                delegate.processFinish(result);
            }
        }catch (Exception e) {
            Log.e("log_tag", "Error parsing data "+e.toString());
        }
    }
}



