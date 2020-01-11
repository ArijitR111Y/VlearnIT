package com.royarijit998.vlearnit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>{

    private ArrayList<Comments> commentsArrayList;
    private DatabaseReference usersRef;

    public CommentListAdapter(DatabaseReference usersRef, ArrayList<Comments> commentsArrayList) {
        this.commentsArrayList = commentsArrayList;
        this.usersRef = usersRef;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout, null, false);
        CommentListAdapter.CommentViewHolder commentViewHolder = new CommentListAdapter.CommentViewHolder(layoutView);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        holder.commentDateTextView.setText(commentsArrayList.get(position).getDate());
        holder.commentTimeTextView.setText(commentsArrayList.get(position).getTime());
        holder.commentTextView.setText(commentsArrayList.get(position).getComment());

        usersRef.child(commentsArrayList.get(position).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    holder.commentFullnameTextView.setText(userFullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView commentFullnameTextView, commentDateTextView, commentTextView, commentTimeTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentFullnameTextView = itemView.findViewById(R.id.commentFullnameTextView);
            commentDateTextView = itemView.findViewById(R.id.commentDateTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentTimeTextView = itemView.findViewById(R.id.commentTimeTextView);

        }
    }
}
