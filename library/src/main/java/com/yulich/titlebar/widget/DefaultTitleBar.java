package com.yulich.titlebar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yulich.titlebar.R;

public class DefaultTitleBar extends TitleBar {

    public View leftBtn, rightBtn;

    public DefaultTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        int leftId, rightId, leftStrId = 0, rightStrId = 0;

        {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultTitleBar);
            leftId = ta.getResourceId(R.styleable.DefaultTitleBar_left_src, 0);
            rightId = ta.getResourceId(R.styleable.DefaultTitleBar_right_src, 0);

            if (leftId == 0)
                leftStrId = ta.getResourceId(R.styleable.DefaultTitleBar_lef_text, 0);

            if (rightId == 0)
                rightStrId = ta.getResourceId(R.styleable.DefaultTitleBar_right_text, 0);

            ta.recycle();
        }

        if (leftId > 0)
            leftBtn = addImageBtnToLeft(context, leftId);
        else if (leftStrId > 0)
            leftBtn = addTextBtnToLeft(context, context.getString(leftStrId));

        if (rightId > 0)
            rightBtn = addImageBtnToRight(context, rightId);
        else if (rightStrId > 0)
            rightBtn = addTextBtnToRight(context, context.getString(rightStrId));

    }

    public void setLeftBtnSrc(@DrawableRes int res) {
        if (leftBtn instanceof ImageView)
            ((ImageView) leftBtn).setImageResource(res);
    }

    public void setRightBtnSrc(@DrawableRes int res) {
        if (rightBtn instanceof ImageView)
            ((ImageView) rightBtn).setImageResource(res);
    }

    public void setRightBtnSrcAnimation(Drawable composedAnimation) {
        if (rightBtn instanceof ImageView)
            ((ImageView) rightBtn).setImageDrawable(composedAnimation);
    }

    public void setLeftBtnText(String text) {
        if (leftBtn instanceof TextView)
            ((TextView) leftBtn).setText(text);
    }

    public void setLeftBtnText(int textId) {
        if (leftBtn instanceof TextView)
            ((TextView) leftBtn).setText(textId);
    }

    public void setRightBtnText(String text) {
        if (rightBtn instanceof TextView)
            ((TextView) rightBtn).setText(text);
    }

    public void setRightBtnText(int textId) {
        if (leftBtn instanceof TextView)
            ((TextView) leftBtn).setText(textId);
    }

    public void setLeftBtnVisibility(int visibility) {
        setViewVisibility(leftBtn, visibility);
    }

    public void setRightBtnVisibility(int visibility) {
        setViewVisibility(rightBtn, visibility);
    }
}