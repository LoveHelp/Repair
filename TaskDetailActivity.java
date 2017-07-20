package com.xianyi.chen.repair;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Map;

public class TaskDetailActivity extends AppCompatActivity {

    private TableRow trRepairDescrip,trRepairReason,trRepairSolution,tr_completedescrip,trPaichaPic,trWanGongPic,tr_parts;
    private TableRow tr_bugsendtime,tr_repairsendtime,tr_repairuserid,tr_type,tr_repairchecktime,tr_charge,tr_chargetime;
    private TableRow tr_checktime_zr,tr_checktime,tr_completetime,tr_compeletchecktime_zr,tr_compeletchecktime;
    private TableRow tr_isonrepair,tr_basecharge;

    private TextView tvBugType,tvBugAddress,tvBugDescrip,tvRepairDescrip,tvRepairReason,tvRepairSolution,tv_completedescrip;
    private TextView tv_bugfindtime,tv_bugsendtime,tv_repairsendtime,tv_repairuserid,tv_type,tv_repairchecktime,tv_charge,tv_chargetime;
    private TextView tv_checktime_zr,tv_checktime,tv_completetime,tv_compeletchecktime_zr,tv_compeletchecktime;
    private TextView tv_basecharge,tv_isonrepair;
    private ListView lv_parts;
    private Button btnChakan,btnPaichaPic,btnWanGongPic,btnRepairChargeFile;
    private String spec = UserModel.myhost+"getbugbyid.php";
    private String data = "";
    private String bugfindphoto,repaircheckphoto,completephoto;
    private String result="";
    public String roleid=UserModel.getroleid();
    private String bugid = "";
    private Activity activity;
    private Integer state = 0;
    private Bundle bundle=new Bundle();
    //维修人员名单
    //private String[] repairUsersArray = null;
    private PartDetailAdapter partDetailAdapter;

    private TextView tv_title;//头部标题

