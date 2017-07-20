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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class RepairDetailActivity extends AppCompatActivity {

    private TextView tvBugType,tvBugAddress,tvBugDescrip,tvRepairDescrip,tvRepairReason,tvRepairSolution;
    private Button btnChakan,btnSubmit,btnPaichaPic;
    private AutoCompleteTextView et_completedescrip;

    private String spec = UserModel.myhost+"getrepairbyid.php";
    private String data = "";//flag=0首次加载；flag=1提交修改数据
    private String bugtypename,bugaddr,bugfindphoto,bugfinddescrip,repairdescrip,repairreason,repairsolution,bugfinduserid,repaircheckphoto;
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
    private ImgcAdapter imgAdapter;
    private Map filemap;

    private String result="",results="";
    public String roleid=UserModel.getroleid();

    private Integer version=1;
    private DbManager dbm;
    private List<TemplateModel> templateModels_wgms=null;
    private List<TemplateModel> templateModelList=null;

    public static final int CHOOSE_PHOTO=2;
    private Integer upd=0;//1维修提交后的修改;0维修

    private TextView tv_title;//头部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_detail);

        bugfinduserid=UserModel.getuserid();

        upd = getIntent().getIntExtra("upd",0);
        bugid = getIntent().getStringExtra("bugid");
        data="flag=0&bugid=" + bugid;

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        imgAdapter=new ImgcAdapter(RepairDetailActivity.this);

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
        tvRepairDescrip=(TextView)findViewById(R.id.tvRepairDescrip);
        tvRepairReason=(TextView)findViewById(R.id.tvRepairReason);
        tvRepairSolution=(TextView)findViewById(R.id.tvRepairSolution);
        et_completedescrip=(AutoCompleteTextView)findViewById(R.id.et_completedescrip);

        btnPaichaPic = (Button) findViewById(R.id.btnPaichaPic);
        btnChakan = (Button) findViewById(R.id.btnChakan);
        imglist= (ListView) findViewById(R.id.imglist);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);

        tv_title=(TextView)findViewById(R.id.tv_title);
        if(upd==1){
            tv_title.setText("已维修故障详情");
        }else {
            tv_title.setText("待维修故障详情");
        }

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){

        //查看故障照片
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepairDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",bugfindphoto);
                startActivity(intent);
            }
        });
        //确认故障照片
        btnPaichaPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepairDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",repaircheckphoto);
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
            Toast.makeText(RepairDetailActivity.this, "请先安装好sd卡",Toast.LENGTH_LONG).show();}
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
    //拍照/选择照片结束后显示图片;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PICTURE://拍照
                // TODO Auto-generated method stub
                // 判断请求码和结果码是否正确，如果正确的话就在activity上显示刚刚所拍照的图片;
                if (resultCode == this.RESULT_OK) {
                    addData(srcPath);
                    imglist.setAdapter(imgAdapter);
                    setListViewHeightBasedOnChildren(imglist);
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
    private class ImgcAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public ImgcAdapter(Context context) {
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
                    Toast.makeText(RepairDetailActivity.this, "还有"+list.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RepairDetailActivity.this,ShowImageActivity.class);
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
                    Toast.makeText(RepairDetailActivity.this, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    InitUpdateData(result);
                    break;
                case 2:
                    Toast.makeText(RepairDetailActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 已维修修改：绑定初始数据
     * **/
    private void InitUpdateData(String result) {
        try{
            JSONArray jsonArray=new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            bugtypename = jsonObject.getString("bugtype");

            bugaddr=jsonObject.getString("bugaddr");
            bugfindphoto=jsonObject.getString("bugfindphoto");
            repaircheckphoto=jsonObject.getString("repaircheckphoto");
            bugfinddescrip=jsonObject.getString("bugfinddescrip");
            repairdescrip=jsonObject.getString("repairdescrip");
            repairreason=jsonObject.getString("repairreason");
            repairsolution=jsonObject.getString("repairsolution");

            //更新界面
            tvBugType.setText(bugtypename);//故障类型
            tvBugAddress.setText(bugaddr);//故障地址
            tvBugDescrip.setText(bugfinddescrip);//故障描述
            tvRepairDescrip.setText(repairdescrip);
            tvRepairReason.setText(repairreason);
            tvRepairSolution.setText(repairsolution);

            if(upd==1){//提交后修改
                String cdescrip = jsonObject.getString("completedescrip");
                if(!cdescrip.equals("null")){
                    et_completedescrip.setText(cdescrip);
                }
                String completephoto = jsonObject.getString("completephoto");
                if(!completephoto.equals("null") && !completephoto.equals("")){
                    String[] arrayphoto = completephoto.split(",");
                    for (int i=0;i<arrayphoto.length;i++){
                        addData(arrayphoto[i]);
                    }
                }
            }

            setautocomplete();

        }catch (Exception e){
            e.printStackTrace();
        }
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
            templateModels_wgms=new ArrayList<TemplateModel>();
            if(templateModelList!=null && templateModelList.size()>0){
                String bugtype=tvBugType.getText().toString();
                templateModels_wgms=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","完工描述").findAll();
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
                    Toast.makeText(RepairDetailActivity.this, "网络链接超时...",
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
        RepairDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(templateModels_wgms!=null && templateModels_wgms.size()>0){
                    final String[] wgms=new String[templateModels_wgms.size()];
                    int i=0;
                    for (TemplateModel pm : templateModels_wgms) {
                        wgms[i]=pm.getTempcontent();
                        i++;
                    }
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(RepairDetailActivity.this, android.R.layout.simple_list_item_1, wgms);
                    et_completedescrip.setAdapter(arrayAdapter);
                }else {
                    et_completedescrip.setAdapter(null);
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
                templateModels_wgms=new ArrayList<TemplateModel>();
                if(templateModelList!=null && templateModelList.size()>0){
                    String bugtype=tvBugType.getText().toString();
                    templateModels_wgms=dbm.selector(TemplateModel.class).where("temptype","=",bugtype).and("contenttype","=","完工描述").findAll();
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
     * 现场排查，提交故障确认
     * **/
    private void SureBug()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String completephoto = "";
                    filemap=new <String,File>HashMap();//从图片列表中读取图片文件
                    if (list.size()>12){Toast.makeText(RepairDetailActivity.this,"最多只能上传12张图片",Toast.LENGTH_SHORT).show();};
                    for(int i=0;i<list.size()&&i<12;i++)
                    {
//                        String myfilepath=list.get(i).get("filepath").toString();
//                        file_go=new File(myfilepath);
//                        filemap.put("file"+Integer.toString(i),file_go );
//                        completephoto=completephoto+PostParamTools.getFileName(myfilepath);
//                        if(i!=list.size()-1) completephoto+=",";

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
                            completephoto=completephoto+strFileName;
                            if(i!=list.size()-1) completephoto+=",";
                        }
                    }
                    String completedescrip = et_completedescrip.getText().toString();
                    //data = "flag=3&bugid="+bugid+"&completephoto=" + URLEncoder.encode(completephoto, "UTF-8") + "&completedescrip=" + URLEncoder.encode(completedescrip, "UTF-8");
                    //String result = PostParamTools.postGetInfo(spec, data);
                    Map <String, String>  parammap=new HashMap <String, String> ();
                    parammap.put("flag",  "3");
                    parammap.put("bugid",  bugid);
                    parammap.put("roleid",  roleid);
                    parammap.put("completephoto",  URLEncoder.encode(completephoto, "UTF-8"));
                    parammap.put("completedescrip",  URLEncoder.encode(completedescrip, "UTF-8"));

                    try {
                        results= PostParamTools.post(spec, parammap, filemap);

                    }
                    catch (Exception e)
                    {
                        e.getMessage();
                    }
                    RepairDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (results.equals("1")) {
                                //提交成功，跳转页面
                                Toast.makeText(RepairDetailActivity.this, "维修成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RepairDetailActivity.this,RepairListActivity.class);
                                intent.putExtra("upd",upd);
                                intent.putExtra("isindex",1);//当返回到列表页时，点击返回按钮是否返回到首页：1是；0否
                                startActivity(intent);
                            } else {
                                //提交失败
                                Toast.makeText(RepairDetailActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
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
