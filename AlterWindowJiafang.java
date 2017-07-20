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

public class AlterWindowJiafang extends AppCompatActivity {

    private TextView tvTitle;
    private ImageButton ibtnJia;
    private TableLayout tlTask,tlSearch;
    private int menuflag = 3;//底部菜单标示：1首页；2任务；3查询；4我的

    private LinearLayout ll_jfenpai,ll_junhejia,ll_junwangong;//任务
    private ImageView iv_jfenpai,iv_junhejia,iv_junwangong;
    private LinearLayout ll_yifenpailist,ll_unhejialist,ll_hejialist,ll_unzwangonglist,ll_unwangonglist,ll_wangonglist;//查询
    private ImageView iv_yifenpai,iv_unhejia,iv_hejialist,iv_unzwangonglist,iv_unwangonglist,iv_wangonglist;

    public String spec,data,result;
    public String userid=UserModel.getuserid();
    public String roleid=UserModel.getroleid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_window_jiafang);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        RepairList();//加载任务列表
    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){

        //任务
        ll_jfenpai=(LinearLayout)findViewById(R.id.ll_jfenpai);
        ll_junhejia=(LinearLayout)findViewById(R.id.ll_junhejia);
        ll_junwangong=(LinearLayout)findViewById(R.id.ll_junwangong);

        //查询
        ll_yifenpailist=(LinearLayout)findViewById(R.id.ll_yifenpailist);
        ll_unhejialist=(LinearLayout)findViewById(R.id.ll_unhejialist);
        ll_hejialist=(LinearLayout)findViewById(R.id.ll_hejialist);
        ll_unzwangonglist=(LinearLayout)findViewById(R.id.ll_unzwangonglist);
        ll_unwangonglist=(LinearLayout)findViewById(R.id.ll_unwangonglist);
        ll_wangonglist=(LinearLayout)findViewById(R.id.ll_wangonglist);

        //图标，用于未处理任务提醒iv_fenpai,iv_unhejia,iv_unwangong;
        iv_jfenpai=(ImageView) findViewById(R.id.iv_jfenpai);
        iv_junhejia=(ImageView) findViewById(R.id.iv_junhejia);
        iv_junwangong=(ImageView) findViewById(R.id.iv_junwangong);

        //查询统计
        iv_yifenpai=(ImageView)findViewById(R.id.iv_yifenpai);
        iv_unhejia=(ImageView)findViewById(R.id.iv_unhejia);
        iv_hejialist=(ImageView)findViewById(R.id.iv_hejialist);
        iv_unzwangonglist=(ImageView)findViewById(R.id.iv_unzwangonglist);
        iv_unwangonglist=(ImageView)findViewById(R.id.iv_unwangonglist);
        iv_wangonglist=(ImageView)findViewById(R.id.iv_wangonglist);

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

        //任务-甲方专人
        //未分派任务
        ll_jfenpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,ManagerActivity.class);
                startActivity(intent);
            }
        });
        //未核价任务
        ll_junhejia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,CheckActivity.class);
                intent.putExtra("state",4);//4甲方专人核价；41指挥中心核价
                startActivity(intent);
            }
        });
        //完工待专人确认任务
        ll_junwangong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,UnCompleteListActivity.class);
                startActivity(intent);
            }
        });

        //查询-甲方专人
        //查询-已分派任务
        ll_yifenpailist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",5);
                startActivity(intent);
            }
        });
        //专人已核价待指挥核价
        ll_unhejialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",2);
                startActivity(intent);
            }
        });
        //已核价任务
        ll_hejialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",3);
                startActivity(intent);
            }
        });
        //完工待指挥中心确认任务
        ll_unzwangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",4);//完工待指挥中心审核
                startActivity(intent);
            }
        });
        //未完成任务
        ll_unwangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        //已完成任务
        ll_wangonglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlterWindowJiafang.this,TaskListActivity.class);
                intent.putExtra("flag",1);
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
                    Toast.makeText(AlterWindowJiafang.this, "网络链接超时!", Toast.LENGTH_LONG).show();
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
                setBadgeView(iv_jfenpai,fenpai, Color.parseColor("#FF0000"));
                //未处理任务提醒-待核价
                Integer unhejia= Integer.parseInt(jsonObject.getString("numUnHeJia"));
                setBadgeView(iv_junhejia,unhejia,Color.parseColor("#FF0000"));
                //未处理任务提醒-待完工确认
                Integer unwangong= Integer.parseInt(jsonObject.getString("numUnWanGongZRSure"));
                setBadgeView(iv_junwangong,unwangong,Color.parseColor("#FF0000"));

            }else {//查询

                //查询-已分派
                Integer numYiFenpai= Integer.parseInt(jsonObject.getString("numYiFenpai"));
                setBadgeView(iv_yifenpai,numYiFenpai,Color.parseColor("#5e3ab8"));
                //查询-中心核价
                Integer numHeJia= Integer.parseInt(jsonObject.getString("numHeJia"));
                setBadgeView(iv_unhejia,numHeJia,Color.parseColor("#5e3ab8"));
                //查询-已核价
                Integer numYiHeJia= Integer.parseInt(jsonObject.getString("numHeJiaed"));
                setBadgeView(iv_hejialist,numYiHeJia,Color.parseColor("#5e3ab8"));
                //查询-中心完工
                Integer numUnZWanGong= Integer.parseInt(jsonObject.getString("numUnWanGongZHZXSure"));
                setBadgeView(iv_unzwangonglist,numUnZWanGong,Color.parseColor("#5e3ab8"));
                //查询-未完工
                Integer numUnWanGong= Integer.parseInt(jsonObject.getString("numUnWanGong"));
                setBadgeView(iv_unwangonglist,numUnWanGong,Color.parseColor("#5e3ab8"));
                //查询-已完工
                Integer numWanGong= Integer.parseInt(jsonObject.getString("numWanGong"));
                setBadgeView(iv_wangonglist,numWanGong,Color.parseColor("#5e3ab8"));

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
