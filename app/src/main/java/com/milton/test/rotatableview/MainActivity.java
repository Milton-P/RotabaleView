package com.milton.test.rotatableview;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    RotatableView rotatableView;
    RotatableWindow rotatableWindow;
    GridView body;
    GridView tab;
    TabAdapter tabAdapter;
    TabAdapter[] bodyAdapters = new TabAdapter[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton","通过windowManager实现 1");
                FloatingView1();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*android.util.Log.d("milton","通过windowManager实现 1");
                FloatingView2();*/
            }
        });

        Button button3 = findViewById(R.id.test);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton","通过模仿popupwind实现 ");
                RotatableWindow rotatableWindow = new testRotableWindow(MainActivity.this);
                rotatableWindow.show();
            }
        });
    }

    private void FloatingView1() {
        android.util.Log.d("milton"," FloatingView1 ");
        View content = View.inflate(this, R.layout.custom_view, null);
        Button buttonCancel = content.findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton"," buttonCancel ");
                RotatableView.getInstance(MainActivity.this).dismiss();
            }
        });
        rotatableView = RotatableView.getInstance(this);
        rotatableView.show(content);
    }

    private void FloatingView2() {
        View content = View.inflate(this, R.layout.custom_view2, null);
        rotatableView = RotatableView.getInstance(this);
        rotatableView.show(content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //RotatableView.getInstance(this).dismiss();
    }

    public View getView () {
        View content = View.inflate(this, R.layout.tab_view, null);
        tab = content.findViewById(R.id.tab);
        body = content.findViewById(R.id.body);
        tabAdapter = new TabAdapter(this, new String[] { "video",
                "photo" });
        tab.setAdapter(tabAdapter);
        tab.setOnItemClickListener(new TabClickEvent());

        bodyAdapters[0] = new TabAdapter(this, new String[] { "photo",
                "test2",  "photo", "test4",   "photo",
                "test2",  "photo", "test4" });
        bodyAdapters[1] = new TabAdapter(this, new String[] { "video",
                "test2",  "video", "test4",   "video",
                "test2",  "video", "test4" });
        tab.setSelection(0);
        body.setAdapter(bodyAdapters[0]);
        return content;
    }

    class TabClickEvent implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long id) {
            android.util.Log.d("milton","test onItemClick");
            tab.setSelection(position);
            body.setAdapter(bodyAdapters[position]);
        }
    }
}
