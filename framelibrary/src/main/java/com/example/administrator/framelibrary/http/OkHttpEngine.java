package com.example.administrator.framelibrary.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.framelibrary.db.DaoSupport;
import com.example.administrator.framelibrary.db.DaoSupportFactory;
import com.example.administrator.framelibrary.db.IDaoSupport;
import com.example.administrator.framelibrary.db.curd.QuerySupport;
import com.hc.baselibrary.http.EngineCallBack;
import com.hc.baselibrary.http.HttpUtils;
import com.hc.baselibrary.http.IHttpEngine;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Email: 2185134304@qq.com
 * Created by JackChen 2018/4/6 12:29
 * Version 1.0
 * Params:
 * Description:   我们自己默认的网络请求  -->  OkHttpEngine
*/
public class OkHttpEngine implements IHttpEngine {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public void post(boolean cache , Context context, String url, Map<String, Object> params, final EngineCallBack callBack) {

        final String jointUrl = HttpUtils.jointParams(url,params);  //打印
        Log.e("Post请求路径：",jointUrl);

        // 了解 Okhhtp
        RequestBody requestBody = appendBody(params);
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(requestBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callBack.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 这个 两个回掉方法都不是在主线程中
                        String result = response.body().string();
                        Log.e("Post返回结果：",jointUrl);
                        callBack.onSuccess(result);
                    }
                }
        );
    }

    /**
     * 组装post请求参数body
     */
    protected RequestBody appendBody(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    // 添加参数
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addFormDataPart(key, params.get(key) + "");
                Object value = params.get(key);
                if (value instanceof File) {
                    // 处理文件 --> Object File
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(guessMimeType(file
                                    .getAbsolutePath())), file));
                } else if (value instanceof List) {
                    // 代表提交的是 List集合
                    try {
                        List<File> listFiles = (List<File>) value;
                        for (int i = 0; i < listFiles.size(); i++) {
                            // 获取文件
                            File file = listFiles.get(i);
                            builder.addFormDataPart(key + i, file.getName(), RequestBody
                                    .create(MediaType.parse(guessMimeType(file
                                            .getAbsolutePath())), file));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    builder.addFormDataPart(key, value + "");
                }
            }
        }
    }

    /**
     * 猜测文件类型
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /**
     * 首先进入app会调用get()方法，
     *              1>: 如果需要缓存，就从本地数据库中去获取，如果本地数据库有数据，就直接使用缓存的数据来显示界面；
     *              2>: 对于请求服务器成功的数据逻辑是：
     *                       在请求服务器成功后，首先首先获取上一次的数据，如果上次的数据集合不为0，就表示之前有缓存数据，然后取出第1条数据；
     *                       然后把取出的数据和请求成功后返回的数据做对比，如果一样，就return，表示不需要执行请求成功的逻辑来刷新界面；
     *                       如果本地没有缓存，或者有缓存但是比对后数据不一样，就去判断，如果需要缓存，就把原来的数据删除掉，再去插入新的数据
     *
     *
     *
     *
     * @param cache：表示是否需要缓存，true表示需要缓存；false表示不需要缓存
     * @param context
     * @param url
     * @param params
     * @param callBack
     */
    @Override
    public void get(final boolean cache , Context context, String url, Map<String, Object> params, final EngineCallBack callBack) {

        // 请求的路径 参数 + 路径 代表唯一的标识
        final String finalUrl = HttpUtils.jointParams(url, params);
        Log.e("Get请求路径：", finalUrl);

        // 1. 判断是否需要缓存，然后判断有没有缓存
        if (cache){
            // 缓存中的数据
            String resultJson = CacheDataUtil.getCacheResultJson(finalUrl) ;
            if (!TextUtils.isEmpty(resultJson)){
                Log.e("TAG" , "已读到缓存") ;
                // 然后直接使用缓存的数据去刷新界面
                callBack.onSuccess(resultJson);
            }
        }


        Request.Builder requestBuilder = new Request.Builder().url(finalUrl).tag(context);
        //可以省略，默认是GET请求
        Request request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resultJson = response.body().string();

                // 获取服务器的数据之后，会去执行callBack.onSuccess(resultJson)方法

                // 如果需要缓存
                if (cache){
                    // 从数据库中获取缓存的数据
                    String cacheResultJson = CacheDataUtil.getCacheResultJson(finalUrl) ;
                    if (!TextUtils.isEmpty(cacheResultJson)){
                        // 然后比对 数据库中的缓存数据 和 服务器返回的数据是否一样
                        if (resultJson.equals(cacheResultJson)){
                            // 如果一样，就直接return，不做任何操作
                            Log.e("TAG" , "数据和缓存一致"+resultJson) ;
                            return;
                        }
                    }
                }

                // 如果不需要缓存，就直接执行成功的方法
                callBack.onSuccess(resultJson);

                // 然后缓存服务器中的数据
                if (cache){
                    CacheDataUtil.cacheData(finalUrl , resultJson) ;
                }
            }
        });
    }

}
