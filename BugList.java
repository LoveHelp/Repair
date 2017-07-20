package com.xianyi.chen.repair;

/**
 * Created by Administrator on 2016/8/22 0022.
 */
public class BugList{
    private int bugId;
    private String bugName;
    private String bugReceiveTime;

    public BugList(int bugId, String bugName, String bugReceiveTime){
        this.bugId = bugId;
        this.bugName = bugName;
        this.bugReceiveTime = bugReceiveTime;
    }

    public int getBugId(){
        return bugId;
    }

    public String getBugName(){
        return bugName;
    }

    public String getBugReceiveTime(){
        return bugReceiveTime;
    }
}

