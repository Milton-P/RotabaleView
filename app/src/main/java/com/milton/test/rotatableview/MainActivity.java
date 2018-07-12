package com.milton.test.rotatableview;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    RotatableView rotatableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton","XXX FloatingView1 ");
                FloatingView1();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton","XXX FloatingView@222222 ");
                FloatingView2();
            }
        });
    }

    private void FloatingView1() {
        android.util.Log.d("milton"," FloatingView1 ");
        View root = View.inflate(this, R.layout.custom_view, null);
        Button buttonCancel = root.findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("milton"," buttonCancel ");
                RotatableView.getInstance(MainActivity.this).dismiss();
            }
        });
        rotatableView = RotatableView.getInstance(this);
        rotatableView.show(root);
    }

    private void FloatingView2() {
        View root = View.inflate(this, R.layout.custom_view2, null);
        rotatableView = RotatableView.getInstance(this);
        rotatableView.show(root);
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
}
