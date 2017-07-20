package com.xianyi.chen.repair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class BigPicActivity extends AppCompatActivity {

    private ViewPager viewPager;
    //private PhotoViewPager viewPager;
    private ImageView[] tips;//提示性点点数组
    private int currentPage = 0;//当前展示的页码
    private String[] bugPhoto = null;//图片名称数组
    private String[] newPhoto=null;
    private static String wurl = UserModel.IMAGE_PATH;//图片服务器地址
    private PagerAdapter adapter;
    private ProgressDialog proDialog;
    private List<Bitmap> images = null;
    private int count = 0;//计数器

    private Activity activity;
    private GestureDetector gestureDetector;
    private int downX, downY;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_big_pic);
        activity=this;

        viewPager = (ViewPager)findViewById(R.id.vpBigPic);
        //viewPager = (PhotoViewPager) findViewById(R.id.vpBigPic);

        Intent getIntent = getIntent();
        String strPhoto = getIntent.getStringExtra("photo");
        if(strPhoto.equals("null") || strPhoto.equals("")){
            Toast.makeText(BigPicActivity.this, "暂无图片！", Toast.LENGTH_SHORT).show();
            activity.finish();
        }else {
            bugPhoto = strPhoto.replace("，",",").split(",");
            proDialog = ProgressDialog.show(BigPicActivity.this, "", "加载中，请稍候...");
            images = new ArrayList<>(bugPhoto.length);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        newPhoto=new String[bugPhoto.length];
                        for(int i=0; i<bugPhoto.length; i++) {
                            String photo=bugPhoto[i];
                            String photoname = photo.substring(0,photo.length()-4);
                            String p=wurl +URLEncoder.encode(photoname,"UTF-8")+".jpg";
                            //String p=wurl + bugPhoto[i];
                            newPhoto[i]=p;
                            Bitmap bitmap = PostParamTools.getHttpBitmap(newPhoto[i]);
                            images.add(bitmap);
                            count++;
                        }
                        if(count == 0){
                            handler.sendEmptyMessage(0);
                        }else if(count == bugPhoto.length){
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(2);
                        }
                    }catch (Exception e){
                        proDialog.dismiss();
                        Toast.makeText(BigPicActivity.this, "图片加载失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                downX = (int) e.getX();
                downY = (int) e.getY();
            }
        });

    }

    @Override
    protected void onDestroy(){
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            proDialog.dismiss();//万万不可少这句，否则程序会卡死。
            switch (msg.what){
                case 0:
                    Toast.makeText(BigPicActivity.this, "暂无图片！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    setViewPager();//发送消息通知ListView更新
                    break;
                case 2:
                    Toast.makeText(BigPicActivity.this, "部分图片加载失败！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(BigPicActivity.this, "图片加载失败！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void setViewPager(){
        //存放点点的容器
        LinearLayout tipsBox = (LinearLayout)findViewById(R.id.tipsBox);
        //初始化 提示点点
        tips = new ImageView[bugPhoto.length];
        for(int i=0;i<tips.length;i++){
            ImageView img = new ImageView(this);
            img.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            tips[i] = img;
            if(i == 0) {
                img.setBackgroundResource(R.drawable.page_now);
            }else{
                img.setBackgroundResource(R.drawable.page);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin=5;
            params.rightMargin=5;
            tipsBox.addView(img, params);
        }
        //数据适配器
        adapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return bugPhoto.length;
            }

            @Override
            //断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            //是从ViewGroup中移出当前View
            public void destroyItem(ViewGroup container, int position, Object o){
                //container.removeViewAt(position);
            }

            @Override
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(ViewGroup container,final int position){

                final ImageView im = new ImageView(BigPicActivity.this);
                im.setImageBitmap(images.get(position));
                PhotoViewAttacher attacher=new PhotoViewAttacher(im);

                try {
                    String photo=bugPhoto[position];
                    String photoname = photo.substring(0,photo.length()-4);
                    imgurl = wurl +URLEncoder.encode(photoname,"UTF-8")+".jpg";
                    //imgurl=wurl +bugPhoto[position];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                attacher.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // 相应长按事件弹出菜单
                        final ItemLongClickedPopWindow itemLongClickedPopWindow = new ItemLongClickedPopWindow(BigPicActivity.this,
                                ItemLongClickedPopWindow.IMAGE_VIEW_POPUPWINDOW,220, 120);
                        itemLongClickedPopWindow.showAtLocation(v,  Gravity.CENTER| Gravity.CENTER, downX, downY + 10);
                        itemLongClickedPopWindow.getView(R.id.item_longclicked_saveImage)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        itemLongClickedPopWindow.dismiss();
                                        new SaveImage().execute(newPhoto[position]); // Android 4.0以后要使用线程来访问网络
                                    }
                                });
                        return true;
                    }
                });

                container.addView(im);
                return im;
            }
        };

        viewPager.setAdapter(adapter);

        //更改当前tip
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override

            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                //Log.e("rf", String.valueOf(position));
                tips[currentPage].setBackgroundResource(R.drawable.page);
                currentPage=position;
                tips[position].setBackgroundResource(R.drawable.page_now);
            }

        });
    }
    private String imgurl = "";

    /***
     * 功能：用线程保存图片
     *
     * @author wangyp
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
            String iurl = params[0];
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();// /storage/emulated/0
                //pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();// /storage/emulated/0/DCIM
                file = new File(sdcard + "/DCIM/Camera");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = iurl.lastIndexOf(".");
                String ext = iurl.substring(idx);
                file = new File(sdcard + "/DCIM/Camera/" + new Date().getTime() + ext);
                InputStream inputStream = null;
                URL url = new URL(iurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                //result = "图片已保存至：" + file.getAbsolutePath();
                result = "图片已保存至相册";
                msc.connect();
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(BigPicActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存完图片后，可以在内存设备的文件系统相册目录下看到对应图片（以oppo手机为例，系统相册的路径为：/storage/emulated/0/DCIM/Camera）。
     * 但是，使用系统图库无法马上看到该图片，需要重启手机才能看到，因为保存图片后没有更新图库的缘故。
     * 因此，可以在保存图片后使用MediaScannerConnection来更新图库。
     * **/
    final MediaScannerConnection msc = new MediaScannerConnection(BigPicActivity.this, new MediaScannerConnection.MediaScannerConnectionClient() {

        public void onMediaScannerConnected() {
            msc.scanFile(file.getAbsolutePath(), "image/jpeg");
        }

        public void onScanCompleted(String path, Uri uri) {
            msc.disconnect();
        }
    });

}
