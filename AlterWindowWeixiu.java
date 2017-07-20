package com.xianyi.chen.repair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jauker.widget.BadgeView;

import org.json.JSONObject;

public class AlterWindowWeixiu extends AppCompatActivity {

    private TextView tvTitle;
    private ImageButton ibtnJia;
    private TableLayout tlTask,tlSearch;
    private int menuflag = 3;//底部菜单标示：1首页；2任务；3查询；4我的

    private LinearLayout ll_paicha,ll_baojia,ll_weixiu;//任务
    private ImageView iv_paicha,iv_baojia,iv_weixiu;
    private LinearLayout ll_wangonglist,ll_unwangonglist;//查询
    private ImageView iv_yiwangong,iv_weiwangong;

    public String spec,data,result;
    public String userid=UserModel.getuserid();
    public String roleid=UserModel.getroleid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_window_weixiu);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        RepairList();//加载任务列表
    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){

        //任务
        ll_paicha=(LinearLayout)findViewById(R.id.ll_paicha);
        ll_baojia =(LinearLayout)findViewById(R.id.ll_baojia);
        ll_weixiu=(LinearLayout)findViewById(R.id.ll_weixiu);

        //查询
        ll_wangonglist=(LinearLayout)findViewById(R.id.ll_wangonglist);
        ll_unwangonglist=(LinearLayout)findViewById(R.id.ll_unwangonglist);

        //图标，用于未处理任务提醒iv_fenpai,iv_unhejia,iv_unwangong;
        iv_paicha=(ImageView) findViewById(R.id.iv_paicha);
        iv_baojia=(ImageView) findViewById(R.id.iv_baojia);
        iv_weixiu=(ImageView) findViewById(R.id.iv_weixiu);

        //查询统计
        iv_yiwangong=(ImageView)findViewById(R.id.iv_yiwangong);
        iv_weiwangong=(ImageView)findViewById(R.id.iv_weiwangong);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ibtnJia = (ImageButton) findViewById(R.id.ibtnJia);
        tlTask = (TableLayout) findViewById(R.id.tlTask);
        tlSearch = (TableLayout) findViewById(R.id.tlSearch);

        menuflag = getIntent().getIntExtra("menuflag",3);
        if(menuflag==2){
            tvTitle.setText("任务管理");
            tlTask.setVisibility(View.VISIBLE);
            tlSearch.setVisibility(View.GONE);
        }else {
            tvTitle.setText("查询管理");
            tlTask.setVisibility(View.GONE);
            tlSearch.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 初始化控件事件
     *  **/
    private void InitControlEvent(){

        //任务-维修人员
        //排查
        ll_paicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowWeixiu.this,UnRepairListActivity.class);
                startActivity(intent);
            }
        });
        //报价
        ll_baojia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowWeixiu.this,UnOfferListActivity.class);
                intent.putExtra("state",4);//4甲方专人核价；41指挥中心核价
                startActivity(intent);
            }
        });
        //维修
        ll_weixiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowWeixiu.this,RepairListActivity.class);
                startActivity(intent);
            }
        });
        //已完成任务
        ll_wangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowWeixiu.this,TaskListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
        //未完成任务
        ll_unwangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowWeixiu.this,TaskListActivity.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });

        //返回
        ibtnJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(AlterWindowJiafang.this,Main.class);
//                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * handler更新操作
     */
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    Toast.makeText(AlterWindowWeixiu.this, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    GetCount();
                    break;
            }

        }
    };

    /**
     * 获取网络数据-统计数量
     * **/
    private void RepairList(){

        new Thread(){
            @Override
            public void run() {
                spec=UserModel.myhost+"getCount.php";
                data="roleid="+roleid+"&userid="+userid;
                result = PostParamTools.postGetInfo(spec, data);
                if(result == null){
                    mHandler.sendEmptyMessage(0);
                }else {
                    mHandler.sendEmptyMessage(2);//获取统计数量,并更新界面
                }

            }
        }.start();

    }

    /**
     * 获取统计数量,并更新界面
     * **/
    private void GetCount(){
        try {
            JSONObject jsonObject = new JSONObject(result);

            if(menuflag==2){//任务

                //未处理任务提醒-待排查
                Integer paicha= Integer.parseInt(jsonObject.getString("numUnPaiCha"));
                setBadgeView(iv_paicha,paicha, Color.parseColor("#FF0000"));
                //未处理任务提醒-待报价
                Integer baojia= Integer.parseInt(jsonObject.getString("numUnBaoJia"));
                setBadgeView(iv_baojia,baojia,Color.parseColor("#FF0000"));
                //未处理任务提醒-待维修
                Integer weixiu= Integer.parseInt(jsonObject.getString("numUnWeixiu"));
                setBadgeView(iv_weixiu,weixiu,Color.parseColor("#FF0000"));

            }else {//查询

                //查询-未完工
                Integer numUnWanGong= Integer.parseInt(jsonObject.getString("numUnWangong"));
                setBadgeView(iv_weiwangong,numUnWanGong,Color.parseColor("#5e3ab8"));
                //查询-已完工
                Integer numWanGong= Integer.parseInt(jsonObject.getString("numWangong"));
                setBadgeView(iv_yiwangong,numWanGong,Color.parseColor("#5e3ab8"));

            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置数字提醒
     * **/
    private void setBadgeView(ImageView imageView,int count,int color){
        BadgeView badgeView = new com.jauker.widget.BadgeView(this);
        badgeView.setTargetView(imageView);
        badgeView.setBadgeCount(count);
        badgeView.setBackground(10,color);
    }

}
