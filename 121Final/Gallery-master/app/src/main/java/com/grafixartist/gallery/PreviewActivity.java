package com.grafixartist.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by bryce on 3/1/2016.
 */
public class PreviewActivity extends AppCompatActivity {
    private Uri imageUri;
    private Bitmap bitmap;
    private Bitmap bitmapScaled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Log.i("MyApplication" , "preview activity");
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("imageUri"));

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            if(bitmap.getWidth()>width || bitmap.getHeight() > height){
                bitmapScaled = scaleDown(bitmap, width,true);
                iv.setImageBitmap(bitmapScaled);
            }else {
                iv.setImageBitmap(bitmap);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                    boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

}
