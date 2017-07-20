package com.xianyi.chen.repair;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017-02-20 .
 **/
public class FooterLayout extends LinearLayout {

    public FooterLayout(Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.footer, this);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

    }

    /** 初始化控件 **/
    private void InitControl(){

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){

    }

}
