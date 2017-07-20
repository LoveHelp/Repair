package com.xianyi.chen.repair;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016/9/6 0006.
 * 创建表 利用注解的方式进行表的创建
 */
@Table(name="part")
public class PartModel {
    @Column(name="partid",isId=true,autoGen = false)
    private int partid;
    //姓名
    @Column(name="partname")
    private String partname;
    //项目分类
    @Column(name="bugtype")
    private String bugtype;
    //配件型号
    @Column(name="partxh")
    private String partxh;
    //配件品牌
    @Column(name="partbrand")
    private String partbrand;
    //配件单价
    @Column(name="partprice")
    private Float partprice;
    //报价依据
    @Column(name="reference")
    private String reference;

    //配件id
    public int getPartid(){   return partid;    }
    public void setPartid(int partid){   this.partid=partid;    }

    //配件名称
    public String getPartname(){   return partname;    }
    public void setPartname(String partname){   this.partname=partname;    }

    //项目分类
    public String getBugtype(){   return bugtype;    }
    public void setBugtype(String bugtype){   this.bugtype=bugtype;    }

    //配件型号
    public String getPartxh(){   return partxh;    }
    public void setPartxh(String partxh){   this.partxh=partxh;    }

    //配件品牌
    public String getPartbrand(){   return partbrand;    }
    public void setPartbrand(String partbrand){   this.partbrand=partbrand;    }

    //配件单价
    public Float getPartprice(){   return partprice;    }
    public void setPartprice(Float partprice){   this.partprice=partprice;    }

    //报价依据
    public String getReference(){   return reference;    }
    public void setReference(String reference){   this.reference=reference;    }

    @Override
    public String toString() {
        return "PartModel [partid=" + partid + ", partname=" + partname + ", bugtype=" + bugtype
                + ", partxh=" + partxh + ", partbrand=" + partbrand + ", partprice=" + partprice + ", reference=" + reference + "]";
    }

}
