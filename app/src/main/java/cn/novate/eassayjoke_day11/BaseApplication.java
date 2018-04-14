package cn.novate.eassayjoke_day11;

import com.alipay.euler.andfix.patch.PatchManager;
import com.example.administrator.framelibrary.http.OkHttpEngine;
import com.hc.baselibrary.http.HttpUtils;

import org.litepal.LitePalApplication;

/**
 * Email 240336124@qq.com
 * Created by Darren on 2017/2/17.
 * Version 1.0
 * Description:
 */
public class BaseApplication extends LitePalApplication {

    public static PatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();

        HttpUtils.init(new OkHttpEngine());

        // 设置全局异常捕捉类
        // ExceptionCrashHandler.getInstance().init(this);

        // 初始化阿里的热修复
        /*mPatchManager = new PatchManager(this);

        try {
            // 初始化版本，获取当前应用的版本
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            mPatchManager.init(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 加载之前的 apatch 包
        mPatchManager.loadPatch();*/

//        try {
//            // 很耗时  热启动和冷启动  2s   400 ms
//            FixDexManager fixDexManager = new FixDexManager(this);
//            // 加载所有修复的Dex包
//            fixDexManager.loadFixDex();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
