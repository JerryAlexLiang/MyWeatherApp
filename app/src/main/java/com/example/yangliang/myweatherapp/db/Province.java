package com.example.yangliang.myweatherapp.db;

import org.litepal.crud.DataSupport;

/**
 * 创建日期：2017/9/25 on 上午9:41
 * 描述:创建数据库的表结构，分别存放省市县的数据信息，对应到实体类中，创建三个实体类
 * LitePal用于对数据库进行操作，LitePal中的每个实体类都是继承自DataSupport类的。
 * 注意：数据库和表会在首次执行任意数据库操作的时候自动创建。
 * 作者:yangliang
 */
public class Province extends DataSupport {

    //id为每个实体类都应该有的字段
    private int id;

    //记录省的名字
    private String provinceName;

    //记录省的代码
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
