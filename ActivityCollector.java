package com.xianyi.chen.repair;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理器
 * Created by Administrator on 2017-04-05 .
 **/
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    /**
     * 添加活动到活动集合中
     * **/
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    /**
     * 从活动集合中删除活动
     * **/
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    /**
     * 结束掉活动集合中所有活动
     * **/
    public  static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }

}
