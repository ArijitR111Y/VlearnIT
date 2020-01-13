package com.royarijit998.vlearnit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    private ArrayList<Posts> postsArrayList;
    private DatabaseReference likesRef;
    private DatabaseReference usersRef;
    private FirebaseUser firebaseUser;

    public PostListAdapter(DatabaseReference usersRef, DatabaseReference likesRef, FirebaseUser firebaseUser, ArrayList<Posts> postsArrayList) {
        this.usersRef = usersRef;
        this.postsArrayList = postsArrayList;
        this.likesRef = likesRef;
        this.firebaseUser = firebaseUser;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, null, false);
        PostViewHolder postViewHolder = new PostViewHolder(layoutView);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {

        final String postKey = postsArrayList.get(position).getKey();

        // TextViews
        holder.postDateTextView.setText(postsArrayList.get(position).getDate());
        holder.postTimeTextView.setText(postsArrayList.get(position).getTime());
        holder.postDescTextView.setText(postsArrayList.get(position).getDescription());

        // ImageViews
        Picasso.get().load(postsArrayList.get(position).getPostImg()).placeholder(R.drawable.profile).into(holder.postImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClickPostActivity.class);
                intent.putExtra("postKey", postKey);
                v.getContext().startActivity(intent);

            }
        });

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.numLikesTextView.setText(dataSnapshot.child(postKey).getChildrenCount()+" likes");
                if (dataSnapshot.child(postKey).hasChild(firebaseUser.getUid())) {
                    holder.likeImageBtn.setImageResource(R.drawable.like);
                } else {
                    holder.likeImageBtn.setImageResource(R.drawable.dislike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersRef.child(postsArrayList.get(position).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImg = "";
                    if(dataSnapshot.child("profileImg").getValue() != null){
                        userProfileImg = dataSnapshot.child("profileImg").getValue().toString();
                    }

                    holder.postUserTextView.setText(userName);
                    if(!userProfileImg.isEmpty())
                        Picasso.get().load(userProfileImg).placeholder(R.drawable.profile).into(holder.postProfileImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        holder.likeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(postKey).hasChild(firebaseUser.getUid())){
                            likesRef.child(postKey).child(firebaseUser.getUid()).removeValue();
                        }else{
                            likesRef.child(postKey).child(firebaseUser.getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.commentImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentsActivity.class);
                intent.putExtra("postKey", postKey);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        private ImageView postImageView, postProfileImageView;
        private TextView postUserTextView, postDateTextView, postTimeTextView, postDescTextView;
        private ImageButton likeImageBtn, commentImageBtn;
        private TextView numLikesTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImageView = itemView.findViewById(R.id.postImageView);
            postProfileImageView = itemView.findViewById(R.id.postProfileImageView);

            likeImageBtn = itemView.findViewById(R.id.likeImageBtn);
            commentImageBtn = itemView.findViewById(R.id.commentImageBtn);

            postUserTextView = itemView.findViewById(R.id.postUserTextView);
            postDateTextView = itemView.findViewById(R.id.postDateTextView);
            postTimeTextView = itemView.findViewById(R.id.postTimeTextView);
            postDescTextView = itemView.findViewById(R.id.postClickDescTextView);
            numLikesTextView = itemView.findViewById(R.id.numLikesTextView);

        }
    }
}
