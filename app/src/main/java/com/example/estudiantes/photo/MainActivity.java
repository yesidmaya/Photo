package com.example.estudiantes.photo;

import android.graphics.Bitmap;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    private TextInputEditText textvCode;
    private Button btnshow;
    private ImageView imgPhoto;

    private String localPhath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localPhath = getApplicationContext().getFilesDir().getAbsolutePath();

        textvCode = findViewById(R.id.textvCode);
        btnshow = findViewById(R.id.btnshow);
        imgPhoto = findViewById(R.id.imgPhoto);

        CAFData data = CAFData.dataWithContentsOfFile(localPhath + "/lastPhoto.jpg");
        if (data != null){
            Bitmap bitmap = data.toImage();

            if (bitmap != null){
                imgPhoto.setImageBitmap(bitmap);
            }
        }

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoto();
            }
        });
    }

    public void showPhoto(){
        String code = textvCode.getText().toString().trim();
        String strUrl = "http://acad.ucaldas.edu.co/fotos/";
        //String strUrl = "http://acad.ucaldas.edu.co/fotos/1701520879.jpg"; 27017220144
        URL url = null;
        Runnable thread = null;

        if (!code.isEmpty()){
            strUrl += code + ".jpg";

            try {
                url = new URL(strUrl);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }

            if (url != null){
                final URL urlTemp = url;
                thread = new Runnable() {
                    @Override
                    public void run() {
                        CAFData data = CAFData.dataWithContentsOfURL(urlTemp);
                        Bitmap bitmap = null;
                        if (data!= null){
                            bitmap = data.toImage();
                            if (bitmap != null){
                                final Bitmap bitmapTmp = bitmap;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgPhoto.setImageBitmap(bitmapTmp);
                                    }
                                });
                                data.writeToFile(localPhath + "/lastPhoto.jpg",true);
                            }
                        }
                    }
                };
                queue.execute(thread);
            }
        }
    }
}
