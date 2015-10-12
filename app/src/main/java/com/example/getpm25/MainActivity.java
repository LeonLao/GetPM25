package com.example.getpm25;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText editText;
    private Button button;
    private TextView textView;
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText)findViewById(R.id.editText);
        button =(Button)findViewById(R.id.button);
        textView = (TextView)findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText.getText().toString();
                if (id.length()<1){
                    Toast.makeText(MainActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();;
                    return;
                }
                task = new Task(MainActivity.this,textView);
                task.execute(id);
            }
        });





    }


}
