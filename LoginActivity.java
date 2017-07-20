package com.xianyi.chen.repair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity {
    private EditText et_name, et_pass;
    private TextView tv_result;
    private CheckBox autocheckbox;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SQLiteDatabase repairdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repairdb=openOrCreateDatabase("repair", Context.MODE_PRIVATE,null);
        setContentView(R.layout.activity_login);

        SQLiteDatabase db=openOrCreateDatabase("mydb",MODE_PRIVATE,null);
        et_name = (EditText) findViewById(R.id.username);
        // 通过 findViewById(id)方法获取用户密码的控件对象
        et_pass = (EditText) findViewById(R.id.password);
        autocheckbox=(CheckBox)findViewById(R.id.checkBox);
        // 通过 findViewById(id)方法获取显示返回数据的控件对象
        sharedPreferences=getSharedPreferences("autologin",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        Integer exit = getIntent().getIntExtra("exit",0);
        if(exit==1){
            UserModel.settimerflag(false);
            UserModel.setuserid("");
            UserModel.setusername("");
            UserModel.setpersonname("");
            UserModel.settel("");
            UserModel.setroleid("");
            UserModel.setrole("");
            editor.clear();
            editor.commit();
        }

        tv_result = (TextView) findViewById(R.id.result);
        Boolean bool= sharedPreferences.getBoolean("autocheck", false);
        autocheckbox.setChecked(bool);

        if(autocheckbox.isChecked()){
            String username=sharedPreferences.getString("username","");
            String password=sharedPreferences.getString("password","");
            et_name.setText(username);
            et_pass.setText(password);
            login(findViewById(R.id.loginbutton));
        }
    }

    public void login(View v) {
        // 获取点击控件的id
        int id = v.getId();
        // 根据id进行判断进行怎么样的处理
        switch (id) {
            // 登陆事件的处理
            case R.id.loginbutton:
                // 获取用户名
                final String userName = et_name.getText().toString();
                // 获取用户密码
                final String userPass = et_pass.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPass)) {
                    Toast.makeText(this, "用户名或者密码不能为空", Toast.LENGTH_LONG).show();
                }  else {
                    // 开启子线程
                    new Thread() {
                        public void run() {
                            loginByPost(userName, userPass); // 调用loginByPost方法
                        };
                    }.start();

                }

                break;
        }

    }

    public void loginByPost(String userName, String userPass) {

        try{
            String spec = UserModel.myhost+"login.php";
            String data = "username=" + URLEncoder.encode(userName, "UTF-8")+ "&userpass=" + URLEncoder.encode(userPass, "UTF-8");
            final String result = PostParamTools.postGetInfo(spec,data);

            // 通过runOnUiThread方法进行修改主线程的控件内容
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(result == null){
                        Toast.makeText(LoginActivity.this, "网络链接超时!",
                                Toast.LENGTH_LONG).show();
                    }else {
                        // 返回主界面
                        switch (result)
                        {
                            case "1":
                                //tv_result.setText("用户名错误!");
                                Toast.makeText(LoginActivity.this, "用户名错误!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            case "2":
                                //tv_result.setText("密码错误!");
                                Toast.makeText(LoginActivity.this, "密码错误!",
                                        Toast.LENGTH_LONG).show();
                                break;
                            default:
                                try
                                {// 将json字符串转换为json对象
                                    JSONObject jsonObj = new JSONObject(result);// 得到指定json key对象的value对象
                                    UserModel.setuserid(jsonObj.getString("userid"));
                                    UserModel.setusername(jsonObj.getString("username"));
                                    UserModel.setpersonname(jsonObj.getString("personname"));
                                    UserModel.settel(jsonObj.getString("tel"));
                                    UserModel.setroleid(jsonObj.getString("roleid"));
                                    UserModel.setrole(jsonObj.getString("role"));
                                }
                                catch (Exception e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                //tv_result.setText("登录成功");
                                Toast.makeText(LoginActivity.this, "登录成功!",
                                        Toast.LENGTH_LONG).show();
                                if(autocheckbox.isChecked()){//记录自动登录信息
                                    editor.putBoolean("autocheck",true);
                                    editor.putString("username", et_name.getText().toString());
                                    editor.putString("password",et_pass.getText().toString());
                                    editor.commit();
                                }else {
                                    editor.putBoolean("autocheck",false);
                                    editor.commit(); }
                                Intent intent = new Intent();
                                String roleid=UserModel.getroleid();
                                switch (roleid)
                                {
                                    case "1":
                                        intent.setClass(LoginActivity.this, FindbugActivity.class);
                                        startActivity(intent) ;
                                        break;
                                    case "2":
                                        intent.setClass(LoginActivity.this,MainActivity.class);
                                        startActivity(intent) ;
                                        break;
                                    case "3":
                                        intent.setClass(LoginActivity.this,MainJiaFangActivity.class);
                                        startActivity(intent) ;
                                        break;
                                    case "4":
                                        intent.setClass(LoginActivity.this,MainWeixiuActivity.class);
                                        startActivity(intent) ;
                                        break;
                                    default:
                                        break;
                                }
                                LoginActivity.this.finish();

                        }
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
