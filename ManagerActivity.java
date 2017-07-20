package com.xianyi.chen.repair;

        import android.content.Context;
        import android.content.Intent;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.BaseAdapter;
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
        import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    List<Map<String, Object>> bugList = null;

    private BugAdapter bugAdapter = null;

    private ListView listView;

    public int repairsenduserid;
    private String bugListsStr="";

    private TextView tv_title;//头部标题

    private LinearLayout ll_nodata;//暂无数据
    private TextView tv_nodata;
    private ImageView iv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        setBugList();//获取任务列表

    }

    /** 初始化控件 **/
    private void InitControl(){
        repairsenduserid = Integer.parseInt(UserModel.getuserid());

        listView = (ListView) findViewById(R.id.bugList);
        ll_nodata = (LinearLayout)findViewById(R.id.ll_nodata);
        tv_nodata=(TextView)findViewById(R.id.tv_nodata);
        iv_nodata=(ImageView)findViewById(R.id.iv_nodata);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("任务分派列表");
    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
        //查看详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //HashMap<String, Object> hashMap = bugList.get(i);
                Map<String,Object> map = bugList.get(i);
                String bugid = map.get("bugId").toString();

                Intent intent = new Intent();//context, BugDetailActivity.class
                intent.setClass(ManagerActivity.this,BugDetailActivity.class);//专人修改维修人员上报内容
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
                    GetBugList();
                    break;
                case 1:
                    listView.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nonet);
                    tv_nodata.setText("网络链接超时...");
                    //Toast.makeText(ManagerActivity.this, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(ManagerActivity.this, "暂无数据！", Toast.LENGTH_SHORT).show();
                    listView.setAdapter(null);
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

    /* 获取bugList */
    private void setBugList(){
        new Thread() {
            @Override
            public void run() {
                String getBugListStr =  UserModel.myhost + "getBugList.php";
                String data = "userid=" + repairsenduserid;
                //获取bug列表
                bugListsStr = PostParamTools.postGetInfo(getBugListStr, data);
                if(bugListsStr==null){
                    handler.sendEmptyMessage(1);// 发送处理消息
                }else if("".equals(bugListsStr)){
                    handler.sendEmptyMessage(3);// 发送处理消息
                }else{
                    handler.sendEmptyMessage(0);// 发送处理消息
                }
            }
        }.start();
    }

    private void GetBugList(){
        try {
            bugList = new ArrayList<Map<String, Object>>();
            JSONArray bugListsJSON = new JSONArray(bugListsStr);
            for (int i = 0; i < bugListsJSON.length(); i++) {
                JSONObject jsonObject = bugListsJSON.getJSONObject(i);
                Map<String, Object> bug = new HashMap<String, Object>();
                bug.put("bugId", jsonObject.getString("bugid"));
                bug.put("bugaddr", jsonObject.getString("bugaddr"));
                bug.put("bugtype", jsonObject.getString("bugtype"));
                bug.put("bugfinddescrip", jsonObject.getString("bugfinddescrip"));
                bug.put("bugsendtime", jsonObject.getString("bugsendtime"));//指挥中心分派故障时间

                bugList.add(bug);
            }
            bugAdapter = new BugAdapter(ManagerActivity.this, bugList);
            listView.setAdapter(bugAdapter); // 重新设置ListView的数据适配器
            bugAdapter.notifyDataSetChanged();//发送消息通知ListView更新
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class BugAdapter extends BaseAdapter {
        private List<Map<String, Object>> data;
        private LayoutInflater layoutInflater;
        private Context context;
        private Boolean[] arr;

        public BugAdapter(Context context, List<Map<String, Object>> data){
            this.context = context;
            this.data = data;
            this.layoutInflater = layoutInflater.from(context);
        }
        /**
         * 组件集合，对应activity_main.xml中的控件
         * @author guojing
         */
        public final class ViewHolder{
            public TextView bugtype,bugaddr,bugfinddescrip,bugsendtime;
            public String bugid;
        }

        @Override
        public int getCount() {
            return data.size();
        }
        /**
         * 获得某一位置的数据
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }
        /**
         * 获得唯一标识
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                //获得组件，实例化组件
                convertView = layoutInflater.inflate(R.layout.list_view, null);

                holder.bugtype = (TextView)convertView.findViewById(R.id.tvBugType);
                holder.bugaddr = (TextView)convertView.findViewById(R.id.tvBugAddr);
                holder.bugfinddescrip = (TextView)convertView.findViewById(R.id.tvBugDescription);
                holder.bugsendtime = (TextView)convertView.findViewById(R.id.tvBugSendTime);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            //绑定数据
            holder.bugid = (String)data.get(position).get("bugId");
            String btype = "故障类型："+ data.get(position).get("bugtype");
            holder.bugtype.setText(btype);
            String baddr = "故障地址："+ data.get(position).get("bugaddr");
            holder.bugaddr.setText(baddr);
            String bfinddescrip = "故障描述："+ data.get(position).get("bugfinddescrip");
            holder.bugfinddescrip.setText(bfinddescrip);
            String bugsendtime = "分派时间："+ PostParamTools.getDateFormat(data.get(position).get("bugsendtime").toString());
            holder.bugsendtime.setText(bugsendtime);

//            /**为Button添加点击事件：选择维修人员*/
//            holder.bugSend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showMultiChoiceDialog(bugId, workers);
//                }
//            });

            return convertView;
        }
    }

}

