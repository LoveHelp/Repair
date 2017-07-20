package com.xianyi.chen.repair;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ListView适配器
 * Created by Administrator on 2016/8/23 0023.
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, Object>> list;
    public MyAdapter(Context context, ArrayList<HashMap<String, Object>> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {

//        ImageOptions imageOptions = new ImageOptions.Builder()
//                .setSize(DensityUtil.dip2px(60), DensityUtil.dip2px(60))//图片大小
//                .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
//                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                .setLoadingDrawableId(R.drawable.bug)//加载中默认显示图片
//                .setFailureDrawableId(R.drawable.bug)//加载失败后默认显示图片
//                .build();

        final ViewHolder holder;
        if (convertView == null) {
            convertView  = LayoutInflater.from(context).inflate(R.layout.item, null);
            holder = new ViewHolder();


            holder.tvBugid = (TextView) convertView.findViewById(R.id.tvBugid);
            //holder.imgBugPhoto = (ImageView) convertView.findViewById(R.id.imgBugPhoto);
            holder.tvBugType = (TextView) convertView.findViewById(R.id.tvBugType);
            holder.tvBugDescription = (TextView) convertView.findViewById(R.id.tvBugDescription);
            holder.tvBugAddr = (TextView) convertView.findViewById(R.id.tvBugAddr);
            holder.tvBugTime = (TextView) convertView.findViewById(R.id.tvBugTime);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.imgBugPhoto.setImageResource(R.drawable.bug);
        holder.tvBugid.setText(list.get(position).get("bugid").toString());
        holder.tvBugType.setText("故障类型："+list.get(position).get("bugtype").toString());
        holder.tvBugDescription.setText("故障现象："+list.get(position).get("bugfinddescrip").toString());
        holder.tvBugAddr.setText("故障地址："+list.get(position).get("bugaddr").toString());

        String state = list.get(position).get("state").toString();
        String showState = "";
        String strTime = "";
        switch (state){
            case "1":
                strTime = list.get(position).get("bugsendtime").toString();
                showState="分派时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "2":
                strTime = list.get(position).get("repairsendtime").toString();
                showState="派单时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "3":
                strTime = list.get(position).get("repairchecktime").toString();
                showState="排查时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "4":
                strTime = list.get(position).get("chargetime").toString();
                showState="报价时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "41":
                strTime = list.get(position).get("checktime_zr").toString();
                showState="报价时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "5":
                String type = list.get(position).get("type").toString();
                if(type.equals("1")){
                    strTime = list.get(position).get("checktime").toString();
                    showState="审核通过时间：" + PostParamTools.getDateFormat(strTime);
                }else {
                    strTime = list.get(position).get("repairchecktime").toString();
                    showState="排查时间：" + PostParamTools.getDateFormat(strTime);
                }
                break;
            case "6":
                strTime = list.get(position).get("completetime").toString();
                showState="完工时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "61":
                strTime = list.get(position).get("compeletchecktime_zr").toString();
                showState="完工时间：" + PostParamTools.getDateFormat(strTime);
                break;
            case "7":
                strTime = list.get(position).get("compeletchecktime").toString();
                showState="完工确认时间：" + PostParamTools.getDateFormat(strTime);
                break;
            default:
                strTime = list.get(position).get("bugfindtime").toString();
                showState="发现时间：" + PostParamTools.getDateFormat(strTime);
                break;
        }
        holder.tvBugTime.setText(showState);

        //String wurl= UserModel.myhost + "upload/"+list.get(position).get("bugfindphoto").toString();
        //x.image().bind(holder.imgBugPhoto, wurl,imageOptions);

        return convertView;
    }



    class ViewHolder{
        TextView tvBugid,tvBugDescription,tvBugTime,tvBugAddr,tvBugType;
        //ImageView imgBugPhoto;
    }

}
