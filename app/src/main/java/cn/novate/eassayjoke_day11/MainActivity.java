package cn.novate.eassayjoke_day11;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.administrator.framelibrary.BaseSkinActivity;
import com.example.administrator.framelibrary.HttpCallBack;
import com.example.administrator.framelibrary.db.DaoSupportFactory;
import com.example.administrator.framelibrary.db.IDaoSupport;
import com.hc.baselibrary.http.HttpUtils;

import java.util.List;

import cn.novate.eassayjoke_day11.mode.DiscoverListResult;

public class MainActivity extends BaseSkinActivity {


    @Override
    protected void initData() {
//        IDaoSupport<Person> daoSupport = DaoSupportFactory.getFactory().getDao(Person.class);
//        List<Person> persons = daoSupport.querySupport().
//                selection("age = ?").selectionArgs("23").query() ;


        // url地址和参数 一定要放到 jni中，否则会被别人反编译
        // 所以为了安全性，就只能用 jni 把url地址和参数变成 .so库
        HttpUtils.with(this).url("http://is.snssdk.com/2/essay/discovery/v3/")
                .cache(true) // true表示 需要读取缓存
                .addParam("iid","6152551759").addParam("aid","7").execute(

            new HttpCallBack<DiscoverListResult>() {
            @Override
            public void onSuccess(DiscoverListResult result) {
                // 请求网络成功会调用这个方法，对于某些接口需要读取缓存的，那么请自带缓存的标识，就是上边的cache(true)
                Log.e("TAG" , "result -> " + result) ;
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    protected void initView() {
        //初始化权限
        initPermission();
    }


    /**
     * 初始化权限事件
     */
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }


    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }
}
