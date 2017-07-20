package com.xianyi.chen.repair;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckActivity extends AppCompatActivity {

    List<HashMap<String, Object>> checkList = null;

    private CheckAdapter checkAdapter = null;

    private ListView listView;

    private String userid = UserModel.getuserid();
    private String roleid = UserModel.getroleid();
    private String bugListsStr="";

    private TextView tv_title;//头部标题

    private LinearLayout ll_nodata;//暂无数据
    private TextView tv_nodata;
    private ImageView iv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        setCheckList();

    }


    /** 初始化控件 **/
    private void InitControl(){
        listView = (ListView) findViewById(R.id.pirceList);
        ll_nodata = (LinearLayout)findViewById(R.id.ll_nodata);
        tv_nodata=(TextView)findViewById(R.id.tv_nodata);
        iv_nodata=(ImageView)findViewById(R.id.iv_nodata);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("任务核价列表");
    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
        //查看详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, Object> hashMap = checkList.get(i);
                String bugid = hashMap.get("bugId").toString();

                Intent intent = new Intent();//context, BugDetailActivity.class
                if(roleid.equals("2")){
                    intent.setClass(CheckActivity.this,BugDetailActivity.class);//故障详情
                }else if (roleid.equals("3")){
                    intent.setClass(CheckActivity.this,UpdateCheckActivity.class);//专人修改维修人员上报内容
                }

                intent.putExtra("bugId", bugid);
                startActivity(intent);

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
                    listView.setVisibility(View.VISIBLE);
                    ll_nodata.setVisibility(View.GONE);
                    GetCheckList();
                    break;
                case 1:
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nonet);
                    tv_nodata.setText("网络链接超时...");
                    Toast.makeText(CheckActivity.this, "网络链接超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(CheckActivity.this, "暂无数据！", Toast.LENGTH_SHORT).show();
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nodata);
                    tv_nodata.setText("暂无相关数据...");
                    break;
                default:
                    break;
            }
        }
    };

    /* 获取checkList */
    private void setCheckList(){
        new Thread() {
            @Override
            public void run() {
                String getPriceListStr =  UserModel.myhost + "getCheckList.php";
                String data = "state=" + getIntent().getIntExtra("state",4)+"&userid="+userid;
                //获取bug列表
                bugListsStr = PostParamTools.postGetInfo(getPriceListStr, data);
                if(bugListsStr == null){
                    handler.sendEmptyMessage(1);
                }else if("".equals(bugListsStr)){
                    handler.sendEmptyMessage(3);// 发送处理消息
                }else{
                    handler.sendEmptyMessage(0);// 发送处理消息

                }

            }
        }.start();
    }

    private void GetCheckList(){
        try {
            checkList = new ArrayList<HashMap<String, Object>>();
            JSONArray bugListsJSON = new JSONArray(bugListsStr);

            for (int i = 0; i < bugListsJSON.length(); i++) {
                JSONObject jsonObject = bugListsJSON.getJSONObject(i);
                HashMap<String, Object> bug = new HashMap<String, Object>();
                bug.put("bugId", jsonObject.getString("bugid"));
                bug.put("bugaddr", jsonObject.getString("bugaddr"));
                bug.put("bugtype", jsonObject.getString("bugtype"));
                bug.put("bugfinddescrip", jsonObject.getString("bugfinddescrip"));
                bug.put("chargetime", jsonObject.getString("chargetime"));
                checkList.add(bug);
            }

            checkAdapter = new CheckAdapter(CheckActivity.this, checkList);
            listView.setAdapter(checkAdapter); // 重新设置ListView的数据适配器
            checkAdapter.notifyDataSetChanged();//发送消息通知ListView更新

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
