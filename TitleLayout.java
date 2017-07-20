package com.xianyi.chen.repair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017-06-26 .
 **/
public class TitleLayout extends LinearLayout {

    private Button btnIndex;
    private LinearLayout llReturn;
    private String rid=UserModel.getroleid();

    public TitleLayout(Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.title,this);

        btnIndex = (Button)findViewById(R.id.btnIndex);
        llReturn=(LinearLayout)findViewById(R.id.llReturn);

        //返回上一级
        llReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int isindex = ((Activity)getContext()).getIntent().getIntExtra("isindex",0);
                if(isindex==0){
                    ((Activity)getContext()).finish();
                }else {
                    Intent intent = new Intent();
                    switch (rid){
                        case "2":
                            intent.setClass(getContext(),MainActivity.class);
                            break;
                        case "3":
                            intent.setClass(getContext(),MainJiaFangActivity.class);
                            break;
                        case "4":
                            intent.setClass(getContext(),MainWeixiuActivity.class);
                            break;
                        default:
                            intent.setClass(getContext(),LoginActivity.class);
                            break;
                    }
                    getContext().startActivity(intent);
                }

            }
        });
        //返回首页
        btnIndex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (rid){
                    case "2":
                        intent.setClass(getContext(),MainActivity.class);
                        break;
                    case "3":
                        intent.setClass(getContext(),MainJiaFangActivity.class);
                        break;
                    case "4":
                        intent.setClass(getContext(),MainWeixiuActivity.class);
                        break;
                    default:
                        intent.setClass(getContext(),LoginActivity.class);
                        break;
                }
                getContext().startActivity(intent);
            }
        });

    }

}
