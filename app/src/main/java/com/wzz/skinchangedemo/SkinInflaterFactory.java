package com.wzz.skinchangedemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * 一个Activity对应一个Factory2
 */
public class SkinInflaterFactory implements LayoutInflater.Factory2, Observer {

    //属性处理类
    private SkinAttribute skinAttribute;


    public SkinInflaterFactory(Activity activity, Typeface typeface) {
        this.skinAttribute = new SkinAttribute( typeface );
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        Log.d("wzz------", "onCreateView: "+ name + "   count:" + attrs.getAttributeCount() );

        for (int i = 0; i < attrs.getAttributeCount(); i++) {

            Log.d("------",  attrs.getAttributeName(i) + "  ==  " + attrs.getAttributeValue( i ) );

        }

        View view = createViewFromTag( context , name , attrs );
        // 筛选符合属性View
        skinAttribute.load( view , attrs );
        Log.d("wzz------", "view：=======================" + view );

        return view ;
    }

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    /**
     * 根据xml中的标签创建出View对象
     * @param context
     * @param name  "TextView" 或是 "android.support.v7.widget.SwitchCompat"
     * @param attrs "textSize" "textColor"等的属性集合
     */
    private View createViewFromTag(Context context, String name, AttributeSet attrs) {


        //这里判断一下name(即在xml中写的控件名称)中是否含有'.'
        //如果没有那么肯定就是系统控件(比如ProgressBar,在布局中是不需要加ProgressBar的具体包名的)
        //如果有那么就是自定义控件,或者是系统的控件(比如android.support.v7.widget.SwitchCompat)

        if ( name.indexOf(".") == -1 ){
            for (int i = 0; i < sClassPrefixList.length; i++) {
                View view = createView( context , sClassPrefixList[i] + name , attrs );
                if ( view != null ){
                    return  view ;
                }
            }
            return  null ;
        }else {
            View view = createView( context ,  name , attrs );
            return view ;
        }

    }

    private static final Class[] mConstructorSignature = { Context.class , AttributeSet.class } ;
    private static final Map<String , Constructor<? extends View> > mConstructorMap = new HashMap<>();

    /**
     * 源码中创建系统控件和非系统控件分开去创建.其实方法都是同一个,只是一个传了前缀,一个没有传前缀.来看看创建方法实现 (全类名的View就不需要拼接前缀)
     *
     * 其实这个创建View就是利用ClassLoader去寻找这个类的class,然后获取其
     * {Context.class, AttributeSet.class}这个构造方法,然后通过反射将View创建出来.具体逻辑在代码中已标明注释.
     *
     * @param context
     * @param name
     * @param attrs
     * @return
     */
    private View createView(Context context, String name, AttributeSet attrs) {
        //这里的sConstructorMap是用来做缓存的,如果之前已经创建,则会将构造方法缓存起来,下次直接用
        Constructor<? extends View> constructor = mConstructorMap.get(name);

        try {
            if ( constructor == null ){
                // Class not found in the cache, see if it's real, and try to add it
                //通过classLoader去寻找该class,这里的classLoader其实是PathClassLoader
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                //获取构造方法
                constructor = aClass.getConstructor(mConstructorSignature);
                //获取构造方法
                mConstructorMap.put( name , constructor );

            }

            //设置构造方法可访问
            constructor.setAccessible(true);
            //通过构造方法new一个View对象出来
            View view = constructor.newInstance(context, attrs);

            return view ;
        }catch (Exception e){
            e.printStackTrace();
            return null ;
        }

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    /**
     * 观察者 当被观察者调用notifyObservers()时做出改变
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {

    }
}
