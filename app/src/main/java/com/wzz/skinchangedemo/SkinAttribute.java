package com.wzz.skinchangedemo;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzz.skinchangedemo.utils.L;
import com.wzz.skinchangedemo.utils.SkinResources;
import com.wzz.skinchangedemo.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class SkinAttribute {

    /**
     * 属性名 如background、textColor
     */
    private static final List<String> mAttributes = new ArrayList<>();


    static {
        mAttributes.add("background");
        mAttributes.add("src");

        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");

    }

    private Typeface typeface;

    //记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * 筛选符合属性View
     * @param view
     * @param attrs
     */
    public void load(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPars = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获得属性名  mAttributes
            String attributeName = attrs.getAttributeName(i); // attributeName为background、layout_width等
//            L.e("   " + attributeName);
            if (mAttributes.contains(attributeName)) {
                String attributeValue = attrs.getAttributeValue(i);  //attributeValue为 @2130968615等
                // 如果是color的 以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId( view.getContext(), new int[]{attrId} ) [0];
                } else {
                    // 正常以 @ 开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                L.e("   " + attributeName + " = " + attributeValue + "       resId:" + resId );
                SkinPair skinPair = new SkinPair(attributeName, resId); // 属性名和值 的对象
                mSkinPars.add(skinPair);
            }
        }

        if (!mSkinPars.isEmpty()) {
            SkinView skinView = new SkinView(view, mSkinPars);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }  else if (view instanceof TextView || view instanceof SkinViewSupport) {
            //没有属性满足 但是需要修改字体
            SkinView skinView = new SkinView(view, mSkinPars);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }
    }

    public void applySkin() {
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin(typeface);
        }
    }

    static class SkinView {
        View view;
        List<SkinPair> skinPairs = new ArrayList<>();

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(Typeface typeface) {
            /** 设置字体样式 */
            applyTypeFace(typeface);
            applySkinSupport();
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;

                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
                        applyTypeFace(SkinResources.getInstance().getTypeface
                                (skinPair.resId));
                        break;
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }

        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }

        private void applyTypeFace(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    static class SkinPair {

        String attributeName; // textColor等
        int resId; // 2130968615等（资源的值）

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
