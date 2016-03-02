package com.grafixartist.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by bryce on 3/1/2016.
 */
public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Log.i("MyApplication" , "preview activity");

        Intent intent = getIntent();
        // failed binder transaction
//        byte[] bytes = intent.getByteArrayExtra("BitmapImage");
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Bitmap temp = Global.img.get();
        if(temp!= null) {
            iv.setImageBitmap(temp);
        }
    }


}
