package com.hager.note;

import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.NoteViewHolder> {

    ArrayList<Note> myNotes = new ArrayList<>();
    OnRecyclerViewClickListener listener ;
    Note note ;


    public RecyclerViewAdaptor(ArrayList<Note> myNotes, OnRecyclerViewClickListener listener) {
        this.myNotes = myNotes;
        this.listener = listener;
    }

    @NonNull

    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
       note = myNotes.get(position);
       holder.NoteTitle.setText(myNotes.get(position).getNoteTitle());
       holder.NoteSubTitle.setText(myNotes.get(position).getNoteSubtitle());
       holder.NoteDateTime.setText(myNotes.get(position).getDate());
       holder.Note.setText(myNotes.get(position).getNote());
       if (note.getImagePath()!= null){
           holder.NoteImage.setImageBitmap(BitmapFactory.decodeFile(myNotes.get(position).getImagePath()));
           holder.NoteImage.setVisibility(View.VISIBLE);
       }
       else{
           holder.NoteImage.setVisibility(View.GONE);
       }
       holder.Note.setTag(note.getID());

    }

    @Override
    public int getItemCount() {
        return myNotes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView NoteTitle ;
        TextView NoteSubTitle ;
        TextView NoteDateTime ;
        TextView Note ;
        RoundedImageView NoteImage;
        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            NoteTitle  = itemView.findViewById(R.id.textTitle);
            NoteSubTitle = itemView.findViewById(R.id.textSubTitle);
            NoteDateTime = itemView.findViewById(R.id.textDateTime);
            Note = itemView.findViewById(R.id.textNote);
            NoteImage = itemView.findViewById(R.id.imageNote);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) Note.getTag();
                    listener.itemOnClick(id);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int id = (int) Note.getTag();
                    listener.itemOnLongClick(id);
                    return false;
                }
            });
        }
    }
    public void setList(ArrayList<Note> myNotes){
      this.myNotes = myNotes ;
      notifyDataSetChanged();
    }
}
