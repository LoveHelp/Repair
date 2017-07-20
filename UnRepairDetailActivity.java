package com.xianyi.chen.repair;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnRepairDetailActivity extends AppCompatActivity {

    private TextView tvBugType,tvBugAddress,tvBugDescrip;
    private AutoCompleteTextView tvBugDescrip2,tvRepairReason,tvRepairSolution;
    private Button btnChakan,btnSubmit;
    private Spinner spinner;

    private String spec = UserModel.myhost+"getrepairbyid.php";
    private String data = "";//flag=0首次加载；flag=1提交修改数据
    private String bugtypename,bugaddr,bugfindphoto,bugfinddescrip,bugfinduserid,bugsendtime;
    private int tbug=0;
    private String bugid = "";

    private Intent intent;

    // 定义一个button打开照相机，定义一个imageview显示照相机所拍摄的相片;
    private ListView imglist;
    private String srcPath="";
    final int TAKE_PICTURE = 1;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    // 获取sd卡根目录地址,并创建图片父目录文件对象和文件的对象;
    String file_str = Environment.getExternalStorageDirectory().getPath();
    File mars_file = new File(file_str + "/my_camera");
    String filename;
    File file_go;
    private ImgAdapter imgAdapter;
    private Map filemap;

    private String result="",results="";
    public String roleid=UserModel.getroleid();

    private Integer version=1;
    private DbManager dbm;
    private List<TemplateModel> templateModels_gzxx=null;
    private List<TemplateModel> templateModels_gzyy=null;
    private List<TemplateModel> templateModels_jjbf=null;
    private List<TemplateModel> templateModelList=null;
    private CheckBox ckb_basecharge,ckb_isonrepair;

    public static final int CHOOSE_PHOTO=2;
    private Integer upd=0;//1排查提交后的修改;0排查
    private Integer type=0;

    private TextView tv_title;//头部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_repair_detail);

        bugfinduserid=UserModel.getuserid();

        bugid = getIntent().getStringExtra("bugid");
        upd = getIntent().getIntExtra("upd",0);
        data="flag=0&bugid=" + bugid;

        imgAdapter=new ImgAdapter(UnRepairDetailActivity.this);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        /**
         * 开始获取数据
         */
        DetailInfo();

    }

    /** 初始化控件 **/
    private void InitControl(){

        tvBugType=(TextView)findViewById(R.id.tvBugType);
        tvBugAddress=(TextView)findViewById(R.id.tvBugAddress);
        tvBugDescrip=(TextView)findViewById(R.id.tvBugDescrip);
        tvBugDescrip2=(AutoCompleteTextView) findViewById(R.id.tvBugDescrip2);
        tvRepairReason=(AutoCompleteTextView)findViewById(R.id.tvRepairReason);
        tvRepairSolution=(AutoCompleteTextView)findViewById(R.id.tvRepairSolution);

        btnChakan = (Button) findViewById(R.id.btnChakan);
        imglist= (ListView) findViewById(R.id.imglist);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);

        ckb_basecharge=(CheckBox) findViewById(R.id.ckb_basecharge);
        ckb_isonrepair=(CheckBox) findViewById(R.id.ckb_isonrepair);
        spinner = (Spinner)findViewById(R.id.spibugname);// 初始化控件//sppartname,sppartxh,sppartbrand

        tv_title=(TextView)findViewById(R.id.tv_title);
        if(upd==1){
            tv_title.setText("已排查故障详情");
        }else {
            tv_title.setText("待排查故障详情");
        }

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){

        //查看故障照片
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnRepairDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",bugfindphoto);
                startActivity(intent);
            }
        });
        //提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SureBug();
            }
        });

    }

    public void setautocomplete(){//完成地址输入提示

        new Thread(){
            @Override
            public void run() {
                String content=PostParamTools.postGetInfo(UserModel.myhost+"readversion_temp.php","");
                if(content==null){
                    version=1;
                }else if (!content.equals("")) {
                    version=Integer.parseInt(content);
                }
                else {
                    version=1;
                }
                dbm = x.getDb(initDb(version));

                List<TemplateModel> list = query();
                if (list==null || list.size()<=0) {
                    ReturnUpdateData();//获取网络请求，更新本地数据
                }else {
                    UpdateAddr();
                }
            }
        }.start();

    }
    /**
     * 查询本地数据
     * **/
    private List<TemplateModel> query() {
        try {
            templateModelList=new ArrayList<TemplateModel>();
            templateModelList = dbm.findAll(TemplateModel.class);//
            templateModels_gzxx=new ArrayList<TemplateModel>();
            templateModels_gzyy=new ArrayList<TemplateModel>();
            templateModels_jjbf=new ArrayList<TemplateModel>();
            if(templateModelList!=null && templateModelList.size()>0){
                String bugtype=tvBugType.getText().toString();
                templateModels_gzxx=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","故障现象").findAll();
                templateModels_gzyy=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","故障原因").findAll();
                templateModels_jjbf=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","解决办法").findAll();
            }
            return templateModelList;
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
                String jsonAddr=PostParamTools.postGetInfo(UserModel.myhost+"template.php","");
                if(jsonAddr==null){
                    Toast.makeText(UnRepairDetailActivity.this, "网络链接超时...",
                            Toast.LENGTH_LONG).show();
                }else {
                    BindData(jsonAddr);
                    UpdateAddr();
                }

            }
        }.start();
    }

    //更新界面
    private void UpdateAddr(){
        UnRepairDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(templateModels_gzxx!=null && templateModels_gzxx.size()>0){
                    final String[] gzxx=new String[templateModels_gzxx.size()];
                    int i=0;
                    for (TemplateModel pm : templateModels_gzxx) {
                        gzxx[i]=pm.getTempcontent();
                        i++;
                    }
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(UnRepairDetailActivity.this, android.R.layout.simple_list_item_1, gzxx);
                    tvBugDescrip2.setAdapter(arrayAdapter);
                }else {
                    tvBugDescrip2.setAdapter(null);
                }

                if(templateModels_gzyy!=null && templateModels_gzyy.size()>0){
                    final String[] gzyy=new String[templateModels_gzyy.size()];
                    int i=0;
                    for (TemplateModel pm : templateModels_gzyy) {
                        gzyy[i]=pm.getTempcontent();
                        i++;
                    }
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(UnRepairDetailActivity.this, android.R.layout.simple_list_item_1, gzyy);
                    tvRepairReason.setAdapter(arrayAdapter);
                }else {
                    tvRepairReason.setAdapter(null);
                }

                if(templateModels_jjbf!=null && templateModels_jjbf.size()>0){
                    final String[] jjbf=new String[templateModels_jjbf.size()];
                    int i=0;
                    for (TemplateModel pm : templateModels_jjbf) {
                        jjbf[i]=pm.getTempcontent();
                        i++;
                    }
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(UnRepairDetailActivity.this, android.R.layout.simple_list_item_1, jjbf);
                    tvRepairSolution.setAdapter(arrayAdapter);
                }else {
                    tvRepairSolution.setAdapter(null);
                }
            }
        });
    }

    /**
     * 根据返回的网络请求数据，更新本地数据
     * **/
    private void BindData(String jsonAddr){
        try{
            templateModelList=new ArrayList<TemplateModel>();
            JSONArray jsonArray_temp=new JSONArray(jsonAddr);
            for (int i=0;i<jsonArray_temp.length();i++){
                JSONObject jsonObject1 = jsonArray_temp.getJSONObject(i);
                TemplateModel templateModel=new TemplateModel();
                templateModel.setTempid(jsonObject1.getInt("tempid"));
                templateModel.setTemptype(jsonObject1.getString("temptype"));
                templateModel.setContenttype(jsonObject1.getString("contenttype"));
                templateModel.setTempcontent(jsonObject1.getString("tempcontent"));
                templateModelList.add(templateModel);
            }
            try{
                dbm.delete(TemplateModel.class);
                dbm.saveOrUpdate(templateModelList);
                templateModels_gzxx=new ArrayList<TemplateModel>();
                templateModels_gzyy=new ArrayList<TemplateModel>();
                templateModels_jjbf=new ArrayList<TemplateModel>();
                if(templateModelList!=null && templateModelList.size()>0){
                    String bugtype=tvBugType.getText().toString();
                    templateModels_gzxx=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","故障现象").findAll();
                    templateModels_gzyy=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","故障原因").findAll();
                    templateModels_jjbf=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","解决办法").findAll();
                }
            }catch (DbException e){e.printStackTrace();}

        }catch (Exception e){
            e.printStackTrace();
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

    /**
     * 获取拍照按钮
     * **/
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
            Toast.makeText(UnRepairDetailActivity.this, "请先安装好sd卡",Toast.LENGTH_LONG).show();}
        // 设置跳转的系统拍照的activity为：MediaStore.ACTION_IMAGE_CAPTURE ;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 并设置拍照的存在方式为外部存储和存储的路径；
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file_go));
        //跳转到拍照界面;
        startActivityForResult(intent, TAKE_PICTURE);
    }
    /**
     * 获取选择照片按钮
     * **/
    public void getchoosephotobtn(View v){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }
    //拍照结束后显示图片;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case TAKE_PICTURE://拍照
                // TODO Auto-generated method stub
                // 判断请求码和结果码是否正确，如果正确的话就在activity上显示刚刚所拍照的图片;
                if (resultCode == this.RESULT_OK) {
                    addData(srcPath);
                } else {
                    System.out.println("不显示图片");
                }
                break;
            case CHOOSE_PHOTO://从相册中选择照片
                if (resultCode == this.RESULT_OK) {
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
    private class ImgAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public ImgAdapter(Context context) {
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
            holder.itemimgfilename.setText(PostParamTools.getFileName((String)list.get(position).get("filepath")));
            /**为Button添加点击事件*/
            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(FindbugActivity.this,"删除图片吧",Toast.LENGTH_SHORT).show();
                    delData(position);
                    Toast.makeText(UnRepairDetailActivity.this, "还有"+list.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UnRepairDetailActivity.this,ShowImageActivity.class);
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

    /**
     * 故障选择下拉框：大故障；小故障
     * **/
    private void spBind_Type()
    {
        String[] mItems = getResources().getStringArray(R.array.bugname);// 建立数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);// 建立Adapter并且绑定数据源
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);//绑定 Adapter到控件
        if(type==1){
            spinner.setSelection(1);
            tbug=1;
        }else {
            spinner.setSelection(0);
            tbug=0;
        }

        //故障选择下拉框值改变事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String[] bItems = getResources().getStringArray(R.array.bugname);
                if(i==0){
                    tbug=0;
                }
                else if(i==1){
                    tbug=1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void DetailInfo(){
        new Thread(){
            @Override
            public void run(){
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result==null){
                    handler.sendEmptyMessage(0);
                }else if (!result.equals("null") && !result.equals("")) {
                    //如果result包含+则为大故障报价
                    // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                    handler.sendEmptyMessage(1);
                } else{
                    handler.sendEmptyMessage(2);
                }

            }
        }.start();

    }

    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(UnRepairDetailActivity.this, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    InitUpdateData(result);
                    spBind_Type();
                    break;
                case 2:
                    Toast.makeText(UnRepairDetailActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 排查/已排查修改：绑定初始数据
     * **/
    private void InitUpdateData(String result) {
        try{
            JSONArray jsonArray=new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            bugtypename = jsonObject.getString("bugtype");

            bugaddr=jsonObject.getString("bugaddr");
            bugfindphoto=jsonObject.getString("bugfindphoto");
            bugfinddescrip=jsonObject.getString("bugfinddescrip");
            bugsendtime=jsonObject.getString("bugsendtime");

            //更新界面
            tvBugType.setText(bugtypename);//故障类型
            tvBugAddress.setText(bugaddr);//故障地址
            tvBugDescrip.setText(bugfinddescrip);//故障描述

            if(upd==1){
                tvBugDescrip2.setText(jsonObject.getString("repairdescrip"));
                tvRepairReason.setText(jsonObject.getString("repairreason"));
                tvRepairSolution.setText(jsonObject.getString("repairsolution"));
                type = Integer.parseInt(jsonObject.getString("type"));
                String repaircheckphoto = jsonObject.getString("repaircheckphoto");
                if(repaircheckphoto != null && !repaircheckphoto.equals("")){
                    String[] arrayphoto = repaircheckphoto.split(",");
                    for (int i=0;i<arrayphoto.length;i++){
                        addData(arrayphoto[i]);
                    }
                }

                if(jsonObject.getString("isonrepair").equals("1")){
                    ckb_isonrepair.setChecked(true);
                }else {
                    ckb_isonrepair.setChecked(false);
                }
                if(jsonObject.getString("charge").equals("0")){
                    ckb_basecharge.setChecked(false);
                }else {
                    ckb_basecharge.setChecked(true);
                }
            }

            setautocomplete();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 现场排查，提交故障确认
     * **/
    private void SureBug()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String repaircheckphoto = "";
                    filemap=new <String,File>HashMap();//从图片列表中读取图片文件
                    if (list.size()>12){Toast.makeText(UnRepairDetailActivity.this,"最多只能上传12张图片",Toast.LENGTH_SHORT).show();}
                    for(int i=0;i<list.size()&&i<12;i++)
                    {
                        String pic_path=list.get(i).get("filepath").toString();
                        String strFileName=PostParamTools.getFileName(pic_path);
                        if(pic_path.split("/").length > 1){
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
                        }else {
                            repaircheckphoto=repaircheckphoto+strFileName;
                            if(i!=list.size()-1) repaircheckphoto+=",";
                        }
                    }
                    String repairdescrip = tvBugDescrip2.getText().toString();
                    String repairreason = tvRepairReason.getText().toString();
                    String repairsolution = tvRepairSolution.getText().toString();
                    Map <String, String>  parammap=new HashMap <String, String> ();
                    if(tbug==0){
                        //data = "flag=1&";
                        parammap.put("flag",  "1");
                    }
                    else {
                        //data = "flag=2&";
                        parammap.put("flag",  "2");
                    }
                    parammap.put("bugid",  bugid);
                    parammap.put("roleid",  roleid);
                    parammap.put("repaircheckphoto",  URLEncoder.encode(repaircheckphoto, "UTF-8"));
                    parammap.put("repairdescrip",  URLEncoder.encode(repairdescrip, "UTF-8"));
                    parammap.put("repairreason",  URLEncoder.encode(repairreason, "UTF-8"));
                    parammap.put("repairsolution",  URLEncoder.encode(repairsolution, "UTF-8"));
                    parammap.put("bugsendtime",  URLEncoder.encode(bugsendtime, "UTF-8"));
                    String chargeflag="0";
                    if(ckb_basecharge.isChecked()){
                        chargeflag="1";
                    }
                    parammap.put("chargeflag",  chargeflag);
                    String isonrepair="0";
                    if(ckb_isonrepair.isChecked()){
                        isonrepair="1";
                    }
                    parammap.put("isonrepair",  isonrepair);

                    if(bugtypename.equals("信号灯")){
                        parammap.put("bugtype", "1");
                    }else {
                        parammap.put("bugtype", "0");
                    }
                    //data += "bugid="+bugid+"&repaircheckphoto=" + URLEncoder.encode(repaircheckphoto, "UTF-8") + "&repairdescrip=" + URLEncoder.encode(repairdescrip, "UTF-8");
                    //String result = PostParamTools.postGetInfo(spec, data);
                    try {
                        results= PostParamTools.post(spec, parammap, filemap);
                    }
                    catch (Exception e)
                    {
                        e.getMessage();
                    }
                    UnRepairDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (results.equals("1")) {
                                //提交成功，跳转页面
                                Toast.makeText(UnRepairDetailActivity.this, "排查成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UnRepairDetailActivity.this,UnRepairListActivity.class);
                                intent.putExtra("upd",upd);
                                intent.putExtra("isindex",1);//当返回到列表页时，点击返回按钮是否返回到首页：1是；0否
                                startActivity(intent);
                            } else {
                                //提交失败
                                Toast.makeText(UnRepairDetailActivity.this, "请求数据失败...",  Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}