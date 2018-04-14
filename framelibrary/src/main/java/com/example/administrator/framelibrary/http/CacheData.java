package com.example.administrator.framelibrary.http;

/**
 * Email: 2185134304@qq.com
 * Created by JackChen 2018/4/14 10:05
 * Version 1.0
 * Params:
 * Description:   缓存的实体类
*/
public class CacheData {

    // 请求的连接
    private String mUrlKey ;
    // 后台返回的Json
    private String mResultJson ;

    public CacheData(){}

    public CacheData(String urlKey , String resultJson){
        this.mUrlKey = urlKey ;
        this.mResultJson = resultJson ;
    }

    public String getResultJson() {
        return mResultJson;
    }
}
