package com.xianyi.chen.repair;

/**
 * Created by Administrator on 2016-8-17.
 **/
public class UserModel {
    public static String IP = "http://122.114.144.156";//"http://192.168.16.99";//
    public static String PROJECT = "repair";
    public static String myhost = IP + "/" + PROJECT + "/";
    public static String IMAGE_PATH = myhost + "upload/";
    public static String TOUXIANG_PATH = IMAGE_PATH + "touxiang/";
    public static String FILE_PATH = myhost + "upload/baojiafile/";
    public static String LOG = myhost + "log/";

    /** 下载文件的完整路径 **/
    public static String APKNAME = PROJECT + ".apk";
    public static String URL= myhost + "APK/" + APKNAME;

    public static String DBNAME = PROJECT + ".db";

    private static String userid ="0";
    private static String username ="";
    private static String personname ="";
    private static String tel ="";
    private static String roleid ="";
    private static String role ="";
    private static Boolean timerflag=false;
    private static int flag = 1;//底部菜单标志：1首页；2任务；3查询；4我的
    public static String getuserid() {
        return userid;
    }
    public static void setuserid(String _userid) {
        UserModel.userid = _userid;
    }
    public static String getusername() {
        return username;
    }
    public static void setusername(String _username) {
        UserModel.username = _username;
    }
    public static String getpersonname() {
        return personname;
    }
    public static void setpersonname(String _personname) {
        UserModel.personname = _personname;
    }

    public static String gettel() {
        return tel;
    }
    public static void settel(String _tel) {
        UserModel.tel = _tel;
    }
    public static String getroleid() {
        return roleid;
    }
    public static void setroleid(String _roleid) {
        UserModel.roleid = _roleid;
    }
    public static String getrole() {
        return role;
    }
    public static void setrole(String _role) {
        UserModel.role = _role;
    }
    public static Boolean gettimerflag() {
        return timerflag;
    }
    public static void settimerflag(Boolean _timerflag) {
        UserModel.timerflag = _timerflag;
    }
    public static int getFlag(){return flag;}
    public static void setFlag(int _flag){UserModel.flag = _flag;}
}
