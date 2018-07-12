package com.milton.test.rotatableview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/7/12.
 */

public class TabAdapter extends BaseAdapter {
    private Context mContext;
    private String[] texts;

    public TabAdapter(Context context, String[] texts) {
        this.mContext = context;
        this.texts = texts;
    }

    public int getCount() {
        return texts.length;
    }

    public Object getItem(int position) {

        return makeMenyBody(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return makeMenyBody(position);
    }

    private View makeMenyBody(int position) {
//        LinearLayout result = new LinearLayout(this.mContext);
//        result.setOrientation(LinearLayout.VERTICAL);
//        result.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//        result.setPadding(10, 10, 10, 10);

        TextView text = new TextView(this.mContext);
        text.setText(texts[position]);
        //text.setTextSize(fontSize);
        //text.setTextColor(fontColor);
        text.setGravity(Gravity.CENTER);
        text.setPadding(5, 5, 5, 5);
//        ImageView img = new ImageView(this.mContext);
//        img.setBackgroundResource(resID[position]);
//        result.addView(img, new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
//        result.addView(text);
        return text ;
    }


}
