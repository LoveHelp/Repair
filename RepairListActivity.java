package com.xianyi.chen.repair;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RepairListActivity extends AppCompatActivity {

    private ListView listView;
    private String spec = UserModel.myhost+"getunrepairlist.php";
    private Integer state = 5;
    private String data = "state="+state+"&repairuserid="+UserModel.getuserid();

    private Intent intent;
    private ArrayList<HashMap<String,Object>> datalist=null;
    private MyAdapter myAdapter;

    private String result="";

    private String repairuserid=UserModel.getuserid();
    public String roleid=UserModel.getroleid();

    private Integer upd=0;//1报价提交后的修改;0报价

    private TextView tv_title;//头部标题

    private LinearLayout ll_nodata;//暂无数据
    private TextView tv_nodata;
    private ImageView iv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);

        if(repairuserid.equals("")){
            Intent intent = new Intent(RepairListActivity.this,LoginActivity.class);
            intent.putExtra("exit",1);
            startActivity(intent);
        }

        upd = getIntent().getIntExtra("upd",0);
        data += "&upd=" + upd;

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        RepairList();

    }

    @Override
    protected void onDestroy(){
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /** 初始化控件 **/
    private void InitControl(){
        listView=(ListView)findViewById(R.id.listView);
        ll_nodata = (LinearLayout)findViewById(R.id.ll_nodata);
        tv_nodata=(TextView)findViewById(R.id.tv_nodata);
        iv_nodata=(ImageView)findViewById(R.id.iv_nodata);
        tv_title=(TextView)findViewById(R.id.tv_title);
        if(upd==1){
            tv_title.setText("已维修故障列表");
        }else {
            tv_title.setText("待维修故障列表");
        }
    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
        //item上的onclick事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String,Object> hashMap=datalist.get(i);
                String bugid = hashMap.get("bugid").toString();
                intent = new Intent(RepairListActivity.this,RepairDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("upd",upd);
                bundle.putString("bugid",bugid);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }

    /**
     * 新任务提醒
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    //无数据
                    listView.setAdapter(null);
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nodata);
                    tv_nodata.setText("暂无相关数据...");
                    break;
                case 2:
                    //请求数据失败
                    listView.setAdapter(null);
                    Toast.makeText(RepairListActivity.this, "请求数据失败...",
                            Toast.LENGTH_LONG).show();
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nonet);
                    tv_nodata.setText("请求数据失败...");
                    break;
                case 3:
                    // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                    listView.setVisibility(View.VISIBLE);
                    ll_nodata.setVisibility(View.GONE);
                    datalist = JSONAnalysis(result);
                    myAdapter = new MyAdapter(RepairListActivity.this, datalist);
                    listView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 5:
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nonet);
                    tv_nodata.setText("网络链接超时...");
                    //Toast.makeText(RepairListActivity.this, "网络链接超时...", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    private void RepairList(){

        new Thread(){
            @Override
            public void run() {
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result==null){
                    mHandler.sendEmptyMessage(5);
                }else if(result.equals("null")) {
                    mHandler.sendEmptyMessage(2);
                }else if (result.equals("0")) {
                    mHandler.sendEmptyMessage(0);
                }  else{
                    mHandler.sendEmptyMessage(3);
                }

            }
        }.start();

    }

    /**
     * 通过POST方式提交请求，并读取json数组数据
     **/
    private ArrayList<HashMap<String,Object>> JSONAnalysis(String result){
        try
        {
            JSONArray jsonArray=new JSONArray(result);
            datalist = new ArrayList<HashMap<String, Object>>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> item = new HashMap<String, Object>();
                item.put("bugid", jsonObject.getString("bugid"));
                item.put("bugtype", jsonObject.getString("bugtype"));
                item.put("bugfinddescrip", jsonObject.getString("bugfinddescrip"));
                item.put("bugfindtime", jsonObject.getString("bugfindtime"));
                item.put("bugfindphoto", jsonObject.getString("bugfindphoto"));
                item.put("bugaddr", jsonObject.getString("bugaddr"));

                item.put("state", jsonObject.getString("state"));
                item.put("type", jsonObject.getString("type"));
                item.put("repairsendtime", jsonObject.getString("repairsendtime"));//派单时间
                item.put("repairchecktime", jsonObject.getString("repairchecktime"));//排查时间
                item.put("checktime", jsonObject.getString("checktime"));//审核通过时间
                item.put("completetime", jsonObject.getString("completetime"));//完工时间
                datalist.add(item);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return datalist;
    }

}
