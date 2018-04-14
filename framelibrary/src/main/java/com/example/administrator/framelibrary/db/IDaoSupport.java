package com.example.administrator.framelibrary.db;

import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.framelibrary.db.curd.QuerySupport;

import java.util.List;

/**
 * Email: 2185134304@qq.com
 * Created by JackChen 2018/4/6 19:41
 * Version 1.0
 * Params:
 * Description:  接口
*/

public interface IDaoSupport<T> {


    /**
     * 创建表
     */
    void init(SQLiteDatabase sqLiteDatabase , Class<T> clazz) ;
    /**
     * 插入数据
     */
    public long insert(T t) ;

    /**
     * 批量插入，用于检测性能
     */
    public void insert(List<T> datas) ;

    /**
     * 获取专门查询的支持类
     */
    QuerySupport<T> querySupport();

    /**
     * 按照语句查询
     */

    /**
     * 删除
     */
    int delete(String whereClause , String... whereArgs) ;


}
