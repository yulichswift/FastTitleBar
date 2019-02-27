package com.yulich.titlebar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;

import androidx.annotation.DrawableRes;

import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yulich.titlebar.R;

public class TitleBar extends RelativeLayout {

    public TextView tvTitle;
    public LinearLayout rightButtons, leftButtons;
    protected TitleBarListener titleBarListener;

    protected float density;
    protected int screenWidth;

    protected final int defaultBarHeight;
    protected final int defaultIconSize;
    protected final int layoutMarginInside;
    protected final int layoutMarginParent;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        screenWidth = displayMetrics.widthPixels;

        int titleStrId, titeTextColor, barStyle;

        if (attrs == null) {
            defaultBarHeight = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
            defaultIconSize = getResources().getDimensionPixelSize(R.dimen.title_bar_icon_size);

            titleStrId = 0;
            titeTextColor = getResources().getColor(R.color.color_white);
            barStyle = 0;
        } else {
            {
                int[] systemAttrs = {android.R.attr.layout_height};
                TypedArray ta = context.obtainStyledAttributes(attrs, systemAttrs);

                int barHeight;

                try {
                    barHeight = ta.getDimensionPixelSize(0, getResources().getDimensionPixelSize(R.dimen.title_bar_height));
                } catch (Exception e) {
                    barHeight = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
                }

                defaultBarHeight = barHeight;

                ta.recycle();
            }

            {
                TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
                defaultIconSize = ta.getDimensionPixelSize(R.styleable.TitleBar_title_bar_icon_size, getResources().getDimensionPixelSize(R.dimen.title_bar_icon_size));

                // 設定背景顏色
                int barColor = ta.getColor(R.styleable.TitleBar_title_bar_color, 0);

                if (barColor > 0) {
                    this.setBackgroundColor(barColor);
                } else {
                    TypedValue defValue = new TypedValue();
                    TypedArray defArray = context.obtainStyledAttributes(defValue.data, new int[]{R.attr.colorPrimary});
                    int colorPrimary = defArray.getColor(0, 0);
                    defArray.recycle();

                    this.setBackgroundColor(colorPrimary);
                }

                titleStrId = ta.getResourceId(R.styleable.TitleBar_title_bar_text, 0);

                titeTextColor = ta.getColor(R.styleable.TitleBar_title_bar_text_color, getResources().getColor(R.color.color_white));

                barStyle = ta.getInteger(R.styleable.TitleBar_title_bar_style, 0);

                ta.recycle();
            }
        }

        layoutMarginInside = (int) (8 * density);
        layoutMarginParent = (int) (16 * density);

        rightButtons = new LinearLayout(context);
        rightButtons.setId(R.id.title_bar_right);
        rightButtons.setGravity(Gravity.CENTER_VERTICAL);

        leftButtons = new LinearLayout(context);
        leftButtons.setId(R.id.title_bar_left);
        leftButtons.setGravity(Gravity.CENTER_VERTICAL);

        tvTitle = new TextView(context);
        tvTitle.setId(R.id.title_bar_title);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle.setTextColor(titeTextColor);
        if (titleStrId > 0)
            tvTitle.setText(titleStrId);

        {
            LayoutParams rightLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            rightLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            rightLayoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);

