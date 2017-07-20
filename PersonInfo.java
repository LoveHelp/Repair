package com.xianyi.chen.repair;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class PersonInfo extends AppCompatActivity {

    private TextView tv_title;
    private String spec = "";
    private String data = "";
    private String result="";
    private String userid=UserModel.getuserid();
    private String personname,username,rolename,tel;
    private TextView tvPersonname,tvUsername,tvRolename,tvTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);

        InitControl();//初始化控件

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
        tvPersonname=(TextView)findViewById(R.id.tvPersonname);
        tvUsername=(TextView)findViewById(R.id.tvUsername);
        tvRolename=(TextView)findViewById(R.id.tvRolename);
        tvTel=(TextView)findViewById(R.id.tvTel);

        tv_title.setText("个人信息");
    }

    /**
     * 开始获取数据
     */
    private void DetailInfo(){
        new Thread(){
            @Override
            public void run(){
                data="userid=" + userid;
                spec=UserModel.myhost+"GetPersoninfo.php";
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                PersonInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result==null){
                            Toast.makeText(PersonInfo.this, "网络请求超时...", Toast.LENGTH_LONG).show();
                        }else if (!result.equals("null") && !result.equals("")) {
                            // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                            try{
                                JSONArray jsonArray=new JSONArray(result);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                personname = "真实姓名：";
                                if(!jsonObject.getString("personname").equals("null")){
                                    personname += jsonObject.getString("personname");
                                }
                                username = "用户账号：" + jsonObject.getString("username");
                                rolename="所属角色：" + jsonObject.getString("role");
                                tel = "电话号码：";
                                if(!jsonObject.getString("tel").equals("null")){
                                    tel += jsonObject.getString("tel");
                                }
                                //更新界面
                                tvPersonname.setText(personname);
                                tvUsername.setText(username);
                                tvRolename.setText(rolename);
                                tvTel.setText(tel);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else{
                            Toast.makeText(PersonInfo.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }.start();

    }

}
