package com.wzz.skinchangedemo;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;

import com.wzz.skinchangedemo.utils.SkinThemeUtils;

import java.lang.reflect.Field;

public class SkinActivityLifeCallback implements Application.ActivityLifecycleCallbacks {

    public ArrayMap<Activity, SkinInflaterFactory> mLayoutInflaterFactories = new ArrayMap<>();

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        //在Activity创建的时候,直接设置Factory
        installLayoutFactory(activity);

    }

    private void installLayoutFactory(Activity activity) {

        /** mFactory只能设置一次,不然就要抛异常,所以需要先利用发射将mFactorySet的值设置为false才不会抛异常.然后才能setFactory(). */

        LayoutInflater inflater = LayoutInflater.from(activity);
        Field field ;
        try {
            //setFactory只能调用一次,用于设置Factory(创建View),  设置了Factory了mFactorySet就会是true
            //如果需要重新设置Factory,则需要先将mFactorySet设置为false,不然系统判断到mFactorySet是true则会抛异常.
            //这里使用自己构建的Factory去创建View,在创建View时当然也就可以控制它的背景或者文字颜色.
            //(在这里之前需要知道哪些控件需要换肤,其中一部分是继承自三方库的控件,这些控件是实现了SkinCompatSupportable接口的,可以很方便的控制.
            // 还有一部分是系统的控件,在创建时直接创建三方库中的控件(比如View就创建SkinCompatView).
            // 在设置系统控件的背景颜色和文字颜色时,直接从三方库缓存颜色中取值,然后进行设置.)

            field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible( true );
            field.setBoolean( inflater , false );

        } catch ( Exception e) {
            e.printStackTrace();
        }

        /**
         * 更新字体
         */
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

        SkinInflaterFactory inflaterFactory = new SkinInflaterFactory( activity , Typeface.DEFAULT_BOLD );
        inflater.setFactory2( inflaterFactory );
        // 添加至集合保存
        mLayoutInflaterFactories.put( activity , inflaterFactory );
        // 添加观察者
        SkinManager.getInstance().addObserver( inflaterFactory );

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    /**
     * 当activity销毁时，移除此factory并取消监听
     * @param activity
     */
    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinInflaterFactory factory = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver( factory );
    }
}
