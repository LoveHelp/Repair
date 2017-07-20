package com.xianyi.chen.repair;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UpdateCheckActivity extends AppCompatActivity {
    private String bugId = null;

    private Bundle bundle = new Bundle();
    private TableLayout table;

    //维修人员名单
    //private String[] repairUsersArray = null;
    private static String wurl = UserModel.myhost;
    private Integer stateCode;


    private TextView tvBugState,tvBugType,tvBugAddress,tvBugDescrip,tvRepairUsers,tvRepairCheckTime,tvType;
    private Button btnBugFindPhoto,btnRepairCheck,btnCompletePhoto,btn_hejia,btn_wangong,btnUploadPhoto,btnUploadCompletePhoto;
    private EditText tvRepairDescription,tvRepairReason,tvRepairSolution,tvCompleteDes;
    private Button btnUpdateCheck,btnRepairChargeFile;
    private TextView tvRepairCharge;
    private CheckBox ckb_basecharge,ckb_isonrepair;

    private Adapter_SelBaojia adapter_selBaojia=null;
    private ArrayList<HashMap<String,Object>> dlist_part=null;
    private ListView lvPart;
    private EditText et_WPartName,et_WPartPrice,et_WPartNum;
    private TableRow trSelPart,trWritePart,trRepairCharge;
    private Button btnSelPart,btnWritePart;
    private boolean zdy=false;

    private TextView tv_title;//头部标题

    private ListView imglist,imglist_wangong;
    private String srcPath="";
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> list_wangong = new ArrayList<Map<String, Object>>();
    // 获取sd卡根目录地址,并创建图片父目录文件对象和文件的对象;
    String file_str = Environment.getExternalStorageDirectory().getPath();
    File mars_file = new File(file_str + "/my_camera");
    String filename;
    File file_go;
    private ImgAdapter imgAdapter;
    private ImgAdapter_wangong imgAdapter_wangong;
    private Map filemap;

    private String chargeFileUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_check);

        // 获取CheckActivity传递的bugId
        Intent getIntent = getIntent();
        bugId = getIntent.getStringExtra("bugId");

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        imgAdapter=new ImgAdapter(UpdateCheckActivity.this);
        imgAdapter_wangong=new ImgAdapter_wangong(UpdateCheckActivity.this);

        initialTable();
        getOfferInfo();
    }

    /**
     * 初始化控件
     **/
    private void InitControl() {
        table = (TableLayout) findViewById(R.id.bugTable);
        tvType = (TextView)findViewById(R.id.tvType);//故障类型
        trSelPart=(TableRow) findViewById(R.id.trSelPart);
        trWritePart=(TableRow) findViewById(R.id.trWritePart);
        trRepairCharge=(TableRow) findViewById(R.id.trRepairCharge);
        lvPart=(ListView)findViewById(R.id.lvPart);
        btnSelPart=(Button)findViewById(R.id.btnSelPart);
        btnWritePart=(Button)findViewById(R.id.btnWritePart);
        et_WPartName=(EditText)findViewById(R.id.et_WPartName);
        et_WPartPrice=(EditText)findViewById(R.id.et_WPartPrice);
        et_WPartNum=(EditText)findViewById(R.id.et_WPartNum);
        btnUpdateCheck=(Button)findViewById(R.id.btnUpdateCheck);

        tvBugState = (TextView)findViewById(R.id.tvBugState);//状态
        tvBugType = (TextView)findViewById(R.id.tvBugType);//项目分类
        tvBugAddress = (TextView)findViewById(R.id.tvBugAddress);//故障地址
        btnBugFindPhoto = (Button)findViewById(R.id.btnBugFindPhoto);//查看故障照片
        tvBugDescrip = (TextView)findViewById(R.id.tvBugDescrip);//故障描述
        tvRepairUsers = (TextView)findViewById(R.id.tvRepairUsers);//维修人员名单
        btnRepairCheck = (Button)findViewById(R.id.btnRepairCheck);//维修确认图片
        tvRepairDescription = (EditText) findViewById(R.id.tvRepairDescription);//故障现象
        tvRepairReason = (EditText)findViewById(R.id.tvRepairReason);//故障原因
        tvRepairSolution = (EditText)findViewById(R.id.tvRepairSolution);//解决办法
        tvRepairCheckTime = (TextView)findViewById(R.id.tvRepairCheckTime);//维修到达时间
        tvRepairCharge = (TextView)findViewById(R.id.tvRepairCharge);//维修基本费用
        tvCompleteDes = (EditText)findViewById(R.id.evCompleteDes);//完工描述
        btnCompletePhoto = (Button)findViewById(R.id.btnCompletePhoto);//完工照片
        btn_hejia=(Button)findViewById(R.id.btn_hejia);//核价
        btn_wangong=(Button)findViewById(R.id.btn_wangong);//完工

        ckb_basecharge=(CheckBox) findViewById(R.id.ckb_basecharge);
        ckb_isonrepair=(CheckBox) findViewById(R.id.ckb_isonrepair);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("故障详情");

        imglist= (ListView) findViewById(R.id.imglist);
        imglist_wangong= (ListView) findViewById(R.id.imglist_wangong);

        btnUploadPhoto=(Button)findViewById(R.id.btnUploadPhoto);//补传照片
        btnUploadCompletePhoto=(Button)findViewById(R.id.btnUploadCompletePhoto);//补传照片
        btnRepairChargeFile = (Button)findViewById(R.id.btnRepairChargeFile);//报价单下载
    }

    /**
     * 初始化控件事件
     **/
    private void InitControlEvent() {
        //选择配件
        btnSelPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tvBugType.getText().toString();
                zdy=false;//自定义配件
                trWritePart.setVisibility(View.GONE);
                trSelPart.setVisibility(View.VISIBLE);
                Intent intent = new Intent(UpdateCheckActivity.this,BaojiaActivity.class);
                //intent.putExtra("bugid",bugId);
                Bundle bundle=new Bundle();
                bundle.putString("bugid",bugId);
                String bugtype = tvBugType.getText().toString();
                bundle.putString("bugtype",bugtype);
                intent.putExtras(bundle);
                startActivityForResult(intent,0);
            }
        });
        //自定义配件
        btnWritePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zdy=true;//自定义配件
                trSelPart.setVisibility(View.INVISIBLE);
                lvPart.setAdapter(null);
                setListViewHeightBasedOnChildren(lvPart);
                trWritePart.setVisibility(View.VISIBLE);
            }
        });

        btnUpdateCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SureUpdate();
            }
        });
    }

    @Override
    protected void onDestroy(){
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /* 接收消息，更新UI */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            switch (msg.what){
                case 0:
                    setTableDetail(msg.getData());
                    break;
                case 1:
                    Toast.makeText(UpdateCheckActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(UpdateCheckActivity.this, "网络链接超时！", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(UpdateCheckActivity.this, "确认完毕", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateCheckActivity.this,UnCompleteListActivity.class);
                    //intent.putExtra("isindex",1);//当返回到列表页时，点击返回按钮是否返回到首页：1是；0否
                    startActivity(intent);
                    break;
                case 4:
                    Toast.makeText(UpdateCheckActivity.this, "操作失败,请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(UpdateCheckActivity.this, "报价单已保存至：文件管理 下的 baojiadan 文件夹下", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initialTable(){
        new Thread(){
            @Override
            public void run(){
                String getBugDetailStr = wurl + "getBugDetail.php";
                String data = "bugid=" + bugId;

                String bugDetailStr = PostParamTools.postGetInfo(getBugDetailStr, data);

                try{
                    JSONObject jsonObject = new JSONObject(bugDetailStr);

                    if(bugDetailStr==null){
                        handler.sendEmptyMessage(2);
                    }else if(!("null".equals(jsonObject.getString("bugDetail")))){
                        JSONObject jsonDetail = new JSONObject(jsonObject.getString("bugDetail"));
                        Message msg = new Message();
                        msg.what = 0;

                        stateCode = Integer.parseInt(jsonDetail.getString("state"));
                        String state = "维修完工待专人确认";
                        bundle.putString("tvBugState", state);//故障状态

                        bundle.putString("tvBugType", jsonDetail.getString("bugtype"));//项目分类
                        bundle.putString("tvBugAddress", jsonDetail.getString("bugaddr"));//故障地址
                        bundle.putString("tvBugFindTime", jsonDetail.getString("bugfindtime"));//发现时间
                        bundle.putString("tvBugFindUser", jsonDetail.getString("bugfind_username"));//发现人员
                        bundle.putString("btnBugFindPhoto", jsonDetail.getString("bugfindphoto"));//故障照片
                        bundle.putString("tvBugDescrip", jsonDetail.getString("bugfinddescrip"));//故障描述
                        //指挥中心分派故障
                        bundle.putString("tvBugSendTime", jsonDetail.getString("bugsendtime"));//指挥中心分派故障时间
                        bundle.putString("tvBugSendUser", jsonDetail.getString("bugsend_username"));// 指挥中心分派任务人
                        //甲方专人派单
                        bundle.putString("tvRepairSendTime", jsonDetail.getString("repairsendtime"));//派单时间
                        bundle.putString("tvRepairSendUser", jsonDetail.getString("repairsend_username"));// 派单人
                        //维修人员名单
//                        repairUsersArray = jsonDetail.getString("repairuserid").split(",");
//                        for(int i=0; i<repairUsersArray.length; i++){
//                            getUserName(String.valueOf(i), repairUsersArray[i]);
//                        }
                        if(!bugDetailStr.equals("null") && !bugDetailStr.equals("")){
                            String[] temp = bugDetailStr.split("\\+");
                            if(temp.length==2 && !"null".equals(temp[1]) && !"".equals(temp[1])){
                                String usernames=temp[1].toString();
                                bundle.putString("repairusername", usernames);//维修人员
                            }
                        }
                        //维修到达
                        bundle.putString("tvRepairCheckTime", jsonDetail.getString("repairchecktime"));//维修到达时间
                        String type = "";
                        if(!"null".equals(jsonDetail.getString("type"))){
                            Integer typeCode = Integer.parseInt(jsonDetail.getString("type"));
                            if(typeCode == 0){
                                type = "小故障";
                            }else if(typeCode == 1){
                                type = "大故障";
                            }else{
                                type = "故障类型错误！";
                            }
                        }
                        bundle.putString("tvType", type);//故障类型
                        bundle.putString("btnRepairCheck", jsonDetail.getString("repaircheckphoto"));//维修确认图片
                        if("null".equals(jsonDetail.getString("repairdescrip"))){
                            bundle.putString("tvRepairDescription", "");//故障现象
                        }else{
                            bundle.putString("tvRepairDescription", jsonDetail.getString("repairdescrip"));//故障现象
                        }
                        if("null".equals(jsonDetail.getString("repairreason"))){
                            bundle.putString("tvRepairReason", "");//故障原因
                        }else{
                            bundle.putString("tvRepairReason", jsonDetail.getString("repairreason"));//故障原因
                        }
                        if("null".equals(jsonDetail.getString("repairsolution"))){
                            bundle.putString("tvRepairSolution", "");//解决办法
                        }else{
                            bundle.putString("tvRepairSolution", jsonDetail.getString("repairsolution"));//解决办法
                        }
                        if("null".equals(jsonDetail.getString("charge"))){
                            bundle.putString("tvRepairCharge", "0");//维修基本费用
                        }else{
                            bundle.putString("tvRepairCharge", jsonDetail.getString("charge"));//维修基本费用
                        }
                        bundle.putString("btnRepairChargeFile", jsonDetail.getString("chargefile"));//维修报价文件
                        bundle.putString("isonrepair",jsonDetail.getString("isonrepair"));//是否在保

                        //完工
                        if(stateCode > 5 && stateCode != 41){
                            bundle.putString("btnCompletePhoto", jsonDetail.getString("completephoto"));//完工照片
                            if("null".equals(jsonDetail.getString("completedescrip"))){
                                bundle.putString("tvCompleteDes", "");//完工描述
                            }else{
                                bundle.putString("tvCompleteDes", jsonDetail.getString("completedescrip"));//完工描述
                            }
                        }

                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setTableDetail(Bundle bun){
        //发现故障
        tvBugState.setText(bun.getString("tvBugState"));//状态
        tvBugType.setText(bun.getString("tvBugType"));//项目分类
        tvBugAddress.setText(bun.getString("tvBugAddress"));//故障地址

        final String bugFindPhoto = bun.getString("btnBugFindPhoto");//查看故障照片
        btnBugFindPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCheckActivity.this, BigPicActivity.class);
                intent.putExtra("photo", bugFindPhoto);
                startActivity(intent);
            }
        });
        tvBugDescrip.setText(bun.getString("tvBugDescrip"));//故障描述

        tvRepairUsers.setText(bun.getString("repairusername"));//维修人员名单

        final String bugRepairPhoto = bun.getString("btnRepairCheck");//维修确认图片
        btnRepairCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateCheckActivity.this, BigPicActivity.class);
                intent.putExtra("photo", bugRepairPhoto);
                startActivity(intent);
            }
        });
        if(bugRepairPhoto != null && !bugRepairPhoto.equals("null") && bugRepairPhoto.length()>0){
            String[] arrayphoto = bugRepairPhoto.split(",");
            for (int i=0;i<arrayphoto.length;i++){
                addData(arrayphoto[i]);
            }
        }
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getchoosephotobtn(1);
            }
        });

        tvRepairDescription.setText(bun.getString("tvRepairDescription"));//故障现象
        tvRepairReason.setText(bun.getString("tvRepairReason"));//故障原因
        tvRepairSolution.setText(bun.getString("tvRepairSolution"));//解决办法
        tvRepairCheckTime.setText(bun.getString("tvRepairCheckTime"));//维修到达时间
        tvType.setText(bun.getString("tvType"));//故障类型
        if(tvType.getText().equals("大故障") && stateCode == 4){
            trRepairCharge.setVisibility(View.VISIBLE);
        }
        tvRepairCharge.setText(bun.getString("tvRepairCharge"));//维修基本费用
        String charge = bun.getString("tvRepairCharge");
        if(charge.equals("0")){
            ckb_basecharge.setChecked(false);
        }else {
            ckb_basecharge.setChecked(true);
        }
        String isonrepair = bun.getString("isonrepair");
        if(isonrepair.equals("1")){
            ckb_isonrepair.setChecked(true);
        }else {
            ckb_isonrepair.setChecked(false);
        }

        //下载维修报价文件
        String chargeFile = bun.getString("btnRepairChargeFile");
        if("".equals(chargeFile) || "null".equals(chargeFile) || (chargeFile == null)){
            TableRow tvRepairChargeFile = (TableRow)findViewById(R.id.trRepairChargeFile);
            table.removeView(tvRepairChargeFile);
        }else{
            chargeFileUrl = UserModel.FILE_PATH  + chargeFile;
            btnRepairChargeFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            download(chargeFileUrl);
                        }
                    }).start();
                }
            });
        }

        //完工
        if(stateCode <6 || stateCode == 41){
            TableRow trCompleteDes = (TableRow)findViewById(R.id.trCompleteDes);//完工描述
            table.removeView(trCompleteDes);
            TableRow trCompletePhoto = (TableRow)findViewById(R.id.trCompletePhoto);//完工照片
            table.removeView(trCompletePhoto);
        }else{
            final String bugCompletePhoto = bun.getString("btnCompletePhoto");//完工照片
            btnCompletePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UpdateCheckActivity.this, BigPicActivity.class);
                    intent.putExtra("photo", bugCompletePhoto);
                    startActivity(intent);
                }
            });
            if(bugCompletePhoto != null && !bugCompletePhoto.equals("null") && bugCompletePhoto.length()>0){
                String[] arrayphoto = bugCompletePhoto.split(",");
                for (int i=0;i<arrayphoto.length;i++){
                    addData_wangong(arrayphoto[i]);
                }
            }
            btnUploadCompletePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getchoosephotobtn(2);
                }
            });

            tvCompleteDes.setText(bun.getString("tvCompleteDes"));//完工描述
        }

        if(stateCode == 4){
            HejiaEvent();//甲方专人核价-显示修改和核价按钮
        }else if(stateCode == 6){
            WangongEvent();//甲方专人完工确认-显示修改和完工确认按钮
        }

    }

    /**
     * 甲方专人核价-显示修改和核价按钮
     * **/
    private void HejiaEvent(){
        btn_hejia.setVisibility(View.VISIBLE);
        btn_wangong.setVisibility(View.GONE);
        btn_hejia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateCheckActivity.this, CheckDetailActivity.class);
                intent.putExtra("bugId", bugId);
                startActivity(intent);
            }
        });
    }

    private String roleid=UserModel.getroleid();
    private String data,writers;
    /**
     * 甲方专人完工确认-显示修改和完工确认按钮
     * **/
    private void WangongEvent(){
        btn_wangong.setVisibility(View.VISIBLE);
        btn_hejia.setVisibility(View.GONE);
        btn_wangong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = "bugid="+ bugId;
                        String spec=UserModel.myhost+"writecomplete.php";
                        data=data+"&roleid="+roleid;
                        writers = PostParamTools.postGetInfo(spec, data);
                        if(writers==null){
                            handler.sendEmptyMessage(2);
                        }else if(!("null").equals(writers) && !("").equals(writers)) {
                            handler.sendEmptyMessage(3);
                        }else {
                            handler.sendEmptyMessage(4);
                        }
                    }
                }).start();

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == 0){//选择配件
                    GetSelPartList(data);
                }
                break;
            case 1://故障确定照片
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
                    addData(path);
                }
                break;
            case 2://完工照片
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
                    addData_wangong(path);
                }
                break;
        }
    }

    /**绑定初始配件报价信息**/
    private void getOfferInfo(){
        new Thread(){
            @Override
            public void run(){
                String res = PostParamTools.postGetInfo(UserModel.myhost+"getPartsDetail.php", "bugid=" + bugId);//显示列表
                if(res==null){
                    Toast.makeText(UpdateCheckActivity.this, "网络链接超时", Toast.LENGTH_SHORT).show();
                }else if (!res.equals("null") && !res.equals("")) {
                    // 如果获取的result数据不为空，那么对其进行JSON解析。
                    try{
                        dlist_part = new ArrayList<HashMap<String, Object>>();
                        JSONArray jsonArray=new JSONArray(res);
                        ArrayList<HashMap<String,Object>> partArrayList = new ArrayList<HashMap<String, Object>>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, Object> part = new HashMap<String, Object>();

                            part.put("partid", jsonObject.getString("partid"));
                            part.put("partname", jsonObject.getString("part_name"));
                            part.put("partprice", jsonObject.getString("parts_price"));
                            part.put("reference", jsonObject.getString("reference"));
                            part.put("num", jsonObject.getString("parts_num"));

                            dlist_part.add(part);
                        }
                        UpdateCheckActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dlist_part!=null){
                                    adapter_selBaojia=new Adapter_SelBaojia(UpdateCheckActivity.this,dlist_part);
                                    lvPart.setAdapter(adapter_selBaojia);
                                    setListViewHeightBasedOnChildren(lvPart);
                                    trSelPart.setVisibility(View.VISIBLE);
                                    adapter_selBaojia.notifyDataSetChanged();
                                }
                            }
                        });
                        //handler.sendEmptyMessage(2);// 发送处理消息
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }.start();

    }

    /**
     * 选中配件
     * **/
    private void GetSelPartList(Intent data){
        Bundle bundle = data.getExtras();
        HashMap<Object, Boolean> hashMap = (HashMap<Object, Boolean>) bundle.get("map");

        try{
            if (hashMap!=null) {
                dlist_part = new ArrayList<HashMap<String, Object>>();
                String keys = "";
                Iterator iter = hashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    //keys+=key.toString()+" ; ";
                    String[] arry_part = key.toString().split("[|]");
                    String partid = arry_part[0];
                    String partname = arry_part[1];
                    String partprice = arry_part[2];
                    String reference = arry_part[3];

                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("partid", partid);
                    item.put("partname", partname);
                    item.put("partprice", partprice);
                    item.put("reference", reference);
                    item.put("num", 1);
                    dlist_part.add(item);
                }
                UpdateCheckActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dlist_part!=null){
                            adapter_selBaojia=new Adapter_SelBaojia(UpdateCheckActivity.this,dlist_part);
                            lvPart.setAdapter(adapter_selBaojia);
                            setListViewHeightBasedOnChildren(lvPart);
                            trSelPart.setVisibility(View.VISIBLE);
                            adapter_selBaojia.notifyDataSetChanged();
                        }
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 100;
        listView.setLayoutParams(params);
    }

    /**
     * 专人：提交修改维修人员上报内容
     * **/
    private void SureUpdate()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = "";
                    Float bigcharge=Float.parseFloat("0");
                    String sqllist="";
                    if(adapter_selBaojia!=null && !zdy) {//选择配件
                        HashMap<Integer, String> hm=adapter_selBaojia.hm;
                        ArrayList<HashMap<String, Object>> dlist_selparts = dlist_part;
                        if (dlist_selparts != null && dlist_selparts.size()>0) {
                            HashMap<String, Object> item;
                            sqllist="insert into offer(bugid,partid,num,price,partname) values";
                            for (int i=0;i<dlist_selparts.size();i++)
                            {
                                item = new HashMap<String, Object>();
                                item=dlist_selparts.get(i);

                                String partid = item.get("partid").toString();
                                String partprice=item.get("partprice").toString();//单价
                                String partname=item.get("partname").toString();
                                String num=hm.get(i);
                                Float zj=Float.parseFloat(partprice)*Float.parseFloat(num);
                                bigcharge = bigcharge + zj;

                                //sqllist+="("+bugid+","+partid+","+num+","+zj+"),";
                                sqllist+="("+bugId+","+partid+","+num+","+partprice+",'"+partname+"'),";
                            }
                            sqllist=sqllist.substring(0,sqllist.length()-1);
                        }
                    }else if(zdy){//自定义配件
                        String partname=et_WPartName.getText().toString();
                        Float partprice=Float.parseFloat(et_WPartPrice.getText().toString());
                        Integer partnum=Integer.parseInt(et_WPartNum.getText().toString());
                        bigcharge=partprice*partnum;

                        //sqllist="insert into offer(bugid,partid,num,price,partname) values("+bugid+",0,"+partnum+","+charge+",'"+partname+"')";
                        sqllist="insert into offer(bugid,partid,num,price,partname) values("+bugId+",0,"+partnum+","+partprice+",'"+partname+"')";

                    }

                    String repaircheckphoto = "";
                    String repaircheckphoto_old = "";
                    filemap=new <String,File>HashMap();//从图片列表中读取图片文件
                    if (list.size()>12){Toast.makeText(UpdateCheckActivity.this,"最多只能上传12张图片",Toast.LENGTH_SHORT).show();}
                    for(int i=0;i<list.size()&&i<12;i++)
                    {
                        String pic_path=list.get(i).get("filepath").toString();
                        String strFileName=PostParamTools.getFileName(pic_path);
                        if(pic_path.split("/").length > 1){
                            String newName = "ys_"+strFileName;
                            String targetPath = pic_path.substring(0,pic_path.indexOf(strFileName))+newName;
                            //调用压缩图片的方法，返回压缩后的图片path
                            final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 60);
                            final File compressedPic = new File(compressImage);
                            if (compressedPic.exists()) {
                                filemap.put("file"+Integer.toString(i),compressedPic );
                            }else{//直接上传
                                file_go=new File(pic_path);
                                filemap.put("file"+Integer.toString(i),file_go );
                            }
                            repaircheckphoto=repaircheckphoto+newName;
                            if(i!=list.size()-1) repaircheckphoto+=",";
                        }else {
                            repaircheckphoto_old=repaircheckphoto_old+strFileName;
                            if(i!=list.size()-1) repaircheckphoto_old+=",";
                        }
                    }

                    String completephoto = "";
                    String completephoto_old = "";
                    //filemap=new <String,File>HashMap();//从图片列表中读取图片文件
                    if (list_wangong.size()>12){Toast.makeText(UpdateCheckActivity.this,"最多只能上传12张图片",Toast.LENGTH_SHORT).show();};
                    for(int i=0;i<list_wangong.size()&&i<12;i++)
                    {
                        String pic_path=list_wangong.get(i).get("filepath").toString();
                        String strFileName=PostParamTools.getFileName(pic_path);
                        if(pic_path.split("/").length > 1){
                            String newName = "ys_"+strFileName;
                            String targetPath = pic_path.substring(0,pic_path.indexOf(strFileName))+newName;
                            //调用压缩图片的方法，返回压缩后的图片path
                            final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 60);
                            final File compressedPic = new File(compressImage);
                            if (compressedPic.exists()) {
                                filemap.put("filewg"+Integer.toString(i),compressedPic );
                            }else{//直接上传
                                file_go=new File(pic_path);
                                filemap.put("filewg"+Integer.toString(i),file_go );
                            }
                            completephoto=completephoto+newName;
                            if(i!=list_wangong.size()-1) completephoto+=",";
                        }else {
                            completephoto_old=completephoto_old+strFileName;
                            if(i!=list.size()-1) completephoto_old+=",";
                        }
                    }

                    String repairdescrip = tvRepairDescription.getText().toString();
                    String repairreason = tvRepairReason.getText().toString();
                    String repairsolution = tvRepairSolution.getText().toString();
                    //String charge = tvRepairCharge.getText().toString();
                    String completedes = tvCompleteDes.getText().toString();
