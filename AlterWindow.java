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

public class AlterWindow extends AppCompatActivity {

    private TextView tvTitle;
    private ImageButton ibtnJia;
    private TableLayout tlTask,tlSearch;
    private int menuflag = 3;//底部菜单标示：1首页；2任务；3查询；4我的

    private LinearLayout ll_fenpai2,ll_luru2,ll_unwangong2,ll_unhejia2;//任务
    private LinearLayout ll_yifenpailist,ll_hejialist,ll_unwangonglist,ll_wangonglist;//查询
    private ImageView iv_fenpai,iv_unhejia,iv_unwangong;
    private ImageView iv_yifenpai,iv_yihejia,iv_weiwangong,iv_yiwangong;

    public String spec,data,result;
    public String userid=UserModel.getuserid();
    public String roleid=UserModel.getroleid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_window);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        RepairList();//加载任务列表
    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){

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

        //任务-指挥中心
        //任务-录入
        ll_luru2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,FindbugActivity.class);
                startActivity(intent);
            }
        });
        //任务-未分派任务
        ll_fenpai2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,DispatchActivity.class);
                startActivity(intent);
            }
        });
        //任务-待核价任务
        ll_unhejia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,CheckActivity.class);
                intent.putExtra("state",41);//4甲方专人核价；41指挥中心核价
                startActivity(intent);
            }
        });
        //任务-完工确认任务
        ll_unwangong2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,UnCompleteListActivity.class);
                startActivity(intent);
            }
        });

        //查询-已分派任务
        ll_yifenpailist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,TaskListActivity.class);
                intent.putExtra("flag",5);
                startActivity(intent);
            }
        });
        //查询-已核价任务
        ll_hejialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,TaskListActivity.class);
                intent.putExtra("flag",3);
                startActivity(intent);
            }
        });
        //查询-未完成任务
        ll_unwangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,TaskListActivity.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        //查询-已完成任务
        ll_wangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindow.this,TaskListActivity.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
        //返回
        ibtnJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(AlterWindow.this,Main.class);
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
                    Toast.makeText(AlterWindow.this, "网络链接超时!", Toast.LENGTH_LONG).show();
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

                //未处理任务提醒-待分派
                Integer fenpai= Integer.parseInt(jsonObject.getString("numUnPaiFa"));
                setBadgeView(iv_fenpai,fenpai,Color.parseColor("#FF0000"));
                //未处理任务提醒-待核价
                Integer unhejia= Integer.parseInt(jsonObject.getString("numUnHeJia"));
                setBadgeView(iv_unhejia,unhejia,Color.parseColor("#FF0000"));
                //未处理任务提醒-待完工确认
                Integer unwangong= Integer.parseInt(jsonObject.getString("numUnWanGongSure"));
                setBadgeView(iv_unwangong,unwangong,Color.parseColor("#FF0000"));

            }else {//查询

                //查询-已分派
                Integer numYiFenpai= Integer.parseInt(jsonObject.getString("numYiFenpai"));
                setBadgeView(iv_yifenpai,numYiFenpai,Color.parseColor("#5e3ab8"));
                //查询-已核价
                Integer numHeJia= Integer.parseInt(jsonObject.getString("numHeJia"));
                setBadgeView(iv_yihejia,numHeJia,Color.parseColor("#5e3ab8"));
                //查询-未完工
                Integer numUnWanGong= Integer.parseInt(jsonObject.getString("numUnWanGong"));
                setBadgeView(iv_weiwangong,numUnWanGong,Color.parseColor("#5e3ab8"));
                //查询-已完工
                Integer numWanGong= Integer.parseInt(jsonObject.getString("numWanGong"));
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
