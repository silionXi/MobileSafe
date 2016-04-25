package com.silion.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.silion.mobilesafe.R;

/**
 * Created by silion on 2016/3/29.
 */
public class SettingItemView extends LinearLayout {
    private static String NAME_SPACE = "http://schemas.android.com/apk/com.silion.mobilesafe";
    private TextView mTitleTextView;
    private TextView mDescTextView;
    private CheckBox mStatusCheckBox;
    private ImageView mMoreImageView;

    private String mTitle;
    private String mDescOn;
    private String mDescOff;
    private int mIcon;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SettingItemView, 0, 0);
//        try {
//            mTitle = a.getString(R.styleable.SettingItemView_titleText);
//            mDescOn = a.getString(R.styleable.SettingItemView_descOnText);
//            mDescOff = a.getString(R.styleable.SettingItemView_descOffText);
//            mIcon = a.getInteger(R.styleable.SettingItemView_icon, 0);
//        } finally {
//            a.recycle();
//        }
        mTitle = attrs.getAttributeValue(NAME_SPACE, "titleText");
        mDescOn = attrs.getAttributeValue(NAME_SPACE, "descOnText");
        mDescOff = attrs.getAttributeValue(NAME_SPACE, "descOffText");
        mIcon = attrs.getAttributeIntValue(NAME_SPACE, "icon", 0);

        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_setting_item, this);

        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mDescTextView = (TextView) findViewById(R.id.descTextView);
        mStatusCheckBox = (CheckBox) findViewById(R.id.statusCheckBox);
        mMoreImageView = (ImageView) findViewById(R.id.moreImageView);

        setTitle(mTitle);
        setIcon(mIcon);
    }

    public void setIcon(int icon) {
        if (icon == 0) {
            mStatusCheckBox.setVisibility(VISIBLE);
            mMoreImageView.setVisibility(GONE);
        } else {
            mStatusCheckBox.setVisibility(GONE);
            mMoreImageView.setVisibility(VISIBLE);
        }
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setDesc(String desc) {
        mDescTextView.setText(desc);
    }

    public void setChecked(boolean checked) {
        mStatusCheckBox.setChecked(checked);
        if (checked) {
            setDesc(mDescOn);
        } else {
            setDesc(mDescOff);
        }
    }

    public boolean isCheck() {
        return mStatusCheckBox.isChecked();
    }
}
