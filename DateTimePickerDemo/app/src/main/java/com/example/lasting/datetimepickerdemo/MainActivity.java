package com.example.lasting.datetimepickerdemo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends Activity {
    private DatePicker datePicker1;
    private TimePicker timePicker1;
    private Calendar calendar1;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar1=Calendar.getInstance();
        year=calendar1.get(Calendar.YEAR);
        month=calendar1.get(Calendar.MONTH)+1;
        day=calendar1.get(Calendar.DAY_OF_MONTH);
        hour=calendar1.get(Calendar.HOUR_OF_DAY);
        minute=calendar1.get(Calendar.MINUTE);
        setTitle(year+"-"+month+"-"+day+" "+hour+":"+minute);
        datePicker1=(DatePicker)findViewById(R.id.datePicker1);
        timePicker1=(TimePicker)findViewById(R.id.timePicker1);
        datePicker1.init(year, calendar1.get(Calendar.MONTH), day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                setTitle(i + "-" + (i1 + 1) + "-" + i2);
            }
        });
        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                setTitle(i+":"+i);
            }
        });
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                setTitle(i+"-"+i1+"-"+i2);
            }
        },day,calendar1.get(Calendar.MONTH),day).show();
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                setTitle(i+":"+i);
            }
        },hour,minute,true).show();
    }
}
