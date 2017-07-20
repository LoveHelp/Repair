package com.xianyi.chen.repair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/9/3 0003.
 */
public class ViewPagerActivity extends Activity {
    private ViewPager viewPager;

    private ImageView[] tips;//提示性点点数组

    private int currentPage = 0;//当前展示的页码

    private String[] bugPhoto = null;//图片名称数组
    private String[] newPhoto=null;

    private static String WEBURL = UserModel.IMAGE_PATH;//图片服务器地址

    private List<Bitmap> images = null;
    private PagerAdapter adapter;

    private int count = 0;//计数器
    private ProgressDialog proDialog;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_view);
        activity=this;

        // 获取BugDetail传递的照片List
        Intent getIntent = getIntent();
        String strPhoto = getIntent.getStringExtra("bugPhoto");
        bugPhoto = strPhoto.replace("，",",").split(",");
        if(strPhoto.equals("null") || strPhoto.equals("")){
            Toast.makeText(ViewPagerActivity.this, "暂无图片！", Toast.LENGTH_SHORT).show();
            activity.finish();
        }else {
            proDialog = ProgressDialog.show(ViewPagerActivity.this, "", "加载中，请稍候...");
            images = new ArrayList<>(bugPhoto.length);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        newPhoto=new String[bugPhoto.length];
                        for(int i=0; i<bugPhoto.length; i++) {
                            String photo=bugPhoto[i];
                            String photoname = photo.substring(0,photo.length()-4);
                            String p=WEBURL + URLEncoder.encode(photoname,"UTF-8")+".jpg";
                            newPhoto[i]=p;

                            Bitmap bitmap = PostParamTools.getHttpBitmap(WEBURL + bugPhoto[i]);
                            images.add(bitmap);
                            count++;
                        }
                        if(count == bugPhoto.length){
                            handler.sendEmptyMessage(0);
                        }
                    }catch (Exception e) {
                        proDialog.dismiss();
                        Toast.makeText(ViewPagerActivity.this, "图片加载失败！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    public void setViewPager(){
        viewPager = (ViewPager)findViewById(R.id.viewpager1);

        //存放点点的容器
        LinearLayout tipsBox = (LinearLayout)findViewById(R.id.tipsBox);

        //初始化图片资源
        //images = new int[]{R.drawable.view1,R.drawable.view2,R.drawable.view3,R.drawable.image2,R.drawable.image1};

        //初始化 提示点点
        tips = new ImageView[bugPhoto.length];
        for(int i=0;i<tips.length;i++){
            ImageView img = new ImageView(this);
            img.setLayoutParams(new LayoutParams(10,10));
            tips[i] = img;
            if(i == 0) {
                img.setBackgroundResource(R.drawable.page_now);
            }else{
                img.setBackgroundResource(R.drawable.page);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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
            public void destroyItem(ViewGroup container,int position,Object o){
                //container.removeViewAt(position);
            }

            @Override
            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(ViewGroup container,final int position){

                final ImageView im = new ImageView(ViewPagerActivity.this);
                im.setImageBitmap(images.get(position));
                PhotoViewAttacher attacher=new PhotoViewAttacher(im);
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

    @Override
    protected void onDestroy(){
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            proDialog.dismiss();//万万不可少这句，否则会程序会卡死。
            switch (msg.what){
                case 0:
                    setViewPager();//发送消息通知ListView更新
                    break;
                default:
                    Toast.makeText(ViewPagerActivity.this, "图片加载失败！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
