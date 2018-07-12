package com.milton.test.rotatableview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/7/12.
 */

public class BodyAdapter extends BaseAdapter {
    private Context mContext;
    private TextView[] title;
    public BodyAdapter(Context context, String[] titles) {
        this.mContext = context;
        this.title = new TextView[titles.length];
        for (int i = 0; i < titles.length; i++) {
            title[i] = new TextView(mContext);
            title[i].setText(titles[i]);
            title[i].setGravity(Gravity.CENTER);
            title[i].setPadding(10, 10, 10, 10);
        }
    }

    public int getCount() {
        return title.length;
    }

    public Object getItem(int position) {
        return title[position];
    }

    public long getItemId(int position) {
        return title[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = title[position];
        } else {
            v = convertView;
        }
        return v;
    }

//    /**
//     * 设置选中的效果
//     */
//    private void SetFocus(int index) {
//        for (int i = 0; i < title.length; i++) {
//            if (i != index) {
//                //title[i].setBackgroundDrawable(new ColorDrawable(unselcolor));//设置没选中的颜色
//                //title[i].setTextColor(fontColor);//设置没选中项的字体颜色
//            }
//        }
//        //title[index].setBackgroundColor(0x00);//设置选中项的颜色
//        //title[index].setTextColor(selcolor);//设置选中项的字体颜色
//    }
}
