package com.xianyi.chen.repair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
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

public class TaskListActivity extends AppCompatActivity {

    private ListView lvTask;
    private String spec = UserModel.myhost+"getTaskList.php";
    private Integer flag = 0;
    private String data = "flag="+flag;
    private String result="";
    private ArrayList<HashMap<String,Object>> datalist=null;
    private MyAdapter myAdapter;
    private String roleid=UserModel.getroleid();
    private Intent intent;
    private ProgressDialog proDialog;

    private TextView tv_title;//头部标题
    private LinearLayout ll_searchkuang;//搜索框

    private LinearLayout ll_nodata;//暂无数据
    private TextView tv_nodata;
    private ImageView iv_nodata;

    private SwipeRefreshLayout swipeRefresh;//下拉刷新

    private Bundle b;//筛选回传参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        proDialog = ProgressDialog.show(TaskListActivity.this, "", "加载中，请稍候...");

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        TaskList("","88","","","","","","");

    }

    /**
     * 初始化控件
     * **/
    private void InitControl(){
        lvTask=(ListView)findViewById(R.id.lvTask);

        tv_title=(TextView)findViewById(R.id.tv_title);
        ll_searchkuang = (LinearLayout)findViewById(R.id.ll_searchkuang);
        ll_nodata = (LinearLayout)findViewById(R.id.ll_nodata);
        tv_nodata=(TextView)findViewById(R.id.tv_nodata);
        iv_nodata=(ImageView)findViewById(R.id.iv_nodata);

        flag=getIntent().getIntExtra("flag",0);
        if(flag==0){
            tv_title.setText("未完工任务列表");
        }else if(flag==1){
            tv_title.setText("已完工任务列表");
        }else if(flag==2){
            tv_title.setText("待指挥中心核价任务");
        }else if(flag==3){
            tv_title.setText("已核价任务列表");
        }else if(flag==4){
            tv_title.setText("完工待指挥中心确认任务列表");
        }else if(flag==4){
            tv_title.setText("待分派任务列表");
        }

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
    }

    /**
     * 初始化控件事件
     *  **/
    private void InitControlEvent(){

        //筛选
        ll_searchkuang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(TaskListActivity.this,SearchActivity.class);
                intent.putExtra("flag",flag);
                startActivityForResult(intent,0);
            }
        });

        //item上的onclick事件
        lvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String,Object> hashMap=datalist.get(i);
                String bugid = hashMap.get("bugid").toString();

                intent = new Intent(TaskListActivity.this,TaskDetailActivity.class);
                intent.putExtra("bugid",bugid);
                startActivity(intent);

            }
        });

        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMain();
            }
        });

        //解决swiperefreshlayout 与listview滑动冲突
        lvTask.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    boolean enable = false;
                    if(lvTask != null && lvTask.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = lvTask.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = lvTask.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    swipeRefresh.setEnabled(enable);
            }
        });

    }

    private void refreshMain(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GetList();
                        swipeRefresh.setRefreshing(false);//刷新事件结束，隐藏刷新进度条
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent datas){
        if(requestCode==0 && resultCode==1) {
            proDialog = ProgressDialog.show(TaskListActivity.this, "", "加载中，请稍候...");
            b = datas.getExtras(); //data为B中回传的Intent
            GetList();
        }
    }

    /**
     * 是否有筛选条件
     * **/
    private void GetList(){
        if(b != null){
            String type = b.getString("type");
            String bugtype = b.getString("bugtype");
            String address = b.getString("address");
            String description = b.getString("description");
            String startbugfindtime = b.getString("startbugfindtime");
            String endbugfindtime = b.getString("endbugfindtime");
            String startcompletechecktime = b.getString("startcompletechecktime");
            String endcompletechecktime = b.getString("endcompletechecktime");
            TaskList(bugtype,type,address,description,startbugfindtime,endbugfindtime,startcompletechecktime,endcompletechecktime);
        }
    }


    /**
     * 最终数据列表
     * **/
    private void TaskList(final String bugtype,final String type,final String address, final String description,final String startbugfindtime, final String endbugfindtime,final String startcompletechecktime, final String endcompletechecktime){

        new Thread(){
            @Override
            public void run(){
                data = "flag="+flag+"&roleid="+roleid+"&userid="+UserModel.getuserid()+"&type="+type+"&bugtype="+bugtype;
                //新增筛选
                if(address != null && !address.equals("")){
                    data+="&address="+address;
                }
                if(description != null && !description.equals("")){
                    data+="&description="+description;
                }
                if(startbugfindtime != null && !startbugfindtime.equals("")){
                    data+="&startbugfindtime="+startbugfindtime;
                }
                if(endbugfindtime != null && !endbugfindtime.equals("")){
                    data+="&endbugfindtime="+endbugfindtime;
                }
                if(startcompletechecktime != null && !startcompletechecktime.equals("")){
                    data+="&startcompletechecktime="+startcompletechecktime;
                }
                if(endcompletechecktime != null && !endcompletechecktime.equals("")){
                    data+="&endcompletechecktime="+endcompletechecktime;
                }
                result = PostParamTools.postGetInfo(spec, data);
                proDialog.dismiss();
                TaskListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(result==null){
                            lvTask.setVisibility(View.GONE);
                            ll_nodata.setVisibility(View.VISIBLE);
                            iv_nodata.setImageResource(R.drawable.nonet);
                            tv_nodata.setText("网络链接超时...");

                            //Toast.makeText(TaskListActivity.this, "网络链接超时...", Toast.LENGTH_LONG).show();
                        }else if(result.equals("0")) {
                            lvTask.setAdapter(null);
                            lvTask.setVisibility(View.GONE);
                            ll_nodata.setVisibility(View.VISIBLE);
                            iv_nodata.setImageResource(R.drawable.nodata);
                            tv_nodata.setText("暂无相关数据...");
                            //Toast.makeText(TaskListActivity.this, "暂无数据...", Toast.LENGTH_LONG).show();
                        }else if (!result.equals("null")) {
                            // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                            lvTask.setVisibility(View.VISIBLE);
                            ll_nodata.setVisibility(View.GONE);
                            datalist = JSONAnalysis(result);
                            myAdapter = new MyAdapter(TaskListActivity.this, datalist);
                            lvTask.setAdapter(myAdapter);
                            myAdapter.notifyDataSetChanged();
                        }  else{
                            lvTask.setAdapter(null);
                            Toast.makeText(TaskListActivity.this, "请求数据失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
                item.put("bugfinddescrip", jsonObject.getString("bugfinddescrip"));
                item.put("bugfindphoto", jsonObject.getString("bugfindphoto"));
                item.put("bugaddr", jsonObject.getString("bugaddr"));
                item.put("bugtype", jsonObject.getString("bugtype"));
                String showState=jsonObject.getString("state");
                String showType=jsonObject.getString("type");
                item.put("state", showState);
                item.put("type", showType);
                switch (showState){
                    case "1":
                        item.put("bugsendtime", jsonObject.getString("bugsendtime"));//分派时间
                        break;
                    case "2":
                        item.put("repairsendtime", jsonObject.getString("repairsendtime"));//派单时间
                        break;
                    case "3":
                        item.put("repairchecktime", jsonObject.getString("repairchecktime"));//排查时间
                        break;
                    case "4":
                        item.put("chargetime", jsonObject.getString("chargetime"));//报价时间
                        break;
                    case "41":
                        item.put("checktime_zr", jsonObject.getString("checktime_zr"));//报价时间
                        break;
                    case "5":
                        if(showType.equals("1")){
                            item.put("checktime", jsonObject.getString("checktime"));//审核通过时间
                        }else {
                            item.put("repairchecktime", jsonObject.getString("repairchecktime"));//排查时间
                        }
                        break;
                    case "6":
                        item.put("completetime", jsonObject.getString("completetime"));//完工时间
                        break;
                    case "61":
                        item.put("compeletchecktime_zr", jsonObject.getString("compeletchecktime_zr"));//完工时间
                        break;
                    case "7":
                        item.put("compeletchecktime", jsonObject.getString("compeletchecktime"));//完工确认时间
                        break;
                    default:
                        item.put("bugfindtime", jsonObject.getString("bugfindtime"));
                        break;
                }
                datalist.add(item);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return datalist;
    }

}
