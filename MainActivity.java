package com.xianyi.chen.repair;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jauker.widget.BadgeView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ScrollView svMain;//“首页”菜单
    private LinearLayout llMy;//“我的”菜单

    private LinearLayout ll_fenpai2,ll_luru2,ll_unwangong2,ll_unhejia2;//任务
    private LinearLayout ll_yifenpailist,ll_hejialist,ll_unwangonglist,ll_wangonglist;//查询
    private ImageView iv_fenpai,iv_unhejia,iv_unwangong;
    private ImageView iv_yifenpai,iv_yihejia,iv_weiwangong,iv_yiwangong;

    public String spec,data,result,countresult;
    public int oldcount0,newcount0,oldcount41,newcount41,oldcount61,newcount61;//查询数据记录条数
    public String bugsenduserid=UserModel.getuserid();
    public String roleid=UserModel.getroleid();

    private String strNoticeTitle = "您有未处理任务！";
    private String strNoticeContent = "";
    private NotificationManager manager;

    private int menuflag = 1;//底部菜单标示：1首页；2任务；3查询；4我的
    private LinearLayout ll_index,ll_task,ll_search,ll_my;//footer
    private ImageView iv_index,iv_task,iv_search,iv_my;
    private TextView tv_index,tv_task,tv_search,tv_my;//footer

    private LinearLayout llSettouxiang,llPersoninfo,llUpdpwd,llHelp;//my

    private Button btnExit3;//退出

    TakePhotoPopWindow takePhotoPopWin;

    private ImageView ivTouxiang_pop;
    private final int TAKE_PICTURE = 1;
    private final int CHOOSE_PHOTO = 2;
    private String srcPath="";
    String filename;
    File file_go;
    private Uri imageUri;
    private Intent intent;

    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    private BadgeView badgeView_fenpai,badgeView_hejia,badgeView_wangong,badgeView_yifenpai,badgeView_yihejia,badgeView_weiwangong,badgeView_yiwangong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            //Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1
            );
        }

        if(bugsenduserid.equals("")){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            intent.putExtra("exit",1);
            startActivity(intent);
        }

        // 这里来检测版本是否需要更新
        UpdateManager mUpdateManager = new UpdateManager(this);
        mUpdateManager.checkUpdateInfo();

        oldcount0=0;
        newcount0=0;
        oldcount41=0;
        newcount41=0;
        oldcount61=0;
        newcount61=0;

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        resetTabState();//初始化底部菜单

        RepairList();//获取网络数据-统计数量

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(!UserModel.gettimerflag()) {
            UserModel.settimerflag(true);
            mHandler.postDelayed(runnable, 5000);
        }

    }

    @Override
    protected void onDestroy(){
        mHandler.removeCallbacksAndMessages(null);
        manager.cancelAll();
        super.onDestroy();
    }

    /** 初始化控件 **/
    private void InitControl(){

        btnExit3 = (Button)findViewById(R.id.btnExit3);

        //底部菜单4个Linearlayout
        ll_index=(LinearLayout)findViewById(R.id.ll_index);
        ll_task=(LinearLayout)findViewById(R.id.ll_task);
        ll_search=(LinearLayout)findViewById(R.id.ll_search);
        ll_my=(LinearLayout)findViewById(R.id.ll_my);

        //底部菜单4个ImageView
        iv_index=(ImageView) findViewById(R.id.iv_index);
        iv_task=(ImageView) findViewById(R.id.iv_task);
        iv_search=(ImageView) findViewById(R.id.iv_search);
        iv_my=(ImageView) findViewById(R.id.iv_my);

        // 底部菜单4个菜单标题
        tv_index=(TextView)findViewById(R.id.tv_index);
        tv_task=(TextView)findViewById(R.id.tv_task);
        tv_search=(TextView)findViewById(R.id.tv_search);
        tv_my=(TextView)findViewById(R.id.tv_my);

        llMy=(LinearLayout)findViewById(R.id.llMy);
        svMain=(ScrollView)findViewById(R.id.svMain);

        //任务
        ll_luru2=(LinearLayout)findViewById(R.id.ll_luru2);
        ll_fenpai2=(LinearLayout)findViewById(R.id.ll_fenpai2);
        ll_unhejia2=(LinearLayout)findViewById(R.id.ll_unhejia2);
        ll_unwangong2=(LinearLayout)findViewById(R.id.ll_unwangong2);

        //查询
        ll_yifenpailist=(LinearLayout)findViewById(R.id.ll_yifenpailist);
        ll_hejialist=(LinearLayout)findViewById(R.id.ll_hejialist);
        ll_unwangonglist=(LinearLayout)findViewById(R.id.ll_unwangonglist);
        ll_wangonglist=(LinearLayout)findViewById(R.id.ll_wangonglist);

        //图标，用于未处理任务提醒iv_fenpai,iv_unhejia,iv_unwangong;
        iv_fenpai=(ImageView) findViewById(R.id.iv_fenpai);
        iv_unhejia=(ImageView) findViewById(R.id.iv_unhejia);
        iv_unwangong=(ImageView) findViewById(R.id.iv_unwangong);

        //查询统计
        iv_yifenpai=(ImageView) findViewById(R.id.iv_yifenpai);
        iv_yihejia=(ImageView) findViewById(R.id.iv_yihejia);
        iv_weiwangong=(ImageView) findViewById(R.id.iv_weiwangong);
        iv_yiwangong=(ImageView) findViewById(R.id.iv_yiwangong);

        //my
        llSettouxiang=(LinearLayout)findViewById(R.id.llSettouxiang);
        llPersoninfo=(LinearLayout)findViewById(R.id.llPersoninfo);
        llUpdpwd=(LinearLayout)findViewById(R.id.llUpdpwd);
        llHelp=(LinearLayout)findViewById(R.id.llHelp);

        ivTouxiang_pop=(ImageView)findViewById(R.id.ivTouxiang);

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){

        //任务-指挥中心
        //任务-录入
        ll_luru2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FindbugActivity.class);
                startActivity(intent);
            }
        });
        //任务-未分派任务
        ll_fenpai2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DispatchActivity.class);
                startActivity(intent);
            }
        });
        //任务-待核价任务
        ll_unhejia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CheckActivity.class);
                intent.putExtra("state",41);//4甲方专人核价；41指挥中心核价
                startActivity(intent);
            }
        });
        //任务-完工确认任务
        ll_unwangong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,UnCompleteListActivity.class);
                startActivity(intent);
            }
        });

        //查询-已分派任务
        ll_yifenpailist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TaskListActivity.class);
                intent.putExtra("flag",5);
                startActivity(intent);
            }
        });
        //查询-已核价任务
        ll_hejialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TaskListActivity.class);
                intent.putExtra("flag",3);
                startActivity(intent);
            }
        });
        //查询-未完成任务
        ll_unwangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TaskListActivity.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        //查询-已完成任务
        ll_wangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,TaskListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });

        //footer菜单点击事件
        ll_index.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserModel.getFlag()!=1){
                    UserModel.setFlag(1);
                    menuflag=1;
                    SetShow();
                    resetTabState();
                    RepairList();
                }
            }
        });
        //任务
        ll_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserModel.getFlag()!=2) {
                    Intent intent = new Intent(MainActivity.this, AlterWindow.class);
                    //UserModel.setFlag(2);
                    intent.putExtra("menuflag",2);
                    startActivity(intent);
                }
            }
        });
        //查询
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserModel.getFlag()!=3) {
                    Intent intent = new Intent(MainActivity.this, AlterWindow.class);
                    intent.putExtra("state", 5);
                    intent.putExtra("menuflag",3);
                    //UserModel.setFlag(3);
                    startActivity(intent);
                }
            }
        });
        //我的
        ll_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserModel.getFlag()!=4){
                    UserModel.setFlag(4);
                    menuflag=4;
                    SetShow();
                    resetTabState();
                }
            }
        });
        //我的：个人信息
        llPersoninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonInfo.class);
                startActivity(intent);
            }
        });
        //我的：修改密码
        llUpdpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UpdatePwd.class);
                startActivity(intent);
            }
        });
        //我的：使用帮助
        llHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Main.this, PersonInfo.class);
