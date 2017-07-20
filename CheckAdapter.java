package com.xianyi.chen.repair;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23 0023.
 **/
public class CheckAdapter extends BaseAdapter{
    private List<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private String roleid=UserModel.getroleid();

    public CheckAdapter(Context context, List<HashMap<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = layoutInflater.from(context);
    }
    /**
     * 组件集合，对应price.xml中的控件
     * @author guojing
     */
    public final class Zujian{
        public TextView bugaddr,bugtype,bugfinddescrip,chargetime;
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
        Zujian zujian = null;
        if(convertView == null){
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.price_item, null);
            zujian.bugtype = (TextView)convertView.findViewById(R.id.tvBugType);
            zujian.bugaddr = (TextView)convertView.findViewById(R.id.tvBugAddr);
            zujian.bugfinddescrip = (TextView)convertView.findViewById(R.id.tvBugDescription);
            zujian.chargetime = (TextView)convertView.findViewById(R.id.tvChargeTime);
            convertView.setTag(zujian);
        }else{
            zujian = (Zujian)convertView.getTag();
        }
        //绑定数据
        zujian.bugid = (String)data.get(position).get("bugId");
        String btype = "故障类型："+ data.get(position).get("bugtype");
        zujian.bugtype.setText(btype);
        String baddr = "故障地址："+ data.get(position).get("bugaddr");
        zujian.bugaddr.setText(baddr);
        String bfinddescrip = "故障描述："+ data.get(position).get("bugfinddescrip");
        zujian.bugfinddescrip.setText(bfinddescrip);
        String bchargetime = "核价时间："+ PostParamTools.getDateFormat(data.get(position).get("chargetime").toString());
        zujian.chargetime.setText(bchargetime);


        return convertView;
    }


}
