package com.example.delllatitudee5440.barcode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.security.PublicKey;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {

    private  static final int RESULT_LOAD_IMAGE = 0;
    private static final int MY_PERMISSION_STORAGE = 1;
    TextView textView;
    Button button;
    ImageView imageView;
    BarcodeDetector detoctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSION_STORAGE);
        }

        textView = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.btn);
        imageView = (ImageView) findViewById(R.id.image);

        detoctor = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!detoctor.isOperational()) {
            textView.setText("Could No Find Code");


        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try{
            if (requestCode ==  RESULT_LOAD_IMAGE &&  resultCode == RESULT_OK && data != null){

                Uri  selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor =  getContentResolver().query(selectedImage, filePathColumn ,null,null,null);
                cursor.moveToFirst();
                int columIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columIndex);
                cursor.close();


                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                imageView.setImageBitmap(bitmap);
                processData(bitmap);
            }else{
                Toast.makeText(MainActivity.this, "Image Couldn't loaded", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception ex){
            Log.d("Main Activity", "onActivityResult: " + ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSION_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOt Granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void processData(Bitmap bitmap){

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detoctor.detect(frame);

        Barcode thiscode = barcodes.valueAt(0);
        textView.setText(thiscode.rawValue);

    }
}

