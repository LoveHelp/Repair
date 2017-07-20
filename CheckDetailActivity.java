package com.xianyi.chen.repair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29 0029.
 **/
public class CheckDetailActivity extends AppCompatActivity{
    private Button agree_btn;
    private Button disagree_btn;
    private Intent intent;
    private ListView listView;
    private List<HashMap<String, Object>> partsList = null;
    private PartDetailAdapter partDetailAdapter = null;
    private Integer total = 0,basecharge=0;
    private String bugId;
    private int checkeuserid;//用户ID
    private Button downloadBtn;//下载附件按钮
    private String downloadUrl;
    public String roleid=UserModel.getroleid();

    private TextView tv_title;//头部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_detail);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        // 获取用户ID
        checkeuserid = Integer.parseInt(UserModel.getuserid());

        // 获取CheckActivity传递的bugId
        Intent getIntent = getIntent();
        bugId = getIntent.getStringExtra("bugId");

        setPartsDetail();

    }

    /** 初始化控件 **/
    private void InitControl(){
        downloadBtn = (Button) findViewById(R.id.download_file);
        listView = (ListView)findViewById(R.id.parts_detail);
        agree_btn = (Button)findViewById(R.id.agree);
        disagree_btn = (Button)findViewById(R.id.disagree);

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("核价");
    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
        // 下载附件
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download();
                    }
                }).start();
            }
        });
        //同意
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheckResult(1);
            }
        });
        //不同意
        disagree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCheckResult(0);
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
                    partDetailAdapter.notifyDataSetChanged();//发送消息通知ListView更新
                    // 获得报价总价
                    getTotalPrice();
                    break;
                case 1:
                    Toast.makeText(CheckDetailActivity.this, "核价成功！", Toast.LENGTH_SHORT).show();
                    intent = new Intent(CheckDetailActivity.this, CheckActivity.class);
                    Integer state=4;
                    if(roleid.equals("2")){
                        state=41;
                    }

                    intent.putExtra("state",state);//4甲方专人核价；41指挥中心核价
                    intent.putExtra("isindex",1);//当返回到列表页时，点击返回按钮是否返回到首页：1是；0否
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(CheckDetailActivity.this, "核价失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(CheckDetailActivity.this, "报价单已保存至：文件管理下的 baojiadan 文件夹下", Toast.LENGTH_SHORT).show();//msg.getData().getString("fileurl")
                    break;
                case 4:
                    TextView totalText1 = (TextView)findViewById(R.id.total);
                    TextView tv_basecharge1 = (TextView)findViewById(R.id.tv_basecharge);
                    totalText1.setText("总价：" + String.valueOf(total) + " 元");
                    tv_basecharge1.setText("基本费用：" + String.valueOf(basecharge) + " 元");
                    downloadUrl =  UserModel.FILE_PATH + msg.getData().getString("chargefile");
                    downloadBtn.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    TextView totalText = (TextView)findViewById(R.id.total);
                    TextView tv_basecharge = (TextView)findViewById(R.id.tv_basecharge);
                    totalText.setText("总价：" + String.valueOf(total) + " 元");
                    tv_basecharge.setText("基本费用：" + String.valueOf(basecharge) + " 元");
                    downloadBtn.setVisibility(View.GONE);
                    break;
                case 6:
                    Toast.makeText(CheckDetailActivity.this, "网络链接失败！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void setPartsDetail(){
        new Thread(){
            @Override
            public void run(){
                String getPartsListStr =  UserModel.myhost + "getPartsDetail.php";
                // 获取需更换配件列表
                String data = "bugid=" + bugId;
                String partsListsStr = PostParamTools.postGetInfo(getPartsListStr, data);
                if(partsListsStr == null){
                    handler.sendEmptyMessage(6);
                }else {
                    try {
                        partsList = new ArrayList<HashMap<String, Object>>();
                        if(!("null".equals(partsListsStr))){
                            JSONArray bugListsJSON = new JSONArray(partsListsStr);

                            for (int i = 0; i < bugListsJSON.length(); i++) {
                                JSONObject jsonObject = bugListsJSON.getJSONObject(i);
                                HashMap<String, Object> part = new HashMap<String, Object>();
                                part.put("num", jsonObject.getString("parts_num"));
                                part.put("partName", jsonObject.getString("part_name"));
                                part.put("partPrice", jsonObject.getString("parts_price"));
                                String reference = "";
                                if(!"null".equals(jsonObject.getString("reference"))){
                                    reference = jsonObject.getString("reference");
                                }
                                part.put("partDescription", reference);
                                partsList.add(part);
                            }
                        }

                        partDetailAdapter = new PartDetailAdapter(CheckDetailActivity.this, partsList);
                        CheckDetailActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(partDetailAdapter); // 重新设置ListView的数据适配器
                            }
                        });

                        handler.sendEmptyMessage(0);// 发送处理消息

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private void updateCheckResult(final int clickedBtn){
        new Thread(){
            @Override
            public void run() {
                String updateCheckResultStr =  UserModel.myhost + "updateCheckResult.php";

                int result = 41;
                if(roleid.equals("2")){
                    result = 5;
                }
                if(clickedBtn == 0){
                    result = 3;
                }

                String data = "bugid=" + bugId + "&checkeuserid=" + checkeuserid + "&state=" + result;
                // 更新核价结果
                String res = PostParamTools.postGetInfo(updateCheckResultStr, data);

                if(res == null){
                    handler.sendEmptyMessage(6);
                }else if("success".equals(res)){
                    handler.sendEmptyMessage(1);// 发送处理消息
                }else{
                    handler.sendEmptyMessage(2);// 发送处理消息
                }
            }
        }.start();
    }

    //下载具体操作
    private void download() {
        try {
            URL url = new URL(downloadUrl);
            //打开连接
            URLConnection conn = url.openConnection();
            //打开输入流
            InputStream is = conn.getInputStream();
            //获得长度
            //int contentLength = conn.getContentLength();
            //Log.e(TAG, "contentLength = " + contentLength);
            //创建文件夹 baojiadan，在存储卡下
            String dirName = Environment.getExternalStorageDirectory().getPath() + "/baojiadan";
            File file = new File(dirName);
            //不存在创建
            if (!file.exists()) {
                file.mkdirs();
            }
            //下载后的文件名
            String newFilename = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
            String fileName = dirName + "/" + newFilename;
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
            }
            //创建字节流
            byte[] bs = new byte[1024];
            int len;
            file1.createNewFile();
            OutputStream os = new FileOutputStream(fileName);
            //写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            //完成后关闭流
            //Log.e(TAG, "download-finish");
            os.close();
            is.close();
            Message message=new Message();
            message.what=3;
            Bundle bundle=new Bundle();
            bundle.putString("fileurl",dirName);
            message.setData(bundle);
            handler.sendMessage(message);
            //handler.sendEmptyMessage(3);// 发送处理消息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTotalPrice(){
        new Thread(){
            @Override
            public void run() {
                String getTotalPriceStr =  UserModel.myhost + "getTotalPrice.php";

                String data = "bugid=" + bugId;
                // 更新核价结果
                String res = PostParamTools.postGetInfo(getTotalPriceStr, data);
                if(res == null){
                    handler.sendEmptyMessage(6);
                }else {
                    try{
                        JSONArray jsonArray = new JSONArray(res);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        basecharge=jsonObject.getInt("charge");
                        total = jsonObject.getInt("charge")+jsonObject.getInt("bigcharge");
                        String chargefile = jsonObject.getString("chargefile");
                        if("null".equals(chargefile)|| chargefile.isEmpty()){
                            handler.sendEmptyMessage(5);// 发送处理消息
                        }else{
                            Message msg = new Message();
                            msg.what = 4;
                            Bundle bundle = new Bundle();
                            bundle.putString("chargefile", chargefile);
                            msg.setData(bundle);
                            handler.sendMessage(msg);// 发送处理消息
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
}