    private TableLayout table;
    private String chargeFileUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        activity = this;
        bugid = getIntent().getStringExtra("bugid");
        data="bugid=" + bugid;

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件
        getDetailInfo();//开始获取数据
        getOfferInfo();//获取相关配件信息
    }

    /** 初始化控件 **/
    private void InitControl(){
        table = (TableLayout) findViewById(R.id.bugTable);
        tr_bugsendtime = (TableRow)findViewById(R.id.tr_bugsendtime);
        trRepairDescrip=(TableRow)findViewById(R.id.trRepairDescrip);
        trRepairReason=(TableRow)findViewById(R.id.trRepairReason);
        trRepairSolution=(TableRow)findViewById(R.id.trRepairSolution);
        tr_completedescrip=(TableRow)findViewById(R.id.tr_completedescrip);
        tr_repairsendtime=(TableRow)findViewById(R.id.tr_repairsendtime);
        tr_repairuserid=(TableRow)findViewById(R.id.tr_repairuserid);
        tr_type=(TableRow)findViewById(R.id.tr_type);
        tr_repairchecktime=(TableRow)findViewById(R.id.tr_repairchecktime);

        tr_isonrepair = (TableRow)findViewById(R.id.tr_isonrepair);//是否在保
        tr_basecharge = (TableRow)findViewById(R.id.tr_basecharge);//是否有基本维修费用
        tv_basecharge=(TextView) findViewById(R.id.tv_basecharge);
        tv_isonrepair=(TextView) findViewById(R.id.tv_isonrepair);

        tr_charge=(TableRow)findViewById(R.id.tr_charge);
        tr_chargetime=(TableRow)findViewById(R.id.tr_chargetime);
        tr_checktime_zr=(TableRow)findViewById(R.id.tr_checktime_zr);
        tr_checktime=(TableRow)findViewById(R.id.tr_checktime);
        tr_completetime=(TableRow)findViewById(R.id.tr_completetime);
        tr_compeletchecktime_zr=(TableRow)findViewById(R.id.tr_compeletchecktime_zr);
        tr_compeletchecktime=(TableRow)findViewById(R.id.tr_compeletchecktime);
        trPaichaPic = (TableRow) findViewById(R.id.trPaichaPic);
        trWanGongPic = (TableRow) findViewById(R.id.trWanGongPic);
        tr_parts = (TableRow) findViewById(R.id.tr_parts);

        tvBugType=(TextView)findViewById(R.id.tvBugType);
        tvBugAddress=(TextView)findViewById(R.id.tvBugAddress);
        tvBugDescrip=(TextView)findViewById(R.id.tvBugDescrip);
        tvRepairDescrip=(TextView)findViewById(R.id.tvRepairDescrip);
        tvRepairReason=(TextView)findViewById(R.id.tvRepairReason);
        tvRepairSolution=(TextView)findViewById(R.id.tvRepairSolution);
        tv_completedescrip=(TextView)findViewById(R.id.tv_completedescrip);
        tv_bugfindtime=(TextView)findViewById(R.id.tv_bugfindtime);
        tv_bugsendtime=(TextView)findViewById(R.id.tv_bugsendtime);
        tv_repairsendtime=(TextView)findViewById(R.id.tv_repairsendtime);
        tv_repairuserid=(TextView)findViewById(R.id.tv_repairuserid);
        tv_type=(TextView)findViewById(R.id.tv_type);
        tv_repairchecktime=(TextView)findViewById(R.id.tv_repairchecktime);
        tv_charge=(TextView)findViewById(R.id.tv_charge);
        tv_chargetime=(TextView)findViewById(R.id.tv_chargetime);
        tv_checktime_zr=(TextView)findViewById(R.id.tv_checktime_zr);
        tv_checktime=(TextView)findViewById(R.id.tv_checktime);
        tv_completetime=(TextView)findViewById(R.id.tv_completetime);
        tv_compeletchecktime_zr=(TextView)findViewById(R.id.tv_compeletchecktime_zr);
        tv_compeletchecktime=(TextView)findViewById(R.id.tv_compeletchecktime);

        lv_parts=(ListView) findViewById(R.id.lv_parts);

        btnPaichaPic = (Button) findViewById(R.id.btnPaichaPic);
        btnChakan = (Button) findViewById(R.id.btnChakan);
        btnWanGongPic = (Button) findViewById(R.id.btnWanGongPic);
        btnRepairChargeFile = (Button)findViewById(R.id.btnRepairChargeFile);//报价单下载

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("故障详情");

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
    }

    @Override
    protected void onDestroy(){
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /** 接收消息，更新UI **/
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Bundle bun = msg.getData();
            switch (msg.what){
                case 1:
                    setDetailInfo(bun);//根据bugid获取故障数据
                    break;
                case 2:
                    Toast.makeText(TaskDetailActivity.this, "网络链接超时...",
                            Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(TaskDetailActivity.this, "报价单已保存至：文件管理 下的 baojiadan 文件夹下", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /** 获取网络数据 **/
    private void getDetailInfo(){
        new Thread(){
            @Override
            public void run(){
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result==null){
                    handler.sendEmptyMessage(2);
                }else if (!result.equals("null") && !result.equals("")) {
                    // 如果获取的result数据不为空，那么对其进行JSON解析。
                    try{
                        String[] temp = result.split("\\+");
                        JSONArray jsonArray=new JSONArray(temp[0]);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Message msg = new Message();
                        msg.what = 1;
                        state=Integer.parseInt(jsonObject.getString("state"));
                        bundle.putString("state",jsonObject.getString("state"));
                        bundle.putString("bugtype",jsonObject.getString("bugtype"));//项目分类
                        bundle.putString("bugaddr",jsonObject.getString("bugaddr"));//故障地址
                        bundle.putString("bugfindtime",jsonObject.getString("bugfindtime"));//发现时间
                        bundle.putString("bugfindphoto",jsonObject.getString("bugfindphoto"));//故障照片
                        bundle.putString("bugfinddescrip",jsonObject.getString("bugfinddescrip"));//故障描述
                        //指挥中心分派故障
                        if(state>0){
                            bundle.putString("bugsendtime",jsonObject.getString("bugsendtime"));//指挥中心分派故障时间
                        }
                        //甲方专人派单
                        if(state>1){
                            bundle.putString("repairsendtime",jsonObject.getString("repairsendtime"));//派单时间
                            //维修人员名单
//                            repairUsersArray = jsonObject.getString("repairuserid").split(",");
//                            for(int i=0; i<repairUsersArray.length; i++){
//                                getUserName(String.valueOf(i), repairUsersArray[i]);
//                            }
                            if(temp.length==2 && !"null".equals(temp[1]) && !"".equals(temp[1])){
                                String usernames=temp[1].toString();
                                bundle.putString("repairusername", usernames);//维修人员
                            }
                        }
                        //维修到达
                        if(state>2){
                            bundle.putString("repairchecktime",jsonObject.getString("repairchecktime"));//维修到达时间
                            String type = "";
                            if(!"null".equals(jsonObject.getString("type"))){
                                Integer typeCode = Integer.parseInt(jsonObject.getString("type"));
                                if(typeCode == 0){
                                    type = "小故障";
                                }else if(typeCode == 1){
                                    type = "大故障";
                                }else{
                                    type = "故障类型错误！";
                                }
                            }
                            bundle.putString("type", type);//故障类型
                            bundle.putString("repaircheckphoto",jsonObject.getString("repaircheckphoto"));//维修确认图片
                            bundle.putString("repairdescrip",jsonObject.getString("repairdescrip"));//故障现象
                            bundle.putString("repairreason",jsonObject.getString("repairreason"));//故障原因
                            bundle.putString("repairsolution",jsonObject.getString("repairsolution"));//解决办法
                            if("null".equals(jsonObject.getString("charge"))){
                                bundle.putString("charge", "0");//维修基本费用
                            }else{
                                bundle.putString("charge", jsonObject.getString("charge"));//维修基本费用
                            }
                            bundle.putString("isonrepair",jsonObject.getString("isonrepair"));//是否在保
                        }
                        //维修报价
                        if(state>3){
                            //bundle.putString("charge",jsonObject.getString("charge"));//维修报价-基本费用
                            bundle.putString("bigcharge",jsonObject.getString("bigcharge"));//维修报价-配件费用
                            bundle.putString("chargetime",jsonObject.getString("chargetime"));//维修报价时间
                            bundle.putString("btnRepairChargeFile", jsonObject.getString("chargefile"));//维修报价文件
                        }
                        //核价
                        if(state>4){
                            bundle.putString("checktime_zr",jsonObject.getString("checktime_zr"));//专人核价时间
                        }
                        //核价
                        if(state>4 && state!=41){
                            bundle.putString("checktime",jsonObject.getString("checktime"));//指挥核价时间
                        }
                        //维修完工
                        if(state > 5 && state != 41 && state != 51){
                            bundle.putString("completephoto",jsonObject.getString("completephoto"));//完工照片
                            bundle.putString("completedescrip",jsonObject.getString("completedescrip"));//完工描述
                            bundle.putString("completetime",jsonObject.getString("completetime"));//完工时间
                        }
                        //完工确认
                        if(state > 6 && state != 41 && state != 51){
                            bundle.putString("compeletchecktime_zr",jsonObject.getString("compeletchecktime_zr"));//专人完工确认时间
                        }
                        //完工确认
                        if(state > 6 && state != 41 && state != 51 && state !=61){
                            bundle.putString("compeletchecktime",jsonObject.getString("compeletchecktime"));//指挥完工确认时间
                        }
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else{
                    Toast.makeText(TaskDetailActivity.this, "请求数据失败...",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.start();

    }
    private void getOfferInfo(){
        new Thread(){
            @Override
            public void run(){
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
                        partDetailAdapter = new PartDetailAdapter(TaskDetailActivity.this,partArrayList);
                        TaskDetailActivity.this.runOnUiThread(new Runnable() {
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 100;
        listView.setLayoutParams(params);
    }


    /** 绑定页面数据 **/
    private void setDetailInfo(Bundle bun){
        //更新界面
        tvBugType.setText(bun.getString("bugtype",""));//故障类型
        tvBugAddress.setText(bun.getString("bugaddr",""));//故障地址
        tv_bugfindtime.setText(bun.getString("bugfindtime",""));//发现时间
        tvBugDescrip.setText(bun.getString("bugfinddescrip",""));//故障描述
        bugfindphoto=bun.getString("bugfindphoto","");//故障照片
        //查看故障照片
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",bugfindphoto);
                startActivity(intent);
            }
        });

        //指挥中心分派故障
        if(state < 1){
            tr_bugsendtime.setVisibility(View.GONE);
        }else{
            tv_bugsendtime.setText(bun.getString("bugsendtime"));//指挥中心分派故障时间
        }

        //派单
        if(state < 2){
            tr_repairuserid.setVisibility(View.GONE);
            tr_repairsendtime.setVisibility(View.GONE);
        }else{
            tv_repairsendtime.setText(bun.getString("repairsendtime",""));//派单时间
            tv_repairuserid.setText(bun.getString("repairusername"));
        }
        String type="";
        //维修
        if(state < 3){
            tr_repairchecktime.setVisibility(View.GONE);
            tr_type.setVisibility(View.GONE);
            trPaichaPic.setVisibility(View.GONE);
            trRepairDescrip.setVisibility(View.GONE);
            trRepairReason.setVisibility(View.GONE);
            trRepairSolution.setVisibility(View.GONE);
            tr_basecharge.setVisibility(View.GONE);
            tr_isonrepair.setVisibility(View.GONE);
        }else{
            tv_repairchecktime.setText(bun.getString("repairchecktime",""));//维修到达时间
            type=bun.getString("type","");
            tv_type.setText(type);//故障类型
            tvRepairDescrip.setText(bun.getString("repairdescrip",""));//故障现象
            tvRepairReason.setText(bun.getString("repairreason",""));//故障原因
            tvRepairSolution.setText(bun.getString("repairsolution",""));//解决办法
            repaircheckphoto=bun.getString("repaircheckphoto","");//维修确认图片
            //确认故障照片
            btnPaichaPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(TaskDetailActivity.this,BigPicActivity.class);
                        String photo = repaircheckphoto;
                        intent.putExtra("photo",photo);
                        startActivity(intent);
                    }catch (Exception e){
                        e.getMessage();
                    }

                }
            });

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
        if(state <4){
            tr_charge.setVisibility(View.GONE);
            tr_chargetime.setVisibility(View.GONE);
            tr_parts.setVisibility(View.GONE);
        }else{
            Float baseCharge = Float.parseFloat(bun.getString("charge","0"));
            Float bigCharge = Float.parseFloat(bun.getString("bigcharge","0"));
            Float totalPrice=baseCharge+bigCharge;
            String strTotalPrice=baseCharge.toString()+"(基本) + "+bigCharge+"(配件) = "+totalPrice+"(总费用)";
            tv_charge.setText(strTotalPrice);//维修报价
            if(type=="小故障"){
                tr_chargetime.setVisibility(View.GONE);
                tr_parts.setVisibility(View.GONE);
            }else {
                tv_chargetime.setText(bun.getString("chargetime",""));//维修报价时间
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
        }
        //核价
        if(state<5 || type=="小故障"){
            tr_checktime_zr.setVisibility(View.GONE);
        }else {
            tv_checktime_zr.setText(bun.getString("checktime_zr",""));//专人核价时间
        }
        if(state < 5 || state == 41 || type=="小故障"){
            tr_checktime.setVisibility(View.GONE);
        }else {
            tv_checktime.setText(bun.getString("checktime",""));//指挥核价时间
        }
        //完工
            if(state < 6 || state == 41 || state == 51){
                trWanGongPic.setVisibility(View.GONE);
                tr_completedescrip.setVisibility(View.GONE);
                tr_completetime.setVisibility(View.GONE);
            }else{
                tv_completedescrip.setText(bun.getString("completedescrip",""));//完工描述
                tv_completetime.setText(bun.getString("completetime",""));//完工时间
                completephoto = bun.getString("completephoto","");//完工照片
                //完工照片
                btnWanGongPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TaskDetailActivity.this,BigPicActivity.class);
                        intent.putExtra("photo",completephoto);
                        startActivity(intent);
                    }
                });
            }
            //完工确认
            if(state < 7 || state == 41 || state == 51){
                tr_compeletchecktime_zr.setVisibility(View.GONE);
            }else{
                tv_compeletchecktime_zr.setText(bun.getString("compeletchecktime_zr",""));//专人完工确认时间
            }
            //完工确认
            if(state < 7 || state == 41 || state == 61 || state == 51){
                tr_compeletchecktime.setVisibility(View.GONE);
            }else{
                tv_compeletchecktime.setText(bun.getString("compeletchecktime",""));//指挥完工确认时间
            }
//            if(state==7){
//                tr_charge.setVisibility(View.GONE);
//            }



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
