package com.xianyi.chen.repair;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/2 0002.
 */
public class BugDetailActivity extends AppCompatActivity {
    private String bugId = null;

    private Bundle bundle = new Bundle();

    //维修人员名单
    //private String[] repairUsersArray = null;

    private static String WEBURL =  UserModel.myhost ;
    private String spec,data,result_del;
    private String jsonReceiverStr,writers;//故障接收者返回数据
    private String ruserid="";//接收人

    private String chargeFileUrl = "";

    private static String EMPTY = "";

    private TableLayout table;

    private Integer stateCode;

    private AutoCompleteTextView tvBugAddress;
    private EditText tvBugDescrip;
    private TextView tvBugState,tvBugType,tvBugFindTime,tvBugFindUser,tvBugSendTime,tvBugSendUser,tvRepairSendTime,tvRepairSendUser,tvRepairUsers;
    private TextView tvRepairDescription,tvRepairReason,tvRepairSolution,tvRepairCheckTime,tvType,tvRepairCharge,tvRepairChargeTime,tvRemark,tvCompleteTime,tvCompleteDes,tvCompleteCheckTimez_zr,tvCompleteCheckTime;
    private Button btnBugFindPhoto,btnRepairCheck,btnRepairChargeFile,btnCompletePhoto,btn_update,btnUploadPhoto;
    private TextView tv_basecharge,tv_isonrepair;

    private TableRow tr_fenpai,tr_wangong,tr_hejia,tr_jfenpai,tr_photolist;
    private Button btn_fenpai,btn_delete,btn_wangong,btn_hejia,btn_jfenpai;
    private String roleid=UserModel.getroleid();
    private static Boolean[] arr = null;
    private List<HashMap<String, String>> workers = null;
    private PartDetailAdapter partDetailAdapter;
    private ListView lv_parts;

    private TextView tv_title;//头部标题

    private ListView imglist;
    File file_go;
    private ImgAdapter imgAdapter;
    private Map filemap;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    public static final int CHOOSE_PHOTO=2;

