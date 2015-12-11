package com.example.getpm25;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText editText;
    private Button pm25,weather;
    private TextView textView;

    Task task;
    WeatherTask weatherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        pm25 =(Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView);
        weather = (Button)findViewById(R.id.button2);

        pm25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText.getText().toString();
                //if (id.length()<1){
                 if(TextUtils.isEmpty(id)){
                    Toast.makeText(MainActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                task = new Task(MainActivity.this,textView);
                task.execute(id);
            }
        });


        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add = editText.getText().toString().trim();
                if(TextUtils.isEmpty(add)){
                    Toast.makeText(MainActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                weatherTask = new WeatherTask(MainActivity.this,textView);
                weatherTask.execute(add);
            }
        });





    }


}
