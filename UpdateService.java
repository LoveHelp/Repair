package com.xianyi.chen.repair;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

/**
 * 检测安装更新文件的助手类
 * Created by Administrator on 2017-03-02 .
 **/
public class UpdateService extends Service{
    public UpdateService(){
    }

    /** 安卓系统下载类 **/
    DownloadManager manager;

    /** 接收下载完的广播 **/
    DownloadCompleteReceiver receiver;

    /** 下载进度提醒 **/
    ProgressDialog proDialog;

    /** 下载文件的完整路径 **/
    private String URL=UserModel.URL;
    private String APKNAME=UserModel.APKNAME;

    /** 初始化下载器 **/
    private void initDownManager(){
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(URL));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(URL));
        down.setMimeType(mimeString);

        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        String url1=Environment.getExternalStorageDirectory().getAbsolutePath();///storage/emulated/0
        String url2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();///storage/emulated/0/Download
        String url3 = Environment.DIRECTORY_DOWNLOADS;//Download
        down.setDestinationInExternalFilesDir(this, url2, APKNAME);
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 显示正在下载进度条
     * **/
    private void progress(){
        proDialog=new ProgressDialog(this);
        proDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));//在dialog show 方法之前添加这个代码，表示该dialog是系统的dialog。
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        proDialog.setTitle("提示");
        proDialog.setMessage("正在下载，请稍候...");
        proDialog.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        progress();
        String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + APKNAME;// /storage/emulated/0/Download/repair.apk
        File file = new File(path);
        if(file.exists()){
            deleteFileWithPath(path);
        }

        try{
            initDownManager();//调用下载
        }catch (Exception e){
            e.printStackTrace();
            proDialog.dismiss();
        }
        //return Service.START_NOT_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);

        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                proDialog.dismiss();
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //自动安装apk
                if(manager.getUriForDownloadedFile(downId)!=null){
                    String path = getRealFilePath(context,manager.getUriForDownloadedFile(downId));
                    installAPK(context,path);
                }else{
                    Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
                }
                //停止服务并关闭广播
                UpdateService.this.stopSelf();
            }
        }

        public String getRealFilePath(Context context, Uri uri) {
            if (null == uri) return null;
            final String scheme = uri.getScheme();
            String data = null;
            if (scheme == null)
                data = uri.getPath();
            else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                data = uri.getPath();
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            }
            return data;
        }

        /**
         * 安装apk文件
         */
        private void installAPK(Context context,String path) {
            File file = new File(path);
            if(file.exists()){
                openFile(file,context);
            }else{
                Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     *重点在这里
     */
    public void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
            return true;
        }
        return false;
    }

}