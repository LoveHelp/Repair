package com.xianyi.chen.repair;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class UpdatePwd extends AppCompatActivity {

    private TextView tv_title;
    private Button btnUpdpwd;
    private EditText etOldpwd,etNewpwd,etSurepwd;
    private String spec = "";
    private String data = "";
    private String result="";
    private String userid=UserModel.getuserid();
    private String pwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_pwd);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        /**
         * 开始获取数据
         */
        DetailInfo();

    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){
        tv_title=(TextView)findViewById(R.id.tv_title);

        btnUpdpwd=(Button)findViewById(R.id.btnUpdpwd);
        etOldpwd=(EditText)findViewById(R.id.etOldpwd);
        etNewpwd=(EditText)findViewById(R.id.etNewpwd);
        etSurepwd=(EditText)findViewById(R.id.etSurepwd);

        tv_title.setText("修改密码");
    }

    /**
     * 初始化控件事件
     *  **/
    private void InitControlEvent(){

        //确定修改密码
        btnUpdpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SureBug();
            }
        });

    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    Toast.makeText(UpdatePwd.this, "网络链接超时...", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(UpdatePwd.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                    break;
                case 2://获取密码
                    try{
                        JSONArray jsonArray=new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        pwd = jsonObject.getString("password");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    Toast.makeText(UpdatePwd.this, "旧密码输入错误，请重新输入！", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(UpdatePwd.this, "修改成功！", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(UpdatePwd.this, "两次密码输入不一致，请重新输入！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 开始获取数据
     */
    private void DetailInfo(){
        new Thread(){
            @Override
            public void run(){
                data="userid=" + userid;
                spec=UserModel.myhost+"GetPwdByUserid.php";
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result==null){
                    mHandler.sendEmptyMessage(0);
                }else if (!result.equals("null") && !result.equals("")) {
                    // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                    mHandler.sendEmptyMessage(2);
                } else{
                    mHandler.sendEmptyMessage(1);
                }

            }
        }.start();

    }

    /**
     * 修改密码
     * **/
    private void SureBug(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String oldPwd = etOldpwd.getText().toString();
                    String newPwd = etNewpwd.getText().toString();
                    String surePwd = etSurepwd.getText().toString();

                    String inputPwd = PostParamTools.md5(oldPwd);
                    if(!inputPwd.equals(pwd)){
                        mHandler.sendEmptyMessage(3);
                    }else if(!newPwd.equals(surePwd)){
                        mHandler.sendEmptyMessage(5);
                    }else {
                        data = "userid=" + userid + "&newPwd=" + newPwd;
                        spec=UserModel.myhost+"SavePwd.php";
                        result= PostParamTools.postGetInfo(spec, data);
                        if(result == null){
                            mHandler.sendEmptyMessage(0);
                        }else if(!result.equals("null") && !result.equals("0")){
                            //提交成功，跳转页面
                            mHandler.sendEmptyMessage(4);
                        } else {
                            //提交失败
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
            }
        }).start();
    }
}
