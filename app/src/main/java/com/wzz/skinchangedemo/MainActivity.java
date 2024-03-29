package com.wzz.skinchangedemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wzz.skinchangedemo.utils.SkinPreference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 刚进入就换肤
        SkinManager.getInstance().loadSkin( SkinPreference.getInstance().getSkin() );

    }

    /**
     * 还原皮肤
     * @param view
     */
    public void reset(View view) {
        SkinManager.getInstance().loadSkin( null );
    }

    /**
     * 换肤
     * @param view
     */
    public void change(View view) {
        /**
         * 加载外部皮肤包 获取属性 进行更改控件颜色和值
         */
        SkinManager.getInstance().loadSkin(Environment.getExternalStorageDirectory() + "/skin.skin");
    }
}