//                    if(charge==null || charge.equals("")){
//                        charge="0";
//                    }
                    Map<String, String> parammap=new HashMap <String, String> ();
                    parammap.put("bugid",  bugId);
                    parammap.put("roleid",  roleid);
                    parammap.put("repaircheckphoto",  URLEncoder.encode(repaircheckphoto, "UTF-8"));
                    parammap.put("completephoto",  URLEncoder.encode(completephoto, "UTF-8"));
                    parammap.put("repaircheckphoto_old",  URLEncoder.encode(repaircheckphoto_old, "UTF-8"));
                    parammap.put("completephoto_old",  URLEncoder.encode(completephoto_old, "UTF-8"));
                    parammap.put("repairdescrip",  URLEncoder.encode(repairdescrip, "UTF-8"));
                    parammap.put("repairreason",  URLEncoder.encode(repairreason, "UTF-8"));
                    parammap.put("repairsolution",  URLEncoder.encode(repairsolution, "UTF-8"));
                    parammap.put("bigcharge",  bigcharge.toString());
                    parammap.put("completedes",  URLEncoder.encode(completedes, "UTF-8"));
                    parammap.put("sqllist",  URLEncoder.encode(sqllist, "UTF-8"));
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
                    //data += "bugid="+bugid+"&repaircheckphoto=" + URLEncoder.encode(repaircheckphoto, "UTF-8") + "&repairdescrip=" + URLEncoder.encode(repairdescrip, "UTF-8");
                    //String result = PostParamTools.postGetInfo(spec, data);

                    try {
                        result= PostParamTools.post(wurl+"updateCheck.php", parammap,filemap);
                    }
                    catch (Exception e)
                    {
                        e.getMessage();
                    }
                    if (result.equals("1")) {
                        //提交成功，跳转页面
                        //Intent intent = new Intent();

                        if(stateCode==6){
                            //intent.setClass(UpdateCheckActivity.this,UnCompleteListActivity.class);//完工待专人审核
                            //intent.putExtra("qf","3");
                            handler.sendEmptyMessage(1);
                        }else if(stateCode==4){
//                            intent.setClass(UpdateCheckActivity.this,CheckActivity.class);
//                            intent.putExtra("state",4);//4甲方专人核价；41指挥中心核价
                            handler.sendEmptyMessage(1);
                        }
                        //startActivity(intent);
                    } else {
                        //提交失败
                        Toast.makeText(UpdateCheckActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Created by Administrator on 2016/9/1 0001.
     */
    private class Adapter_SelBaojia extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, Object>> list;
        Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();
        public HashMap<Object,Boolean> hashMap=new HashMap<Object, Boolean>();
        public ArrayList<HashMap<String,Object>> dlist_selpart= new ArrayList<HashMap<String, Object>>();
        //定义一个HashMap，用来存放EditText的值，Key是position
        public HashMap<Integer, String> hm = new HashMap<Integer, String>();

        public Adapter_SelBaojia(Context context, ArrayList<HashMap<String, Object>> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView  = LayoutInflater.from(context).inflate(R.layout.selpart, null);
                holder = new ViewHolder();

                //holder.ckb_parts = (CheckBox) convertView.findViewById(R.id.ckb_parts);
                holder.btnDelPart = (Button) convertView.findViewById(R.id.btnDelPart);
                holder.tv_partid = (TextView) convertView.findViewById(R.id.tv_partid);
                holder.tv_partname = (TextView) convertView.findViewById(R.id.tv_partname);
                holder.tv_partprice = (TextView) convertView.findViewById(R.id.tv_partprice);
                holder.et_Money = (EditText) convertView.findViewById(R.id.et_Money);

                if(stateCode==6){
                    holder.btnDelPart.setVisibility(View.GONE);
                    holder.et_Money.setEnabled(false);
                }

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_partid.setText(list.get(position).get("partid").toString());
            holder.tv_reference = (TextView) convertView.findViewById(R.id.tv_reference);
            holder.tv_partname.setText(list.get(position).get("partname").toString());
            holder.tv_partprice.setText(list.get(position).get("partprice").toString()+" 元");
            holder.et_Money.setText(list.get(position).get("num").toString());
            holder.tv_reference.setText(list.get(position).get("reference").toString());

            hm.put(position,list.get(position).get("num").toString());

            holder.et_Money.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    hm.put(position, editable.toString());

                    //String num = holder.et_Money.getText().toString();
                    //Integer partid = Integer.parseInt(list.get(position).get("partid").toString());
                    //Float partprice = Float.parseFloat(list.get(position).get("partprice").toString());
                    //HashMap<String, Object> item = new HashMap<String, Object>();
                    //item.put("partid", partid);
                    //item.put("partprice", partprice);
                    //item.put("num", num);
                    //dlist_selpart.add(item);
                }
            });

            //如果hashMap不为空，就设置editText
            if(hm.get(position) != null){
                holder.et_Money.setText(hm.get(position));
            }

            //删除
            holder.btnDelPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfo(position);
                }

            });

            return convertView;
        }

        /**
         * listview item 删除
         * **/
        public void showInfo(final int position) {
            list.remove(position);
            // 通过程序我们知道删除了，但是怎么刷新ListView呢？
            // 只需要重新设置一下adapter
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lvPart);
        }

        class ViewHolder{
            TextView tv_partid,tv_partname,tv_partprice,tv_reference;
            EditText et_Money;
            Button btnDelPart;
            //CheckBox ckb_parts;
        }

    }

    /**
     * 获取选择照片按钮
     * **/
    public void getchoosephotobtn(int i){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,i);//打开相册
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
        map.put("filepath", path);
        list.add(map);
        imglist.setAdapter(imgAdapter);
        setListViewHeightBasedOnChildren(imglist);
    }

    public void delData_wangong(int pos)
    {
        list_wangong.remove(pos);
        imglist_wangong.setAdapter(imgAdapter_wangong);
        setListViewHeightBasedOnChildren(imglist_wangong);
    }
    public void addData_wangong(String path)
    {
        Map<String, Object> map;
        map = new HashMap<String, Object>();
        map.put("filepath", path);
        list_wangong.add(map);
        imglist_wangong.setAdapter(imgAdapter_wangong);
        setListViewHeightBasedOnChildren(imglist_wangong);
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
                    Toast.makeText(UpdateCheckActivity.this, "还有"+list.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateCheckActivity.this,ShowImageActivity.class);
                    intent.putExtra("imgpath",(String)list.get(position).get("filepath"));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    private class ImgAdapter_wangong extends BaseAdapter {
        private LayoutInflater mInflater;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public ImgAdapter_wangong(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list_wangong.size();//返回数组的长度
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
            holder.itemimgfilename.setText(PostParamTools.getFileName((String)list_wangong.get(position).get("filepath")));
            /**为Button添加点击事件*/
            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(FindbugActivity.this,"删除图片吧",Toast.LENGTH_SHORT).show();
                    delData_wangong(position);
                    Toast.makeText(UpdateCheckActivity.this, "还有"+list_wangong.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UpdateCheckActivity.this,ShowImageActivity.class);
                    intent.putExtra("imgpath",(String)list_wangong.get(position).get("filepath"));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    //下载具体操作
    private void download( String downloadUrl) {
        try {
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            //int contentLength = conn.getContentLength();
            //Log.e(TAG, "contentLength = " + contentLength);
            //创建文件夹 MyDownLoad，在存储卡下
            String dirName = Environment.getExternalStorageDirectory().getPath() + "/baojiadan";
            File file = new File(dirName);
            //不存在创建
            if (!file.exists()) {
                file.mkdirs();
            }
            //下载后的文件名
            String newFilename = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
            String fileName = dirName + "/" + newFilename;
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            file1.createNewFile();
            OutputStream os = new FileOutputStream(fileName);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            //Log.e(TAG, "download-finish");
            os.close();
            is.close();
            handler.sendEmptyMessage(5);// 发送处理消息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