//                startActivity(intent);
            }
        });

        //注销
        btnExit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel.setFlag(1);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("exit",1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent);
            }
        });

        //设置头像
        ivTouxiang_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopFormBottom();
            }
        });

        //设置头像
        llSettouxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopFormBottom();
            }
        });

        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMain();
            }
        });
    }

    private void refreshMain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RepairList();
                        swipeRefresh.setRefreshing(false);//刷新事件结束，隐藏刷新进度条
                    }
                });
            }
        }).start();
    }

    /** 初始化显示界面，默认显示首页 **/
    private void SetShow(){
        if(menuflag==1){
            svMain.setVisibility(View.VISIBLE);
            llMy.setVisibility(View.GONE);
        }else {
            svMain.setVisibility(View.GONE);
            llMy.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化底部菜单
     */
    private void resetTabState() {
        int i = UserModel.getFlag();
        switch (i){
            case 1:
                iv_index.setImageResource(R.drawable.menu_index_sel);
                tv_index.setTextColor(Color.parseColor("#1975cf"));
                iv_my.setImageResource(R.drawable.menu_my);
                tv_my.setTextColor(Color.parseColor("#6d6e71"));
                break;
            case 4:
                iv_index.setImageResource(R.drawable.menu_index);
                tv_index.setTextColor(Color.parseColor("#6d6e71"));
                iv_my.setImageResource(R.drawable.menu_my_sel);
                tv_my.setTextColor(Color.parseColor("#1975cf"));
                break;
            default:
                iv_index.setImageResource(R.drawable.menu_index_sel);
                tv_index.setTextColor(Color.parseColor("#1975cf"));
                iv_my.setImageResource(R.drawable.menu_my);
                tv_my.setTextColor(Color.parseColor("#6d6e71"));
                break;
        }
    }

    /**
     * 从底部弹出菜单用于设置头像
     */
    public void showPopFormBottom() {
        takePhotoPopWin = new TakePhotoPopWindow(this, onClickListener);
        takePhotoPopWin.showAtLocation(findViewById(R.id.ll_footer), Gravity.CENTER, 0, 0);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            takePhotoPopWin.dismiss();
            switch (v.getId()) {
                case R.id.btnTaking:
                    takingPicture();
                    break;
                case R.id.btnOpen:
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }else {
                        openPicture();
                    }
                    break;
            }
        }
    };

    /**
     * 拍照
     */
    private void takingPicture(){
        //图片文件名格式为userid+当前时间
        SimpleDateFormat filetime = new SimpleDateFormat("yyyyMMddHHmmss");
        filename = bugsenduserid + filetime.format(new java.util.Date())+".jpg";
        //创建file对象用于存放摄像头拍下的图片，并将它存放在手机SD卡的应用关联缓存目录（SD卡用于存放当前缓存数据的位置）下，在6.0以后不需要运行时权限
        srcPath = getExternalCacheDir() + "/"+ filename;// /storage/emulated/0/Android/data/com.xianyi.chen.wrepair/cache/320170619163737.jpg
        file_go = new File(srcPath);//getExternalCacheDir(),filename
        try{
            if(file_go.exists()){
                file_go.delete();
            }
            file_go.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        //系统版本大于等于Android7.0时，直接使用本地真实路径的Uri被认为是不安全的，FileProvider是一种特殊的内容提供器
        if(Build.VERSION.SDK_INT >= 24){//系统版本大于等于Android7.0时
            imageUri = FileProvider.getUriForFile(MainActivity.this,"com.xianyi.chen.repair.fileprovider",file_go);
        }else {
            imageUri = Uri.fromFile(file_go);
        }

        //启动相机程序
        intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 并设置拍照的存在方式为外部存储和存储的路径；
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //跳转到拍照界面;
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * 从相册中选择照片
     */
    private void openPicture(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case TAKE_PICTURE:
                if(resultCode == RESULT_OK){
                    //将拍摄的照片显示出来,并保存到数据库中
                    ImageOptions options = new ImageOptions.Builder()
                            .setFadeIn(true)//淡入效果
                            .setCircular(true)//圆形
                            .setUseMemCache(true) //设置使用MemCache，默认true
                            .build();
                    x.image().bind(ivTouxiang_pop,srcPath,options);
                    saveTouxiang();//将头像保存到数据库中
                }
                break;
            case CHOOSE_PHOTO://从相册中选择照片
                if (resultCode == RESULT_OK) {
                    String path=null;
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        path = PostParamTools.handleImageOnKitKat(data,this);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        path = PostParamTools.handleImageBeforeKitKat(data,this);
                    }
                    srcPath = path;
                    //filename = PostParamTools.getFileName(path);
                    //将拍摄的照片显示出来,并保存到数据库中
                    ImageOptions options = new ImageOptions.Builder()
                            .setFadeIn(true)//淡入效果
                            .setCircular(true)//圆形
                            .setUseMemCache(true) //设置使用MemCache，默认true
                            .build();
                    x.image().bind(ivTouxiang_pop,srcPath,options);
                    saveTouxiang();//将头像保存到数据库中
                }
                break;
        }
    }

    /**
     * 将头像保存到数据库中
     */
    private void saveTouxiang(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap filemap=new <String,File>HashMap();
                String pic_path = srcPath;
                String strFileName=PostParamTools.getFileName(pic_path);
                String targetPath = pic_path.substring(0,pic_path.indexOf(strFileName))+"ys_"+strFileName;
                //调用压缩图片的方法，返回压缩后的图片path
                final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 50);
                final File compressedPic = new File(compressImage);
                if (compressedPic.exists()) {
                    filemap.put("file"+Integer.toString(0),compressedPic );
                }else{//直接上传
                    file_go=new File(pic_path);
                    filemap.put("file"+Integer.toString(0),file_go );
                }
                Map<String, String> parammap=new HashMap <String, String> ();
                parammap.put("userid",  bugsenduserid);
                parammap.put("roleid",  roleid);

                String url=UserModel.myhost+"SaveTouxiang.php";
                String res = "";

                try {
                    res = PostParamTools.post(url, parammap, filemap);
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
                if(res.equals("1")){
                    //头像修改成功
                    mHandler.sendEmptyMessage(4);
                } else {
                    //提交失败
                    mHandler.sendEmptyMessage(3);
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openPicture();
                }else {
                    Toast.makeText(MainActivity.this,"拒绝权限将无法选择照片",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 新任务提醒
     */
    public Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this, "网络链接超时!",
                        Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Refresh();//有更新时声音提醒
                    break;
                case 2:
                    GetCount();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this, "数据提交失败!", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(MainActivity.this, "头像修改成功!", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    };

    /**
     * 定时任务
     * **/
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            new Thread(){
                @Override
                public void run(){
                    spec=UserModel.myhost+"timer_zhihui.php";
                    data = "userid="+bugsenduserid ;
                    countresult=PostParamTools.postGetInfo(spec, data);
                    if(countresult == null){
                        mHandler.sendEmptyMessage(0);
                    }else if(!("null".equals(countresult) && !("".equals(countresult)))){
                        mHandler.sendEmptyMessage(1);
                    }
                }
            }.start();

            mHandler.postDelayed(this, 5000);
        }
    };

    /**
     * 有更新时声音提醒
     * **/
    private void Refresh(){
        try {
            JSONObject mcount = new JSONObject(countresult);
            newcount0=mcount.getInt("count0");
            newcount41=mcount.getInt("count41");
            newcount61=mcount.getInt("count61");
        }catch (JSONException e)
        {e.printStackTrace();}
        String strNoticeContent1="";
        String strNoticeContent2="";
        String strNoticeContent3="";
        Boolean bool = false;
        if (newcount0!=oldcount0)
        {
            //soundPool.play(1, 1, 1, 0, 0, 1);
            oldcount0=newcount0;
            bool = true;
        }
        if (newcount41!=oldcount41)
        {
            //soundPool.play(1, 1, 1, 0, 0, 1);
            oldcount41=newcount41;
            bool = true;
        }
        if (newcount61!=oldcount61)
        {
            //soundPool.play(1, 1, 1, 0, 0, 1);
            oldcount61=newcount61;
            bool = true;
        }
        if(newcount0!=0){
            strNoticeContent1="待分派:" + newcount0 + "个;";
        }
        if(newcount41!=0){
            strNoticeContent2="待核价:" + newcount41 + "个;";
        }
        if(newcount61!=0){
            strNoticeContent3="待完工:" + newcount61 + "个。";
        }
        if(bool){
            strNoticeContent = strNoticeContent1 + strNoticeContent2 + strNoticeContent3;
            loadNotice();
            RepairList();//刷新主界面
        }
    }

    /**
     * 加载通知
     * **/
    private void loadNotice(){
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent,0);
        Uri sound=Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.newrepair );
        Notification notification = new NotificationCompat.Builder(MainActivity.this)
                .setContentTitle(strNoticeTitle)
                .setContentText(strNoticeContent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setAutoCancel(true)//点击通知后系统状态栏上通知消失
                .setVibrate(new long[]{0,300,300,300})//添加震动,（还要在主页中添加权限）
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(1,notification);
    }

    /**
     * 获取网络数据-统计数量
     * **/
    private void RepairList(){

        new Thread(){
            @Override
            public void run() {
                spec=UserModel.myhost+"getCount.php";
                data="roleid="+roleid+"&userid="+bugsenduserid;
                result = PostParamTools.postGetInfo(spec, data);
                if(result == null){
                    mHandler.sendEmptyMessage(0);
                }else {
                    mHandler.sendEmptyMessage(2);//获取统计数量,并更新界面
                }

            }
        }.start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            //此处写退向后台的处理
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取统计数量,并更新界面
     * **/
    private void GetCount(){
        try {
            JSONObject jsonObject = new JSONObject(result);

            //未处理任务提醒-待分派
            Integer fenpai= Integer.parseInt(jsonObject.getString("numUnPaiFa"));
            setBadgeView_fenpai(iv_fenpai,fenpai,Color.parseColor("#FF0000"));
            //未处理任务提醒-待核价
            Integer unhejia= Integer.parseInt(jsonObject.getString("numUnHeJia"));
            setBadgeView_hejia(iv_unhejia,unhejia,Color.parseColor("#FF0000"));
            //未处理任务提醒-待完工确认
            Integer unwangong= Integer.parseInt(jsonObject.getString("numUnWanGongSure"));
            setBadgeView_wangong(iv_unwangong,unwangong,Color.parseColor("#FF0000"));

            //查询-已分派
            Integer numYiFenpai= Integer.parseInt(jsonObject.getString("numYiFenpai"));
            setBadgeView_yifenpai(iv_yifenpai,numYiFenpai,Color.parseColor("#5e3ab8"));
            //查询-已核价
            Integer numHeJia= Integer.parseInt(jsonObject.getString("numHeJia"));
            setBadgeView_yihejia(iv_yihejia,numHeJia,Color.parseColor("#5e3ab8"));
            //查询-未完工
            Integer numUnWanGong= Integer.parseInt(jsonObject.getString("numUnWanGong"));
            setBadgeView_weiwangong(iv_weiwangong,numUnWanGong,Color.parseColor("#5e3ab8"));
            //查询-已完工
            Integer numWanGong= Integer.parseInt(jsonObject.getString("numWanGong"));
            setBadgeView_yiwangong(iv_yiwangong,numWanGong,Color.parseColor("#5e3ab8"));

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置数字提醒
     * **/
    private void setBadgeView_fenpai(ImageView imageView,int count,int color){

        if(badgeView_fenpai != null && count == 0){
            badgeView_fenpai.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_fenpai = new com.jauker.widget.BadgeView(this);
            badgeView_fenpai.setBackground(10, color);
            badgeView_fenpai.setTargetView(imageView);
        }
        badgeView_fenpai.setBadgeCount(count);

    }
    private void setBadgeView_hejia(ImageView imageView,int count,int color){

        if(badgeView_hejia != null && count == 0){
            badgeView_hejia.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_hejia = new com.jauker.widget.BadgeView(this);
            badgeView_hejia.setBackground(10, color);
            badgeView_hejia.setTargetView(imageView);
        }
        badgeView_hejia.setBadgeCount(count);

    }
    private void setBadgeView_wangong(ImageView imageView,int count,int color){

        if(badgeView_wangong != null && count == 0){
            badgeView_wangong.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_wangong = new com.jauker.widget.BadgeView(this);
            badgeView_wangong.setBackground(10, color);
            badgeView_wangong.setTargetView(imageView);
        }
        badgeView_wangong.setBadgeCount(count);

    }
    private void setBadgeView_yifenpai(ImageView imageView,int count,int color){

        if(badgeView_yifenpai != null && count == 0){
            badgeView_yifenpai.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_yifenpai = new com.jauker.widget.BadgeView(this);
            badgeView_yifenpai.setBackground(10, color);
            badgeView_yifenpai.setTargetView(imageView);
        }
        badgeView_yifenpai.setBadgeCount(count);

    }
    private void setBadgeView_yihejia(ImageView imageView,int count,int color){

        if(badgeView_yihejia != null && count == 0){
            badgeView_yihejia.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_yihejia = new com.jauker.widget.BadgeView(this);
            badgeView_yihejia.setBackground(10, color);
            badgeView_yihejia.setTargetView(imageView);
        }
        badgeView_yihejia.setBadgeCount(count);

    }
    private void setBadgeView_weiwangong(ImageView imageView,int count,int color){

        if(badgeView_weiwangong != null && count == 0){
            badgeView_weiwangong.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_weiwangong = new com.jauker.widget.BadgeView(this);
            badgeView_weiwangong.setBackground(10, color);
            badgeView_weiwangong.setTargetView(imageView);
        }
        badgeView_weiwangong.setBadgeCount(count);

    }
    private void setBadgeView_yiwangong(ImageView imageView,int count,int color){

        if(badgeView_yiwangong != null && count == 0){
            badgeView_yiwangong.setBackground(10, Color.parseColor("#00000000"));
        }else {
            badgeView_yiwangong = new com.jauker.widget.BadgeView(this);
            badgeView_yiwangong.setBackground(10, color);
            badgeView_yiwangong.setTargetView(imageView);
        }
        badgeView_yiwangong.setBadgeCount(count);

    }

}
