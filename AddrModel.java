package com.xianyi.chen.repair;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2016-12-20 .
 */
@Table(name="equipmentrecord")
public class AddrModel {
    @Column(name="equipmentid",isId=true,autoGen = false)
    private int equipmentid;
    //项目分类
    @Column(name="equipmenttype")
    private String equipmenttype;
    //设备名称
    @Column(name="equipmentname")
    private String equipmentname;
    //设备所在地址
    @Column(name="equipmentaddr")
    private String equipmentaddr;

    //id
    public int getEquipmentid(){   return equipmentid;    }
    public void setEquipmentid(int equipmentid){   this.equipmentid=equipmentid;    }

    //项目分类
    public String getEquipmenttype(){   return equipmenttype;    }
    public void setEquipmenttype(String equipmenttype){   this.equipmenttype=equipmenttype;    }

    //设备名称
    public String getEquipmentname(){   return equipmentname;    }
    public void setEquipmentname(String equipmentname){   this.equipmentname=equipmentname;    }

    //设备所在地址
    public String getEquipmentaddr(){   return equipmentaddr;    }
    public void setEquipmentaddr(String equipmentaddr){   this.equipmentaddr=equipmentaddr;    }

    @Override
    public String toString() {
        return "AddrModel [equipmentid=" + equipmentid + ", equipmenttype=" + equipmenttype + ", equipmentname=" + equipmentname
                + ", equipmentaddr=" + equipmentaddr  + "]";
    }

}
