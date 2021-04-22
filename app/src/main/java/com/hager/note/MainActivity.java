package com.hager.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.hager.note.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    public final int RQC_ADD_NOTE_ACTIVITY = 1;
    public final int RQC_UPDATE_NOTE_ACTIVITY = 2;
    public final int RESULT_LOAD_IMAGE = 4 ;
    public final int RQC_STORAGE_PERMEATION=5;

    ArrayList<Note> myNotes = new ArrayList<>();
    RecyclerViewAdaptor adaptor ;
    MyDataBase db = new MyDataBase(this);
    ActivityMainBinding binding ;
    public int pItemId ;
    @Override
    public void onResume() {
        super.onResume();
        if (binding.notesRecyclerView != null && binding.notesRecyclerView.getAdapter() != null) {
            binding.notesRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.AddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(),createNoteActivity.class);
                startActivityForResult(intent,RQC_ADD_NOTE_ACTIVITY);
            }
        });

        binding.imageAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            ,RQC_STORAGE_PERMEATION);
                }
                else {
                    selectImage();

                }

            }
        });


        // searching
        binding.imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = binding.inputSearch.getText().toString();
                if(!search.equals("")) {
                    myNotes = db.searchNotes(search);
                }
                else{
                    myNotes = db.RestoreAllNotes();
                }
                updateRecyclerView(myNotes);
            }
        });



        myNotes = db.RestoreAllNotes();
        adaptor = new RecyclerViewAdaptor(myNotes, new OnRecyclerViewClickListener() {
            @Override
            public void itemOnClick(int itemId) {

                Intent intent = new Intent(getBaseContext(),createNoteActivity.class);
                intent.putExtra("editOrView" , true);
                intent.putExtra("id" , itemId);
                startActivityForResult(intent , RQC_UPDATE_NOTE_ACTIVITY);

            }

            @Override
            public void itemOnLongClick(int itemId) {
                pItemId = itemId ;

                Toast.makeText(MainActivity.this, "on long click", Toast.LENGTH_SHORT).show();
                registerForContextMenu(binding.notesRecyclerView);
            }
        });

        adaptor.setList(myNotes);
        adaptor.notifyItemInserted(0);
        binding.notesRecyclerView.setAdapter(adaptor);
     GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
     binding.notesRecyclerView.setLayoutManager(gridLayoutManager);
     binding.notesRecyclerView.smoothScrollToPosition(1);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RQC_ADD_NOTE_ACTIVITY || requestCode ==RQC_UPDATE_NOTE_ACTIVITY && resultCode == RESULT_OK){
            myNotes = db.RestoreAllNotes();
            updateRecyclerView(myNotes);

        }
        else if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Intent intent = new Intent(getBaseContext(),createNoteActivity.class);
                intent.putExtra("image",picturePath);
                startActivityForResult(intent,RQC_ADD_NOTE_ACTIVITY);
            }
        }

        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.long_click_menu , menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        boolean result;
        switch (item.getItemId()){
            case R.id.deleteOption :
               result = db.deleteNote(pItemId);
                myNotes = db.RestoreAllNotes();
                updateRecyclerView(myNotes);
                if (result){
                    Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
                }
                return true ;
            default:
                return super.onContextItemSelected(item);

        }
    }

    public void updateRecyclerView(ArrayList<Note> myNotes){
        adaptor.setList(myNotes);
        binding.notesRecyclerView.setAdapter(adaptor);
    }

    public void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,RESULT_LOAD_IMAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
           if (requestCode == RQC_STORAGE_PERMEATION && grantResults.length >0) {
               if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   selectImage();
               }

        }
    }
}