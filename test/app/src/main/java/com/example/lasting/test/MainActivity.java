package com.example.lasting.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener,RadioGroup.OnCheckedChangeListener{
    private Button button1;
    private Button button2;
    private Button button3;
    private AutoCompleteTextView autoCompleteTextView1;
    private MultiAutoCompleteTextView multiAutoCompleteTextView1;
    private ToggleButton toggleButton1;
    private ImageButton imageButton1;
    private String[] res = {"beijing1","beijing2","beijing3","shanghai1","shanghai2","shanghai3"};
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private RadioGroup radioGroup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1=(Button)findViewById(R.id.button1);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        autoCompleteTextView1=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView1);
        multiAutoCompleteTextView1=(MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextView1);
        toggleButton1=(ToggleButton)findViewById(R.id.toggleButton1);
        imageButton1=(ImageButton)findViewById(R.id.imageButton1);
        checkBox1=(CheckBox)findViewById(R.id.checkBox1);
        radioGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
        button1.setOnClickListener(new MyOnClickListener(){
            @Override
            public void onClick(View v) {
                super.onClick(v);
                Toast.makeText(MainActivity.this, "hello world", Toast.LENGTH_LONG).show();
            }
        });
        button2.setOnClickListener(this);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "hello world", Toast.LENGTH_LONG).show();
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,res);
        autoCompleteTextView1.setAdapter(arrayAdapter);
        multiAutoCompleteTextView1.setAdapter(arrayAdapter);
        multiAutoCompleteTextView1.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        toggleButton1.setOnCheckedChangeListener(this);
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String selectedText = checkBox1.getText().toString();
                    Log.i("tag",selectedText);
                }
            }
        });
        radioGroup1.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"hello world",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        imageButton1.setBackgroundResource(isChecked?R.drawable.ic_launcher1:R.drawable.ic_launcher2);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i){
            case R.id.radioButton1:
                Toast.makeText(this,"男",Toast.LENGTH_LONG).show();
                break;
            case R.id.radioButton2:
                Toast.makeText(this,"女",Toast.LENGTH_LONG).show();
                break;
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i("tag","hello world");
        }
    }
}