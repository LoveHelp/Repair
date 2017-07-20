package com.xianyi.chen.repair;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaojiaActivity extends AppCompatActivity {

    private ListView lvPart;
    private String spec = UserModel.myhost+"GetPartList.php";//首次加载页面初始化数据
    private String data = "";
    private ArrayList<HashMap<String,Object>> datalist=null;
    HashMap<Object, Boolean> hashMap = null;
    private Adapter_Baojia adapter_baojia;
    private Button btnSurePart,btnReturnPart;
    private String bugid="";
    private Integer version=1;

    private DbManager dbm;
    private List<PartModel> partModels=null;
    private List<PartModel> partModels_type=null;
    private Handler handler;
    private String result="";
    private String bugtype="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baojia);

        handler=new Handler();

        //bugid = getIntent().getStringExtra("bugid");
        Bundle bundle = getIntent().getExtras();
        bugid = bundle.getString("bugid");
        bugtype = bundle.getString("bugtype");
        data = "bugtype="+bugtype;

        InitControl();//初始化控件
        InitControlEvent();//初始化控件事件

        GetPartList();//获取配件列表

        ShowCheckBoxListView();//获取选中配件

    }

    /** 初始化控件 **/
    private void InitControl(){
        lvPart=(ListView)findViewById(R.id.lvPart);
        btnSurePart=(Button)findViewById(R.id.btnSurePart);
        btnReturnPart=(Button)findViewById(R.id.btnReturnPart);
    }

    /** 初始化控件事件 **/
    private void InitControlEvent(){
        //返回
        btnReturnPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaojiaActivity.this,UnOfferDetailActivity.class);
                intent.putExtra("bugid",bugid);
                setResult(-1,intent);//返回
                finish();
            }
        });
        //选定配件
        btnSurePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HashMap<Object, Boolean> hashMap = adapter_baojia.hashMap;
                Intent intent = new Intent(BaojiaActivity.this,UnOfferDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("map",hashMap);
                intent.putExtras(bundle);
                intent.putExtra("bugid",bugid);
                setResult(0,intent);//选择配件0
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取配件列表
     * **/
    private void GetPartList(){

        new Thread() {
            @Override
            public void run() {
                String content=PostParamTools.postGetInfo(UserModel.myhost+"readversion.php","");
                if(content == null){
                    Toast.makeText(BaojiaActivity.this, "网络链接超时!",
                            Toast.LENGTH_LONG).show();
                }else if (!content.equals("")) {
                    version=Integer.parseInt(content);
                }
                else {
                    version=1;
                }
                dbm = x.getDb(initDb(version));

                List<PartModel> list = query();
                if (list==null || list.size()<=0) {
                    ReturnUpdateData();//获取网络请求，更新本地数据
                }else {
                    handler.post(runnable);
                }
            }
        }.start();

    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            //List<PartModel> partModelList=partModels;//query();
            if(partModels!=null && partModels.size()>0){
                datalist = new ArrayList<HashMap<String, Object>>();
                for (PartModel pm : partModels) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("partid", pm.getPartid());
                    item.put("partname", pm.getPartname());
                    //item.put("bugtype",pm.getBugtype());
                    item.put("partxh", pm.getPartxh());
                    item.put("partbrand", pm.getPartbrand());
                    item.put("partprice", pm.getPartprice());
                    item.put("reference", pm.getReference());
                    datalist.add(item);
                }
                btnSurePart.setVisibility(View.VISIBLE);
                adapter_baojia=new Adapter_Baojia(BaojiaActivity.this, datalist);
                lvPart.setAdapter(adapter_baojia);
                adapter_baojia.notifyDataSetChanged();
            }else {
                lvPart.setAdapter(null);
                btnSurePart.setVisibility(View.GONE);
            }

        }
    };

    /**
     * 异步 获取网络请求，更新本地数据
     * **/
    private void ReturnUpdateData(){
        new Thread(){
            @Override
            public void run(){
                result=PostParamTools.postGetInfo(spec,data);
                if(result == null){
                    Toast.makeText(BaojiaActivity.this, "网络链接超时!",
                            Toast.LENGTH_LONG).show();
                }else {
                    BindData(result);
                    handler.post(runnable);
                }

            }
        }.start();
    }

    /**
     * 根据返回的网络请求数据，更新本地数据
     * **/
    private void BindData(String result){
        if (result != null && !result.equals("")) {
            //如果result包含+则为大故障报价
            // 如果获取的result数据不为空，那么对其进行JSON解析。并显示在手机屏幕上。
            try{
                partModels=new ArrayList<PartModel>();
                JSONArray jsonArray_part=new JSONArray(result);
                for (int i=0;i<jsonArray_part.length();i++){
                    JSONObject jsonObject1 = jsonArray_part.getJSONObject(i);
                    PartModel partModel=new PartModel();
                    partModel.setPartid(jsonObject1.getInt("partid"));
                    partModel.setPartname(jsonObject1.getString("partname"));
                    partModel.setBugtype(jsonObject1.getString("bugtype"));
                    partModel.setPartxh(jsonObject1.getString("partxh"));
                    partModel.setPartbrand(jsonObject1.getString("partbrand"));
                    partModel.setPartprice(Float.parseFloat(jsonObject1.getString("partprice")));
                    partModel.setReference(jsonObject1.getString("reference"));
                    partModels.add(partModel);
                }

                try{
                    dbm.delete(PartModel.class);
                    dbm.saveOrUpdate(partModels);
                    partModels_type = dbm.selector(PartModel.class).where("buttype","=",bugtype).findAll();
                }catch (DbException e){e.printStackTrace();}

            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            Toast.makeText(BaojiaActivity.this, "请求数据失败...",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BaojiaActivity.this,UnOfferDetailActivity.class);
            intent.putExtra("bugid",bugid);
            setResult(-1,intent);//返回
            finish();
        }
    }

    /**
     * 查询本地数据
     * **/
    private List<PartModel> query() {
        try {
            partModels=new ArrayList<PartModel>();
            partModels = dbm.findAll(PartModel.class);
            partModels_type=new ArrayList<PartModel>();

            if(partModels!=null && partModels.size()>0){
                partModels_type = dbm.selector(PartModel.class).where("buttype","=",bugtype).findAll();
            }
            return partModels;
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 DbManager.DaoConfig 对象创建数据库
     * **/
    protected DbManager.DaoConfig initDb(final int version){
        //本地数据的初始化
        final DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName(UserModel.DBNAME) //设置数据库名
                .setDbVersion(version) //设置数据库版本,每次启动应用时将会检查该版本号,
                //发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true)//设置是否开启事务,默认为false关闭事务
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {

                    }
                })//设置数据库创建时的Listener
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        Integer o = oldVersion;
                        Integer n = newVersion;
                        if(oldVersion != newVersion){
                            ReturnUpdateData();//获取网络请求，更新本地数据
                        }
                    }
                });
        //设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下

        return daoConfig;
    }

    // 获取选中的配件
    public void ShowCheckBoxListView() {
        hashMap = new HashMap<Object, Boolean>();
        lvPart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Adapter_Baojia.ViewHolder holder = (Adapter_Baojia.ViewHolder) view.getTag();
                holder.ckb_parts.toggle();// 改变CheckBox的状态
                boolean isChecked = holder.ckb_parts.isChecked();
                Adapter_Baojia.isSelected.put(position, isChecked);// 将CheckBox的选中状况记录下来
                String partid = holder.tv_partid.getText().toString();
                String partname = holder.tv_partname.getText().toString();
                String partprice = holder.tv_partprice.getText().toString();
                String reference = holder.tv_reference.getText().toString();
                Object key=partid+"|"+partname+"|"+partprice+"|"+reference;
                if(isChecked){
                    hashMap.put(key,isChecked);// 将CheckBox的选中状况记录下来
                }else {
                    hashMap.remove(key);
                }

            }
        });

    }

}
