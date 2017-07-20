package com.xianyi.chen.repair;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016-12-21 .
 */
@Table(name="template")
public class TemplateModel {
    @Column(name="tempid",isId=true,autoGen = false)
    private int tempid;
    //模板类型
    @Column(name="temptype")
    private String temptype;
    //内容类型：故障现象；故障原因；解决办法
    @Column(name="contenttype")
    private String contenttype;
    //模板内容
    @Column(name="tempcontent")
    private String tempcontent;

    //id
    public int getTempid(){   return tempid;    }
    public void setTempid(int tempid){   this.tempid=tempid;    }

    //项目分类
    public String getTemptype(){   return temptype;    }
    public void setTemptype(String temptype){   this.temptype=temptype;    }

    //设备名称
    public String getContenttype(){   return contenttype;    }
    public void setContenttype(String contenttype){   this.contenttype=contenttype;    }

    //设备所在地址
    public String getTempcontent(){   return tempcontent;    }
    public void setTempcontent(String tempcontent){   this.tempcontent=tempcontent;    }

    @Override
    public String toString() {
        return "TemplateModel [tempid=" + tempid + ", temptype=" + temptype + ", contenttype=" + contenttype
                + ", tempcontent=" + tempcontent  + "]";
    }

}
