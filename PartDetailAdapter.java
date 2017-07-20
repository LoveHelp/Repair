package com.xianyi.chen.repair;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/23 0023.
 */
public class PartDetailAdapter extends BaseAdapter{
    private List<HashMap<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private Boolean[] arr;

    public PartDetailAdapter(Context context, List<HashMap<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = layoutInflater.from(context);
    }
    /**
     * 组件集合，对应price.xml中的控件
     * @author guojing
     */
    public final class Zujian{
        public TextView partNum;
        public TextView partName;
        public TextView num;
        public TextView partPrice;
        public TextView partDescription;
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
            convertView = layoutInflater.inflate(R.layout.check_detail_item, null);
            zujian.partNum = (TextView)convertView.findViewById(R.id.part_num);
            zujian.partName = (TextView)convertView.findViewById(R.id.part_name);
            zujian.num = (TextView)convertView.findViewById(R.id.num);
            zujian.partPrice = (TextView)convertView.findViewById(R.id.part_price);
            zujian.partDescription = (TextView)convertView.findViewById(R.id.part_description);
            convertView.setTag(zujian);
        }else{
            zujian = (Zujian)convertView.getTag();
        }
        //绑定数据
        //zujian.image.setBackgroundResource((Integer)data.get(position).get("image"));
        zujian.partNum.setText(String.valueOf(position + 1));
        zujian.partName.setText((String)data.get(position).get("partName"));
        zujian.num.setText((String)data.get(position).get("num"));//数量
        zujian.partPrice.setText((String)data.get(position).get("partPrice"));//单价
        zujian.partDescription.setText((String)data.get(position).get("partDescription"));

        int[] colors = {  Color.rgb(219, 238, 244),Color.WHITE };//RGB颜色
        convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

        return convertView;
    }

}