    //本地缓存故障地址
    private Integer version=1;
    private DbManager dbm;
    private List<AddrModel> addrModels=null;
    private List<AddrModel> addrModelArrayList=null;
    private String bugtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bug_detail);

        // 获取CheckActivity传递的bugId
        Intent getIntent = getIntent();
        bugId = getIntent.getStringExtra("bugId");

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        imgAdapter=new ImgAdapter(BugDetailActivity.this);

        queryreceiver();//查询甲方专人

        getAvailableWorkers();//获取维修人员列表

        initialTable();//获取网络数据
        getOfferInfo();//获取相关配件信息

    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){
        table = (TableLayout) findViewById(R.id.bugTable);

        //发现故障
        tvBugState = (TextView)findViewById(R.id.tvBugState);//状态
        tvBugType = (TextView)findViewById(R.id.tvBugType);//项目分类
        tvBugAddress = (AutoCompleteTextView)findViewById(R.id.tvBugAddress);//故障地址
        tvBugFindTime = (TextView)findViewById(R.id.tvBugFindTime);//发现时间
        tvBugFindUser = (TextView)findViewById(R.id.tvBugFindUser);//发现人
        btnBugFindPhoto = (Button)findViewById(R.id.btnBugFindPhoto);//查看故障照片
        tvBugDescrip = (EditText)findViewById(R.id.tvBugDescrip);//故障描述

        //指挥中心分派故障
        tvBugSendTime = (TextView)findViewById(R.id.tvBugSendTime);//指挥中心分派故障时间
        tvBugSendUser = (TextView)findViewById(R.id.tvBugSendUser);//指挥中心分派任务人

        //派单
        tvRepairSendTime = (TextView)findViewById(R.id.tvRepairSendTime);//派单时间
        tvRepairSendUser = (TextView)findViewById(R.id.tvRepairSendUser);//派单人
        tvRepairUsers = (TextView)findViewById(R.id.tvRepairUsers);//维修人员名单

        //现场排查
        btnRepairCheck = (Button)findViewById(R.id.btnRepairCheck);//维修确认图片
        tvRepairDescription = (TextView)findViewById(R.id.tvRepairDescription);//故障现象
        tvRepairReason = (TextView)findViewById(R.id.tvRepairReason);//故障原因
        tvRepairSolution = (TextView)findViewById(R.id.tvRepairSolution);//解决办法
        tvRepairCheckTime = (TextView)findViewById(R.id.tvRepairCheckTime);//维修到达时间
        tvType = (TextView)findViewById(R.id.tvType);//故障类型

        tv_basecharge=(TextView) findViewById(R.id.tv_basecharge);
        tv_isonrepair=(TextView) findViewById(R.id.tv_isonrepair);

        //维修报价
        tvRepairCharge = (TextView)findViewById(R.id.tvRepairCharge);//维修报价
        tvRepairChargeTime = (TextView)findViewById(R.id.tvRepairChargeTime);//维修报价时间
        lv_parts=(ListView) findViewById(R.id.lv_parts);
        tvRemark = (TextView)findViewById(R.id.tvRemark);//备注
        btnRepairChargeFile = (Button)findViewById(R.id.btnRepairChargeFile);

        //维修
        btnCompletePhoto = (Button)findViewById(R.id.btnCompletePhoto);//完工照片
        tvCompleteTime = (TextView)findViewById(R.id.tvCompleteTime);//完工时间
        tvCompleteDes = (TextView)findViewById(R.id.tvCompleteDes);//完工描述

        //完工确认-专人
        tvCompleteCheckTimez_zr = (TextView)findViewById(R.id.tvCompleteCheckTime_zr);//专人完工确认时间
        //完工确认-指挥中心
        tvCompleteCheckTime = (TextView)findViewById(R.id.tvCompleteCheckTime);//指挥中心完工确认时间


        tr_fenpai=(TableRow)findViewById(R.id.tr_fenpai);
        btn_fenpai=(Button)findViewById(R.id.btn_fenpai);//分派
        btn_delete=(Button)findViewById(R.id.btn_delete);//删除任务
        btn_update=(Button)findViewById(R.id.btn_update);//修改任务

        btnUploadPhoto=(Button)findViewById(R.id.btnUploadPhoto);//补传照片
        tr_photolist=(TableRow)findViewById(R.id.tr_photolist);

        tr_hejia=(TableRow)findViewById(R.id.tr_hejia);
        btn_hejia=(Button)findViewById(R.id.btn_hejia);//核价

        tr_wangong=(TableRow)findViewById(R.id.tr_wangong);
        btn_wangong=(Button)findViewById(R.id.btn_wangong);//指挥中心完工确认

        tr_jfenpai=(TableRow)findViewById(R.id.tr_jfenpai);//
        btn_jfenpai=(Button)findViewById(R.id.btn_jfenpai);//分派

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("故障详情");

        imglist= (ListView) findViewById(R.id.imglist);

    }

    /**
     * 初始化控件事件
     *  **/
    private void InitControlEvent(){

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
                    Toast.makeText(BugDetailActivity.this, "报价单已保存至：文件管理 下的 baojiadan 文件夹下", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(BugDetailActivity.this, "分派成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BugDetailActivity.this, ManagerActivity.class);
                    startActivity(intent);
                    break;
                case 3:
                    Toast.makeText(BugDetailActivity.this, "分派失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(BugDetailActivity.this, "网络链接超时", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(BugDetailActivity.this, "操作失败,请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(BugDetailActivity.this, "确认完毕", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(BugDetailActivity.this,UnCompleteListActivity.class);
                    startActivity(intent1);
                    break;
                case 7:
                    Toast.makeText(BugDetailActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取网络数据
     * **/
    private void initialTable(){
        new Thread(){
            @Override
            public void run(){
                String getBugDetailStr = WEBURL + "getBugDetail.php";
                String data = "bugid=" + bugId;

                String bugDetailStr = PostParamTools.postGetInfo(getBugDetailStr, data);

                if(bugDetailStr == null){
                    handler.sendEmptyMessage(4);
                }else {
                    try{
                        JSONObject jsonObject = new JSONObject(bugDetailStr);
                        if(!("null".equals(jsonObject.getString("bugDetail")))){
                            JSONObject jsonDetail = new JSONObject(jsonObject.getString("bugDetail"));
                            Message msg = new Message();
                            msg.what = 0;

                            stateCode = Integer.parseInt(jsonDetail.getString("state"));
                            String state;
                            if(stateCode == 0){
                                state = "指挥中心待分派";
                            }else if(stateCode == 1){
                                state = "甲方负责人待分派";
                            }else if(stateCode == 2){
                                state = "维修人员待排查";
                            }else if(stateCode == 3){
                                state = "维修人员待报价";
                            }else if(stateCode == 4){
                                state = "维修报价待专人审核";
                            }else if(stateCode == 41){
                                state = "维修报价待指挥中心审核";
                            }else if(stateCode == 51){
                                state = "报价驳回挂起";
                            }else if(stateCode == 5){
                                state = "待维修";
                            }else if(stateCode == 6){
                                state = "维修完工待专人确认";
                            }else if(stateCode == 61){
                                state = "维修完工待指挥中心确认";
                            }else if(stateCode == 7){
                                state = "完工已确认";
                            }else{
                                state = "状态错误！";
                            }

                            bundle.putString("tvBugState", state);//故障状态

                            bundle.putString("tvBugType", jsonDetail.getString("bugtype"));//项目分类
                            bundle.putString("tvBugAddress", jsonDetail.getString("bugaddr"));//故障地址
                            bundle.putString("tvBugFindTime", jsonDetail.getString("bugfindtime"));//发现时间
                            bundle.putString("tvBugFindUser", jsonDetail.getString("bugfind_username"));//发现人员
                            bundle.putString("btnBugFindPhoto", jsonDetail.getString("bugfindphoto"));//故障照片
                            bundle.putString("tvBugDescrip", jsonDetail.getString("bugfinddescrip"));//故障描述
                            //指挥中心分派故障
                            if(stateCode > 0){
                                bundle.putString("tvBugSendTime", jsonDetail.getString("bugsendtime"));//指挥中心分派故障时间
                                bundle.putString("tvBugSendUser", jsonDetail.getString("bugsend_username"));// 指挥中心分派任务人
                            }
                            //甲方专人派单
                            if(stateCode > 1){
                                bundle.putString("tvRepairSendTime", jsonDetail.getString("repairsendtime"));//派单时间
                                bundle.putString("tvRepairSendUser", jsonDetail.getString("repairsend_username"));// 派单人
                                if(!bugDetailStr.equals("null") && !bugDetailStr.equals("")){
                                    String[] temp = bugDetailStr.split("\\+");
                                    if(temp.length==2 && !"null".equals(temp[1]) && !"".equals(temp[1]) && !"无".equals(temp[1])){
                                        String usernames=temp[1].toString();
                                        bundle.putString("repairusername", usernames);//维修人员
                                    }
                                }
                                //维修人员名单
//                            repairUsersArray = jsonDetail.getString("repairuserid").split(",");
//                            for(int i=0; i<repairUsersArray.length; i++){
//                                getUserName(String.valueOf(i), repairUsersArray[i]);
//                            }
                            }
                            //维修到达
                            if(stateCode > 2){
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
                                    bundle.putString("tvRepairDescription", EMPTY);//故障现象
                                }else{
                                    bundle.putString("tvRepairDescription", jsonDetail.getString("repairdescrip"));//故障现象
                                }

                                if("null".equals(jsonDetail.getString("repairreason"))){
                                    bundle.putString("tvRepairReason", EMPTY);//故障原因
                                }else{
                                    bundle.putString("tvRepairReason", jsonDetail.getString("repairreason"));//故障原因
                                }

                                if("null".equals(jsonDetail.getString("repairsolution"))){
                                    bundle.putString("tvRepairSolution", EMPTY);//解决办法
                                }else{
                                    bundle.putString("tvRepairSolution", jsonDetail.getString("repairsolution"));//解决办法
                                }
                                if("null".equals(jsonDetail.getString("charge"))){
                                    bundle.putString("charge", "0");//维修基本费用
                                }else{
                                    bundle.putString("charge", jsonDetail.getString("charge"));//维修基本费用
                                }
                                bundle.putString("isonrepair",jsonDetail.getString("isonrepair"));//是否在保
                            }
                            //维修报价
                            if(stateCode > 3){
                                Float baseCharge = Float.parseFloat(jsonDetail.getString("charge"));
                                Float bigCharge = Float.parseFloat(jsonDetail.getString("bigcharge"));
                                Float totalPrice=baseCharge+bigCharge;
                                String strTotalPrice=baseCharge.toString()+"(基本) + "+bigCharge+"(配件) = "+totalPrice+"(总费用)";
                                bundle.putString("tvRepairCharge", strTotalPrice);//维修报价
                                //bundle.putString("tvRepairCharge", jsonDetail.getString("charge"));//维修报价
                                bundle.putString("btnRepairChargeFile", jsonDetail.getString("chargefile"));//维修报价文件
                                bundle.putString("tvRepairChargeTime", jsonDetail.getString("chargetime"));//维修报价时间
                                if("null".equals(jsonDetail.getString("remark"))){
                                    bundle.putString("tvRemark", EMPTY);//备注
                                }else{
                                    bundle.putString("tvRemark", jsonDetail.getString("remark"));//备注
                                }
                            }
                            //核价
                            if(stateCode > 4){
                                String checkChargeResult;
                                if("0".equals(jsonDetail.getString("checkcharge"))){
                                    checkChargeResult = "否";
                                }else if("1".equals(jsonDetail.getString("checkcharge"))){
                                    checkChargeResult = "是";
                                }else{
                                    checkChargeResult = "状态错误！";
                                }
                                bundle.putString("tvIsCheckCharge", checkChargeResult);//是否核准
                                bundle.putString("tvCheckTime", jsonDetail.getString("checktime"));//核价时间
                                bundle.putString("tvCheckUser", jsonDetail.getString("check_username"));//核价人
                            }

                            //完工
                            if(stateCode > 5 && stateCode != 41 && stateCode != 51){
                                bundle.putString("btnCompletePhoto", jsonDetail.getString("completephoto"));//完工照片
                                bundle.putString("tvCompleteTime", jsonDetail.getString("completetime"));//完工时间
                                if("null".equals(jsonDetail.getString("completedescrip"))){
                                    bundle.putString("tvCompleteDes", EMPTY);//完工描述
                                }else{
                                    bundle.putString("tvCompleteDes", jsonDetail.getString("completedescrip"));//完工描述
                                }
                            }

                            //完工确认
                            if(stateCode > 6 && stateCode != 41 && stateCode != 51){
                                bundle.putString("tvCompleteCheckTime_zr", jsonDetail.getString("compeletchecktime_zr"));//完工确认时间
                            }
                            //完工确认
                            if(stateCode > 6 && stateCode != 41 && stateCode != 51 && stateCode != 61){
                                bundle.putString("tvCompleteCheckTime", jsonDetail.getString("compeletchecktime"));//完工确认时间
                            }

                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    /**
     * 获取选择照片按钮
     * **/
    public void getchoosephotobtn(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
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
                    addData(path);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 赋值更新界面
     * **/
    private void setTableDetail(Bundle bun){
        //发现故障
        tvBugState.setText(bun.getString("tvBugState"));//状态
        bugtype = bun.getString("tvBugType");
        tvBugType.setText(bugtype);//项目分类
        tvBugAddress.setText(bun.getString("tvBugAddress"));//故障地址
        tvBugFindTime.setText(bun.getString("tvBugFindTime"));//发现时间
        final String bugFindPhoto = bun.getString("btnBugFindPhoto");//查看故障照片
        btnBugFindPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BugDetailActivity.this, BigPicActivity.class);
                intent.putExtra("photo", bugFindPhoto);
                startActivity(intent);
            }
        });
        tvBugFindUser.setText(bun.getString("tvBugFindUser"));//发现人
        tvBugDescrip.setText(bun.getString("tvBugDescrip"));//故障描述

        //指挥中心分派故障
        if(stateCode < 1){
            TableRow trBugSendTime = (TableRow)findViewById(R.id.trBugSendTime);//分派故障时间
            table.removeView(trBugSendTime);//分派故障时间
            TableRow trBugSendUser = (TableRow)findViewById(R.id.trBugSendUser);//分派任务人
            table.removeView(trBugSendUser);//分派任务人
            if(bugFindPhoto != null && !bugFindPhoto.equals("")){
                String[] arrayphoto = bugFindPhoto.split(",");
                for (int i=0;i<arrayphoto.length;i++){
                    addData(arrayphoto[i]);
                }
            }
            //补传照片
            btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getchoosephotobtn();
                }
            });
        }else{
            tvBugSendTime.setText(bun.getString("tvBugSendTime"));//指挥中心分派故障时间
            tvBugSendUser.setText(bun.getString("tvBugSendUser"));//指挥中心分派任务人
        }

        //派单
        if(stateCode < 2){
            TableRow trRepairSendTime = (TableRow)findViewById(R.id.trRepairSendTime);//派单时间
            table.removeView(trRepairSendTime);//派单时间
            TableRow trRepairSendUser = (TableRow)findViewById(R.id.trRepairSendUser);//派单人
            table.removeView(trRepairSendUser);//派单人
            TableRow trRepairUsers = (TableRow)findViewById(R.id.trRepairUsers);//维修人员名单
            table.removeView(trRepairUsers);//维修人员名单
        }else{
            tvRepairSendTime.setText(bun.getString("tvRepairSendTime"));//派单时间
            tvRepairSendUser.setText(bun.getString("tvRepairSendUser"));//派单人

            tvRepairUsers.setText(bun.getString("repairusername"));//维修人员名单
        }
        String type="";
        //排查
        if(stateCode < 3){
            TableRow trRepairCheckTime = (TableRow)findViewById(R.id.trRepairCheckTime);//维修到达时间
            TableRow trType = (TableRow)findViewById(R.id.trType);//故障类型
            TableRow trRepairDescription = (TableRow)findViewById(R.id.trRepairDescription);//故障现象
            TableRow trRepairCheck = (TableRow)findViewById(R.id.trRepairCheck);//维修确认图片
            TableRow trRepairReason = (TableRow)findViewById(R.id.trRepairReason);//故障原因
            TableRow trRepairSolution = (TableRow)findViewById(R.id.trRepairSolution);//解决办法
            TableRow tr_isonrepair = (TableRow)findViewById(R.id.tr_isonrepair);//是否在保
            TableRow tr_basecharge = (TableRow)findViewById(R.id.tr_basecharge);//是否有基本维修费用
            table.removeView(trRepairCheckTime);//维修到达时间
            table.removeView(trType);//故障类型
            table.removeView(trRepairDescription);//故障现象
            table.removeView(trRepairCheck);//维修确认图片
            table.removeView(trRepairReason);//故障原因
            table.removeView(trRepairSolution);//解决办法
            table.removeView(tr_isonrepair);
            table.removeView(tr_basecharge);
        }else{
            final String bugRepairPhoto = bun.getString("btnRepairCheck");//维修确认图片
            btnRepairCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//维修确认图片
                    Intent intent = new Intent(BugDetailActivity.this, BigPicActivity.class);
                    intent.putExtra("photo", bugRepairPhoto);
                    startActivity(intent);
                }
            });
            if(bugRepairPhoto != null && !bugRepairPhoto.equals("")){
                String[] arrayphoto = bugRepairPhoto.split(",");
                for (int i=0;i<arrayphoto.length;i++){
                    addData(arrayphoto[i]);
                }
            }
            tvRepairDescription.setText(bun.getString("tvRepairDescription"));//故障现象
            tvRepairReason.setText(bun.getString("tvRepairReason"));//故障原因
            tvRepairSolution.setText(bun.getString("tvRepairSolution"));//解决办法
            tvRepairCheckTime.setText(bun.getString("tvRepairCheckTime"));//维修到达时间
            type=bun.getString("type","");//故障类型
            tvType.setText(bun.getString("tvType"));

            String charge = bun.getString("charge") + " 元";
            tv_basecharge.setText(charge);
            String isonrepair = bun.getString("isonrepair");
            if(isonrepair.equals("1")){
                tv_isonrepair.setText("在保");
            }else {
                tv_isonrepair.setText("不在保");
            }
        }
        //维修报价
        if(stateCode <4 || type.equals("小故障")){
            TableRow trRepairChargeTime = (TableRow)findViewById(R.id.trRepairChargeTime);//维修报价时间
            table.removeView(trRepairChargeTime);//维修报价时间
            TableRow trRepairCharge = (TableRow)findViewById(R.id.trRepairCharge);//维修报价
            table.removeView(trRepairCharge);//维修报价
            TableRow trParts = (TableRow)findViewById(R.id.tr_parts);//配件详情
            table.removeView(trParts);//配件详情
            TableRow trRepairChargeFile = (TableRow)findViewById(R.id.trRepairChargeFile);//维修报价文件
            table.removeView(trRepairChargeFile);//维修报价文件
            TableRow trRemark = (TableRow)findViewById(R.id.trRemark);//备注
            table.removeView(trRemark);//备注
        }else{
            tvRepairCharge.setText(bun.getString("tvRepairCharge"));//维修报价
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
            tvRepairChargeTime.setText(bun.getString("tvRepairChargeTime"));//维修报价时间
            tvRemark.setText(bun.getString("tvRemark"));//备注
        }
        //核价
//        if(stateCode <5 || type=="小故障"){
//            TableRow trCheckTime = (TableRow)findViewById(R.id.trCheckTime);//核价时间
//            table.removeView(trCheckTime);
//            TableRow trCheckUser = (TableRow)findViewById(R.id.trCheckUser);//核价人
//            table.removeView(trCheckUser);
//            TableRow trIsCheckCharge = (TableRow)findViewById(R.id.trIsCheckCharge);//是否核准
//            table.removeView(trIsCheckCharge);
//        }else{
//            TextView tvIsCheckCharge = (TextView)findViewById(R.id.tvIsCheckCharge);//是否核准
//            tvIsCheckCharge.setText(bun.getString("tvIsCheckCharge"));
//            TextView tvCheckTime = (TextView)findViewById(R.id.tvCheckTime);//核价时间
//            tvCheckTime.setText(bun.getString("tvCheckTime"));
//            TextView tvCheckUser = (TextView)findViewById(R.id.tvCheckUser);//核价人
//            tvCheckUser.setText(bun.getString("tvCheckUser"));
//        }
        //维修
        if(stateCode <6 || stateCode == 41 || stateCode == 51){
            TableRow trCompleteTime = (TableRow)findViewById(R.id.trCompleteTime);//完工时间
            table.removeView(trCompleteTime);//完工时间
            TableRow trCompleteDes = (TableRow)findViewById(R.id.trCompleteDes);//完工描述
            table.removeView(trCompleteDes);//完工描述
            TableRow trCompletePhoto = (TableRow)findViewById(R.id.trCompletePhoto);//完工照片
            table.removeView(trCompletePhoto);//完工照片
        }else{
            final String bugCompletePhoto = bun.getString("btnCompletePhoto");//完工照片
            btnCompletePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BugDetailActivity.this, BigPicActivity.class);
                    intent.putExtra("photo", bugCompletePhoto);
                    startActivity(intent);
                }
            });
            tvCompleteTime.setText(bun.getString("tvCompleteTime"));//完工时间
            tvCompleteDes.setText(bun.getString("tvCompleteDes"));//完工描述
        }
        //完工确认
        if(stateCode < 7 || stateCode == 41 || stateCode == 51){
            TableRow trCompleteCheckTime_zr = (TableRow)findViewById(R.id.trCompleteCheckTime_zr);//完工确认时间
            table.removeView(trCompleteCheckTime_zr);//完工确认时间
        }else{
            tvCompleteCheckTimez_zr.setText(bun.getString("tvCompleteCheckTimez_zr"));//完工确认时间
        }
        //完工确认
        if(stateCode < 7 || stateCode == 41 || stateCode == 61 || stateCode == 51){
            TableRow trCompleteCheckTime = (TableRow)findViewById(R.id.trCompleteCheckTime);//完工确认时间
            table.removeView(trCompleteCheckTime);//完工确认时间
        }else{
            tvCompleteCheckTime.setText(bun.getString("tvCompleteCheckTime"));//完工确认时间
        }

        if(stateCode == 0){
            FenpaiEvent();//指挥中心待分派时-显示分派和删除按钮
        }else if(stateCode == 1){
            FenpaiEvent_Zhuanren();//甲方专人待分派时-显示分派按钮
        }else if(stateCode == 41 || stateCode == 4){
            HejiaEvent();//指挥中心核价-显示核价按钮
        } else if(stateCode == 61){
            WangongEvent();//指挥中心完工确认时-显示完工按钮
        }
    }
    private void getOfferInfo(){
        new Thread(){
            @Override
            public void run(){
                data = "bugid=" + bugId;
                String res = PostParamTools.postGetInfo(UserModel.myhost+"getPartsDetail.php", data);//显示列表
                if(res==null){
                    handler.sendEmptyMessage(2);
                }else if (!res.equals("null") && !res.equals("")) {
                    // 如果获取的result数据不为空，那么对其进行JSON解析。
                    try{
                        JSONArray jsonArray=new JSONArray(res);
                        ArrayList<HashMap<String,Object>> partArrayList = new ArrayList<HashMap<String, Object>>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, Object> part = new HashMap<String, Object>();
                            part.put("partName", jsonObject.getString("part_name"));
                            part.put("partPrice", jsonObject.getString("parts_price"));
                            part.put("num", jsonObject.getString("parts_num"));
                            String reference = "";
                            if(!"null".equals(jsonObject.getString("reference"))){
                                reference = jsonObject.getString("reference");
                            }
                            part.put("partDescription", reference);
                            partArrayList.add(part);
                        }
                        partDetailAdapter = new PartDetailAdapter(BugDetailActivity.this,partArrayList);
                        BugDetailActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lv_parts.setAdapter(partDetailAdapter); // 重新设置ListView的数据适配器
                                setListViewHeightBasedOnChildren(lv_parts);
                                partDetailAdapter.notifyDataSetChanged();
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 50;
        listView.setLayoutParams(params);
    }

    /**
     * 指挥中心待分派时-显示修改、分派和删除按钮
     * **/
    private void FenpaiEvent(){
        tr_fenpai.setVisibility(View.VISIBLE);
        btnUploadPhoto.setVisibility(View.VISIBLE);
        tr_photolist.setVisibility(View.VISIBLE);
        tvBugDescrip.setEnabled(true);
        tvBugAddress.setEnabled(true);
        setautocomplete();
        //分派
        btn_fenpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] receivers = null;
                try {
                    if (!("null".equals(jsonReceiverStr))) {
                        JSONArray jsonReceivers = new JSONArray(jsonReceiverStr);
                        receivers = new String[jsonReceivers.length()];
                        for (int i = 0; i < jsonReceivers.length(); i++) {
                            JSONObject jsonObject = jsonReceivers.getJSONObject(i);
                            receivers[i] = jsonObject.getString("personname");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BugDetailActivity.this, "未找到甲方专人列表.", Toast.LENGTH_SHORT).show();
                }

                new AlertDialog.Builder(BugDetailActivity.this).setTitle("选择一个任务接收人:").setIcon(android.R.drawable.ic_dialog_email).setSingleChoiceItems(
                        receivers, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    JSONArray jsonReceivers = new JSONArray(jsonReceiverStr);
                                    JSONObject jsonObject = jsonReceivers.getJSONObject(which);
                                    String receiverUserId = jsonObject.getString("userid");
                                    writeResult(receiverUserId);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).show();


            }
        });
        //删除
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog();
            }
        });
        //修改任务
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdate();
            }
        });

    }

    /**
     * 显示是否删除提醒框
     * **/
    private void showNoticeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BugDetailActivity.this);
        builder.setTitle("删除操作");
        builder.setIcon(android.R.drawable.ic_dialog_info);//窗口头图标
        String updateMsg = "确定要删除此任务吗？";
        builder.setMessage(updateMsg);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doDelete();//删除任务
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        noticeDialog.show();
    }
    /**
     * 删除任务
     * **/
    private void doDelete(){
        spec=WEBURL+"delBug.php";
        data = "&bugid="+bugId;
        new Thread(){
            @Override
            public void run() {
                result_del = PostParamTools.postGetInfo(spec, data);
                BugDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result_del == null){
                            Toast.makeText(BugDetailActivity.this, "网络链接超时!",
                                    Toast.LENGTH_LONG).show();
                        }
                        else if(("1").equals(result_del)) {
                            Toast.makeText(BugDetailActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BugDetailActivity.this,DispatchActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(BugDetailActivity.this, "操作失败,请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 修改任务
     * **/
    private void doUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String bugfindphoto = "";
                    filemap = new <String, File>HashMap();//从图片列表中读取图片文件
                    if (list.size() > 12) {
                        Toast.makeText(BugDetailActivity.this, "最多只能上传12张图片", Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < list.size() && i < 12; i++) {
                        String pic_path = list.get(i).get("filepath").toString();
                        String strFileName = PostParamTools.getFileName(pic_path);
                        if (pic_path.split("/").length > 1) {
                            String targetPath = pic_path.substring(0, pic_path.indexOf(strFileName)) + "ys_" + strFileName;
                            //调用压缩图片的方法，返回压缩后的图片path
                            final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 60);
                            final File compressedPic = new File(compressImage);
                            if (compressedPic.exists()) {
                                filemap.put("file" + Integer.toString(i), compressedPic);
                            } else {//直接上传
                                file_go = new File(pic_path);
                                filemap.put("file" + Integer.toString(i), file_go);
                            }
                        } else {
                            bugfindphoto = bugfindphoto + strFileName;
                            if (i != list.size() - 1) bugfindphoto += ",";
                        }
                    }
                    String bugaddr = tvBugAddress.getText().toString();
                    String bugfinddescrip = tvBugDescrip.getText().toString();
                    Map<String, String> parammap = new HashMap<String, String>();
                    parammap.put("bugid", bugId);
                    parammap.put("roleid",  roleid);
                    parammap.put("bugaddr", URLEncoder.encode(bugaddr, "UTF-8"));
                    parammap.put("bugfinddescrip", URLEncoder.encode(bugfinddescrip, "UTF-8"));
                    parammap.put("bugfindphoto", URLEncoder.encode(bugfindphoto, "UTF-8"));
                    spec = UserModel.myhost + "updateUnfenpai.php";
                    String res = PostParamTools.post(spec, parammap, filemap);
                    if (("1").equals(res)) {
                        handler.sendEmptyMessage(7);//修改成功
                    } else {
                        handler.sendEmptyMessage(5);//操作失败
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 甲方专人待分派时-显示分派按钮
     * **/
    private void FenpaiEvent_Zhuanren(){
        tr_jfenpai.setVisibility(View.VISIBLE);
        //分派
        btn_jfenpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiChoiceDialog(bugId, workers);
            }
        });
    }

    /**
     * 弹出选择维修人员多选框
     * **/
    private void showMultiChoiceDialog(final String bugid, List<HashMap<String, String>> workers) {
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(BugDetailActivity.this);
            dialog.setIcon(android.R.drawable.ic_dialog_info);//窗口头图标
            dialog.setTitle("选择维修人员");//窗口名
            final List<HashMap<String, String>> availableWorkers = workers;
            final String[] availableWorkersName = new String[availableWorkers.size()];
            for(int i=0; i< availableWorkers.size(); i++){
                availableWorkersName[i] = availableWorkers.get(i).get("bugReceiveUserName");
            }
            arr = new Boolean[availableWorkers.size()];

            //    设置一个单项选择下拉框
            /**
             * 第一个参数指定我们要显示的一组下拉多选框的数据集合
             * 第二个参数代表哪几个选项被选择，如果是null，则表示一个都不选择，如果希望指定哪一个多选选项框被选择，
             * 需要传递一个boolean[]数组进去，其长度要和第一个参数的长度相同，例如 new boolean{true, false, false, true};
             * 第三个参数给每一个多选项绑定一个监听器
             */
            dialog.setMultiChoiceItems(availableWorkersName, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        arr[which] = true;
                    } else {
                        arr[which] = false;
                    }
                }
            });

            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StringBuffer str = new StringBuffer(100);
                    for (int i = 0; i < availableWorkers.size(); i++) {
                        if ((null != arr[i]) && arr[i]) {
                            str.append(availableWorkers.get(i).get("bugId") + ",");
                        }
                    }
                    if(str.length()>0){
                        String userids="";
                        userids=str.substring(0,str.length()-1);
                        insertSelectedWorkers(bugid, userids);
                    }
                }
            });
            dialog.setNegativeButton("取消", null);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
    *获得维修人员List
    *HashMap<维修人员id，维修人员name>
     **/
    public void getAvailableWorkers(){
        //final String[] workers = {"维修人员1", "维修人员2", "维修人员3"};
        workers = new ArrayList<HashMap<String, String>>();
        new Thread(){
            @Override
            public void run() {
                // 请求的地址
                String str =  UserModel.myhost + "worker.php";
                String result = PostParamTools.postGetInfo(str, "");
                if(result == null){
                    Toast.makeText(BugDetailActivity.this, "网络链接超时!",
                            Toast.LENGTH_LONG).show();
                }else {
                    JSONArray jsonArray = null;
                    try{
                        jsonArray = new JSONArray(result);// 得到指定json key对象的value对象
                        for(int i=0;i < jsonArray.length(); i++) {
                            HashMap<String, String> worker = new HashMap<String, String>();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            worker.put("bugId",jsonObject.getString("id"));
                            worker.put("bugReceiveUserName",jsonObject.getString("name"));
                            workers.add(worker);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    /**
     * 将选择的维修人员插入数据库
     * **/
    private void insertSelectedWorkers(final String id, final String str){

        new Thread(){
            @Override
            public void run() {
                String insertStr = UserModel.myhost + "insertSelectedWorkers.php";
                String data = "bugid=" + id + "&repairuserid=" + str;

                String result = PostParamTools.postGetInfo(insertStr, data);

                if(result == null){
                    handler.sendEmptyMessage(4);
                }else if("success".equals(result)){
                    handler.sendEmptyMessage(2);// 发送处理消息
                }else{
                    handler.sendEmptyMessage(3);// 发送处理消息
                }
            }
        }.start();
    }

    /**
     * 查询接收人
     **/
    public void  queryreceiver(){
        new Thread(){
            @Override
            public void run() {
                spec=UserModel.myhost+"queryreceiver.php";
                data = "" ;
                jsonReceiverStr = PostParamTools.postGetInfo(spec, data);
                if(jsonReceiverStr == null){
                    handler.sendEmptyMessage(4);
                }
            }
        }.start();
    }

    /**
     * 写入分派结果
     * **/
    public void  writeResult(String receiveruserid){
        ruserid=receiveruserid;
        new Thread(){
            @Override
            public void run() {
                data = "bugid="+ bugId;
                spec=UserModel.myhost+"writeresult.php";
                data = data+"&receiveruserid="+ruserid+"&bugsenduserid="+UserModel.getuserid();
                writers = PostParamTools.postGetInfo(spec, data);
                BugDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(writers == null){
                            Toast.makeText(BugDetailActivity.this, "网络链接超时！", Toast.LENGTH_SHORT).show();
                        }else if(("").equals(writers)) {
                            Toast.makeText(BugDetailActivity.this, "操作失败,请重试", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(BugDetailActivity.this, writers, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(BugDetailActivity.this,DispatchActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        }.start();
    }

    /**
     * 核价-显示核价按钮
     * **/
    private void HejiaEvent(){
        tr_hejia.setVisibility(View.VISIBLE);
        btn_hejia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BugDetailActivity.this, CheckDetailActivity.class);
                intent.putExtra("bugId", bugId);
                startActivity(intent);
            }
        });
    }

    /**
     * 指挥中心完工确认时-显示完工按钮
     * **/
    private void WangongEvent(){
        tr_wangong.setVisibility(View.VISIBLE);
        //完工确认
        btn_wangong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = "bugid="+ bugId;
                        spec=UserModel.myhost+"writecomplete.php";
                        data=data+"&roleid="+roleid;
                        writers = PostParamTools.postGetInfo(spec, data);
                        if(writers==null){
                            handler.sendEmptyMessage(4);
                        }else if(("").equals(writers)) {
                            handler.sendEmptyMessage(5);
                        }else {
                            handler.sendEmptyMessage(6);
                        }
                    }
                }).start();
            }
        });
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
            handler.sendEmptyMessage(1);// 发送处理消息
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 故障地址提示
     * **/
    public void setautocomplete(){//完成地址输入提示

        new Thread(){
            @Override
            public void run() {
                String content=PostParamTools.postGetInfo(UserModel.myhost+"readversion_addr.php","");
                if(content == null){
                    Toast.makeText(BugDetailActivity.this, "链接超时!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BugDetailActivity.this, "链接超时!", Toast.LENGTH_SHORT).show();
                }else {
                    BindData(jsonAddr);
                    UpdateAddr();
                }

            }
        }.start();
    }

    //更新界面
    private void UpdateAddr(){
        BugDetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(addrModels!=null && addrModels.size()>0){
                    final String[] addrs=new String[addrModels.size()];
                    int i=0;
                    for (AddrModel pm : addrModels) {
                        addrs[i]=pm.getEquipmentaddr();
                        i++;
                    }
                    FilterAdapter arrayAdapter=new FilterAdapter<String>(BugDetailActivity.this, android.R.layout.simple_list_item_1, addrs);
                    tvBugAddress.setAdapter(arrayAdapter);
                }else {
                    tvBugAddress.setAdapter(null);
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
                    Toast.makeText(BugDetailActivity.this, "还有"+list.size()+"张照片。",Toast.LENGTH_SHORT).show() ;
                }
            });
            /**点击查看照片**/
            holder.itemimgfilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BugDetailActivity.this,ShowImageActivity.class);
                    intent.putExtra("imgpath",(String)list.get(position).get("filepath"));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

}