            addView(rightButtons, rightLayoutParams);
        }

        {
            LayoutParams leftLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            leftLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            leftLayoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);

            addView(leftButtons, leftLayoutParams);
        }

        {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            tvTitle.setLayoutParams(layoutParams);

            addView(tvTitle, layoutParams);
        }

        switch (barStyle) {
            case 0:
                setAndroidStyle();
                break;
            case 1:
                setIosStyle();
                break;
        }

        tvTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleBarListener != null) {
                    titleBarListener.onTitleBarClick(v);
                }
            }
        });
    }

    public void setText(String text) {
        tvTitle.setText(text);
    }

    public void setTitleBarListener(TitleBarListener listener) {
        titleBarListener = listener;
    }

    public void addButtonToLeft(View view) {
        addBtn(view, true);
    }

    public void addButtonToRight(View view) {
        addBtn(view, false);
    }

    private void addBtn(View view, boolean isLeft) {
        if (isLeft) {
            leftButtons.addView(view);
            view.setOnTouchListener(touchListenerOfBtn);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (titleBarListener != null) {
                        for (int i = 0; i < leftButtons.getChildCount(); i++) {
                            if (v == leftButtons.getChildAt(i)) {
                                titleBarListener.onTitleBarLeftButtonsClick(v, i);
                                break;
                            }
                        }
                    }
                }
            });
        } else {
            rightButtons.addView(view, 0);
            view.setOnTouchListener(touchListenerOfBtn);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = rightButtons.getChildCount();
                    if (titleBarListener != null) {
                        for (int i = 0; i < count; i++) {
                            if (v == rightButtons.getChildAt(i)) {
                                titleBarListener.onTitleBarRightButtonsClick(v, count - i - 1);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private OnTouchListener touchListenerOfBtn = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                v.setAlpha(.3f);
            } else if (MotionEvent.ACTION_UP == event.getAction()) {
                v.setAlpha(1);
            }
            return false;
        }
    };

    public ImageView addImageBtnToLeft(Context context, @DrawableRes int res) {
        ImageView iv = new ImageView(context);
        if (res != 0)
            iv.setImageResource(res);

        int size = defaultIconSize;
        int btnVerticalSpace = (defaultBarHeight - size) / 2;

        int count = leftButtons.getChildCount();
        if (count == 0) {
            iv.setPadding(0, btnVerticalSpace, 0, btnVerticalSpace);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, defaultBarHeight);
            params.setMargins(layoutMarginParent, 0, 0, 0);

            iv.setLayoutParams(params);

            Rect area = new Rect();
            area.top = 0;
            area.bottom = defaultBarHeight;
            area.left = 0;
            area.right = layoutMarginParent + size + layoutMarginInside;
            TouchDelegate delegate = new TouchDelegate(area, iv);
            leftButtons.setTouchDelegate(delegate);
        } else {
            iv.setPadding(layoutMarginInside, btnVerticalSpace, 0, btnVerticalSpace);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size + layoutMarginInside, defaultBarHeight);
            params.setMargins(layoutMarginInside, 0, 0, 0);
            iv.setLayoutParams(params);
        }

        addBtn(iv, true);
        return iv;
    }

    public ImageView addImageBtnToRight(Context context, @DrawableRes int res) {
        ImageView iv = new ImageView(context);
        if (res != 0)
            iv.setImageResource(res);

        int size = defaultIconSize;
        int btnVerticalSpace = (defaultBarHeight - size) / 2;
        int count = rightButtons.getChildCount();

        if (count == 0) {
            iv.setPadding(0, btnVerticalSpace, 0, btnVerticalSpace);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, defaultBarHeight);
            params.setMargins(0, 0, layoutMarginParent, 0);
            iv.setLayoutParams(params);

            Rect area = new Rect();
            area.top = 0;
            area.bottom = defaultBarHeight;
            area.left = count * (size + layoutMarginParent);
            area.right = area.left + layoutMarginParent + size + layoutMarginInside;

            TouchDelegate delegate = new TouchDelegate(area, iv);
            rightButtons.setTouchDelegate(delegate);
        } else {
            iv.setPadding(0, btnVerticalSpace, layoutMarginInside, btnVerticalSpace);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size + layoutMarginInside, defaultBarHeight);
            params.setMargins(0, 0, layoutMarginInside, 0);
            iv.setLayoutParams(params);
        }

        addBtn(iv, false);
        return iv;
    }

    public TextView addTextBtnToLeft(Context context, String text) {
        return addTextBtn(context, text, true);
    }

    public TextView addTextBtnToRight(Context context, String text) {
        return addTextBtn(context, text, false);
    }

    private TextView addTextBtn(Context context, String text, boolean toLeft) {
        TextView tv = new TextView(context);
        if (text != null) {
            tv.setText(text);
        }

        setBtnTextViewStyle(tv);

        if (toLeft) {
            if (leftButtons.getChildCount() == 0) {
                LinearLayout.LayoutParams params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, defaultBarHeight);
                params.setMargins(layoutMarginInside, 0, 0, 0);
                tv.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, defaultBarHeight);
                params.setMargins(layoutMarginInside, 0, 0, 0);
                tv.setLayoutParams(params);
            }

            tv.setPadding(layoutMarginInside, 0, 0, 0);
        } else {
            if (rightButtons.getChildCount() == 0) {
                LinearLayout.LayoutParams params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, defaultBarHeight);
                params.setMargins(0, 0, layoutMarginInside, 0);
                tv.setLayoutParams(params);
            } else {
                LinearLayout.LayoutParams params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, defaultBarHeight);
                params.setMargins(0, 0, layoutMarginInside, 0);
                tv.setLayoutParams(params);
            }

            tv.setPadding(0, 0, layoutMarginInside, 0);
        }


        addBtn(tv, toLeft);
        return tv;
    }

    private void setBtnTextViewStyle(TextView tv) {
        tv.setLines(1);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.color_white));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        // tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
    }

    private void setIosStyle() {
        LayoutParams layoutParams = (LayoutParams) tvTitle.getLayoutParams();

        tvTitle.setPadding(layoutMarginParent, 0, layoutMarginParent, 0);

        // 左右不對稱會無法置中
        // layoutParams.addRule(LEFT_OF, rightButtons.getId());
        // layoutParams.addRule(RIGHT_OF, leftButtons.getId());
        layoutParams.addRule(CENTER_IN_PARENT, RelativeLayout.TRUE);
    }

    private void setAndroidStyle() {
        LayoutParams layoutParams = (LayoutParams) tvTitle.getLayoutParams();

        tvTitle.setPadding(layoutMarginParent, 0, 0, 0);


        layoutParams.addRule(RIGHT_OF, leftButtons.getId());
    }

    public void setViewVisibility(View view, int visibility) {
        if (view.getVisibility() == visibility)
            return;

        view.setVisibility(visibility);
        if (View.INVISIBLE == visibility)
            view.setEnabled(false);
        else if (View.VISIBLE == visibility)
            view.setEnabled(true);
    }

    public interface TitleBarListener {
        void onTitleBarClick(View v);

        void onTitleBarLeftButtonsClick(View v, int position);

        void onTitleBarRightButtonsClick(View v, int position);
    }

    private int getSize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.EXACTLY: {
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getSize(defaultBarHeight, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }
}
