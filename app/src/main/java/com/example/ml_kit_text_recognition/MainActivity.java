package com.example.ml_kit_text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button Captureimgbtn, detecttxtbtn;
    private ImageView imageView;
    private TextView txtviewdisplay;
    private Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Captureimgbtn = findViewById(R.id.capture_img);
        detecttxtbtn = findViewById(R.id.detect_text_img_btn);
        imageView = findViewById(R.id.image);
        txtviewdisplay = findViewById(R.id.text_display);

        Captureimgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                txtviewdisplay.setText("");

            }
        });

        detecttxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               detectTextFromImage();

            }
        });

    }

    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
       FirebaseVisionTextDetector textDetector =
                FirebaseVision.getInstance().getVisionTextDetector();
        textDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size() == 0){
            Toast.makeText(this, "No text Found in Image",Toast.LENGTH_SHORT).show();
        }else{
            for(FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String text = block.getText();
                txtviewdisplay.setText(text);
            }
        }
    }

    //https://developer.android.com/training/camera/photobasics#java


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }catch (ActivityNotFoundException e){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }


}