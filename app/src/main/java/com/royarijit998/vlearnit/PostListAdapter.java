package com.royarijit998.vlearnit;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    private ArrayList<Posts> postsArrayList;

    public PostListAdapter(ArrayList<Posts> postsArrayList) {
        this.postsArrayList = postsArrayList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout, null, false);
        PostViewHolder postViewHolder = new PostViewHolder(layoutView);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, final int position) {
        // TextViews
        holder.postUserTextView.setText(postsArrayList.get(position).getUfullname());
        holder.postDateTextView.setText(postsArrayList.get(position).getDate());
        holder.postTimeTextView.setText(postsArrayList.get(position).getTime());
        holder.postDescTextView.setText(postsArrayList.get(position).getDescription());

        // ImageViews
        Picasso.get().load(postsArrayList.get(position).getPostImg()).placeholder(R.drawable.profile).into(holder.postImageView);
        Picasso.get().load(postsArrayList.get(position).getUprofileImg()).placeholder(R.drawable.profile).into(holder.postProfileImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ClickPostActivity.class);
                intent.putExtra("postKey", postsArrayList.get(position).getKey());
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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            postImageView = itemView.findViewById(R.id.postImageView);
            postProfileImageView = itemView.findViewById(R.id.postProfileImageView);

            postUserTextView = itemView.findViewById(R.id.postUserTextView);
            postDateTextView = itemView.findViewById(R.id.postDateTextView);
            postTimeTextView = itemView.findViewById(R.id.postTimeTextView);
            postDescTextView = itemView.findViewById(R.id.postClickDescTextView);

        }
    }
}
