package com.xianyi.chen.repair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindbugActivity extends Activity {
    // 定义一个button打开照相机，定义一个imageview显示照相机所拍摄的相片;
    private Button but,upload_image,choose_from_album;
    private TextView timeview,bugdescript;
    private AutoCompleteTextView bugaddrv;
    private ListView imglist;
    private MyAdapter imgAdapter;
    private Map filemap;
    private ImageView bugimg;
    private String srcPath="";
    private String actionUrl =UserModel.myhost+"findbug.php";
    private String bugtype,bugfinddescrip,bugaddr,bugfindphoto,bugfindtime,bugfinduserid,msg;
    final int TAKE_PICTURE = 1;
    private List<Map<String, Object >> list = new ArrayList<Map<String, Object>>();
    // 获取sd卡根目录地址,并创建图片父目录文件对象和文件的对象;
    String file_str = Environment.getExternalStorageDirectory().getPath();
    File mars_file = new File(file_str + "/my_camera");
    String filename;
    File file_go;
    ProgressBar progressBar;
    private Intent intent;
    public String roleid=UserModel.getroleid();

    //本地缓存故障地址
    private Integer version=1;
    private DbManager dbm;
    private List<AddrModel> addrModels=null;
    private List<AddrModel> addrModelArrayList=null;

    public static final int CHOOSE_PHOTO=2;

    private TextView tv_title;//头部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findbug);

        bugfinduserid=UserModel.getuserid();
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        bugimg=(ImageView)findViewById(R.id.bugimg);
        imgAdapter=new MyAdapter(FindbugActivity.this);
        but = (Button) findViewById(R.id.my_camare_button);
        imglist= (ListView) findViewById(R.id.imglist);
        upload_image=(Button)findViewById(R.id.upload_image);
        choose_from_album=(Button)findViewById(R.id.choose_from_album);
        timeview=(TextView)findViewById(R.id.timeview);
        bugdescript=(TextView)findViewById(R.id.bugdescript);
        bugaddrv= (AutoCompleteTextView) findViewById(R.id.bugAddr);

        timeview.setEnabled(false);
        timeview.setFocusable(false);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("故障录入");

        // 初始化控件
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // 建立数据源
        String[] mItems = getResources().getStringArray(R.array.bugtype);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner .setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                String[] languages = getResources().getStringArray(R.array.bugtype);
                bugtype = languages[pos];
                setautocomplete();
                // Toast.makeText(FindbugActivity.this, "你点击的是:" + languages[pos], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        //上传
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filemap=new <String,File>HashMap();//从图片列表中读取图片文件
                bugfindphoto="";
                if (list.size()>12){Toast.makeText(FindbugActivity.this,"最多只能上传12张图片",Toast.LENGTH_SHORT).show();};
                for(int i=0;i<list.size()&&i<12;i++)
                {
//                    String myfilepath=list.get(i).get("filepath").toString();
//                    file_go=new File(myfilepath);
//                    filemap.put("file"+Integer.toString(i),file_go );
//                    bugfindphoto=bugfindphoto+PostParamTools.getFileName(myfilepath);
//                    if(i!=list.size()-1) bugfindphoto+=",";

                    String pic_path=list.get(i).get("filepath").toString();
                    String strFileName=PostParamTools.getFileName(pic_path);
                    String targetPath = pic_path.substring(0,pic_path.indexOf(strFileName))+"ys_"+strFileName;
                    //调用压缩图片的方法，返回压缩后的图片path
                    final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 60);
                    final File compressedPic = new File(compressImage);
                    if (compressedPic.exists()) {
                        filemap.put("file"+Integer.toString(i),compressedPic );
                    }else{//直接上传
                        file_go=new File(pic_path);
                        filemap.put("file"+Integer.toString(i),file_go );
                    }
                    bugfindphoto=bugfindphoto+strFileName;
                    if(i!=list.size()-1) bugfindphoto+=",";
                }
                if(bugaddrv.getText().length()>0)
                {
                    progressBar.setVisibility(View.VISIBLE);
                   //验证图片存在后就实现，上传功能，得到与服务器的输出流...
                    //什么URLconnection ，HttpURLconnectio等都可以.......,bugaddr,bugfindphoto,bugfindtime,bugfinduserid,bugfinddiscrip
                    //bugfindphoto= srcPath.substring(srcPath.lastIndexOf("/") + 1);
                    bugfinddescrip=bugdescript.getText().toString();
                    bugfindtime=timeview.getText().toString();
                    bugaddr=bugaddrv.getText().toString();

                    //bugfinduserid="1";

                    new Thread() {
                        public void run() {
                            // uploadFile(actionUrl); // 调用上传方法
                            Map <String, String>  parammap=new HashMap <String, String> ();
                            parammap.put("bugfinduserid",  bugfinduserid);
                            parammap.put("bugtype",  bugtype);
                            parammap.put("bugfindphoto",  bugfindphoto);
                            parammap.put("bugfinddescrip",  bugfinddescrip);
                            parammap.put("bugfindtime",  bugfindtime);
                            parammap.put("bugaddr",  bugaddr);
                            parammap.put("roleid",  roleid);
                            try {
                               msg= PostParamTools.post(actionUrl, parammap, filemap);

                            }
                            catch (Exception e)
                            {
                                e.getMessage();
                            }
                        FindbugActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(("success").equals(msg)) {//上传成功
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(FindbugActivity.this, "上报成功", Toast.LENGTH_SHORT).show();
                                    bugfindphoto="";
                                    timeview.setText("");
                                    bugdescript.setText("");
                                    bugaddrv.setText("");
                                    list.clear();
                                    imglist.setAdapter(imgAdapter);
                                    setListViewHeightBasedOnChildren(imglist);
                                }
                                else
                                { Toast.makeText(FindbugActivity.this, "上报故障失败,请重试", Toast.LENGTH_SHORT).show();}
                            }
                        });
                        }
                    }.start();

                }
                else
                {
                    Toast.makeText(FindbugActivity.this, "必须填入故障地址", Toast.LENGTH_LONG).show();
                    }
                }
            });

        //从相册中选择照片
        choose_from_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(ContextCompat.checkSelfPermission(FindbugActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//
//                }
                openAlbum();
            }
        });


     }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    public void setautocomplete(){//完成地址输入提示

        new Thread(){
            @Override
            public void run() {
                String content=PostParamTools.postGetInfo(UserModel.myhost+"readversion_addr.php","");
                if(content == null){
                    Toast.makeText(FindbugActivity.this, "链接超时!", Toast.LENGTH_SHORT).show();
                }else{
                    if (!content.equals("")) {
                        version=Integer.parseInt(content);
                    }
                    else {
                        version=1;
                    }
                    dbm = x.getDb(initDb(version));

//                try{
//                    dbm.delete(AddrModel.class);
//                }catch (DbException e){e.printStackTrace();}

                    List<AddrModel> list = query();
                    if (list==null || list.size()<=0) {
                        ReturnUpdateData();//获取网络请求，更新本地数据
                    }else {
                        UpdateAddr();
                    }
                }


            }
        }.start();
    }
    /**
     * 查询本地数据
     * **/
    private List<AddrModel> query() {
        try {
            addrModelArrayList=new ArrayList<AddrModel>();
            addrModelArrayList = dbm.findAll(AddrModel.class);//
            addrModels=new ArrayList<AddrModel>();
            if(addrModelArrayList!=null && addrModelArrayList.size()>0){
                addrModels=dbm.selector(AddrModel.class).where("equipmenttype","=",bugtype).findAll();
            }
            return addrModels;
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 异步 获取网络请求，更新本地数据
     * **/
    private void ReturnUpdateData(){
        new Thread(){
            @Override
            public void run(){
                String jsonAddr=PostParamTools.postGetInfo(UserModel.myhost+"addr.php","");//,"bugtype="+bugtype
                if(jsonAddr==null){
                    Toast.makeText(FindbugActivity.this, "链接超时!", Toast.LENGTH_SHORT).show();
                }else {
                    BindData(jsonAddr);
                    UpdateAddr();
                }

            }
        }.start();
    }

    //更新界面
    private void UpdateAddr(){
        FindbugActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(addrModels!=null && addrModels.size()>0){
                    final String[] addrs=new String[addrModels.size()];
                    int i=0;
                    for (AddrModel pm : addrModels) {
                        addrs[i]=pm.getEquipmentaddr();
                        i++;
                    }
                    //ArrayAdapter arrayAdapter = new ArrayAdapter<String>(FindbugActivity.this, android.R.layout.simple_list_item_1, addrs);
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(FindbugActivity.this, android.R.layout.simple_list_item_1, addrs);
                    bugaddrv.setAdapter(arrayAdapter);
                }else {
                    bugaddrv.setAdapter(null);
                }
            }
        });
    }

    /**
     * 根据返回的网络请求数据，更新本地数据
     * **/
    private void BindData(String jsonAddr){
        if(jsonAddr != null)
        {
            try{
                addrModelArrayList=new ArrayList<AddrModel>();
                JSONArray jsonArray_addr=new JSONArray(jsonAddr);
                for (int i=0;i<jsonArray_addr.length();i++){
                    JSONObject jsonObject1 = jsonArray_addr.getJSONObject(i);
                    AddrModel addrModel=new AddrModel();
                    addrModel.setEquipmentid(jsonObject1.getInt("equipmentid"));
                    addrModel.setEquipmenttype(jsonObject1.getString("equipmenttype"));
                    addrModel.setEquipmentname(jsonObject1.getString("equipmentname"));
                    addrModel.setEquipmentaddr(jsonObject1.getString("equipmentaddr"));
                    addrModelArrayList.add(addrModel);
                }
                try{
                    dbm.delete(AddrModel.class);
                    dbm.saveOrUpdate(addrModelArrayList);
                    addrModels=new ArrayList<AddrModel>();
                    if(addrModelArrayList!=null && addrModelArrayList.size()>0){
                        addrModels=dbm.selector(AddrModel.class).where("equipmenttype","=",bugtype).findAll();
                    }
                }catch (DbException e){e.printStackTrace();}

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取 DbManager.DaoConfig 对象创建数据库
     * **/
    protected DbManager.DaoConfig initDb(final int version){
        //本地数据的初始化
        final DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName(UserModel.DBNAME) //设置数据库名
                .setDbVersion(version) //设置数据库版本,每次启动应用时将会检查该版本号,
                //发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true)//设置是否开启事务,默认为false关闭事务
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {

                    }
                })//设置数据库创建时的Listener
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        if(oldVersion != newVersion){
                            ReturnUpdateData();//获取网络请求，更新本地数据
                        }
                    }
                });
        //设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下

        return daoConfig;
    }

    public void getphotobtn(View v){
        //图片文件名格式为userid+当前时间
        SimpleDateFormat filetime   =   new   SimpleDateFormat("yyyyMMddHHmmss");
        filename   =   bugfinduserid+filetime.format(new java.util.Date())+".jpg";
        srcPath=file_str +"/"+ filename;
        file_go=new File(srcPath);
        // 验证sd卡是否正确安装：
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 先创建父目录，如果新创建一个文件的时候，父目录没有存在，那么必须先创建父目录，再新建文件。
            if (!mars_file.exists()) {
                mars_file.mkdirs();
            }
            //常规情况下，我们这里会 创建子目录，但在这里不用系统拍照完毕后会根据所给的图片路径自动去实现;
            if(!file_go.exists())
            {
                try {
                    file_go.createNewFile();
                } catch (Exception e)
                {e.printStackTrace();
                }
            }


        } else {
            Toast.makeText(FindbugActivity.this, "请先安装好sd卡",Toast.LENGTH_LONG).show();}
        // 设置跳转的系统拍照的activity为：MediaStore.ACTION_IMAGE_CAPTURE ;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 并设置拍照的存在方式为外部存储和存储的路径；
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_go));
        //跳转到拍照界面;
        startActivityForResult(intent, TAKE_PICTURE);
    }
    //拍照结束后显示图片;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case TAKE_PICTURE://拍照
                // TODO Auto-generated method stub
                // 判断请求码和结果码是否正确，如果正确的话就在activity上显示刚刚所拍照的图片;
                if (resultCode == RESULT_OK) {
                    addData(srcPath);
                    SimpleDateFormat sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
                    String   date   =   sDateFormat.format(new java.util.Date());
                    timeview.setText(date);
                } else {
                    System.out.println("不显示图片");
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
                    filename = PostParamTools.getFileName(path);
                    addData(path);
                }
                break;
            default:
                break;
        }
    }

    public void delData(int pos)
    {
        list.remove(pos);
        imglist.setAdapter(imgAdapter);
        setListViewHeightBasedOnChildren(imglist);
    }
    public void addData(String path)
    {
        Map<String, Object> map;
        map = new HashMap<String, Object>();
        // map.put("filename", filename);
        map.put("filepath", path);
        list.add(map);
        imglist.setAdapter(imgAdapter);
        setListViewHeightBasedOnChildren(imglist);

    }

    /**存放控件*/
    static  class ViewHolder{
        public TextView itemimgfilename;
        public Button   delbtn;
    }
    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /***/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            // Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.findbugimgitem,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.itemimgfilename = (TextView) convertView.findViewById(R.id.imgfile);
                holder.delbtn = (Button) convertView.findViewById(R.id.delimgbtn);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/

            //holder.itemimgfilename.setText((String)list.get(position).get("filename"));
            final String photo = PostParamTools.getFileName((String)list.get(position).get("filepath"));
            holder.itemimgfilename.setText(photo);
            /**为Button添加点击事件*/
            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //Toast.makeText(FindbugActivity.this,"删除图片吧",Toast.LENGTH_SHORT).show();
                    delData(position);
                    Toast.makeText(FindbugActivity.this, "还有"+list.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FindbugActivity.this,ShowImageActivity.class);
                    intent.putExtra("imgpath",(String)list.get(position).get("filepath"));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    /**
     * 动态设置ListView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent intent = new Intent(FindbugActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

 }

