package com.hager.note;

import java.io.Serializable;

public class Note implements Serializable {
    int ID ;
    String NoteTitle ;
    String NoteSubtitle ;
    String Note ;
    String Date ;
    String imagePath ;

    public Note(String noteTitle, String noteSubtitle, String note, String date ,String imagePath) {
        NoteTitle = noteTitle;
        NoteSubtitle = noteSubtitle;
        Note = note;
        Date = date;
        this.imagePath = imagePath ;
    }

    public Note(int ID, String noteTitle, String noteSubtitle, String note, String date , String imagePath) {
        this.ID = ID;
        NoteTitle = noteTitle;
        NoteSubtitle = noteSubtitle;
        Note = note;
        Date = date;
        this.imagePath = imagePath ;

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getNoteTitle() {
        return NoteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        NoteTitle = noteTitle;
    }

    public String getNoteSubtitle() {
        return NoteSubtitle;
    }

    public void setNoteSubtitle(String noteSubtitle) {
        NoteSubtitle = noteSubtitle;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
