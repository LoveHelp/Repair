package com.xianyi.chen.repair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnOfferDetailActivity extends AppCompatActivity {

    private TextView tvBugType,tvBugAddress,tvBugDescrip,tvRepairDescrip,tvRepairReason,tvRepairSolution;
    private Button btnChakan,btnSubmit,btnSelPart,btnWritePart,btnPaichaPic;

    private String spec = UserModel.myhost+"getrepairbyid.php";//首次加载页面初始化数据
    private String data = "";//flag=0首次加载；flag=1提交修改数据
    private String bugtypename,bugaddr,bugfindphoto,bugfinddescrip,repairdescrip,repairreason,repairsolution,repaircheckphoto;
    private String bugid = "";
    private ArrayList<HashMap<String,Object>> dlist_part=null;
    private Adapter_SelBaojia adapter_selBaojia;
    private ListView lvPart;
    private TableRow trSelPart,trWritePart;
    private EditText et_WPartName,et_WPartPrice,et_WPartNum;
    private boolean zdy=false;

    private Intent intent;
    String filename;
    File file_go;
    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ListView imglist;
    private ImgAdapter imgAdapter;
    private Map filemap;
    private String results;

    // 获取sd卡根目录地址,并创建图片父目录文件对象和文件的对象;
    String file_str = Environment.getExternalStorageDirectory().getPath();

    private String result="";
    public String roleid=UserModel.getroleid();
    private Integer upd=0;//1报价提交后的修改;0报价

    private TextView tv_title;//头部标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_offer_detail);

        bugid = getIntent().getStringExtra("bugid");
        upd = getIntent().getIntExtra("upd",0);
        data="flag=0&bugid=" + bugid;

        imgAdapter=new ImgAdapter(UnOfferDetailActivity.this);

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        DetailInfo();

    }

    /** 初始化控件 **/
    private void InitControl(){

        tvBugType=(TextView)findViewById(R.id.tvBugType);
        tvBugAddress=(TextView)findViewById(R.id.tvBugAddress);
        tvBugDescrip=(TextView)findViewById(R.id.tvBugDescrip);
        tvRepairDescrip=(TextView)findViewById(R.id.tvRepairDescrip);
        tvRepairReason=(TextView)findViewById(R.id.tvRepairReason);
        tvRepairSolution=(TextView)findViewById(R.id.tvRepairSolution);

        trSelPart=(TableRow) findViewById(R.id.trSelPart);
        trWritePart=(TableRow) findViewById(R.id.trWritePart);

        et_WPartName=(EditText)findViewById(R.id.et_WPartName);
        et_WPartPrice=(EditText)findViewById(R.id.et_WPartPrice);
        et_WPartNum=(EditText)findViewById(R.id.et_WPartNum);

        btnChakan = (Button) findViewById(R.id.btnChakan);
        btnPaichaPic = (Button) findViewById(R.id.btnPaichaPic);
        btnSelPart=(Button)findViewById(R.id.btnSelPart);
        btnWritePart=(Button)findViewById(R.id.btnWritePart);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);

        imglist= (ListView) findViewById(R.id.imglist);
        lvPart=(ListView)findViewById(R.id.lvPart);

        tv_title=(TextView)findViewById(R.id.tv_title);
        if(upd==1){
            tv_title.setText("已报价故障详情");
        }else {
            tv_title.setText("待报价故障详情");
        }

    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){

        //查看故障照片
        btnChakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnOfferDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",bugfindphoto);
                startActivity(intent);
            }
        });
        //确认故障照片
        btnPaichaPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnOfferDetailActivity.this,BigPicActivity.class);
                intent.putExtra("photo",repaircheckphoto);
                startActivity(intent);
            }
        });

        //选择配件
        btnSelPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tvBugType.getText().toString();
                zdy=false;//选择配件
                trWritePart.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(UnOfferDetailActivity.this,BaojiaActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("bugid",bugid);
                String bugtype = tvBugType.getText().toString();
                bundle.putString("bugtype",bugtype);
                intent.putExtras(bundle);
                //intent.putExtra("bugid",bugid);
                //startActivity(intent);
                startActivityForResult(intent,0);
            }
        });
        //自定义配件
        btnWritePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zdy=true;//自定义配件
                trSelPart.setVisibility(View.INVISIBLE);
                lvPart.setAdapter(null);
                setListViewHeightBasedOnChildren(lvPart);
                trWritePart.setVisibility(View.VISIBLE);
            }
        });

        //提交
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SureBug();
            }
        });

    }

    /**
     * 选中配件
     * **/
    private void GetSelPartList(Intent data){
        Bundle bundle = data.getExtras();
        HashMap<Object, Boolean> hashMap = (HashMap<Object, Boolean>) bundle.get("map");

        try{
            if (hashMap!=null) {
                dlist_part = new ArrayList<HashMap<String, Object>>();
                String keys = "";
                Iterator iter = hashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    //keys+=key.toString()+" ; ";
                    String[] arry_part = key.toString().split("[|]");
                    String partid = arry_part[0];
                    String partname = arry_part[1];
                    String partprice = arry_part[2];
                    String reference = arry_part[3];

                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("partid", partid);
                    item.put("partname", partname);
                    item.put("partprice", partprice);
                    item.put("reference", reference);
                    item.put("partnum", "1");
                    dlist_part.add(item);
                }
                UnOfferDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setSelPartList();
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * 上传报价文件
     * **/
    public void uploadfile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);//上传文件1
    }
    /**
     * 然后选择文件后调用
     * **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==0){//选择配件
            GetSelPartList(data);
        }else if(requestCode==1){
            if (resultCode == Activity.RESULT_OK) {
                 String path=null;
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                    path = uri.getPath();
                    //Toast.makeText(this,uri.getPath()+"11111",Toast.LENGTH_SHORT).show();
                }else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    PostParamTools postParamTools=new PostParamTools();
                    path = postParamTools.getPath(this, uri);
                    //Toast.makeText(this,path.toString(),Toast.LENGTH_SHORT).show();
                } else {//4.4以下系统调用方法
                    path = getRealPathFromURI(uri);
                    //Toast.makeText(UnOfferDetailActivity.this, getRealPathFromURI(uri)+"222222", Toast.LENGTH_SHORT).show();
                }
                addData(path);
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public void delData(int pos)
    {
        list.remove(pos);
        imglist.setAdapter(imgAdapter);
        setListViewHeightBasedOnChildren(imglist);
    }
    public void addData(String path)
    {
        Map<String, Object> map;
        map = new HashMap<String, Object>();
        // map.put("filename", filename);
        map.put("filepath", path);
        list.add(map);
        imglist.setAdapter(imgAdapter);
        setListViewHeightBasedOnChildren(imglist);

    }    /**存放控件*/
    static  class ViewHolder{
        public TextView itemimgfilename;
        public Button   delbtn;
    }
    private class ImgAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        //得到一个LayoutInfalter对象用来导入布局
        /**构造函数*/
        public ImgAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /***/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            // Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.findbugimgitem,null);
                holder = new ViewHolder();
                /**得到各个控件的对象*/
                holder.itemimgfilename = (TextView) convertView.findViewById(R.id.imgfile);
                holder.delbtn = (Button) convertView.findViewById(R.id.delimgbtn);
                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /**设置TextView显示的内容，即我们存放在动态数组中的数据*/

            //holder.itemimgfilename.setText((String)list.get(position).get("filename"));
            holder.itemimgfilename.setText(PostParamTools.getFileName((String)list.get(position).get("filepath")));
            /**为Button添加点击事件*/
            holder.delbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(FindbugActivity.this,"删除图片吧",Toast.LENGTH_SHORT).show();
                    delData(position);
                    Toast.makeText(UnOfferDetailActivity.this, "还有"+list.size()+"条数据。",Toast.LENGTH_SHORT).show() ;
                }
            });
            return convertView;
        }
    }

    private void DetailInfo(){
        new Thread(){
            @Override
            public void run(){
                result = PostParamTools.postGetInfo(spec, data);//显示列表
                if(result==null){
                    handler.sendEmptyMessage(0);
                }else if (!result.equals("null") && !result.equals("")) {
                    //如果result包含+则为大故障报价
                    // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
                    handler.sendEmptyMessage(1);
                } else{
                    handler.sendEmptyMessage(2);
                }

            }
        }.start();

    }

    private Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(UnOfferDetailActivity.this, "网络链接超时!", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    InitUpdateData(result);
                    break;
                case 2:
                    Toast.makeText(UnOfferDetailActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 报价/已报价修改：绑定初始数据
     * **/
    private void InitUpdateData(String result) {
        try{
            String[] temp = result.split("\\+");
            String buginfo=temp[0].toString();
            JSONArray jsonArray=new JSONArray(buginfo);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            bugtypename = jsonObject.getString("bugtype");
            bugaddr=jsonObject.getString("bugaddr");
            bugfindphoto=jsonObject.getString("bugfindphoto");
            bugfinddescrip=jsonObject.getString("bugfinddescrip");
            repairdescrip=jsonObject.getString("repairdescrip");
            repaircheckphoto=jsonObject.getString("repaircheckphoto");
            repairreason=jsonObject.getString("repairreason");
            repairsolution=jsonObject.getString("repairsolution");
            //更新界面
            tvBugType.setText(bugtypename);//故障类型
            tvBugAddress.setText(bugaddr);//故障地址
            tvBugDescrip.setText(bugfinddescrip);//故障描述
            tvRepairDescrip.setText(repairdescrip);//故障描述
            tvRepairReason.setText(repairreason);
            tvRepairSolution.setText(repairsolution);

            if(upd==1){
                if(temp.length==2){
                    JSONArray jsonArray_part=new JSONArray(temp[1]);
                    dlist_part = new ArrayList<HashMap<String, Object>>();
                    for (int i=0;i<jsonArray_part.length();i++){
                        JSONObject jsonObject1 = jsonArray_part.getJSONObject(i);
                        HashMap<String, Object> item = new HashMap<String, Object>();
                        item.put("partid", jsonObject1.getInt("partid"));
                        item.put("partname", jsonObject1.getString("partname"));
                        item.put("partprice", jsonObject1.getString("partprice"));
                        item.put("reference", jsonObject1.getString("reference"));
                        item.put("partnum", jsonObject1.getString("partnum"));
                        dlist_part.add(item);
                    }
                }

            }

            setSelPartList();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setSelPartList(){
        if (dlist_part!=null){
            adapter_selBaojia=new Adapter_SelBaojia(UnOfferDetailActivity.this, dlist_part);
            lvPart.setAdapter(adapter_selBaojia);
            setListViewHeightBasedOnChildren(lvPart);
            trSelPart.setVisibility(View.VISIBLE);
            adapter_selBaojia.notifyDataSetChanged();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 100;
        listView.setLayoutParams(params);
    }

    /**
     * 报价，提交报价
     * **/
    private void SureBug()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Float charge=Float.parseFloat("0");
                    String sqllist="";
                    spec = UserModel.myhost+"saverepairandoffer.php";
                    if(adapter_selBaojia!=null && !zdy) {//选择配件
                        HashMap<Integer, String> hm=adapter_selBaojia.hm;
                        ArrayList<HashMap<String, Object>> dlist_selparts = dlist_part;
                        if (dlist_selparts != null && dlist_selparts.size()>0) {
                            HashMap<String, Object> item;
                            sqllist="insert into offer(bugid,partid,num,price) values";
                            for (int i=0;i<dlist_selparts.size();i++)
                            {
                                item = new HashMap<String, Object>();
                                item=dlist_selparts.get(i);

                                String partid = item.get("partid").toString();
                                String partprice=item.get("partprice").toString();//单价
                                String num=hm.get(i);
                                Float zj=Float.parseFloat(partprice)*Float.parseFloat(num);
                                charge = charge + zj;

                                //sqllist+="("+bugid+","+partid+","+num+","+zj+"),";
                                sqllist+="("+bugid+","+partid+","+num+","+partprice+"),";
                            }
                            sqllist=sqllist.substring(0,sqllist.length()-1);
                        }
                    }else if(zdy){//自定义配件
                        String partname=et_WPartName.getText().toString();
                        Float partprice=Float.parseFloat(et_WPartPrice.getText().toString());
                        Integer partnum=Integer.parseInt(et_WPartNum.getText().toString());
                        charge=partprice*partnum;

                        //sqllist="insert into offer(bugid,partid,num,price,partname) values("+bugid+",0,"+partnum+","+charge+",'"+partname+"')";
                        sqllist="insert into offer(bugid,partid,num,price,partname) values("+bugid+",0,"+partnum+","+partprice+",'"+partname+"')";

                    }
                    String baojiafile = "";
                    filemap=new <String,File>HashMap();
                    for(int i=0;i<list.size();i++)
                    {
                        String myfilepath=list.get(i).get("filepath").toString();
                        file_go=new File(myfilepath);// /storage/emulated/0/logcat/crash-2017-07-05-15-39-26-1499240366215.log
                        filemap.put("file"+Integer.toString(i),file_go );
                        baojiafile=baojiafile+PostParamTools.getFileName(myfilepath);
                        if(i!=list.size()-1) baojiafile+=",";
                    }
                    Map <String, String>  parammap=new HashMap <String, String> ();
                    parammap.put("bugid",  bugid);
                    parammap.put("charge",  charge.toString());
                    parammap.put("chargefile",  URLEncoder.encode(baojiafile, "UTF-8"));
                    parammap.put("sqllist",  URLEncoder.encode(sqllist, "UTF-8"));
                    results= PostParamTools.post(spec, parammap, filemap);
                    UnOfferDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (results.equals("1")) {
                                //提交成功，跳转页面
                                Toast.makeText(UnOfferDetailActivity.this, "报价成功", Toast.LENGTH_SHORT).show();
                                intent = new Intent(UnOfferDetailActivity.this,UnOfferListActivity.class);
                                intent.putExtra("upd",upd);
                                intent.putExtra("isindex",1);//当返回到列表页时，点击返回按钮是否返回到首页：1是；0否
                                startActivity(intent);
                            } else {
                                //提交失败
                                Toast.makeText(UnOfferDetailActivity.this, "请求数据失败...", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * Created by Administrator on 2016/9/1 0001.
     */
    private class Adapter_SelBaojia extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, Object>> list;
        Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();
        public HashMap<Object,Boolean> hashMap=new HashMap<Object, Boolean>();
        public ArrayList<HashMap<String,Object>> dlist_selpart= new ArrayList<HashMap<String, Object>>();
        //定义一个HashMap，用来存放EditText的值，Key是position
        public HashMap<Integer, String> hm = new HashMap<Integer, String>();

        public Adapter_SelBaojia(Context context, ArrayList<HashMap<String, Object>> list) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView  = LayoutInflater.from(context).inflate(R.layout.selpart, null);
                holder = new ViewHolder();

                //holder.ckb_parts = (CheckBox) convertView.findViewById(R.id.ckb_parts);
                holder.btnDelPart = (Button) convertView.findViewById(R.id.btnDelPart);
                holder.tv_partid = (TextView) convertView.findViewById(R.id.tv_partid);
                holder.tv_reference = (TextView) convertView.findViewById(R.id.tv_reference);
                holder.tv_partname = (TextView) convertView.findViewById(R.id.tv_partname);
                holder.tv_partprice = (TextView) convertView.findViewById(R.id.tv_partprice);
                holder.et_Money = (EditText) convertView.findViewById(R.id.et_Money);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_partid.setText(list.get(position).get("partid").toString());
            holder.tv_partname.setText(list.get(position).get("partname").toString());
            holder.tv_partprice.setText(list.get(position).get("partprice").toString()+" 元");
            if(upd==1){
                holder.et_Money.setText(list.get(position).get("partnum").toString());
                hm.put(position,list.get(position).get("partnum").toString());
            }else {
                holder.et_Money.setText("1");
                hm.put(position,"1");
            }
            holder.tv_reference.setText(list.get(position).get("reference").toString());


            holder.et_Money.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    hm.put(position, editable.toString());

                    //String num = holder.et_Money.getText().toString();
                    //Integer partid = Integer.parseInt(list.get(position).get("partid").toString());
                    //Float partprice = Float.parseFloat(list.get(position).get("partprice").toString());
                    //HashMap<String, Object> item = new HashMap<String, Object>();
                    //item.put("partid", partid);
                    //item.put("partprice", partprice);
                    //item.put("num", num);
                    //dlist_selpart.add(item);
                }
            });

            //如果hashMap不为空，就设置editText
            if(hm.get(position) != null){
                holder.et_Money.setText(hm.get(position));
            }

            //删除
            holder.btnDelPart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfo(position);
                }

            });

            return convertView;
        }

        /**
         * listview item 删除
         * **/
        public void showInfo(final int position) {
            list.remove(position);
            // 通过程序我们知道删除了，但是怎么刷新ListView呢？
            // 只需要重新设置一下adapter
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lvPart);
        }

        class ViewHolder{
            TextView tv_partid,tv_partname,tv_partprice,tv_reference;
            EditText et_Money;
            Button btnDelPart;
            //CheckBox ckb_parts;
        }

    }


}
