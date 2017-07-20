package com.xianyi.chen.repair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Administrator on 2017-02-20 .
 **/
public class HeaderLayout extends LinearLayout {

    //账号信息
    private TextView tv_truename,tv_username;
    private ImageView ivTouxiang;

    private String spec = "";
    private String data = "";
    private String result="";
    private String userid=UserModel.getuserid();
    private String roleid=UserModel.getroleid();

    public HeaderLayout(Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.header, this);

        ImageView iv_exit = (ImageView)findViewById(R.id.iv_exit);
        //账号信息
        tv_truename=(TextView)findViewById(R.id.tv_truename);
        tv_username=(TextView)findViewById(R.id.tv_username);
        ivTouxiang=(ImageView)findViewById(R.id.ivTouxiang);
        String rolename = UserModel.getrole();
        String truename = UserModel.getpersonname();
        String username = UserModel.getusername();
        String name = rolename + " " + truename;
        tv_truename.setText(name);
        tv_username.setText(username);

        DetailInfo();

        //注销
        iv_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel.setFlag(1);
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.putExtra("exit",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//FLAG_ACTIVITY_CLEAR_TOP
                getContext().startActivity(intent);
                ((Activity) getContext()).finish();
            }
        });

    }

    /**
     * 开始获取数据
     */
    private void DetailInfo(){
        new Thread(){
            @Override
            public void run(){
                data="userid=" + userid;
                spec=UserModel.myhost+"GetTouxiangByUserid.php";
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result == null){

                }else if (!result.equals("null") && !result.equals("")) {
                    try{
                        JSONArray jsonArray=new JSONArray(result);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String touxiang = jsonObject.getString("touxiang");//UserModel.myhost +
                        //touxiang = UserModel.IP + touxiang;
                        touxiang = UserModel.TOUXIANG_PATH + touxiang;

                        /**
                         * 通过ImageOptions.Builder().set方法设置图片的属性
                         */
                        ImageOptions options = new ImageOptions.Builder()
                                .setFadeIn(true)//淡入效果
                                .setCircular(true)//圆形
                                .setUseMemCache(true) //设置使用MemCache，默认true
                                .build();
                        x.image().bind(ivTouxiang,touxiang,options);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else{

                }

            }
        }.start();

    }

}
