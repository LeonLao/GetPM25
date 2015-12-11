package com.example.getpm25;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by jack on 2015/10/9.
 */
public class Task extends AsyncTask<String,Void,String> {
    Context context;
    TextView textView;

    private static final String URL = "http://web.juhe.cn:8080/environment/air/pm";
    private static final String KEY = "70f39814c1b263ed181653ed70728129";

    public Task(Context context, TextView textView) {
        super();
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(String... params) {
        String city = params[0];

        ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
        headerList.add(new BasicNameValuePair("Content-Type", "text/html; charset=utf-8"));

        String targetUrl = URL;

        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("key", KEY));
        paramList.add(new BasicNameValuePair("dtype", "json"));
        paramList.add(new BasicNameValuePair("city", city));

        for (int i = 0; i < paramList.size(); i++) {
            NameValuePair nowPair = paramList.get(i);
            String value = nowPair.getValue();
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i == 0) {
                targetUrl += ("?" + nowPair.getName() + "=" + value);
            } else {
                targetUrl += ("&" + nowPair.getName() + "=" + value);
            }
        }

        HttpGet httpRequest = new HttpGet(targetUrl);
        try {
            for (int i = 0; i < headerList.size(); i++) {
                httpRequest.addHeader(headerList.get(i).getName(), headerList.get(i).getValue());

            }
            HttpClient httpClient = new DefaultHttpClient();

            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result !=null){
            try {
                JSONObject jsonObject = new JSONObject(result);
                int resultCode = jsonObject.getInt("resultcode");
                if (resultCode ==200){
                    JSONArray resultJsonArray = jsonObject.getJSONArray("result");//将结果转换场JSONArray对象的形式
                    JSONObject resultJsonObject = resultJsonArray.getJSONObject(0);//获取json数组中个的第一项
                    String output = context.getString(R.string.city) + ": " + resultJsonObject.getString("city") + "\n"
                            + context.getString(R.string.PM25) + ": " + resultJsonObject.getString("PM2.5") + "\n"
                            + context.getString(R.string.AQI) + ": " + resultJsonObject.getString("AQI") + "\n"
                            + context.getString(R.string.quality) + ": " + resultJsonObject.getString("quality") + "\n"
                            + context.getString(R.string.PM10) + ": " + resultJsonObject.getString("PM10") + "\n"
                            + context.getString(R.string.CO) + ": " + resultJsonObject.getString("CO") + "\n"
                            + context.getString(R.string.NO2) + ": " + resultJsonObject.getString("NO2") + "\n"
                            + context.getString(R.string.O3) + ": " + resultJsonObject.getString("O3") + "\n"
                            + context.getString(R.string.SO2) + ": " + resultJsonObject.getString("SO2") + "\n"
                            + context.getString(R.string.time) + ": " + resultJsonObject.getString("time") + "\n";
                    textView.setText(output);
                }else if (resultCode ==202){
                    String reason = jsonObject.getString("reason");
                    textView.setText(reason);
                }else {
                    Toast.makeText(context,"查询失败",Toast.LENGTH_SHORT).show();
                    textView.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context,"查询失败",Toast.LENGTH_SHORT).show();
            textView.setText("");
        }
    }

}