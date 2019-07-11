package com.wzz.skinchangedemo;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import com.wzz.skinchangedemo.utils.SkinPreference;
import com.wzz.skinchangedemo.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;


public class SkinManager extends Observable {

    private static SkinManager instance = null;

    private Application mContext;

    private SkinManager(Application application) {

        mContext = application;

        //共享首选项 用于记录当前使用的皮肤
        SkinPreference.init(application);
        //资源管理类 用于从 app/皮肤 中加载资源
        SkinResources.init(application);


        /** 注册activity的生命回调 监听activity */
        SkinActivityLifeCallback lifeCallback = new SkinActivityLifeCallback(); // synchronized
        application.registerActivityLifecycleCallbacks(lifeCallback);

    }

    public static void init(Application application) {

        if ( instance == null ){
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }

    }

    public static SkinManager getInstance() {
        return instance;
    }

    /**
     * 记载皮肤并应用
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    public void loadSkin(String skinPath){

        if (TextUtils.isEmpty( skinPath )){

            SkinResources.getInstance().reset();
            SkinPreference.getInstance().setSkin( null );

        }
        try {
            //反射创建AssetManager 与 Resource

            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssertPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssertPath.invoke( assetManager , skinPath ) ;

            Resources resources = mContext.getResources();
            //根据当前的显示与配置(横竖屏、语言等)创建Resources
            Resources skinResource = new Resources(assetManager, resources.getDisplayMetrics(),
                    resources.getConfiguration());

            // 保存当前的皮肤状态
            SkinPreference.getInstance().setSkin( skinPath );

            // 获取外部apk（皮肤包） 包名
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo info = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
            String packageName = info.packageName;

            SkinResources.getInstance().applySkin( skinResource , packageName );

        } catch (Exception e) {
            e.printStackTrace();
        }

        //通知采集的View 更新皮肤
        //被观察者改变 通知所有观察者
        setChanged();
        notifyObservers(  );

    }


}
