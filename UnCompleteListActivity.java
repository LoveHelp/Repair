package com.xianyi.chen.repair;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
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
import java.util.Timer;
import java.util.TimerTask;

public class UnCompleteListActivity extends AppCompatActivity {
    private ListView lv;//主界面列表
    public String spec,data,result;
    private MyAdapter myAdapter;

    private String roleid=UserModel.getroleid();
    private ArrayList<HashMap<String, Object>> listItem=null;

    private TextView tv_title;//头部标题

    private LinearLayout ll_nodata;//暂无数据
    private TextView tv_nodata;
    private ImageView iv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_complete_list);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        querylist();

    }

    /**
     * 初始化控件
     **/
    private void InitControl() {
        lv = (ListView) findViewById(R.id.listView);
        ll_nodata = (LinearLayout)findViewById(R.id.ll_nodata);
        tv_nodata=(TextView)findViewById(R.id.tv_nodata);
        iv_nodata=(ImageView)findViewById(R.id.iv_nodata);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("待完工列表");
    }

    /**
     * 初始化控件事件
     **/
    private void InitControlEvent() {
        //listview的item点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, Object> hashMap = listItem.get(i);
                String bugid = hashMap.get("ItemBugId").toString();
                Intent intent=new Intent();
                switch (roleid){
                    case "2":
                        intent.setClass(UnCompleteListActivity.this,BugDetailActivity.class);
                        break;
                    case "3":
                        intent.setClass(UnCompleteListActivity.this,UpdateCheckActivity.class);
                        break;
                    default:
                        intent.setClass(UnCompleteListActivity.this,LoginActivity.class);
                        break;
                }
                intent.putExtra("bugId", bugid);
                startActivity(intent);
            }
        });
    }

    /*
    查询出未分配故障列表
     */
    public void  querylist(){
        new Thread(){
            @Override
            public void run() {
                spec = UserModel.myhost+"complete.php";
                data="roleid="+roleid;
                result = PostParamTools.postGetInfo(spec, data);
                if(result==null){
                    mHandler.sendEmptyMessage(0);
                }else if(("").equals(result)){
                    mHandler.sendEmptyMessage(1);
                }
                else {
                    mHandler.sendEmptyMessage(2);
                }

            }
        }.start();
    }

    @Override
    protected void onDestroy(){
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 新任务提醒
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    lv.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nonet);
                    tv_nodata.setText("网络链接超时...");
                    //Toast.makeText(UnCompleteListActivity.this, "网络链接超时...", Toast.LENGTH_LONG).show();
                case 1:
                    Toast.makeText(UnCompleteListActivity.this,"所有故障已处理!",Toast.LENGTH_SHORT).show();
                    lv.setAdapter(null);
                    lv.setVisibility(View.GONE);
                    ll_nodata.setVisibility(View.VISIBLE);
                    iv_nodata.setImageResource(R.drawable.nodata);
                    tv_nodata.setText("暂无相关数据...");
                    break;
                case 2:
                    lv.setVisibility(View.VISIBLE);
                    ll_nodata.setVisibility(View.GONE);
                    listItem=getData(result);
                    myAdapter = new MyAdapter(UnCompleteListActivity.this,listItem);
                    lv.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    private ArrayList<HashMap<String, Object>> getData(String result){
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,Object>>();
        /**为动态数组添加数据*/
        try {
            if(!("null".equals(result))) {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemBugId", jsonObject.getString("bugid"));
                    map.put("ItemBugType", jsonObject.getString("bugtype"));
                    map.put("ItemBugAddr", jsonObject.getString("bugaddr"));
                    String showState=jsonObject.getString("state");
                    map.put("state", showState);
                    map.put("ItemBugTime", jsonObject.getString("completetime"));
                    map.put("ItemBugDesc", jsonObject.getString("bugfinddescrip"));
                    map.put("ItemBugFindperson", jsonObject.getString("personname"));
                    map.put("ItemBugFindPhoto", jsonObject.getString("bugfindphoto"));
                    listItem.add(map);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return listItem;
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public String bugid;
        private ArrayList<HashMap<String, Object>> list;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public MyAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /***/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            // Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.directoritem,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.itembugtype = (TextView) convertView.findViewById(R.id.ItemBugType);
                holder.itembugaddr = (TextView) convertView.findViewById(R.id.ItemBugAddr);
                holder.itembugtime = (TextView) convertView.findViewById(R.id.ItemBugTime);

                holder.itembugdesc = (TextView) convertView.findViewById(R.id.ItemBugDesc);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/
            holder.itembugid=(String)list.get(position).get("ItemBugId");
            holder.itembugtype.setText((String)list.get(position).get("ItemBugType"));
            holder.itembugaddr.setText((String)list.get(position).get("ItemBugAddr"));
            holder.itembugtime.setText(PostParamTools.getDateFormat((String)list.get(position).get("ItemBugTime")));
            holder.itembugdesc.setText((String)list.get(position).get("ItemBugDesc"));

            return convertView;
        }

    }

    /**存放控件*/
    public final class ViewHolder{
        public String itembugid;
        public TextView itembugtype;
        public TextView itembugaddr;
        public TextView itembugtime;
        public TextView itembugdesc;
    }



}
