package com.example.getpm25;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jack on 2015/10/9.
 */
public class Task extends AsyncTask<String,Void,String> {
    SimpleDateFormat myFmt = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    /**
     SimpleDateFormat函数语法：

     G 年代标志符
     y 年
     M 月
     d 日
     h 时 在上午或下午 (1~12)
     H 时 在一天中 (0~23)
     m 分
     s 秒
     S 毫秒
     E 星期
     D 一年中的第几天
     F 一月中第几个星期几
     w 一年中第几个星期
     W 一月中第几个星期
     a 上午 / 下午 标记符
     k 时 在一天中 (1~24)
     K 时 在上午或下午 (0~11)
     z 时区
     */


    Context context;
    TextView textView;
    ListView listView;
    private MyAdapter adapter = new MyAdapter();
   // private String[] weilaidates,gaowens,diwens;//日期,高温，低温
    private String[] weilaidates= new String[7];//未来几天
    private String[] gaowens = new String[7];//最高温度
    private String[] diwens = new String[7];//最低温度
    private int[] weatherIDs = new int[7];//天气ID
    private int[] weatherPICs = new int[7];//对应天气图
    String weilaidate;


    //private static final String URL = "http://web.juhe.cn:8080/environment/air/pm";
  // private static final String KEY = "70f39814c1b263ed181653ed70728129";

    //接口地址
    private static final String URL= "http://op.juhe.cn/onebox/weather/query";
    //APIKEY
    private static final String KEY="c9f6af6efa1077151f52aacd2b596441";




    public Task(Context context, TextView textView,ListView listView) {
        super();
        this.context = context;
        this.textView = textView;
        this.listView = listView;
    }
    //String... params表示的是可变参数列表，也就是说，这样的方法能够接受的参数个数是可变的，但不论多少，必须都是String类型的
   // doInBackground("param1","param2","param3") ，或是doInBackground() 。
   // 实际上，在处理可变参数列表的时候，Java是转化为数组来处理的，
   // 比如前面的例子，doInBackground传进来三个参数，此时params实际上是一个String[3],可以通过params[0]来引用传进来的实参"param1"。
   // 当doInBackground()没有提供实参时params就为null。另外，对于可变参数列表的方法，
   // 我们可以直接传递一个数组代替，比如doInBackground(new String[]{"param1", "param2", "param3"}),
   // 效果和doInBackground("param1","param2","param3")是一样的
    @Override
    protected String doInBackground(String... params) {
        String cityname = params[0];

        ArrayList<NameValuePair> headerList = new ArrayList<NameValuePair>();
        headerList.add(new BasicNameValuePair("Content-Type", "text/html; charset=utf-8"));

       String targetUrl = URL;
      // String targetUrl ="";
        //http://op.juhe.cn/onebox/weather/query?key=c9f6af6efa1077151f52aacd2b596441&dtype=json&city=%E5%B9%BF%E5%B7%9E
        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("key", KEY));
        paramList.add(new BasicNameValuePair("dtype", "json"));
        paramList.add(new BasicNameValuePair("cityname", cityname));

