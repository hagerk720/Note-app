package com.hager.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.CaseMap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import com.hager.note.databinding.ActivityCreateNoteBinding;
import com.hager.note.databinding.ActivityMainBinding;

public class createNoteActivity extends AppCompatActivity {
    ActivityCreateNoteBinding bind ;
    private Note noteAvailable = null;
    String Title ;
    String Subtitle ;
    String Note ;
    String DateTime ;
    String imagePath=null ;
    MyDataBase myDataBase = new MyDataBase(this) ;
    int itemId ;
    boolean result ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind =ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        itemId = getIntent().getIntExtra("id",-1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bind.textDateTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        bind.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        bind.imageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Note note = collectDate();
             result = myDataBase.InsertNote(note);
             Intent intent = new Intent();
             setResult(RESULT_OK , intent);
             finish();
            }
        });
        bind.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(createNoteActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            ,new MainActivity().RQC_STORAGE_PERMEATION);
                }
                else {
                    selectImage();

                }
            }
        });
        if (getIntent().getBooleanExtra("editOrView",false)){
            noteAvailable = myDataBase.getNoteById(itemId);

            if (noteAvailable != null) {
                viewOrUpdateNote();
            }

            bind.imageDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean result ;
                    Note note = collectDate();
                    result= myDataBase.UpdateNote(note);
                   if (result) {
                       Toast.makeText(createNoteActivity.this, "updated", Toast.LENGTH_SHORT).show();
                   }
                    Intent intent = new Intent();
                    setResult(RESULT_OK , intent);
                    finish();
                }
            });
        }
         if(getIntent().getStringExtra("image") != null){
             imagePath=getIntent().getStringExtra("image");
            bind.imageNote.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            bind.imageNote.setVisibility(View.VISIBLE);
        }


    }
    Note collectDate(){
        Title = bind.inputNoteTitle.getText().toString();
        Subtitle = bind.inputNoteSubtitle.getText().toString();
        Note = bind.inputNote.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            System.out.println(DateTime);
        }

        Note note = new Note(itemId,Title,Subtitle,Note,DateTime,imagePath);
        return note ;
    }
    private void viewOrUpdateNote (){
    bind.inputNoteTitle.setText(noteAvailable.getNoteTitle());
    bind.inputNoteSubtitle.setText(noteAvailable.getNoteSubtitle());
    bind.inputNote.setText(noteAvailable.getNote());
    bind.textDateTime.setText(noteAvailable.getDate());
    if (noteAvailable.getImagePath() != null){
        bind.imageNote.setImageBitmap(BitmapFactory.decodeFile(noteAvailable.getImagePath()));
        bind.imageNote.setVisibility(View.VISIBLE);
    }

    }
    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,new MainActivity().RESULT_LOAD_IMAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == new MainActivity().RQC_STORAGE_PERMEATION && grantResults.length >0) {
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == new MainActivity().RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                bind.imageNote.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                bind.imageNote.setVisibility(View.VISIBLE);
            }
        }
    }
}