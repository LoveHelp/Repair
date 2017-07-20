package com.xianyi.chen.repair;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.xutils.*;
import org.xutils.BuildConfig;

/**
 * Created by Administrator on 2016/8/25 0025.
 **/
public class MyApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        ////创建默认的ImageLoader配置参数
        //ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs() //打印log信息
                .threadPoolSize(5)
                .memoryCache(new WeakMemoryCache())
                .build();

        ImageLoader.getInstance().init(configuration);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

    }
}
