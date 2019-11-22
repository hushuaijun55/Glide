package com.hsj.glide;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private final String picUrl = "http://cn.bing.com/az/hprichbg/rb/Dongdaemun_ZH-CN10736487148_1920x1080.jpg";
    private final String gifUrl = "http://p1.pstatp.com/large/166200019850062839d3";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.img);
    }

    public void loadImage(View view) {
//        ImageLoader.displayImage(MainActivity.this, picUrl, R.mipmap.ic_launcher_round, new ImageLoadCallback() {
//            @Override
//            public void onResourceReady(Bitmap bitmap) {
//                HLogger.d("loadImage");
//                imageView.setImageBitmap(bitmap);
//            }
//        }
        ImageLoader.displayImage(MainActivity.this, picUrl, imageView, 400, 400);
    }

    public void loadGif(View view) {
        ImageLoader.displayImage(MainActivity.this, gifUrl, imageView);
    }
}
