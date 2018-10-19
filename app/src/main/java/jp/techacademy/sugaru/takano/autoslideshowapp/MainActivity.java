package jp.techacademy.sugaru.takano.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Cursor cursor;

    Timer mTimer;
    Handler mHandler = new Handler();

    ImageView imageView1;

    Button button1;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button1.setEnabled(true);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);
        button2.setEnabled(true);
        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(this);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                getContentsInfo();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
            }
        }else{
            getContentsInfo();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo();
                }else{
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
                }

                break;
            default:
                break;
        }
    }

    private void getContentsInfo(){

        ContentResolver resolver = getContentResolver();
            cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );


        if(cursor.moveToFirst()) {
            setImg();

        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button1) {
            if(cursor.moveToNext()) {
                setImg();

            }else{
                cursor.moveToFirst();
                setImg();
            }
        }else if(v.getId() == R.id.button2){
            if(cursor.moveToPrevious()) {
                setImg();

            }else{
                cursor.moveToLast();
                setImg();
            }
        }else if(v.getId() == R.id.button3) {
            if (mTimer == null) {
                button1.setEnabled(false);
                button2.setEnabled(false);
                button3.setText("停止");
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (cursor.moveToNext()) {
                                    setImg();

                                } else {
                                    cursor.moveToFirst();
                                    setImg();
                                }
                            }
                        });
                    }
                }, 2000, 2000);
            }else if(mTimer != null){
                button1.setEnabled(true);
                button2.setEnabled(true);
                button3.setText("再生");
                mTimer.cancel();
                mTimer = null;
            }
        }

    }

    public void setImg(){
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        imageView1 = findViewById(R.id.imageView1);
        imageView1.setImageURI(imageUri);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

}
