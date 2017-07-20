package com.xianyi.chen.repair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-03-02 .
 **/
public class UpdateManager {
    private Context mContext;
    public UpdateManager(Context context) {
        this.mContext = context;
    }
    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        getVerCodeInfo();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    Toast.makeText(mContext, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "文件不存在!", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    showNoticeDialog();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 新旧版本对比：新版本号大于旧版本号就更新
     * **/
    private void getVerCodeInfo(){
        new Thread(){
            @Override
            public void run(){
                String strNewVerCode = PostParamTools.postGetInfo(UserModel.myhost+"verReadCode.php","");
                if(strNewVerCode == null){
                    handler.sendEmptyMessage(0);//网络链接超时
                }else if(strNewVerCode.equals("")){
                    handler.sendEmptyMessage(1);//文件不存在
                }else {
                    //handler.sendEmptyMessage(2);//文件打开正确
                    int iOldVerCode = getOldVerCode();
                    int iNewVerCode = Integer.parseInt(strNewVerCode);
                    if(iNewVerCode > iOldVerCode){
                        handler.sendEmptyMessage(2);
                    }
                }
            }
        }.start();
    }
    /**
     * 获取版本代码
     * **/
    public int getOldVerCode() {
        int verCode = -1;
        try {
            verCode = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", e.getMessage());
        }
        return verCode;
    }
    /**
     * 显示更新提醒框
     * **/
    private void showNoticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setIcon(android.R.drawable.ic_dialog_info);//窗口头图标
        String updateMsg = "有最新的软件包哦，亲快下载吧~";
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateApp();//自动更新
            }
        });
//        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        Dialog noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        noticeDialog.show();
    }

    /**
     * 启动自动检测软件更新服务
     * **/
    public void updateApp() {
        Intent service = new Intent(mContext,UpdateService.class);
        mContext.startService(service);
    }

}
