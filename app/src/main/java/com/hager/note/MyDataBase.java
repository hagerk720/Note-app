package com.hager.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyDataBase extends SQLiteOpenHelper {
    private  SQLiteDatabase sqLiteDatabase ;
    private SQLiteOpenHelper sqLiteOpenHelper ;

    public static final int database_version = 10 ;

    public static final String database_name = "Notes" ;
    public static final String Table_Name = "notes" ;
    public static final String POST_COL_ID = "id" ;
    public static final String POST_COL_TITLE = "title" ;
    public static final String POST_COL_SUBTITLE = "subtitle" ;
    public static final String POST_COL_NOTE = "note" ;
    public static final String POST_COL_DATE= "date" ;
    public static final String POST_COL_IMG= "image" ;


    public MyDataBase(@Nullable Context context) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create ="create table " +Table_Name +" ("+ POST_COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                ""+POST_COL_TITLE+" TEXT ,"+POST_COL_SUBTITLE+" TEXT ,"+POST_COL_NOTE+"  TEXT , "+
                ""+ POST_COL_DATE+ " TEXT ,"+POST_COL_IMG+" TEXT )";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + database_name );
        onCreate(sqLiteDatabase);
    }
    public void open(){
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }
    public void close (){
        if (this.sqLiteDatabase != null){
            this.sqLiteDatabase.close();
        }
    }
    public boolean InsertNote(Note note){
      SQLiteDatabase sqLiteDatabase = getWritableDatabase() ;
      ContentValues contentValues = new ContentValues() ;
      contentValues.put(POST_COL_TITLE,note.getNoteTitle());
      contentValues.put(POST_COL_SUBTITLE,note.getNoteSubtitle());
      contentValues.put(POST_COL_NOTE,note.getNote());
      contentValues.put(POST_COL_DATE,note.getDate());
      contentValues.put(POST_COL_IMG,note.imagePath);
     long result = sqLiteDatabase.insert(Table_Name ,null ,contentValues);
     return result != -1;
    }
    public ArrayList<Note> RestoreAllNotes(){
        ArrayList<Note> Notes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Table_Name , null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(POST_COL_ID));
                String Title = cursor.getString(cursor.getColumnIndex(POST_COL_TITLE));
                String Subtitle = cursor.getString(cursor.getColumnIndex(POST_COL_SUBTITLE));
                String Note = cursor.getString(cursor.getColumnIndex(POST_COL_NOTE));
                String Date = cursor.getString(cursor.getColumnIndex(POST_COL_DATE));
                String imagePath = cursor.getString(cursor.getColumnIndex(POST_COL_IMG));
                Note note = new Note(id,Title , Subtitle , Note , Date,imagePath);
                Notes.add(note);
            }
            while (cursor.moveToNext());
        }
        return Notes;
    }
    public  Note getNoteById(int itemId){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + Table_Name + " WHERE " + POST_COL_ID + "=?", new String[]{String.valueOf(itemId)});
        if (cursor.moveToNext() && cursor!=null){
            String Title = cursor.getString(cursor.getColumnIndex(POST_COL_TITLE));
            String Subtitle = cursor.getString(cursor.getColumnIndex(POST_COL_SUBTITLE));
            String Note = cursor.getString(cursor.getColumnIndex(POST_COL_NOTE));
            String Date = cursor.getString(cursor.getColumnIndex(POST_COL_DATE));
            String imagePath = cursor.getString(cursor.getColumnIndex(POST_COL_IMG));

            Note note = new Note( Title , Subtitle ,Note ,Date,imagePath);
            cursor.close();

            return note ;
        }
        return null ;
    }
    public boolean UpdateNote(Note note){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase() ;
        ContentValues contentValues = new ContentValues() ;
        contentValues.put(POST_COL_TITLE,note.getNoteTitle());
        contentValues.put(POST_COL_SUBTITLE,note.getNoteSubtitle());
        contentValues.put(POST_COL_NOTE,note.getNote());
        contentValues.put(POST_COL_DATE,note.getDate());
        contentValues.put(POST_COL_IMG,note.getImagePath());
        int result = sqLiteDatabase.update(Table_Name,contentValues ,POST_COL_ID + "=?" ,  new String[]{String.valueOf(note.getID())}) ;
        return result > 0 ;

    }
    public boolean deleteNote(int itemId){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int result =  sqLiteDatabase.delete(Table_Name ,POST_COL_ID + " =? "  ,  new String[]{String.valueOf(itemId)});
        return result>0;
    }
    public ArrayList<Note> searchNotes (String titleSearch){
        ArrayList<Note> notes = new ArrayList<>();
        String[] args = {titleSearch};
        SQLiteDatabase sqLiteDatabase = getReadableDatabase() ;
        Cursor cursor =sqLiteDatabase.rawQuery("SELECT * FROM " +Table_Name+ " WHERE " +POST_COL_TITLE +" =? ", args);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(POST_COL_ID)) ;
                String title = cursor.getString(cursor.getColumnIndex(POST_COL_TITLE)) ;
                String subtitle = cursor.getString(cursor.getColumnIndex(POST_COL_SUBTITLE)) ;
                String Note = cursor.getString(cursor.getColumnIndex(POST_COL_NOTE));
                String date = cursor.getString(cursor.getColumnIndex(POST_COL_DATE));
                String imagePath = cursor.getString(cursor.getColumnIndex(POST_COL_IMG));

                Note n = new Note(id,title,subtitle,Note,date,imagePath);
                notes.add(n);
            }
            while (cursor.moveToNext());
        }
        return notes ;
    }
}