        for (int i = 0; i < paramList.size(); i++) {
            NameValuePair nowPair = paramList.get(i);
            String value = nowPair.getValue();
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

           // targetUrl="http://op.juhe.cn/onebox/weather/query?cityname=%E5%B9%BF%E5%B7%9E&dtype=&key=c9f6af6efa1077151f52aacd2b596441" ;
           // targetUrl="http://www.baidu.com";
            //拼接url
            if (i == 0) {
                targetUrl += ("?" + nowPair.getName() + "=" + value);
            } else {
                targetUrl += ("&" + nowPair.getName() + "=" + value);
            }
        }
        /*GET请求的数据会附在URL之后（就是把数据放置在HTTP协议头中），以?分割URL和传输数据，参数之间以&相连，
        如：login.action?name=hyddd&password=idontknow&verify=%E4%BD%A0%E5%A5%BD。
        如果数据是英文字母/数字，原样发送，如果是空格，转换为+，如果是中文/其他字符，则直接把字符串用BASE64加密，
        得出如：%E4%BD%A0%E5%A5%BD，其中％XX中的XX为该符号以16进制表示的ASCII*/
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
                return "请求出错";
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
                int resultCode = jsonObject.getInt("error_code");
                String resulttype = jsonObject.getString("reason");
               if (resultCode ==0){
               // if (resulttype == "successed"){
                   // JSONArray resultJsonArray = jsonObject.getJSONArray("result");//将结果转换场JSONArray对象的形式
//                   JSONObject resultJsonArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime").getJSONObject("wind");
                   JSONObject resultJsonArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime");
                   JSONArray resultarray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONArray("weather");
                   //JSONArray weilaitianqi = resultarray.getJSONArray(1);//info
                  // JSONObject weilaitianqi = resultarray.getJSONObject(1);//info
                   //JSONArray baitiantianqi = weilaitianqi.getJSONArray("day");
                  // String baitianwendu = baitiantianqi.getString(1);


                  // JSONObject resultJsonArray = jsonObject.getJSONObject("result").getJSONObject("data").getJSONObject("realtime").getJSONObject("weather");
                   // JSONObject resultJsonObject = resultJsonArray.getJSONObject(0);//获取json数组中个的第一项
//                    String output = context.getString(R.string.city) + ": " + resultJsonObject.getString("city") + "\n"
//                            + context.getString(R.string.PM25) + ": " + resultJsonObject.getString("PM2.5") + "\n"
//                            + context.getString(R.string.AQI) + ": " + resultJsonObject.getString("AQI") + "\n"
//                            + context.getString(R.string.quality) + ": " + resultJsonObject.getString("quality") + "\n"
//                            + context.getString(R.string.PM10) + ": " + resultJsonObject.getString("PM10") + "\n"
//                            + context.getString(R.string.CO) + ": " + resultJsonObject.getString("CO") + "\n"
//                            + context.getString(R.string.NO2) + ": " + resultJsonObject.getString("NO2") + "\n"
//                            + context.getString(R.string.O3) + ": " + resultJsonObject.getString("O3") + "\n"
//                            + context.getString(R.string.SO2) + ": " + resultJsonObject.getString("SO2") + "\n"
//                            + context.getString(R.string.time) + ": " + resultJsonObject.getString("time") + "\n";
                    //String output = resultJsonObject.getJSONObject("data").getJSONObject("realtime").getString("city_name");
//                   String output = resultJsonArray.getString("date");
//                   String now_temperature = resultJsonArray.getJSONObject("weather").getString("info");
                  // int date = resultJsonArray.getInt("dataUptime");//1452498724
                   // textView.setText(myFmt.format(date).toString());
                   //weilaidate = resultarray.getJSONObject(1).getString("week");
                  JSONObject dddddd = resultarray.getJSONObject(1).getJSONObject("info");
                  JSONArray ccccc = dddddd.getJSONArray("day");
                   weilaidate = ccccc.getString(2);
                  // String weilaitianqiiii =weilaibaitian.getString(1);

                   for (int i= 0;i<7;i++){
                      weilaidates[i] = resultarray.getJSONObject(i).getString("week");
                   }
                   for (int i=0;i<7;i++){
                       JSONObject dayinfo = resultarray.getJSONObject(i).getJSONObject("info");
                       gaowens[i] = dayinfo.getJSONArray("day").getString(2);
                   }
                   for (int i= 0;i<7;i++){
                       JSONObject nightinfos = resultarray.getJSONObject(i).getJSONObject("info");
                       diwens[i] = nightinfos.getJSONArray("night").getString(2);
                   }
                   for (int i= 0;i<7;i++){
                       JSONObject weitherID = resultarray.getJSONObject(i).getJSONObject("info");
                       weatherIDs[i] = weitherID.getJSONArray("day").getInt(0);
                   }
                   for (int i = 0;i<7;i++){
                       weatherPICs[i] = getPIC(weatherIDs[i]);
                   }

//                   int i = 01;
//                   int e = 1;
//                   if (i==e) {
//                       textView.setText("dddd");
//                   }else {
//                       textView.setText(weilaidate);
//                   }
                   textView.setText(weilaidate);
                  listView.setAdapter(adapter);


                }
//                else if (resultCode ==202){
//                    String reason = jsonObject.getString("reason");
//                    textView.setText(reason);
//                }
                else {
                    Toast.makeText(context,"查询失败"+resulttype+resultCode,Toast.LENGTH_SHORT).show();
                    textView.setText("");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //textView.setText(result);
        }
        else {
            Toast.makeText(context,"查询失败",Toast.LENGTH_SHORT).show();
            textView.setText("");
        }
    }

    private class MyAdapter extends BaseAdapter{
        public MyAdapter(){
            super();
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
                convertView = View.inflate(context,R.layout.list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.riqi = (TextView)convertView.findViewById(R.id.week);
                viewHolder.diwen =(TextView)convertView.findViewById(R.id.diwen);
                viewHolder.gaowen =(TextView)convertView.findViewById(R.id.gaowen);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.weather);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.riqi.setText("星期"+weilaidates[position]);
            viewHolder.gaowen.setText(gaowens[position]);
            viewHolder.diwen.setText(diwens[position] );
            viewHolder.imageView.setImageResource(weatherPICs[position]);
            return convertView;
        }
    }
    private class  ViewHolder
    {
        TextView riqi,gaowen,diwen;
        ImageView imageView;
    }

    private int getPIC(int id){
        int weapic = 0;
       for (int i= 0;i<MY_Weather.length;i++){
           if (id==MY_Weather[i][0]){
               weapic =MY_Weather[i][1];
           }
       }
        return weapic;
    }

    //匹配天气状态
    private final int[][] MY_Weather={
            //{天气ID，图ID}
            {0,R.mipmap.w00},
            {1,R.mipmap.w01},
            {2,R.mipmap.w02},
            {3,R.mipmap.w03},
            {4,R.mipmap.w04},
            {5,R.mipmap.w05},
            {6,R.mipmap.w06},
            {7,R.mipmap.w07},
            {8,R.mipmap.w08},
            {9,R.mipmap.w09},
            {10,R.mipmap.w10},
            {11,R.mipmap.w11},
            {12,R.mipmap.w12},
            {13,R.mipmap.w13},
            {14,R.mipmap.w14},
            {15,R.mipmap.w15},
            {16,R.mipmap.w16},
            {17,R.mipmap.w17},
            {18,R.mipmap.w18},
            {19,R.mipmap.w19},
            {20,R.mipmap.w20},
            {21,R.mipmap.w21},
            {22,R.mipmap.w22},
            {23,R.mipmap.w23},
            {24,R.mipmap.w24},
            {25,R.mipmap.w25},
            {26,R.mipmap.w26},
            {27,R.mipmap.w27},
            {28,R.mipmap.w28},
            {29,R.mipmap.w29},
            {30,R.mipmap.w30},
            {31,R.mipmap.w31},
            {53,R.mipmap.w53}
    };


}