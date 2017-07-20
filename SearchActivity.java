package com.xianyi.chen.repair;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    private TableRow trCompletechecktime;
    private Spinner spiType,spiBugType;
    private EditText etAddress,etDescription,etStartBugfindtime,etEndBugfindtime,etStartCompleteChecktime,etEndCompleteChecktime;
    private Button btnSubmit,btnReturn;
    int year = 2016;
    int month = 10;
    int day = 8;
    private Integer type = 88;
    private String bugtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spiType = (Spinner) findViewById(R.id.spiType);
        spiBugType = (Spinner) findViewById(R.id.spiBugType);
        trCompletechecktime = (TableRow)findViewById(R.id.trCompletechecktime);
        etStartBugfindtime = (EditText) findViewById(R.id.etStartBugfindtime);
        etEndBugfindtime = (EditText) findViewById(R.id.etEndBugfindtime);
        etStartCompleteChecktime = (EditText) findViewById(R.id.etStartCompleteChecktime);
        etEndCompleteChecktime = (EditText) findViewById(R.id.etEndCompleteChecktime);
        etAddress=(EditText) findViewById(R.id.etAddress);
        etDescription=(EditText)findViewById(R.id.etDescription);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnReturn = (Button) findViewById(R.id.btnReturn);

        int flag=getIntent().getIntExtra("flag",0);
        if(flag==1){
            trCompletechecktime.setVisibility(View.VISIBLE);
        }else {
            trCompletechecktime.setVisibility(View.GONE);
        }

        //初始化Calendar日历对象
        Calendar mycalendar=Calendar.getInstance();

        year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天

        //筛选
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this,TaskListActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("type",type.toString());
                bundle.putString("bugtype",bugtype);
                bundle.putString("address",etAddress.getText().toString());
                bundle.putString("description",etDescription.getText().toString());
                bundle.putString("startbugfindtime",etStartBugfindtime.getText().toString());
                bundle.putString("endbugfindtime",etEndBugfindtime.getText().toString());
                bundle.putString("startcompletechecktime",etStartCompleteChecktime.getText().toString());
                bundle.putString("endcompletechecktime",etEndCompleteChecktime.getText().toString());
                intent.putExtras(bundle);
                setResult(1,intent);
                finish();
            }
        });
        //返回
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this,TaskListActivity.class);
                setResult(0,intent);
                finish();
            }
        });

        //故障类型选择下拉框
        spBind_Type();

        //故障分类选择下拉框
        spBind_BugType();

        //发现时间
        etStartBugfindtime.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=1;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touch_flag++;
                if(touch_flag%3==0){
                    getStartDate();
                }
                return false;
            }
        });
        etEndBugfindtime.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=1;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touch_flag++;
                if(touch_flag%3==0){
                    getEndDate();
                }
                return false;
            }
        });

        //完工时间
        etStartCompleteChecktime.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=1;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touch_flag++;
                if(touch_flag%3==0){
                    getStartComDate();
                }
                return false;
            }
        });
        etEndCompleteChecktime.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int touch_flag=1;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                touch_flag++;
                if(touch_flag%3==0){
                    getEndComDate();
                }
                return false;
            }
        });

    }

    /**
     * 故障类型选择下拉框：
     * **/
    private void spBind_Type()
    {
        String[] mItems = new String[3];// 建立数据源
        String strType = "";
        String strType0 = "小故障";
        String strType1 = "大故障";
        mItems[0]=strType;
        mItems[1]=strType0;
        mItems[2]=strType1;
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);// 建立Adapter并且绑定数据源
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiType.setAdapter(adapter);//绑定 Adapter到控件
        //故障选择下拉框值改变事件
        spiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    type=0;
                }
                else if(i==2){
                    type=1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    /**
     * 故障分类选择下拉框：
     * **/
    private void spBind_BugType()
    {
        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.bugtypes);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);// 建立Adapter并且绑定数据源
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiBugType.setAdapter(adapter);//绑定 Adapter到控件
        spiBugType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] languages = getResources().getStringArray(R.array.bugtypes);
                bugtype = languages[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    /**
     * 搜索条件：故障发现时间-开始
     * **/
    public void getStartDate(){

        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                year, month, day);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();
                etStartBugfindtime.setText(year + "-" + (month+1) + "-" + day); // 显示选择日期
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
        mDialog.show();

    }
    /**
     * 搜索条件：故障发现时间-结束
     * **/
    public void getEndDate(){

        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                year, month, day);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();
                etEndBugfindtime.setText(year + "-" + (month+1) + "-" + day); // 显示选择日期
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
        mDialog.show();

    }

    /**
     * 搜索条件：完工确认时间-开始
     * **/
    public void getStartComDate(){

        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                year, month, day);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();
                etStartCompleteChecktime.setText(year + "-" + (month+1) + "-" + day); // 显示选择日期
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
        mDialog.show();

    }
    /**
     * 搜索条件：完工确认时间-结束
     * **/
    public void getEndComDate(){

        final DatePickerDialog mDialog = new DatePickerDialog(this, null,
                year, month, day);
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                DatePicker datePicker = mDialog.getDatePicker();
                year = datePicker.getYear();
                month = datePicker.getMonth();
                day = datePicker.getDayOfMonth();
                etEndCompleteChecktime.setText(year + "-" + (month+1) + "-" + day); // 显示选择日期
            }
        });
        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
        mDialog.show();

    }

}
