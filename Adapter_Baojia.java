package com.xianyi.chen.repair;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/1 0001.
 **/
public class Adapter_Baojia extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    HashMap<Object,Boolean> hashMap=new HashMap<Object, Boolean>();
    // 用来控制CheckBox的选中状况
    public static HashMap<Integer,Boolean> isSelected;

    public Adapter_Baojia(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
        init();
    }
    // 初始化 设置所有checkbox都为未选择
    public void init() {
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.part, null);
            holder = new ViewHolder();

            holder.ckb_parts = (CheckBox) convertView.findViewById(R.id.ckb_parts);
            holder.tv_partid = (TextView) convertView.findViewById(R.id.tv_partid);
            holder.tv_reference = (TextView) convertView.findViewById(R.id.tv_reference);
            holder.tv_partname = (TextView) convertView.findViewById(R.id.tv_partname);
            holder.tv_partxh = (TextView) convertView.findViewById(R.id.tv_partxh);
            holder.tv_partbrand = (TextView) convertView.findViewById(R.id.tv_partbrand);
            holder.tv_partprice = (TextView) convertView.findViewById(R.id.tv_partprice);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_partid.setText(list.get(position).get("partid").toString());
        holder.tv_reference.setText(list.get(position).get("reference").toString());
        holder.tv_partname.setText(list.get(position).get("partname").toString());
        holder.tv_partxh.setText(list.get(position).get("partxh").toString());
        holder.tv_partbrand.setText(list.get(position).get("partbrand").toString());
        holder.tv_partprice.setText(list.get(position).get("partprice").toString());

        // 根据isSelected来设置checkbox的选中状况
        holder.ckb_parts.setChecked(isSelected.get(position));

        return convertView;
    }

    class ViewHolder{
        TextView tv_partid,tv_partname,tv_partxh,tv_partbrand,tv_partprice,tv_reference;
        CheckBox ckb_parts;
    }

}

